package info7.connexion;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Database {
	private static final String DB_URL = "jdbc:sqlite:bdd_compte.db";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }


    public static void addFavorite(int userId, String comicId, String title, String imageUrl, String cover_date) {
        ensureComicExists(comicId, title, imageUrl, cover_date); // Ajout ou vérification dans la table comics

        if (!isFavorite(userId, comicId)) {
            String query = "INSERT INTO favorites (user_id, comic_id, added_at, cover_date) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
            try (Connection connection = getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, comicId);
                stmt.setString(3, cover_date);

                stmt.executeUpdate();
                System.out.println("Favori ajouté avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Ce favori existe déjà !");
        }
}

    

    public static void addPurchase(int userId, String comicId, String title, String imageUrl, String cover_date) {
        ensureComicExists(comicId, title, imageUrl, cover_date); // Vérification ou ajout dans la table comics
        if (!isPurchase(userId, comicId)) {
            String query = "INSERT INTO purchase_list (user_id, comic_id, added_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (Connection connection = getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, comicId);
                stmt.executeUpdate();
                System.out.println("Achat ajouté avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Cet achat existe déjà !");
        }
    }


    
    public static Map<String, Integer> getTitlesWordsWithFrequency(int userId) throws SQLException {
    // Map pour stocker les mots et leurs fréquences
    Map<String, Integer> wordCountMap = new HashMap<>();
    String query = "SELECT title FROM comics " +
                   "JOIN favorites ON comics.id = favorites.comic_id " +
                   "JOIN users ON users.id = favorites.user_id " +
                   "WHERE users.id = ?;";

    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {

        // Remplacement du paramètre "?" par la valeur de userId
        stmt.setInt(1, userId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                if (title != null) {
                    // Diviser le titre en mots
                    String[] words = title.split("[\\s/.]+"); // Séparation par espace ou / ou .
                    for (String word : words) {
                        word = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""); // Nettoyer les mots
                        if (!word.isEmpty() && word.length() >= 4 && !"indisponible".equals(word)) {
                            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            }
        }

        // Trier les mots par occurrence décroissante
        return wordCountMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Trier par occurrences décroissantes
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Gérer les doublons (inutile ici)
                        LinkedHashMap::new // Préserver l'ordre des éléments triés
                ));
    }
}

    
    
 // Vérifie si un achat existe déjà pour un utilisateur donné
    public static boolean isPurchase(int userId, String comicId) {
        String query = "SELECT COUNT(*) FROM purchase_list WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static List<String> getAchat(int userId) {
        List<String> purchases = new ArrayList<>();
        String query = "SELECT comic_id FROM purchase_list WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    purchases.add(rs.getString("comic_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    public static void removePurchase(int userId, String comicId) {
        String query = "DELETE FROM purchase_list WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Achat retiré avec succès !");
            } else {
                System.out.println("L'achat n'existe pas.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère la décennie la plus fréquente dans les favoris
   public static String getMostFrequentDecade() {
    String query = "SELECT cover_date FROM favorites";
    Map<String, Integer> decadeCountMap = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String coverDateText = rs.getString("cover_date");

            if (coverDateText != null && !coverDateText.isEmpty()) {
                try {
                    // Convertir la chaîne de texte en LocalDate
                    LocalDate publicationDate = LocalDate.parse(coverDateText, formatter);
                    int year = publicationDate.getYear();

                    // Calculer la décennie
                    int decadeStart = (year / 10) * 10;
                    String decade = String.valueOf(decadeStart);

                    // Incrémenter le compteur pour cette décennie
                    decadeCountMap.put(decade, decadeCountMap.getOrDefault(decade, 0) + 1);
                } catch (DateTimeParseException e) {
                    System.err.println("Format de date invalide pour cover_date: " + coverDateText);
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Trouver la décennie la plus fréquente
    return decadeCountMap.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Aucune décennie trouvée");
}

    
    public static List<String> getFavorites(int userId) {
        List<String> favorites = new ArrayList<>();
        String query = "SELECT comic_id FROM favorites WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    favorites.add(rs.getString("comic_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }
    
    public static List<String> getMostFrequentWords() {
    List<String> wordList = new ArrayList<>();
    String query = "SELECT word, COUNT(word) AS frequency " +
                   "FROM comics " +
                   "GROUP BY word " +
                   "ORDER BY frequency DESC";
    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            // Ajouter les mots dans la liste triés par fréquence décroissante
            wordList.add(rs.getString("word"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return wordList;
}

    public static boolean isFavorite(int userId, String comicId) {
        String query = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void removeFavorite(int userId, String comicId) {
        String query = "DELETE FROM favorites WHERE user_id = ? AND comic_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, comicId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Comic retiré des favoris avec succès !");
            } else {
                System.out.println("Le comic n'était pas dans les favoris.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static String[] getComicDetailsById(String comicId) {
        String query = "SELECT title, image_url FROM comics WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, comicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String imageUrl = rs.getString("image_url");
                    return new String[]{title, imageUrl};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si aucun comic n'est trouvé
    }

    public static List<Integer> getComicsByStatus(int userId, int readingStatus) {
        List<Integer> comics = new ArrayList<>();
        String query = "SELECT comic_id FROM user_comics WHERE user_id = ? AND reading_status = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, readingStatus);
            
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

    

    public static void ensureComicExists(String comicId, String title, String imageUrl, String cover_date) {
        String query = "INSERT OR IGNORE INTO comics (id, title, image_url, cover_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            System.out.println(query);
            stmt.setString(1, comicId);
            stmt.setString(2, title);
            stmt.setString(3, imageUrl);
            stmt.setString(4, cover_date);
            System.out.println(query);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
}