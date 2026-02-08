package info7.page_recherche_tout;

import info7.affichage_comics.Fenetre_affichage_comics;
import info7.affichage_personnage.Fenetre_affichage_personnages;
import info7.page_accueil.Fenetre;
import info7.page_recherche_personnage.Lecture_fichier_json;
import info7.page_recherche_titre.Lecture_fichier_json_titre;
import info7.page_recherche_personnage.Requete_api_recherche_personnage;
import info7.page_recherche_titre.Requete_api_recherche_titre;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import org.json.simple.JSONObject;
import javax.swing.SwingWorker;

public class Fenetre_affichage_recherche_tout {
    private Fenetre fenetre;
    public JPanel resultPanel;
    public JPanel infoPanel;
    private int currentPage = 0;
    private final int resultsPerPage = 10;
    private String[][] reponse_personnage;
    private String[][] reponse_titre;
    private JLabel loadingLabel;
    private JPanel loadingPanel;

    public Fenetre_affichage_recherche_tout(Fenetre fenetre) {
        this.fenetre = fenetre;
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(50, 50, 50)); // Couleur de fond sombre moderne

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 50));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
    }

    public void affichageDebut(String query, JButton searchButton) {
        loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.setBackground(new Color(40, 40, 40)); // Fond moderne

        ImageIcon loadingIcon = createLoadingIcon();
        if (loadingIcon != null) {
            loadingLabel = new JLabel("Recherche en cours, veuillez patienter...", loadingIcon, JLabel.CENTER);
        } else {
            loadingLabel = new JLabel("Recherche en cours, veuillez patienter...");
        }
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        loadingLabel.setForeground(Color.LIGHT_GRAY);
        loadingPanel.add(loadingLabel);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(loadingPanel);

        searchButton.setEnabled(false);
        fenetre.UpdateCentralPanel(infoPanel);
        SwingUtilities.invokeLater(() -> fenetre.UpdateCentralPanel(infoPanel));

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                affichage(query);
                return null;
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                infoPanel.remove(loadingPanel);
                updatePage();
            }
        }.execute();
    }

    public void affichage(String name) {
        Requete_api_recherche_personnage requete_personnage = new Requete_api_recherche_personnage();
        Requete_api_recherche_titre requete_titre = new Requete_api_recherche_titre();
        JSONObject json_response_personnage = requete_personnage.recherche(name);
        JSONObject json_response_titre = requete_titre.recherche(name);

        Lecture_fichier_json lecture = new Lecture_fichier_json();
        reponse_personnage = lecture.lecture(json_response_personnage);
        Lecture_fichier_json_titre lecture_titre = new Lecture_fichier_json_titre();
        reponse_titre = lecture_titre.lecture_titre(json_response_titre);

        currentPage = 0;
        updatePage();
    }

    private void updatePage() {
        resultPanel.removeAll();
        infoPanel.removeAll();
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel resultsCountLabel = new JLabel(reponse_personnage.length + " résultats trouvés.");
        resultsCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultsCountLabel.setForeground(Color.WHITE);
        resultsCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(resultsCountLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        int start = currentPage * resultsPerPage;
        int end = Math.min(start + resultsPerPage, reponse_personnage.length + reponse_titre.length);

        for (int i = start; i < end; i++) {
            JPanel panel;
            if (i % 2 == 0) {
                panel = createResultPanel(reponse_personnage[i / 2][0], reponse_personnage[i / 2][1], reponse_personnage[i / 2][5], true);
            } else {
                panel = createResultPanel(reponse_titre[(i - 1) / 2][0], reponse_titre[(i - 1) / 2][1], reponse_titre[(i - 1) / 2][3], false);
            }
            resultPanel.add(panel);
            resultPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        infoPanel.add(createPaginationPanel(), BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        fenetre.UpdateCentralPanel(mainPanel);

        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createResultPanel(String name, String imageUrl, String id, boolean isPersonnage) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, BorderLayout.NORTH);

        try {
            ImageIcon originalIcon = new ImageIcon(new URL(imageUrl));
            int targetWidth = 150;
            int targetHeight = (originalIcon.getIconHeight() * targetWidth) / originalIcon.getIconWidth();
            Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(new Color(60, 60, 60));
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(new Color(40, 40, 40));
                    panel.setCursor(Cursor.getDefaultCursor());
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isPersonnage) {
                        Fenetre_affichage_personnages fenetreDetails = new Fenetre_affichage_personnages(fenetre);
                        fenetreDetails.StartUpdate(id);
                    } else {
                        Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(fenetre);
                        fenetreDetails.StartUpdate(id);
                    }
                }
            });
            panel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Erreur de chargement", JLabel.CENTER);
            errorLabel.setForeground(Color.RED);
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBackground(new Color(30, 30, 30));

        int totalPages = (int) Math.ceil((double) (reponse_personnage.length + reponse_titre.length) / resultsPerPage);
        for (int i = 0; i < totalPages; i++) {
            final int pageIndex = i;
            JButton pageButton = new JButton(String.valueOf(i + 1));
            pageButton.setBackground(i == currentPage ? new Color(100, 100, 100) : new Color(70, 70, 70));
            pageButton.setForeground(Color.WHITE);
            pageButton.setFont(new Font("Arial", Font.PLAIN, 14));
            pageButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            pageButton.addActionListener(e -> {
                currentPage = pageIndex;
                updatePage();
            });
            paginationPanel.add(pageButton);
        }

        return paginationPanel;
    }

    public void test(String query, JButton searchButton) {
        affichageDebut(query, searchButton);
    }

    private ImageIcon createLoadingIcon() {
        try {
            URL resource = getClass().getClassLoader().getResource("info7/clock.png");
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("Icône de chargement introuvable !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
