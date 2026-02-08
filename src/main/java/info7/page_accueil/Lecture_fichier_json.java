/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info7.page_accueil;

/**
 *
 * @author trist
 */
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Lecture_fichier_json {
    public static String[][] lecture(JSONObject jsonReponse) {
    String[][] tableau_results;
        
        

            // Accéder au tableau "results"
            JSONArray results = (JSONArray) jsonReponse.get("results");
            
            
            //creer tableau reponse
            int taille_tableau=results.size();
            tableau_results= new String[taille_tableau][3];

            if (results != null && !results.isEmpty()) {
                for(int i=0;i<taille_tableau;i++){
                    // Accéder au premier élément du tableau
                    JSONObject firstResult = (JSONObject) results.get(i);

                    // Lire le champ "name"
                    String name = (String) firstResult.get("name");
                    String api_detail_url = String.valueOf(firstResult.get("api_detail_url")); 
                
                    JSONObject image = (JSONObject) firstResult.get("image");
                
                    String icon_url = (String) image.get("icon_url");
                    tableau_results[i][0]=name;
                    tableau_results[i][1]=icon_url;
                    tableau_results[i][3]=api_detail_url;
                }
                return tableau_results;
            } else {
                System.out.println("Le tableau 'results' est vide ou inexistant.");
                return null;
            }
  
        }
    
}
