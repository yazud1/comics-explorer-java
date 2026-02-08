package info7.page_recherche_titre;

import java.io.FileWriter;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 *
 * @author Assia
 */

public class Requete_api_recherche_titre {
    public static JSONObject recherche(String name) {
        JSONObject jsonObject=null;
        OkHttpClient client = new OkHttpClient();
       
       // URL de l'API et clé API
       String url = "https://comicvine.gamespot.com/api/issues"; // Exemple d'endpoint de l'API
       String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675"; // Updated API key

       // Construire l'URL avec le paramètre api_key
       HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
               .addQueryParameter("api_key", apiKey)
               .addQueryParameter("format", "json") // Formater la réponse en JSON (optionnel)
               .addQueryParameter("filter","name:"+name)
               .addQueryParameter("field_list","api_detail_url,name,image,cover_date")
               .build();

       Request request = new Request.Builder()
               .url(httpUrl)
               .get()
               .addHeader("User-Agent", "ComicVineApp/1.0") // Remplace "YourAppName" par le nom de ton application
               .build();

       try (Response response = client.newCall(request).execute()) {
           if (response.isSuccessful()) {
           	// Obtenir la réponse JSON
               String jsonResponse = response.body().string();
               // Parser JSON
               JSONParser parser = new JSONParser();

               try {
                   // Convertir la chaîne en un objet JSON
                   jsonObject = (JSONObject) parser.parse(jsonResponse);
                   return jsonObject;
               } catch (Exception e) {
                   e.printStackTrace();
                }
           } else {
               System.out.println("Erreur : " + response.code());
           }
       } catch (Exception e) {
           e.printStackTrace();  
       }
       return jsonObject;
   }
   
   public Requete_api_recherche_titre(){
   }
}
