package info7.affichage_comics;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import info7.bibliotheque.Bibliotheque;
import info7.connexion.Database;
import info7.connexion.UserRegistrationForm;
import info7.page_accueil.Fenetre;
import info7.affichage_personnage.Fenetre_affichage_personnages;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.SwingWorker;





public class Fenetre_affichage_comics extends JFrame {
    
    private Fenetre fenetre;
    private JPanel resultPanel;
    private String[] reponse;
    private JLabel loadingLabel;
    private JPanel loadingPanel;
    private JSONObject json_reponse;
    
    
    public Fenetre_affichage_comics(Fenetre fenetre){
        this.fenetre=fenetre;
    }

    public void DebutAffichage(String id) {
        // Ajouter un panneau pour le message de chargement
        loadingPanel = new JPanel();
        loadingPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.setBackground(new Color(50, 50, 50));
        loadingPanel.setForeground(Color.WHITE);
        loadingLabel = new JLabel("Recherche en cours, veuillez patienter...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setBackground(new Color(50, 50, 50));
        loadingPanel.add(loadingLabel);
        System.out.println("1");
      
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.add(loadingPanel);
        resultPanel.setBackground(new Color(50, 50, 50));

        JScrollPane scrollPane = new JScrollPane(resultPanel);
        add(scrollPane, BorderLayout.CENTER);
        System.out.println("2");
        
        
        
        // Désactiver le bouton de recherche
        //searchButton.setEnabled(false);

        // Mettre à jour l'interface immédiatement
        fenetre.UpdateCentralPanel(resultPanel);
        SwingUtilities.invokeLater(() -> fenetre.UpdateCentralPanel(resultPanel));

        // Lancer la recherche dans une tâche asynchrone
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                affichage(id); // Effectuer la recherche
                return null;
            }

            @Override
            protected void done() {
                // Réactiver le bouton
                //searchButton.setEnabled(true);

                // Retirer le message de chargement
                resultPanel.remove(loadingPanel);

                // Mettre à jour l'affichage avec les résultats
                updatePage();
            }
        }.execute();
        
    }

    
    
    public void affichage(String id) {
        System.out.println("5");
        requete_api_comics requete = new requete_api_comics();
        System.out.println(id);
        json_reponse = (JSONObject) requete.recherche(id).get("results");
        System.out.println("5");
        

        //Lecture_fichier_json lecture = new Lecture_fichier_json();
        //reponse = lecture.lecture(json_reponse);
        updatePage();
        System.out.println("5");
    }

    private void updatePage() {
        resultPanel.removeAll();

        if (json_reponse != null) {
            // ID du comic
                String comicId = (String) json_reponse.get("api_detail_url");

                // Panneau principal pour chaque comic
                JPanel panelComics = new JPanel(new BorderLayout(20, 20));
                panelComics.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
                panelComics.setBackground(new Color(50, 50, 50));
                panelComics.setForeground(Color.WHITE);

                // Conteneur principal pour le titre et les icônes
                JPanel titleAndIconsContainer = new JPanel(new BorderLayout());
                titleAndIconsContainer.setBackground(new Color(50, 50, 50));
                titleAndIconsContainer.setForeground(Color.WHITE);


                // Titre centré
                JLabel nameLabel = new JLabel((String)json_reponse.get("name"));
                nameLabel.setFont(new Font("Arial", Font.BOLD, 35));
                nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                nameLabel.setForeground(Color.WHITE);

                // Ajouter le titre directement au centre
                titleAndIconsContainer.add(nameLabel, BorderLayout.CENTER);

                // Panneau des icônes (favoris, achat et bibliothèque)
                JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
                iconPanel.setForeground(Color.WHITE);
                iconPanel.setBackground(new Color(50, 50, 50));


                
                // Conteneur principal pour l'image et la description
                JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
                mainPanel.setBackground(new Color(50, 50, 50));

                // Image du comic
                JLabel imageLabel;
                try {
                    JSONObject image = (JSONObject) json_reponse.get("image");
                    URI uri = new URI((String) image.get("original_url")); // Crée une URI à partir de la chaîne
                    URL imageUrl = uri.toURL(); // Convertit l'URI en URL
                    ImageIcon icon = new ImageIcon(
                        new ImageIcon(imageUrl).getImage()
                                .getScaledInstance(300, 450, Image.SCALE_SMOOTH) // Taille de l'image ajustée
                    );
                    imageLabel = new JLabel(icon);
                } catch (Exception a) {
                    System.err.println("Erreur : " + a.getMessage());
                    imageLabel = new JLabel("Image non disponible");
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                }

                mainPanel.add(imageLabel, BorderLayout.WEST);

                // Informations textuelles
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(new Color(50, 50, 50));
                iconPanel.setForeground(Color.WHITE);
                infoPanel.add(Box.createVerticalStrut(100));

                // Description
                JLabel descriptionLabel = new JLabel("<html><div style='width:400px;'><b>Description :</b><br>" + (String) json_reponse.get("description") + "</div></html>");
                descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
                descriptionLabel.setBackground(new Color(50, 50, 50));
                descriptionLabel.setForeground(Color.WHITE);
                infoPanel.add(descriptionLabel);
                infoPanel.add(Box.createVerticalStrut(20));
             


                // Date de publication
                JLabel dateLabel = new JLabel("<html><b>Date de publication :</b> " + ((String) json_reponse.get("cover_date") != null ? (String) json_reponse.get("cover_date") : "Date inconnue") + "</html>");
                dateLabel.setFont(new Font("Arial", Font.PLAIN, 20));
                dateLabel.setBackground(new Color(50, 50, 50));
                dateLabel.setForeground(Color.WHITE);
                infoPanel.add(dateLabel);
                infoPanel.add(Box.createVerticalStrut(20));

                
                // Personnages présents
                JLabel charactersLabel = new JLabel("<html><b>Personnages présents :</b></html>");
                charactersLabel.setFont(new Font("Arial", Font.PLAIN, 20));
                charactersLabel.setBackground(new Color(50, 50, 50));
                charactersLabel.setForeground(Color.WHITE);
                infoPanel.add(charactersLabel);
                infoPanel.add(Box.createVerticalStrut(10));
                

                JSONArray credits = (JSONArray) json_reponse.get("character_credits");
        
                if (credits != null && !credits.isEmpty()) {
                    for (Object obj : credits) {
                        JSONObject character = (JSONObject) obj;
                        String characterName = (String) character.get("name");

                        // Ajouter chaque nom de personnage
                        JLabel characterLabel = new JLabel("- " + characterName);
                        characterLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                        characterLabel.setForeground(Color.BLUE);
                        characterLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseEntered(MouseEvent e) {
                                // Changer le curseur en "main" quand la souris entre dans le panel
                                characterLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                // Remettre le curseur par défaut quand la souris sort du panel
                                characterLabel.setCursor(Cursor.getDefaultCursor());
                            }
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent e) {
                                // Récupérer l'ID du personnage sélectionné (colonne 5)


                                String url_detail = (String) character.get("api_detail_url");
                                Fenetre_affichage_personnages fenetreDetails = new Fenetre_affichage_personnages(fenetre);
                                fenetreDetails.StartUpdate(url_detail);  
                            }
                        });
                        infoPanel.add(characterLabel);
                        infoPanel.add(Box.createVerticalStrut(5)); // Espacement entre les noms
                        
                    }
                } else {
                    // Si aucun personnage n'est trouvé
                    JLabel noCharacterLabel = new JLabel("Aucun personnage répertorié.");
                    noCharacterLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    noCharacterLabel.setBackground(new Color(50, 50, 50));
                    noCharacterLabel.setForeground(Color.WHITE);
                    infoPanel.add(noCharacterLabel);
                    infoPanel.add(Box.createVerticalStrut(5));
                }
                if (UserRegistrationForm.connectedUserId != -1) { // Vérifie si l'utilisateur est connecté
                    // Charger les icônes
                    ImageIcon starIcon = loadIcon("info7/star.png", 70, 70);
                    ImageIcon starIconFilled = loadIcon("info7/star_remplie.png", 70, 70);
                    ImageIcon bookIcon = loadIcon("info7/bibli.png", 70, 70);
                    ImageIcon bookIconFilled = loadIcon("info7/bibli_remplie.png", 70, 70);
                    ImageIcon buyIcon = loadIcon("info7/achat.png", 70, 70);
                    ImageIcon buyIconFilled = loadIcon("info7/achat_remplie.png", 70, 70);

                    // Bouton Favoris
                    boolean isFavorited = UserRegistrationForm.userFavorites.contains(comicId);
                    JButton favoritesButton = new JButton(isFavorited ? starIconFilled : starIcon);
                    favoritesButton.setBorderPainted(false);
                    favoritesButton.setContentAreaFilled(false);
                    favoritesButton.addActionListener(e -> {
                        int userId = UserRegistrationForm.connectedUserId;
                        String title = (String) json_reponse.get("name"); // Assurez-vous que data[0] contient le titre
                        JSONObject image = (JSONObject) json_reponse.get("image");
                        String imageUrl = (String) image.get("original_url"); // Assurez-vous que data[1] contient l'URL de l'image
                        String cover_date = (String) json_reponse.get("cover_date");

                        if (isFavorited) {
                            Database.removeFavorite(userId, comicId); // Supprime le favori
                            UserRegistrationForm.userFavorites.remove(comicId); // Mise à jour locale
                            favoritesButton.setIcon(starIcon);
                            JOptionPane.showMessageDialog(fenetre, "Comic retiré de vos favoris !");
                        } else {
                            Database.addFavorite(userId, comicId, title, imageUrl, cover_date); // Ajoute le favori
                            UserRegistrationForm.userFavorites.add(comicId); // Mise à jour locale
                            favoritesButton.setIcon(starIconFilled);
                            JOptionPane.showMessageDialog(fenetre, "Comic ajouté à vos favoris !");
                        }
                    });

                 // Gestion Bibliothèque
                    JPanel libraryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    String currentStatus = Bibliotheque.getStatutLecture(UserRegistrationForm.connectedUserId, comicId);
                    JSONObject image = (JSONObject) json_reponse.get("image");
                    String image_url = (String) image.get("original_url");
                    if ("Statut inconnu".equals(currentStatus)) {
                        JButton addToLibraryButton = new JButton();
                        addToLibraryButton.setIcon(bookIcon);
                        addToLibraryButton.addActionListener(e -> {
                            Bibliotheque.ajouterComic(UserRegistrationForm.connectedUserId, comicId, "à lire",image_url);
                            JOptionPane.showMessageDialog(this, "Comic ajouté à votre bibliothèque avec le statut 'à lire' !");
                            affichage(String.valueOf(comicId)); // Recharge les données
                        });
                        libraryPanel.add(addToLibraryButton);
                        iconPanel.add(addToLibraryButton);
                    } else {
                        JLabel statusLabel = new JLabel();
                        JButton deleteToLibraryButton = new JButton();
                        deleteToLibraryButton.setIcon(bookIconFilled);
                        deleteToLibraryButton.addActionListener(e -> {
                            Bibliotheque.retirerComic(UserRegistrationForm.connectedUserId, comicId);
                            JOptionPane.showMessageDialog(this, "Comic supprimé de la bibbliothèque' !");
                            affichage(String.valueOf(comicId)); // Recharge les données
                        });
                        libraryPanel.add(deleteToLibraryButton);
                        iconPanel.add(deleteToLibraryButton);
                        
                        JComboBox<String> statusDropdown = new JComboBox<>(new String[]{"à lire", "en cours de lecture", "déjà lu"});
                        statusDropdown.setSelectedItem(currentStatus);
                        statusDropdown.addActionListener(e -> {
                            String newStatus = (String) statusDropdown.getSelectedItem();
                            Bibliotheque.modifierStatutLecture(UserRegistrationForm.connectedUserId, comicId, newStatus);
                            JOptionPane.showMessageDialog(this, "Statut de lecture mis à jour : " + newStatus);
                        });            
                        iconPanel.add(statusLabel);
                        iconPanel.add(statusDropdown);
                        
                    }
                


                    // Bouton Achat
                    JButton buyButton = new JButton(buyIcon);
                    buyButton.setBorderPainted(false);
                    buyButton.setContentAreaFilled(false);
                    buyButton.addActionListener(e -> {
                        boolean isInBuying = toggleBuying((String) json_reponse.get("name"));
                        buyButton.setIcon(isInBuying ? buyIconFilled : buyIcon);
                        JOptionPane.showMessageDialog(fenetre, isInBuying ? "Ajouté à vos achats !" : "Retiré de vos achats !");
                    });
                    
                    
                 // Bouton Achat
                    boolean isPurchased = Database.isPurchase(UserRegistrationForm.connectedUserId, comicId);
                    JButton purchaseButton = new JButton(isPurchased ? buyIconFilled : buyIcon);
                    purchaseButton.setBorderPainted(false);
                    purchaseButton.setContentAreaFilled(false);
                    purchaseButton.addActionListener(a -> {
                        int userId = UserRegistrationForm.connectedUserId;
                        String title = (String) json_reponse.get("name"); // Assurez-vous que data[0] contient le titre
                        String cover_date = (String) json_reponse.get("cover_date");
                           if (isPurchased) {
                           	Database.removePurchase(userId, comicId); 
                               UserRegistrationForm.userPurchase.remove(comicId); // Mise à jour locale
                               purchaseButton.setIcon(buyIcon);
                               JOptionPane.showMessageDialog(fenetre, "Comic retiré de votre liste d'achat !");
                           } else {
                               Database.addPurchase(userId, comicId, title, image_url,cover_date); // Ajoute l'achat
                               UserRegistrationForm.userPurchase.add(comicId); // Mise à jour locale
                               purchaseButton.setIcon(buyIconFilled);
                               JOptionPane.showMessageDialog(fenetre, "Comic ajouté à votre liste d'achat !");           
                           }
                    });


                    // Ajouter les boutons au panneau des icônes
                    iconPanel.add(purchaseButton);
                    iconPanel.add(favoritesButton);
                }
                    

                // Ajouter les conteneurs au panneau principal
                

                mainPanel.add(infoPanel, BorderLayout.CENTER);
                panelComics.add(mainPanel, BorderLayout.SOUTH);
                panelComics.add(titleAndIconsContainer, BorderLayout.NORTH); // Titre
                panelComics.add(iconPanel, BorderLayout.EAST); // Icônes

                resultPanel.add(panelComics);
                resultPanel.add(Box.createVerticalStrut(50)); // Espacement entre les panneaux 
        }
        else {
        	// Panneau pour afficher le message et l'icône
            JPanel noResultPanel = new JPanel();
            noResultPanel.setLayout(new BoxLayout(noResultPanel, BoxLayout.Y_AXIS)); // Disposition verticale
            noResultPanel.setBackground(resultPanel.getBackground()); // Assure la cohérence des couleurs

            // Ajouter le texte "Aucun Titre trouvé"
            JLabel noResultLabel = new JLabel("Aucun Titre trouvé.", JLabel.CENTER);
            noResultLabel.setFont(new Font("Arial", Font.BOLD, 20));
            noResultLabel.setForeground(Color.RED);
            noResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer horizontalement

            // Ajouter l'icône
            ImageIcon noResultIcon = createStyledIcon2();
            if (noResultIcon != null) {
                JLabel iconLabel = new JLabel(noResultIcon);
                iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer horizontalement
                noResultPanel.add(iconLabel); // Ajouter l'icône en premier
            }

            // Ajouter le texte après l'icône
            noResultPanel.add(Box.createVerticalStrut(10)); // Espacement vertical
            noResultPanel.add(noResultLabel);

            // Ajouter le panneau au panneau de résultats
            resultPanel.add(noResultPanel);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }




    // Méthode utilitaire pour charger une icône avec redimensionnement
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            URL resource = getClass().getClassLoader().getResource(path);
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthodes pour gérer les favoris et la bibliothèque
    private List<String> favorites = new ArrayList<>();
    private List<String> library = new ArrayList<>();
    private List<String> buy = new ArrayList<>();
    
    private boolean toggleFavorite(String title) {
        if (favorites.contains(title)) {
            favorites.remove(title);
            return false;
        } else {
            favorites.add(title);
            return true;
        }
    }

    private boolean toggleLibrary(String title) {
        if (library.contains(title)) {
            library.remove(title);
            return false;
        } else {
            library.add(title);
            return true;
        }
    }
    
    private boolean toggleBuying(String title) {
        if (buy.contains(title)) {
            buy.remove(title);
            return false;
        } else {
            buy.add(title);
            return true;
        }
    }
    
    private static ImageIcon createStyledIcon2() {
        try {
            URL resource = UserRegistrationForm.class.getClassLoader().getResource("info7/pblm.png");
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void StartUpdate(String id){
        System.out.println("0");
        DebutAffichage(id);
        fenetre.UpdateCentralPanel(resultPanel);
    }
}
