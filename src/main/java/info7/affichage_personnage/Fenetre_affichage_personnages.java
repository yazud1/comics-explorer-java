package info7.affichage_personnage;

import javax.swing.*;
import org.json.simple.JSONObject;
import java.awt.*;
import java.net.URL;
import info7.page_accueil.Fenetre;
import info7.affichage_comics.Fenetre_affichage_comics;
import info7.connexion.UserRegistrationForm;
import org.json.simple.JSONArray;
import java.awt.event.MouseEvent;

public class Fenetre_affichage_personnages extends JFrame {

    private Fenetre fenetre;
    private JPanel resultPanel;
    private JSONObject reponse;
    private JLabel loadingLabel;
    private JPanel loadingPanel;

    private static final Color BACKGROUND_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color TITLE_COLOR = new Color(50, 50, 150);

    public Fenetre_affichage_personnages(Fenetre fenetre) {
        this.fenetre = fenetre;
    }

    public void DebutAffichage(String id) {
        loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.setBackground(BACKGROUND_COLOR);

        loadingLabel = new JLabel("Recherche en cours, veuillez patienter...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        loadingLabel.setForeground(Color.WHITE);
        loadingPanel.add(loadingLabel);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        fenetre.UpdateCentralPanel(resultPanel);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                affichage(id);
                return null;
            }

            @Override
            protected void done() {
                resultPanel.remove(loadingPanel);
                updatePage();
            }
        }.execute();
    }

    public void affichage(String id) {
        Requete_api_personnage requete = new Requete_api_personnage();
        reponse = (JSONObject) Requete_api_personnage.recherche(id).get("results");
        updatePage();
    }

    private void updatePage() {
        resultPanel.removeAll();

        if (reponse != null) {
            JPanel panelPersonnage = new JPanel(new BorderLayout(20, 20));
            panelPersonnage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panelPersonnage.setBackground(BACKGROUND_COLOR);

            // Titre du personnage
            JLabel nameLabel = new JLabel((String) reponse.get("name"), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
            nameLabel.setForeground(Color.WHITE);
            panelPersonnage.add(nameLabel, BorderLayout.NORTH);

            // Contenu principal
            JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
            mainPanel.setBackground(BACKGROUND_COLOR);

            // Image
            try {
                JSONObject image = (JSONObject) reponse.get("image");
                URL imageUrl = new URL((String) image.get("original_url"));
                ImageIcon icon = new ImageIcon(new ImageIcon(imageUrl).getImage()
                        .getScaledInstance(300, -1, Image.SCALE_SMOOTH));
                JLabel imageLabel = new JLabel(icon);
                mainPanel.add(imageLabel, BorderLayout.WEST);
            } catch (Exception e) {
                JLabel errorImage = new JLabel("Image non disponible", SwingConstants.CENTER);
                errorImage.setForeground(Color.RED);
                errorImage.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                mainPanel.add(errorImage, BorderLayout.WEST);
            }

            // Informations textuelles
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(BACKGROUND_COLOR);

            addInfoLabel(infoPanel, "Description :", (String) reponse.getOrDefault("deck", null));

            JSONObject originObject = (JSONObject) reponse.get("origin");
            addInfoLabel(infoPanel, "Origine :", originObject != null ? (String) originObject.get("name") : null);

            JSONObject firstAppearanceObject = (JSONObject) reponse.get("first_appeared_in_issue");
            addInfoLabel(infoPanel, "Première apparition :", firstAppearanceObject != null ? (String) firstAppearanceObject.get("name") : null);

            Object genderObject = reponse.get("gender");
            String gender = genderObject != null ? (Integer.parseInt(genderObject.toString()) == 1 ? "M" : "F") : null;
            addInfoLabel(infoPanel, "Genre :", gender);

            addInfoLabel(infoPanel, "Nom réel :", (String) reponse.getOrDefault("real_name", null));
            addInfoLabel(infoPanel, "Année de naissance :", (String) reponse.getOrDefault("birth", null));

            mainPanel.add(infoPanel, BorderLayout.CENTER);
            panelPersonnage.add(mainPanel, BorderLayout.CENTER);

            // Issues associées
            JPanel issuesPanel = new JPanel();
            issuesPanel.setLayout(new BoxLayout(issuesPanel, BoxLayout.Y_AXIS));
            issuesPanel.setBackground(BACKGROUND_COLOR);

            addSectionTitle(issuesPanel, "Issues associées :");
            JSONArray issueCredits = (JSONArray) reponse.get("issue_credits");
            if (issueCredits != null && !issueCredits.isEmpty()) {
                for (Object issueObj : issueCredits) {
                    JSONObject issue = (JSONObject) issueObj;
                    String issueName = (String) issue.get("name");
                    if (issueName != null) {
                        JLabel issueLabel = new JLabel(issueName);
                        issueLabel.setFont(new Font("Arial", Font.PLAIN, 15));
                        issueLabel.setForeground(Color.BLUE);
                        issueLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        issueLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                String url_detail = (String) issue.get("api_detail_url");
                                Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(fenetre);
                                fenetreDetails.StartUpdate(url_detail);
                            }
                        });
                        issuesPanel.add(issueLabel);
                    }
                }
            } else {
                addInfoLabel(issuesPanel, "", "Aucune issue associée.");
            }

            panelPersonnage.add(issuesPanel, BorderLayout.SOUTH);
            resultPanel.add(panelPersonnage);
        } else {
            showNoResultPanel();
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void addInfoLabel(JPanel panel, String title, String content) {
        if (content != null && !content.isEmpty()) {
            JTextArea textArea = new JTextArea(title + " " + content);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            textArea.setForeground(Color.WHITE);
            textArea.setBackground(BACKGROUND_COLOR);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            textArea.setColumns(15); // Limite la largeur à 30 colonnes
            panel.add(textArea);
            panel.add(Box.createVerticalStrut(10));
        }
    }

    private void addSectionTitle(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
    }

    private void showNoResultPanel() {
        JPanel noResultPanel = new JPanel();
        noResultPanel.setLayout(new BoxLayout(noResultPanel, BoxLayout.Y_AXIS));
        noResultPanel.setBackground(BACKGROUND_COLOR);

        JLabel noResultLabel = new JLabel("Aucun Titre trouvé.", JLabel.CENTER);
        noResultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        noResultLabel.setForeground(Color.RED);
        noResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon noResultIcon = createStyledIcon2();
        if (noResultIcon != null) {
            JLabel iconLabel = new JLabel(noResultIcon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noResultPanel.add(iconLabel);
        }

        noResultPanel.add(Box.createVerticalStrut(10));
        noResultPanel.add(noResultLabel);
        resultPanel.add(noResultPanel);
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

    public void StartUpdate(String id) {
        DebutAffichage(id);
        fenetre.UpdateCentralPanel(resultPanel);
    }
}
