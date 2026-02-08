/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info7.page_accueil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.*;

public class Api_page_accueil {

    private JSONObject jsonResponse; // Variable pour stocker la réponse JSON

    public Api_page_accueil() {
        // Récupérer et stocker le JSON lors de l'initialisation de l'objet
        jsonResponse = fetchJsonFromApi();
    }

    private JSONObject fetchJsonFromApi() {
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient();

        String url = "https://comicvine.gamespot.com/api/issues/";
        String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675";

        // Construire l'URL
        HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("api_key", apiKey)
                .addQueryParameter("format", "json")
                .addQueryParameter("sort", "date:desc") // Tri par date décroissante
                .addQueryParameter("limit", "100") // Obtenir jusqu'à 100 comics
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .addHeader("User-Agent", "ComicApp")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                JSONParser parser = new JSONParser();
                jsonObject = (JSONObject) parser.parse(jsonResponse);
            } else {
                System.out.println("Erreur : " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    
    private JSONObject fetchJsonFromApiTitre(String titleFilter) {
    JSONObject jsonObject = null;
    OkHttpClient client = new OkHttpClient();

    String url = "https://comicvine.gamespot.com/api/issues/";
    String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675";

    // Construire l'URL
    HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("format", "json")
            .addQueryParameter("limit", "1");

    // Ajouter le filtre sur le nom si fourni
    if (titleFilter != null && !titleFilter.isEmpty()) {
        httpUrlBuilder.addQueryParameter("filter", "name:" + titleFilter);
    }

    HttpUrl httpUrl = httpUrlBuilder.build();

    Request request = new Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("User-Agent", "ComicApp")
            .build();

    try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
            String jsonResponse = response.body().string();
            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(jsonResponse);
        } else {
            System.out.println("Erreur : " + response.code());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return jsonObject;
}

    public List<JSONObject> getRecentComics() {
        int count=15;
        List<JSONObject> recentComics = new ArrayList<>();

        if (jsonResponse != null && jsonResponse.containsKey("results")) {
            JSONArray results = (JSONArray) jsonResponse.get("results");

            // Ajouter les 'count' premiers comics triés par date (ils le sont déjà)
            for (int i = 0; i < Math.min(count, results.size()); i++) {
                recentComics.add((JSONObject) results.get(i));
            }
        }

        return recentComics;
    }

    public List<JSONObject> getRandomComics() {
        int count=15;
        List<JSONObject> randomComics = new ArrayList<>();

        if (jsonResponse != null && jsonResponse.containsKey("results")) {
            JSONArray results = (JSONArray) jsonResponse.get("results");

            // Convertir JSONArray en une liste
            List<JSONObject> comicsList = new ArrayList<>();
            for (Object result : results) {
                comicsList.add((JSONObject) result);
            }

            // Mélanger les résultats pour un choix aléatoire
            Collections.shuffle(comicsList);

            // Ajouter les 'count' premiers comics aléatoires
            for (int i = 0; i < Math.min(count, comicsList.size()); i++) {
                randomComics.add(comicsList.get(i));
            }
        }

        return randomComics;
    }
    
    public JSONObject getComicByName(String title) {
    // Récupérer les résultats de l'API en filtrant par titre
    JSONObject jsonResponse = fetchJsonFromApiTitre(title);

    if (jsonResponse != null && jsonResponse.containsKey("results")) {
        JSONArray results = (JSONArray) jsonResponse.get("results");

        // Retourner le premier résultat trouvé (si disponible)
        if (!results.isEmpty()) {
            return (JSONObject) results.get(0);
        }
    }

    System.out.println("Comic not found: " + title);
    return null;
}
    
    public List<JSONObject> getClassiques() {
    // Liste des titres à rechercher
    List<String> titles = List.of(
        "fragments", "batman", "superman", "spiderman", "wonder woman",
        "doctor who", "thor", "aquaman", "she-hulk", "wolverine",
        "hulk", "venom", "joker", "captain america", "x men"
    );

    // Liste concurrente pour stocker les résultats
    List<JSONObject> comics = new CopyOnWriteArrayList<>();

    // Création d'un thread pool
    ExecutorService executor = Executors.newFixedThreadPool(5); // 5 threads en parallèle

    try {
        // Liste des tâches pour chaque titre
        List<Future<?>> futures = new ArrayList<>();
        for (String title : titles) {
            futures.add(executor.submit(() -> {
                JSONObject comic = getComicByName(title);
                if (comic != null) {
                    comics.add(comic);
                }
            }));
        }

        // Attendre que toutes les tâches soient terminées
        for (Future<?> future : futures) {
            future.get(); // Bloque jusqu'à la fin de la tâche
        }
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    } finally {
        executor.shutdown(); // Fermer le thread pool
    }

    return comics;
}

    public void recherche(String name) {
        // Implémentez la logique de recherche ici
        System.out.println("Recherche effectuée pour : " + name);
    }
}
