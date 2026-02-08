package info7.affichage_comics;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class requete_api_comics {
    public static JSONObject recherche(String api_detail_url) {
        JSONObject jsonObject=null;
        OkHttpClient client = new OkHttpClient();
       
       // URL de l'API et clé API
       String url = api_detail_url; // Exemple d'endpoint de l'API
       String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675"; // Remplace par ta clé API


       // Construire l'URL avec le paramètre api_key
       HttpUrl httpUrl = HttpUrl.parse(url).newBuilder()
               .addQueryParameter("api_key", apiKey)
               .addQueryParameter("format", "json") // Formater la réponse en JSON (optionnel)
               .addQueryParameter("field_list","id,api_detail_url,character_credits,name,image,cover_date,description,issue_number,volume,")
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
   
   public requete_api_comics(){
   }

}
