package info7.page_recherche_titre;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Assia
 */

public class Lecture_fichier_json_titre {
    public static String[][] lecture_titre(JSONObject reponse) {
    String[][] tableau_results;
        
        
        JSONParser parser = new JSONParser();
            
            // Accéder au tableau "results"
            JSONArray results = (JSONArray) reponse.get("results");
            
            
            //creer tableau reponse
            int taille_tableau=results.size();
            tableau_results= new String[taille_tableau][4];

            if (results != null && !results.isEmpty()) {
                for(int i=0;i<taille_tableau;i++){
                    // Accéder au premier élément du tableau
                    JSONObject firstResult = (JSONObject) results.get(i);

                    // Lire le champ "name"
                    String name = (String) firstResult.get("name");
                    
                    String api_detail_url = (String) firstResult.get("api_detail_url");
                    System.out.println(api_detail_url);
                    System.out.println(api_detail_url);
                    JSONObject image = (JSONObject) firstResult.get("image");
                
                    String icon_url = (String) image.get("original_url");
                    
     
                    String cover_date =(String) firstResult.get("cover_date");
                    tableau_results[i][3]=api_detail_url;
                    tableau_results[i][0]=name;
                    tableau_results[i][1]=icon_url;
                    tableau_results[i][2]=cover_date;
                }
                return tableau_results;
            } else {
                System.out.println("Le tableau 'results' est vide ou inexistant.");
                return null;
            }
    }
    
    public Lecture_fichier_json_titre(){
    
    }
}
