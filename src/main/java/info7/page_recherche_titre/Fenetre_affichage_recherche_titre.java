package info7.page_recherche_titre;

import info7.affichage_comics.Fenetre_affichage_comics;
import info7.page_accueil.Fenetre;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;

public class Fenetre_affichage_recherche_titre {
    private Fenetre fenetre;
    public JPanel resultPanel;
    public JPanel infoPanel;
    private int currentPage = 0;
    private final int resultsPerPage = 10;
    private String[][] reponse;
    private JLabel loadingLabel;
    private JPanel loadingPanel;

    public Fenetre_affichage_recherche_titre(Fenetre fenetre) {
        this.fenetre = fenetre;
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(50, 50, 50));

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 50));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
    }

    public void affichageDebut(String query, JButton searchButton) {
        loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.setBackground(new Color(40, 40, 40));

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
        Requete_api_recherche_titre requete = new Requete_api_recherche_titre();
        JSONObject json_reponse = requete.recherche(name);

        Lecture_fichier_json_titre lecture = new Lecture_fichier_json_titre();
        reponse = lecture.lecture_titre(json_reponse);

        currentPage = 0;
        updatePage();
    }

    private void updatePage() {
        resultPanel.removeAll();
        infoPanel.removeAll();
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel resultsCountLabel = new JLabel(reponse.length + " résultats trouvés.");
        resultsCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultsCountLabel.setForeground(Color.WHITE);
        resultsCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(resultsCountLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        int start = currentPage * resultsPerPage;
        int end = Math.min(start + resultsPerPage, reponse.length);

        for (int i = start; i < end; i++) {
            JPanel panel = createResultPanel(reponse[i][0], reponse[i][1], reponse[i][3]);
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

    private JPanel createResultPanel(String name, String imageUrl, String id) {
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
            URI uri = new URI(imageUrl);
            URL url = uri.toURL();
            ImageIcon originalIcon = new ImageIcon(url);
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
                    Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(fenetre);
                    fenetreDetails.StartUpdate(id);
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

        int totalPages = (int) Math.ceil((double) reponse.length / resultsPerPage);
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

