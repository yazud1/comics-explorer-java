package info7.affichage_personnage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Lecture_fichier_json {
    public static String[][] lecture(JSONObject reponse) {
        String[][] tableau_results = new String[1][12]; // 1 ligne, 12 colonnes pour les nouvelles informations

        // Accéder à "results"
        Object resultsObject = reponse.get("results");
        JSONObject result = null;

        // Vérifiez si "results" est un tableau ou un objet
        if (resultsObject instanceof JSONArray) {
            JSONArray resultsArray = (JSONArray) resultsObject;
            if (!resultsArray.isEmpty()) {
                result = (JSONObject) resultsArray.get(0); // Prendre le premier élément
            }
        } else if (resultsObject instanceof JSONObject) {
            result = (JSONObject) resultsObject; // Directement un objet
        }

        // Traiter le résultat s'il est présent
        if (result != null) {
            // Champs à extraire
            String name = result.get("name") instanceof String ? (String) result.get("name") : "Nom indisponible";
            JSONObject image = (JSONObject) result.get("image");
            String imageUrl = (image != null && image.get("original_url") instanceof String) 
                    ? (String) image.get("original_url") : "URL indisponible";

            String deck = result.get("deck") instanceof String ? (String) result.get("deck") : "Aucune description disponible";
            String count = result.get("count_of_issue_appearances") != null
                    ? String.valueOf(result.get("count_of_issue_appearances")) : "0";

            JSONObject publisher = (JSONObject) result.get("publisher");
            String publisherName = (publisher != null && publisher.get("name") instanceof String)
                    ? (String) publisher.get("name") : "Éditeur inconnu";

            String id = result.get("id") != null ? String.valueOf(result.get("id")) : "ID inconnu";

            // Gestion de champs complexes
            JSONObject originObject = (JSONObject) result.get("origin");
            String origin = (originObject != null && originObject.get("name") instanceof String) 
                    ? (String) originObject.get("name") : "Origine inconnue";

            JSONObject firstAppearanceObject = (JSONObject) result.get("first_appeared_in_issue");
            String firstAppearance = (firstAppearanceObject != null && firstAppearanceObject.get("name") instanceof String)
                    ? (String) firstAppearanceObject.get("name") : "Première apparition inconnue";

            Object genderObject = result.get("gender");
            String gender;

            if (genderObject != null) {
                int genderValue = Integer.parseInt(genderObject.toString());
                gender = (genderValue == 1) ? "M" : (genderValue == 2) ? "F" : "Genre inconnu";
            } else {
                gender = "Genre inconnu";
            }

            	

            JSONArray powersArray = (JSONArray) result.get("powers");
            String powers = (powersArray != null && !powersArray.isEmpty()) ? powersArray.toString() : "Pouvoirs inconnus";

            String realName = result.get("real_name") instanceof String ? (String) result.get("real_name") : "Nom réel inconnu";
            String birth = result.get("birth") instanceof String ? (String) result.get("birth") : "Date de naissance inconnue";

            // Remplir le tableau
            tableau_results[0][0] = name;
            tableau_results[0][1] = imageUrl;
            tableau_results[0][2] = deck;
            tableau_results[0][3] = count;
            tableau_results[0][4] = publisherName;
            tableau_results[0][5] = id;
            tableau_results[0][6] = origin;
            tableau_results[0][7] = firstAppearance;
            tableau_results[0][8] = gender;
            tableau_results[0][9] = powers;
            tableau_results[0][10] = realName;
            tableau_results[0][11] = birth;

            return tableau_results;
        } else {
            System.out.println("Aucun résultat trouvé pour cet ID.");
            return null;
        }
    }
}
