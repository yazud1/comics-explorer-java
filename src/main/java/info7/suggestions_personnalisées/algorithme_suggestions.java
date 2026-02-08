/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info7.suggestions_personnalisées;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import info7.connexion.Database;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.parser.JSONParser;



/**
 *
 * @author trist
 */
public class algorithme_suggestions {
    
    private JSONObject jsonResponse;
    
    public algorithme_suggestions() {
        // Récupérer et stocker le JSON lors de l'initialisation de l'objet
        jsonResponse = fetchJsonFromApi();
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
    
    public List<JSONObject> getSuggestionsWords(int userId) {
    // Liste concurrente pour stocker les résultats
    List<JSONObject> comics = new CopyOnWriteArrayList<>();
    Set<String> addedComicIds = ConcurrentHashMap.newKeySet(); // Pour stocker les IDs des comics déjà ajoutés

    // Création d'un thread pool
    ExecutorService executor = Executors.newFixedThreadPool(15); // 15 threads en parallèle

    try {
        // Obtenir les mots avec leurs fréquences
        Map<String, Integer> wordsWithFrequency = Database.getTitlesWordsWithFrequency(userId);

        // Afficher les mots et leur fréquence pour vérifier le bon fonctionnement de notre méthode
        for (Map.Entry<String, Integer> entry : wordsWithFrequency.entrySet()) {
            System.out.println("Mot : " + entry.getKey() + ", Fréquence : " + entry.getValue());
        }

        // Parcourir chaque mot et récupérer les comics associés
        List<Future<?>> futures = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordsWithFrequency.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();

            // Lancer des tâches pour récupérer les comics
            for (int i = 0; i < frequency; i++) {
                // Vérifier si la liste contient déjà 15 comics
                if (comics.size() >= 15) {
                    break;
                }

                futures.add(executor.submit(() -> {
                    JSONObject comic = getComicByName(word);
                    if (comic != null) {
                        String comicId = String.valueOf(comic.get("id"));
                        
                        // Vérifier que le comic n'est pas déjà dans la liste et pas dans les favoris
                        // Si le comic est déjà dans l'une des deux, on passe au suivant
                        if (!addedComicIds.contains(comicId) && !Database.isFavorite(userId,comicId)) {
                            // Ajouter le comic à la liste des suggestions et à la liste des comics ajoutés
                            comics.add(comic);
                            addedComicIds.add(comicId); // Ajouter l'ID du comic aux IDs ajoutés
                        }
                    }
                }));
            }

            // Sortir de la boucle si on a atteint la limite de 15 comics
            if (comics.size() >= 15) {
                break;
            }
        }

        // Attendre que toutes les tâches en cours soient terminées
        for (Future<?> future : futures) {
            future.get(); // Bloque jusqu'à la fin de la tâche
        }
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    } catch (SQLException e) {
        // Gestion spécifique de SQLException
        System.err.println("Erreur lors de la récupération des mots depuis la base de données : " + e.getMessage());
        e.printStackTrace();
    } finally {
        executor.shutdown(); // Fermer le thread pool
    }

    return comics; // Retourner la liste de comics
}



    
    public List<JSONObject> getDecadeComics(String decade) {
    List<JSONObject> decadeComics = new ArrayList<>();
    int count = 15;

    // Calculer la plage de dates pour la décennie
    int startYear = Integer.parseInt(decade.replace("s", ""));
    int endYear = startYear + 9;

    // Récupérer les comics filtrés par décennie
    JSONObject jsonResponse = fetchJsonFromApiWithDateRange(startYear, endYear);

    if (jsonResponse != null && jsonResponse.containsKey("results")) {
        JSONArray results = (JSONArray) jsonResponse.get("results");

        // Ajouter les 'count' premiers comics
        for (int i = 0; i < Math.min(count, results.size()); i++) {
            decadeComics.add((JSONObject) results.get(i));
        }
    }

    return decadeComics;
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
            .addQueryParameter("limit", "10");

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
    
    
    private JSONObject fetchJsonFromApiWithDateRange(int startYear, int endYear) {
    JSONObject jsonObject = null;
    OkHttpClient client = new OkHttpClient();

    String url = "https://comicvine.gamespot.com/api/issues/";
    String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675";

    // Construire l'URL
    HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("format", "json")
            .addQueryParameter("sort", "date:desc") // Tri par date décroissante
            .addQueryParameter("limit", "15") // Obtenir jusqu'à 100 comics
            .addQueryParameter("filter", String.format("cover_date:%d-01-01|%d-12-31", startYear, endYear)) // Filtre par plage de dates
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

}
