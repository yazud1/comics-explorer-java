package info7.affichage_comics;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Lecture_fichier_json {
    public static String[] lecture(JSONObject reponse) {
        String[] tableau_results = new String[8]; 

        // Accéder à "results"
        
        JSONObject result = (JSONObject) reponse.get("results");


        // Traiter le résultat s'il est présent
        if (result != null) {
            // Champs à extraire
            String name = result.get("name") instanceof String ? (String) result.get("name") : "Nom indisponible";
            JSONObject image = (JSONObject) result.get("image");
            String icon_url = (image != null && image.get("original_url") instanceof String) 
                    ? (String) image.get("original_url") : "URL indisponible";

            String description = result.get("description") instanceof String ? (String) result.get("description") : "Aucune description disponible";
 
            String cover_date = result.get("cover_date") instanceof String ? (String) result.get("cover_date") : "Aucune date disponible";
            

            String id = result.get("id") != null ? String.valueOf(result.get("id")) : "ID inconnu";
            String issue_number = result.get("issue_number") != null ? String.valueOf(result.get("issue_number")) : "Numéro de série inconnu";
            String volume = result.get("volume") != null ? String.valueOf(result.get("volume")) : "volume inconnu";
            JSONObject character_credits= (JSONObject) result.get("character_credits");



            // Remplir le tableau
            tableau_results[0] = name;
            tableau_results[1] = icon_url;
            tableau_results[2] = cover_date;
            tableau_results[3] = description;
            tableau_results[4] = issue_number;
            tableau_results[5] = volume;
            tableau_results[6] = id;
            
            
            return tableau_results;
        } else {
            System.out.println("Aucun résultat trouvé pour cet ID.");
            return null;
        }
    } 
}

