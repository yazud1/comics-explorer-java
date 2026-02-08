package info7.page_recherche_personnage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Lecture_fichier_json {
    public static String[][] lecture(JSONObject reponse) {
        String[][] tableau_results;

        // Accéder au tableau "results"
        JSONArray results = (JSONArray) reponse.get("results");

        if (results != null && !results.isEmpty()) {
            int taille_tableau = results.size();
            tableau_results = new String[taille_tableau][6]; // 6 colonnes : name, image, apparitions, description, éditeur, id

            for (int i = 0; i < taille_tableau; i++) {
                // Accéder à chaque objet "result" dans le tableau
                JSONObject firstResult = (JSONObject) results.get(i);

                // Log des données pour vérifier la structure
                System.out.println("Personnage " + i + " JSON complet : " + firstResult);

                // Champs à extraire
                String name = (String) firstResult.get("name");

                JSONObject image = (JSONObject) firstResult.get("image");
                String icon_url = (image != null) ? (String) image.get("original_url") : "N/A";

                long issue_appearance = (firstResult.get("count_of_issue_appearances") != null) 
                                        ? (long) firstResult.get("count_of_issue_appearances") : 0;
                String issue_appearance_string = String.valueOf(issue_appearance);

                String description = (firstResult.get("deck") != null) ? (String) firstResult.get("deck") : "Aucune description disponible";

                JSONObject publisher = (JSONObject) firstResult.get("publisher");
                String publisherName = (publisher != null) ? (String) publisher.get("name") : "N/A";

                // Extraction de l'ID (Assurez-vous que la clé "id" existe)
                Object idObject = firstResult.get("api_detail_url");
                String api_detail_url = (idObject != null) ? String.valueOf(idObject) : "api_detail_url inconnu";

                // Log pour vérifier l'ID
                System.out.println("api_detail_url trouvé : " + api_detail_url);

                // Remplir le tableau avec les données
                tableau_results[i][0] = name;
                tableau_results[i][1] = icon_url;
                tableau_results[i][2] = issue_appearance_string;
                tableau_results[i][3] = description;
                tableau_results[i][4] = publisherName;
                tableau_results[i][5] = api_detail_url;
            }
            return tableau_results;
        } else {
            System.out.println("Le tableau 'results' est vide ou inexistant.");
            return null;
        }
    }

    public Lecture_fichier_json() {
    }
}

