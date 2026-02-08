package info7.page_accueil;

import info7.affichage_achat.Fenetre_achat;
import info7.affichage_comics.Fenetre_affichage_comics;

import java.util.Collections;
import info7.affichage_favoris.Fenetre_favoris;
import info7.bibliotheque.Bibliotheque;
import info7.page_recherche_personnage.Fenetre_affichage_recherche_personnage;
import info7.page_recherche_titre.Fenetre_affichage_recherche_titre;
import info7.page_recherche_tout.Fenetre_affichage_recherche_tout;
import info7.connexion.Database;
import info7.connexion.UserRegistrationForm;
import info7.affichage_bibliotheque.Fenetre_affichage_bibliotheque;

import info7.suggestions_personnalisées.algorithme_suggestions;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.event.MouseWheelEvent;

public class Fenetre extends JFrame {

    private JPanel centralPanel;
    private String selectedOption;
    private Api_page_accueil api; // Instance unique pour gérer les données

    private String utilisateurConnecte = null; // Stocke l'utilisateur connecté
    private JButton favoritesButton;
    private JButton bibliothequeButton;
    private JButton purchaseButton;
    private JButton connexionButton;
    private algorithme_suggestions algo;
    // Variables globales pour stocker les données des comics
    private List<JSONObject> recentComics = null;
    private List<JSONObject> classicComics = null;
    private List<JSONObject> randomComics = null;
    private List<JSONObject> randomComics2 = null;
    private boolean dataInitialized = false;



    
    public Fenetre() {
        api = new Api_page_accueil(); // Initialiser l'objet API
        algo = new algorithme_suggestions();
        affichageDebut();
        updatePage(); // Charger les contenus depuis les données récupérées
    }
    
  private void initializeData() {
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
            try {
                if (recentComics == null) recentComics = api.getRecentComics();
                if (classicComics == null) classicComics = api.getClassiques();
                if (randomComics == null) randomComics = api.getRandomComics();
                if (randomComics2 == null) randomComics2 = api.getRandomComics();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            dataInitialized = true;
            updatePage(); // Recharge la page une fois les données prêtes
        }
    };
    worker.execute();
}


    public void affichageDebut() {
        // Configuration de base
        selectedOption = "Tout";
        setTitle("Affichage des comics");

        // Définir une taille fixe pour la fenêtre (par exemple 1200x800)
        setSize(1200, 800);
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Barre latérale (gauche)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(150, getHeight()));
        leftPanel.setBackground(new Color(30, 30, 30));
        
        
     // Ajouter un logo en haut de la barre latérale
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(loadIcon("info7/logo.png", 140, 140)); // Ajustez selon la taille du logo
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Espacement autour du logo
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(20)); // Espacement sous le logo
        
        
        JButton homeButton = createStyledButton("Accueil");
        homeButton.addActionListener(e -> updatePage());
        leftPanel.add(homeButton);
        
     // Assurez-vous que cette ligne est présente dans la classe
        bibliothequeButton = createStyledButton("Bibliothèque");
        bibliothequeButton.setVisible(false);
        bibliothequeButton.addActionListener(e -> {
            if (UserRegistrationForm.connectedUserId != -1) {

                bibliothequeButton.setVisible(true);

                int userId = UserRegistrationForm.connectedUserId;

                Fenetre_affichage_bibliotheque bibliotheque = new Fenetre_affichage_bibliotheque(this, userId);
                JPanel libraryPanel = bibliotheque.getCentralPanel();

                centralPanel.removeAll();
                centralPanel.add(libraryPanel);
                centralPanel.revalidate();
                centralPanel.repaint();


            } else {
                JOptionPane.showMessageDialog(this, "Vous devez être connecté pour accéder à la bibliothèque.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        leftPanel.add(Box.createVerticalStrut(10)); // Espacement
        leftPanel.add(bibliothequeButton);
        
        favoritesButton = createStyledButton("Mes Favoris");
        favoritesButton.setVisible(false); // Masquer par défaut
        favoritesButton.addActionListener(e -> {
            if (UserRegistrationForm.connectedUserId != -1) {
                int userId = UserRegistrationForm.connectedUserId;
                Fenetre_favoris favoris = new Fenetre_favoris(this); // Passe l'instance actuelle
                JScrollPane favoritesScrollPane = favoris.createFavoritesPanel(userId);
                favoritesScrollPane.getVerticalScrollBar().setUnitIncrement(18); // Ajuste la vitesse de défilement
                favoritesScrollPane.getVerticalScrollBar().setBlockIncrement(100);

                // Ajoute le JScrollPane au centralPanel
                centralPanel.removeAll();
                centralPanel.add(favoritesScrollPane);
                centralPanel.revalidate();
                centralPanel.repaint();

            } else {
                JOptionPane.showMessageDialog(this, "Vous devez être connecté pour gérer vos favoris.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        leftPanel.add(Box.createVerticalStrut(10)); // Espacement
        leftPanel.add(favoritesButton); // Ajoute le bouton à la barre latérale

        purchaseButton = createStyledButton("Mes Achats");
        purchaseButton.setVisible(false); // Masqué par défaut
        purchaseButton.addActionListener(e -> {
            if (UserRegistrationForm.connectedUserId != -1) {
                int userId = UserRegistrationForm.connectedUserId;
                Fenetre_achat achats = new Fenetre_achat(this); // Passe l'instance actuelle
                JScrollPane achatsScrollPane = achats.createAchatPanel(userId);
                achatsScrollPane.getVerticalScrollBar().setUnitIncrement(18); // Ajuste la vitesse de défilement
                achatsScrollPane.getVerticalScrollBar().setBlockIncrement(100);

                // Ajoute le JScrollPane au centralPanel
                centralPanel.removeAll();
                centralPanel.add(achatsScrollPane);
                centralPanel.revalidate();
                centralPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Vous devez être connecté pour consulter vos achats.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        leftPanel.add(Box.createVerticalStrut(10)); // Espacement
        leftPanel.add(purchaseButton); // Ajoute le bouton "Mes Achats" à la barre latérale

        
        connexionButton = createStyledButton("Se connecter");
        connexionButton.addActionListener(e -> {
            if (UserRegistrationForm.connectedUserId != -1) {
                // Si un utilisateur est connecté, gérer la déconnexion
                UserRegistrationForm.connectedUserId = -1; // Réinitialiser l'ID utilisateur
                UserRegistrationForm.userFavorites.clear(); // Vider les favoris
                JOptionPane.showMessageDialog(this, "Vous avez été déconnecté avec succès.", "Déconnexion", JOptionPane.INFORMATION_MESSAGE);
                refreshSidebar(); // Rafraîchir la barre latérale
                updatePage(); // Réinitialiser la page principale
            } else {
                // Gérer la connexion
                UserRegistrationForm.Connexion(this);
            }
        });
        leftPanel.add(Box.createVerticalStrut(10)); // Espacement
        leftPanel.add(connexionButton);
        

        JButton quitButton = createStyledButton("Quitter");
        quitButton.addActionListener(e -> dispose());
        leftPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible
        leftPanel.add(quitButton);
        add(leftPanel, BorderLayout.WEST);

        // Panneau central
        centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        centralPanel.setBackground(new Color(50, 50, 50)); // Fond gris clair
        JScrollPane scrollPane = new JScrollPane(centralPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18); // Vitesse de défilement
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        add(scrollPane, BorderLayout.CENTER);
        

        // Barre de recherche
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setPreferredSize(new Dimension(getWidth(), 70));
        topPanel.setBackground(new Color(30, 30, 30));

     // ComboBox stylisé
        String[] options = {"Tout", "Comics", "Personnages"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(new Font("Arial", Font.BOLD, 14));
        comboBox.setForeground(new Color(230, 230, 230)); // Texte blanc
        comboBox.setBackground(new Color(40, 40, 40)); // Fond gris foncé
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20), 1)); // Bordure fine
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(comboBox);
        
     // -----> ActionListener pour mettre à jour selectedOption <-----
        comboBox.addActionListener(e -> {
            selectedOption = (String) comboBox.getSelectedItem();
        });


     // Champ de recherche stylisé
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setForeground(new Color(230, 230, 230)); // Texte gris clair
        searchField.setBackground(new Color(40, 40, 40)); // Fond gris foncé
        searchField.setCaretColor(new Color(200, 200, 200)); // Curseur clair
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Bordure + marges internes
        topPanel.add(searchField);

        JButton searchButton = createStyledButton("Rechercher");
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            if (selectedOption.equals("Personnages")) {
                Fenetre_affichage_recherche_personnage nouvelleRecherchePersonnage = new Fenetre_affichage_recherche_personnage(this);
                nouvelleRecherchePersonnage.test(query, searchButton);
            }
            if (selectedOption.equals("Comics")) {
                Fenetre_affichage_recherche_titre nouvelleRechercheTitre = new Fenetre_affichage_recherche_titre(this);
                nouvelleRechercheTitre.test(query, searchButton);
            }
            if (selectedOption.equals("Tout")) {
                Fenetre_affichage_recherche_tout nouvelleRechercheTout = new Fenetre_affichage_recherche_tout(this);
                nouvelleRechercheTout.test(query, searchButton);
            }
            searchField.setText("");
        });
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        setVisible(true);
    }




    @FunctionalInterface
interface DataFetcher {
    List<JSONObject> fetch();
}



    private void updatePage() {
        if (!dataInitialized) {
            centralPanel.removeAll();
            centralPanel.setBackground(new Color(50, 50, 50)); // Fond gris clair
            JLabel loadingLabel = new JLabel("Chargement des données en cours...");
            loadingLabel.setForeground(Color.WHITE); // Texte en blanc
            loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
            loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            ImageIcon loadingIcon = new ImageIcon(getClass().getClassLoader().getResource("info7/clock.png"));
            JLabel iconLabel = new JLabel(loadingIcon);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Ajouter l'icône et le texte au panneau central
            centralPanel.setLayout(new BorderLayout());
            centralPanel.add(iconLabel, BorderLayout.CENTER);
            centralPanel.add(loadingLabel, BorderLayout.SOUTH);
    
            centralPanel.revalidate();
            centralPanel.repaint();
            initializeData(); // Lance l'initialisation si nécessaire
            return;
        }

        centralPanel.removeAll();
        centralPanel.setBackground(new Color(50, 50, 50)); // Fond gris clair
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));

        if (UserRegistrationForm.connectedUserId != -1) {
            addSection("D'après vos favoris", () -> algo.getSuggestionsWords(UserRegistrationForm.connectedUserId));
        }
        
        if (UserRegistrationForm.connectedUserId != -1) {
            List<String> comics = Bibliotheque.getComicsUtilisateurByStatus(UserRegistrationForm.connectedUserId, "en cours de lecture");
            if(!comics.isEmpty()) {
                JPanel sectionPanel = new JPanel();
                sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
                sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel sectionTitle = new JLabel("Continuer vos lectures");
                sectionTitle.setFont(new Font("Arial", Font.BOLD, 19));
                sectionTitle.setForeground(Color.WHITE);
                sectionPanel.add(sectionTitle);


                JPanel horizontalPanel = new JPanel();
                horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));

                for (String comic : comics) {
                    JPanel comicPanel = new JPanel();
                    comicPanel.setLayout(new BoxLayout(comicPanel, BoxLayout.Y_AXIS));
                    comicPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Espacement
                    String[] parts = comic.split(",");
                    String comicId = parts[0];
                    String imageUrl = parts[1];

                    

                    // Ajouter l'image
                    try {
                        ImageIcon originalIcon = new ImageIcon(new URL(imageUrl));
                        int targetHeight = 200;
                        int targetWidth = (originalIcon.getIconWidth() * targetHeight) / originalIcon.getIconHeight();
                        Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                                @Override
                                public void mouseEntered(MouseEvent e) {
                                    // Changer le curseur en "main" quand la souris entre dans le panel
                                    imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                }

                                @Override
                                public void mouseExited(MouseEvent e) {
                                    // Remettre le curseur par défaut quand la souris sort du panel
                                    imageLabel.setCursor(Cursor.getDefaultCursor());
                                }
                                @Override
                                public void mouseClicked(java.awt.event.MouseEvent e) {
                                    // Récupérer l'ID du personnage sélectionné (colonne 5)
                                    
                                
                                    
                                    Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(Fenetre.this);
                                    fenetreDetails.StartUpdate(comicId);  
                                }
                            });
                        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        comicPanel.add(imageLabel);
                    } catch (Exception e) {
                        JLabel errorLabel = new JLabel("Erreur de chargement");
                        errorLabel.setForeground(Color.RED);
                        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        comicPanel.add(errorLabel);
                    }
                    horizontalPanel.add(comicPanel);
                }

                JPanel comicsPanel = horizontalPanel;
                //comicsPanel.setPreferredSize(new Dimension(panelWidth, panelHeight)); // Taille personnalisée

                JScrollPane scrollPane = new JScrollPane(comicsPanel);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Pas de scroll vertical ici
                scrollPane.setPreferredSize(new Dimension(800,300));
                
                scrollPane.addMouseWheelListener(e -> {
                    if (e.isShiftDown() || e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
                        horizontalScrollBar.setUnitIncrement(30); // Augmente la vitesse de défilement par "tick"
                        horizontalScrollBar.setBlockIncrement(100); // Augmente la vitesse de défilement par "bloc"
                        if (horizontalScrollBar.isVisible()) {
                            int scrollAmount = e.getUnitsToScroll() * horizontalScrollBar.getUnitIncrement();
                            horizontalScrollBar.setValue(horizontalScrollBar.getValue() + scrollAmount);
                            e.consume(); // Empêche le traitement par défaut du défilement vertical
                        }
                    }
                });

                sectionPanel.add(scrollPane);
                centralPanel.add(sectionPanel);
            }
        }
        if (UserRegistrationForm.connectedUserId != -1) {
            List<String> comics = Bibliotheque.getComicsUtilisateurByStatus(UserRegistrationForm.connectedUserId, "déjà lu");
            if (!comics.isEmpty()) {
                JPanel sectionPanel = new JPanel();
                sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
                sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel sectionTitle = new JLabel("Continuer vos séries");
                sectionTitle.setFont(new Font("Arial", Font.BOLD, 19));
                sectionTitle.setForeground(Color.WHITE);
                sectionPanel.add(sectionTitle);

                JPanel horizontalPanel = new JPanel();
                horizontalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Utiliser FlowLayout pour un espacement cohérent

                JLabel loadingLabel = new JLabel("Chargement des données...");
                loadingLabel.setForeground(Color.WHITE);
                horizontalPanel.add(loadingLabel);

                JScrollPane scrollPane = new JScrollPane(horizontalPanel);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setPreferredSize(new Dimension(800, 300));

                scrollPane.addMouseWheelListener(e -> {
                    if (e.isShiftDown() || e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
                        horizontalScrollBar.setUnitIncrement(30);
                        horizontalScrollBar.setBlockIncrement(100);
                        if (horizontalScrollBar.isVisible()) {
                            int scrollAmount = e.getUnitsToScroll() * horizontalScrollBar.getUnitIncrement();
                            horizontalScrollBar.setValue(horizontalScrollBar.getValue() + scrollAmount);
                            e.consume();
                        }
                    }
                });

                sectionPanel.add(scrollPane);
                centralPanel.add(sectionPanel);

                SwingWorker<Void, JPanel> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        List<String> volumes = new ArrayList<>();
                        for (String comic : comics) {
                            String[] parts = comic.split(",");
                            String comicURL = parts[0];
                            String vol = volumeString(comicURL);
                            String[] partsVol = vol.split(",");
                            String volURL = partsVol[0];
                            int comNumber = Integer.parseInt(partsVol[1]);
                            boolean isVolumeIn = false;
                            for (String volume : volumes) {
                                String[] partsVolumes = volume.split(",");
                                String volumesURL = partsVolumes[0];
                                if (volumesURL.equals(volURL)) {
                                    int comicNumber = Integer.parseInt(partsVolumes[1]);
                                    if (comicNumber < comNumber) {
                                        volumes.remove(volume);
                                        volumes.add(vol);
                                    }
                                    isVolumeIn = true;
                                    break;
                                }
                            }
                            if (!isVolumeIn) {
                                volumes.add(vol);
                            }
                        }
                        for (String volume : volumes) {
                            String[] partsVolume = volume.split(",");
                            String comicURLVolume = partsVolume[0];
                            String comicNumber = partsVolume[1];
                            JPanel comicPanel = affichageSuivant(comicURLVolume, comicNumber);
                            publish(comicPanel);
                        }
                        return null;
                    }

                    @Override
                    protected void process(List<JPanel> chunks) {
                        for (JPanel comicPanel : chunks) {
                            horizontalPanel.add(comicPanel);
                        }
                        horizontalPanel.revalidate();
                        horizontalPanel.repaint();
                    }

                    @Override
                    protected void done() {
                        horizontalPanel.remove(loadingLabel);
                        horizontalPanel.revalidate();
                        horizontalPanel.repaint();
                    }
                };
                worker.execute();
            }
        }
        if (UserRegistrationForm.connectedUserId != -1) {
            addSection("Publiés lors de votre décennie favorite", () -> algo.getDecadeComics(Database.getMostFrequentDecade()));
        }
           
    addSection("Comics les plus récents", () -> recentComics);
    addSection("Les grands classiques", () -> classicComics);
    addSection("À découvrir", () -> randomComics);
    addSection("Les suggestions du moment", () -> randomComics2);

    centralPanel.revalidate();
    centralPanel.repaint();
}


private String volumeString(String comicURL) {

    JSONObject jsonObject = null;
    OkHttpClient client = new OkHttpClient();
    
    // URL de l'API et clé API
    String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675"; // Updated API key

    // Construire l'URL avec le paramètre api_key
    HttpUrl httpUrl = HttpUrl.parse(comicURL).newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("format", "json")
            .build();

    Request request = new Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("User-Agent", "thibaut") // Remplace "YourAppName" par le nom de ton application
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur : " + response.code());
        }
    } catch (Exception e) {
        e.printStackTrace();  
    }

    JSONObject result = (JSONObject) jsonObject.get("results");
    JSONObject volume = (JSONObject) result.get("volume");
    String volumeURL = (String) volume.get("api_detail_url");
    String comicNumber = result.get("issue_number").toString();



    return volumeURL+","+comicNumber;
}



private JPanel affichageSuivant(String volumeURL, String comicNumbers){
    JPanel comicPanel = new JPanel();

    JSONObject jsonObjectVolume = null;
    OkHttpClient clientVolume = new OkHttpClient();

    int comicNumber = Integer.parseInt(comicNumbers);

    String apiKey = "694dac6fbb76b39db1552a21d1d1de8d2abf9675"; // Updated API key

    // Construire l'URL avec le paramètre api_key
    HttpUrl httpUrlVolume = HttpUrl.parse(volumeURL).newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("format", "json")
            .build();

    Request requestVolume = new Request.Builder()
            .url(httpUrlVolume)
            .get()
            .addHeader("User-Agent", "thibaut") // Remplace "YourAppName" par le nom de ton application
            .build();

    try (Response responseVolume = clientVolume.newCall(requestVolume).execute()) {
        if (responseVolume.isSuccessful()) {
            // Obtenir la réponse JSON
            String jsonResponse = responseVolume.body().string();
            // Parser JSON
            JSONParser parser = new JSONParser();

            try {
                // Convertir la chaîne en un objet JSON
                jsonObjectVolume = (JSONObject) parser.parse(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur : " + responseVolume.code());
        }
    } catch (Exception e) {
        e.printStackTrace();  
    }

    JSONObject resultVolume = (JSONObject) jsonObjectVolume.get("results");
    JSONArray issues = (JSONArray) resultVolume.get("issues");
    String suivantURL = null;
    for (Object issue : issues) {
        JSONObject issueObject = (JSONObject) issue;
        int issueNumber = Integer.parseInt(issueObject.get("issue_number").toString());
        if (issueNumber == comicNumber + 1) {
            suivantURL = (String) issueObject.get("api_detail_url");
            break;
        }
    }

    JSONObject jsonObjectSuivant = null;
    OkHttpClient clientSuivant = new OkHttpClient();

    // Construire l'URL avec le paramètre api_key
    HttpUrl httpUrlSuivant = HttpUrl.parse(suivantURL).newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("format", "json")
            .build();

    Request requestSuivant = new Request.Builder()
            .url(httpUrlSuivant)
            .get()
            .addHeader("User-Agent", "thibaut") // Remplace "YourAppName" par le nom de ton application
            .build();

    try (Response responseSuivant = clientSuivant.newCall(requestSuivant).execute()) {
        if (responseSuivant.isSuccessful()) {
            // Obtenir la réponse JSON
            String jsonResponse = responseSuivant.body().string();
            // Parser JSON
            JSONParser parser = new JSONParser();

            try {
                // Convertir la chaîne en un objet JSON
                jsonObjectSuivant = (JSONObject) parser.parse(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur : " + responseSuivant.code());
        }
    } catch (Exception e) {
        e.printStackTrace();  
    }

    JSONObject resultSuivant = (JSONObject) jsonObjectSuivant.get("results");
    String imageUrl = ((JSONObject) resultSuivant.get("image")).get("original_url").toString();
    try {
        URL url = new URL(imageUrl);
        ImageIcon originalIcon = new ImageIcon(url);
        int targetHeight = 200;
        int targetWidth = (originalIcon.getIconWidth() * targetHeight) / originalIcon.getIconHeight();
        Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Changer le curseur en "main" quand la souris entre dans le panel
                imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Remettre le curseur par défaut quand la souris sort du panel
                imageLabel.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Récupérer l'ID du personnage sélectionné (colonne 5)
                String comicID = String.valueOf(resultSuivant.get("api_detail_url"));
                Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(Fenetre.this);
                fenetreDetails.StartUpdate(comicID);
            }
        });
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        comicPanel.add(imageLabel);
    } catch (Exception e) {
        e.printStackTrace();
    }

    return comicPanel;
}



private void addSection(String title, DataFetcher fetcher) {
    JPanel sectionPanel = new JPanel();
    sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
    sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    sectionPanel.setBackground(new Color(50, 50, 50));

    JLabel sectionTitle = new JLabel(title);
    sectionTitle.setFont(new Font("Arial", Font.BOLD, 19));
    sectionTitle.setForeground(Color.WHITE); // Texte en blanc
    sectionPanel.add(sectionTitle);

    JPanel loadingPanel = new JPanel();
    sectionPanel.setBackground(new Color(50, 50, 50));
    JLabel loadingLabel = new JLabel("Récupération des comics en cours...");
    loadingLabel.setForeground(Color.WHITE); // Texte en blanc
    loadingPanel.add(loadingLabel);
    loadingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    sectionPanel.add(loadingPanel);
    

    centralPanel.add(sectionPanel);

    SwingWorker<List<JSONObject>, Void> worker = new SwingWorker<>() {
        @Override
        protected List<JSONObject> doInBackground() {
            List<JSONObject> data = fetcher.fetch();
            if (data == null) {
                return Collections.emptyList(); // Retourne une liste vide si les données sont nulles
            }
            return data;
        }

        @Override
        protected void done() {
            try {
                List<JSONObject> comics = get();
                if (comics.isEmpty()) {
                    JLabel emptyLabel = new JLabel("Aucun contenu disponible.");
                    emptyLabel.setForeground(Color.WHITE); // Texte en blanc
                    sectionPanel.remove(loadingPanel);
                    sectionPanel.add(emptyLabel);
                } else {
                    JScrollPane comicsPanel = createScrollableComicsPanel(comics, 2800, 300);
                    sectionPanel.remove(loadingPanel);
                    sectionPanel.add(comicsPanel);
                }
                sectionPanel.revalidate();
                sectionPanel.repaint();
            } catch (Exception e) {
                e.printStackTrace();
                JLabel errorLabel = new JLabel("Erreur lors du chargement des comics.");
                errorLabel.setForeground(Color.WHITE); // Texte en blanc
                sectionPanel.remove(loadingPanel);
                sectionPanel.add(errorLabel);
                sectionPanel.revalidate();
                sectionPanel.repaint();
            }
        }
    };
    worker.execute();
}






    private JPanel createHorizontalComicsPanel(List<JSONObject> comics) {
        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));

        for (JSONObject comic : comics) {
            horizontalPanel.add(createComicPanel(comic));
            horizontalPanel.add(Box.createHorizontalStrut(20)); // Espacement horizontal
        }

        return horizontalPanel;
    }

    private JPanel createComicPanel(JSONObject comic) {
        JPanel comicPanel = new JPanel();
        comicPanel.setLayout(new BoxLayout(comicPanel, BoxLayout.Y_AXIS));
        comicPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Espacement

        

        // Ajouter l'image
        try {
            String imageUrl = ((JSONObject) comic.get("image")).get("original_url").toString();
            ImageIcon originalIcon = new ImageIcon(new URL(imageUrl));
            int targetHeight = 200;
            int targetWidth = (originalIcon.getIconWidth() * targetHeight) / originalIcon.getIconHeight();
            Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // Changer le curseur en "main" quand la souris entre dans le panel
                        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // Remettre le curseur par défaut quand la souris sort du panel
                        imageLabel.setCursor(Cursor.getDefaultCursor());
                    }
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // Récupérer l'ID du personnage sélectionné (colonne 5)
                        
                     
                        String comicID = String.valueOf(comic.get("api_detail_url")); 
                        Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(Fenetre.this);
                        fenetreDetails.StartUpdate(comicID);  
                    }
                });
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            comicPanel.add(imageLabel);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Erreur de chargement");
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            comicPanel.add(errorLabel);
        }

        return comicPanel;
    }
    
    public void UpdateCentralPanel(JPanel panel1, JPanel panel2) {
        centralPanel.removeAll();  // Supprime tous les composants actuels
        centralPanel.add(panel1);
        centralPanel.add(Box.createVerticalStrut(10)); // Espacement entre les deux panneaux
        centralPanel.add(panel2);

        // Rafraîchir l'affichage
        centralPanel.revalidate();
        centralPanel.repaint();
    }
    public void UpdateCentralPanel(JPanel panel1) {
        centralPanel.removeAll();  // Supprime tous les composants actuels
        centralPanel.add(panel1);
        centralPanel.add(Box.createVerticalStrut(10)); // Espacement entre les deux panneaux

        // Rafraîchir l'affichage
        centralPanel.revalidate();
        centralPanel.repaint();
    }
    
    private void toggleFavorite(int userId, String comicId, JButton favoritesButton, boolean isFavorited, String title, String imageUrl, String cover_date) {
        if (isFavorited) {
            // Supprimer le favori
            Database.removeFavorite(userId, comicId);
            UserRegistrationForm.userFavorites.remove(comicId);
            favoritesButton.setIcon(loadIcon("info7/star.png", 70, 70)); // Icône étoile vide
            JOptionPane.showMessageDialog(this, "Comic retiré de vos favoris !");
        } else {
            // Ajouter le favori
            Database.addFavorite(userId, comicId, title, imageUrl, cover_date); // Passer les 5 arguments requis
            UserRegistrationForm.userFavorites.add(comicId);
            favoritesButton.setIcon(loadIcon("info7/star_remplie.png", 70, 70)); // Icône étoile remplie
            JOptionPane.showMessageDialog(this, "Comic ajouté à vos favoris !");
        }
    }


    
    public void refreshSidebar() {
        boolean isConnected = UserRegistrationForm.connectedUserId != -1; // Vérifie si un utilisateur est connecté
        favoritesButton.setVisible(isConnected); // Affiche le bouton "Favoris" si connecté
        bibliothequeButton.setVisible(isConnected);
        purchaseButton.setVisible(isConnected);
        connexionButton.setText(isConnected ? "Se déconnecter" : "Se connecter"); // Change le texte du bouton
        revalidate(); // Revalide la mise en page
        repaint(); // Rafraîchit l'affichage
    }

    
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
    
    private JButton createStyledButton(String text) {
    	JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé de fond (gris foncé vers gris clair)
                Color color1 = new Color(50, 50, 50); // Gris foncé
                Color color2 = new Color(80, 80, 80); // Gris clair
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Coins arrondis

                // Contour
                g2.setColor(new Color(30, 30, 30)); // Couleur du contour
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 14)); // Taille de texte réduite
        button.setForeground(Color.WHITE); // Texte en blanc
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 50)); // Dimensions fixes
        button.setMaximumSize(new Dimension(150, 50)); // Taille maximale pour éviter d'étirer le bouton
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Centre le bouton dans le layout
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet au survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(200, 200, 200)); // Texte plus clair
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE); // Texte normal
            }
        });

        return button;
    }



    private JScrollPane createScrollableComicsPanel(List<JSONObject> comics, int panelWidth, int panelHeight) {
    JPanel comicsPanel = createHorizontalComicsPanel(comics);
    comicsPanel.setPreferredSize(new Dimension(panelWidth, panelHeight)); // Taille personnalisée
    comicsPanel.setBackground(new Color(50, 50, 50));

    JScrollPane scrollPane = new JScrollPane(comicsPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Pas de scroll vertical ici
    scrollPane.setPreferredSize(new Dimension(800,300));
    
 // Définir le fond du JScrollPane et de son Viewport
    scrollPane.getViewport().setBackground(new Color(50, 50, 50)); // Fond du viewport (zone défilable)
    scrollPane.setBackground(new Color(50, 50, 50)); // Fond du JScrollPane
    
    scrollPane.addMouseWheelListener(e -> {
        if (e.isShiftDown() || e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
            horizontalScrollBar.setUnitIncrement(30); // Augmente la vitesse de défilement par "tick"
            horizontalScrollBar.setBlockIncrement(100); // Augmente la vitesse de défilement par "bloc"
            if (horizontalScrollBar.isVisible()) {
                int scrollAmount = e.getUnitsToScroll() * horizontalScrollBar.getUnitIncrement();
                horizontalScrollBar.setValue(horizontalScrollBar.getValue() + scrollAmount);
                e.consume(); // Empêche le traitement par défaut du défilement vertical
            }
        }
    });
    
    return scrollPane;
}
   
}
