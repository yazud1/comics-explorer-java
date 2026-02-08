package info7.bibliotheque;

import info7.connexion.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Bibliotheque {

    // Méthode pour ajouter un comic à la bibliothèque
    public static void ajouterComic(int userId, String comicId, String status,String image_url) {
        String query = "INSERT INTO user_comics (user_id, comic_id, reading_status, added_at,image_url) VALUES (?, ?, ?, CURRENT_TIMESTAMP,?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            stmt.setString(3, status);
            stmt.setString(4, image_url);
            stmt.executeUpdate();
            System.out.println("Comic ajouté à la bibliothèque avec le statut : " + status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour le statut de lecture d'un comic
    public static void modifierStatutLecture(int userId, String comicId, String newStatus) {
        String query = "UPDATE user_comics SET reading_status = ? WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
            stmt.setString(3, comicId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Statut de lecture mis à jour : " + newStatus);
            } else {
                System.out.println("Comic non trouvé dans la bibliothèque.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer le statut de lecture d'un comic
    public static String getStatutLecture(int userId, String comicId) {
        String query = "SELECT reading_status FROM user_comics WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("reading_status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Statut inconnu"; // Valeur par défaut si non trouvé
    }

    // Méthode pour récupérer tous les comics d'un utilisateur
    public static List<Integer> getComicsUtilisateur(int userId) {
        List<Integer> comics = new ArrayList<>();
        String query = "SELECT comic_id FROM user_comics WHERE user_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comics.add(rs.getInt("comic_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comics;
    }
    
    public static void retirerComic(int userId, String comicId) {
        String query = "DELETE FROM user_comics WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Comic retiré de la bibliothèque avec succès !");
            } else {
                System.out.println("Le comic n'existe pas dans la bibliothèque.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getComicsUtilisateurByStatus(int userId, String status) {
        List<String> comicIds = new ArrayList<>();
        String query = "SELECT comic_id,image_url FROM user_comics WHERE user_id = ? AND reading_status = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comicIds.add(rs.getString("comic_id")+","+rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comicIds;
    }




}