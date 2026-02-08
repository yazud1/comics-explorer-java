package info7.affichage_bibliotheque;

import info7.affichage_comics.Fenetre_affichage_comics;
import info7.page_accueil.Fenetre;
import info7.bibliotheque.Bibliotheque;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class Fenetre_affichage_bibliotheque extends JFrame {
    private JPanel centralPanel;
    private JScrollPane scrollPane;
    private int userId;
    private Fenetre fenetre;

    public Fenetre_affichage_bibliotheque(Fenetre fenetre, int userId) {
        this.fenetre = fenetre;
        this.userId = userId;
        setupUI();
        updatePage();
    }

    private void setupUI() {
        setTitle("Bibliothèque");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS)); // Organisation verticale des sections

        scrollPane = new JScrollPane(centralPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updatePage() {
        centralPanel.removeAll();

        // Section "À Lire"
        centralPanel.add(createSectionHeader("À Lire", "info7/lire.png", 30, 30));
        centralPanel.add(createCategorySection("à lire"));
        centralPanel.add(Box.createVerticalStrut(20)); // Espacement vertical

        // Section "En Cours"
        centralPanel.add(createSectionHeader("En Cours", "info7/encours.png", 30, 30));
        centralPanel.add(createCategorySection("en cours de lecture"));
        centralPanel.add(Box.createVerticalStrut(20)); // Espacement vertical

        // Section "Déjà Lu"
        centralPanel.add(createSectionHeader("Déjà Lu", "info7/fini.png", 50, 50));
        centralPanel.add(createCategorySection("déjà lu"));
        centralPanel.add(Box.createVerticalStrut(20)); // Espacement vertical

        centralPanel.revalidate();
        centralPanel.repaint();
    }

    private JPanel createSectionHeader(String title, String iconPath, int iconWidth, int iconHeight) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(20, 20, 20)); // Fond sombre
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges

        JLabel iconLabel = new JLabel(loadIcon(iconPath, iconWidth, iconHeight));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createCategorySection(String status) {
        JPanel sectionPanel = new JPanel(new BorderLayout(10, 10));
        sectionPanel.setBackground(new Color(20, 20, 20)); // Fond sombre

        JPanel comicsContainer = new JPanel();
        comicsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        comicsContainer.setBackground(new Color(20, 20, 20));

        JPanel loadingPanel = new JPanel();
        loadingPanel.setBackground(new Color(20, 20, 20));
        JLabel loadingLabel = new JLabel("Chargement des données...");
        loadingLabel.setForeground(Color.WHITE);
        loadingPanel.add(loadingLabel);

        comicsContainer.add(loadingPanel);
        sectionPanel.add(comicsContainer, BorderLayout.CENTER);

        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                return Bibliotheque.getComicsUtilisateurByStatus(userId, status);
            }

            @Override
            protected void done() {
                try {
                    List<String> comicsList = get();
                    comicsContainer.remove(loadingPanel);

                    if (comicsList.isEmpty()) {
                        JLabel emptyLabel = new JLabel("Aucun livre dans cette catégorie.");
                        emptyLabel.setForeground(Color.WHITE);
                        comicsContainer.add(emptyLabel);
                    } else {
                        for (String comicData : comicsList) {
                            String[] parts = comicData.split(",");
                            String comicId = parts[0];
                            String imageUrl = parts[1];
                            JPanel comicPanel = createComicPanel(comicId, "Titre du Comic", imageUrl); // Remplacez "Titre du Comic" par le titre réel si disponible
                            comicsContainer.add(comicPanel);
                        }
                    }

                    comicsContainer.revalidate();
                    comicsContainer.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    JLabel errorLabel = new JLabel("Erreur lors du chargement.");
                    errorLabel.setForeground(Color.RED);
                    comicsContainer.remove(loadingPanel);
                    comicsContainer.add(errorLabel);
                    comicsContainer.revalidate();
                    comicsContainer.repaint();
                }
            }
        };
        worker.execute();

        return sectionPanel;
    }

    private JPanel createComicPanel(String comicId, String title, String imageUrl) {
        JPanel comicPanel = new JPanel(new BorderLayout(5, 5));
        comicPanel.setPreferredSize(new Dimension(100, 150));
        comicPanel.setBackground(Color.WHITE);
        comicPanel.setBorder(createStyledBorder());

        JLabel imageLabel;
        try {
            URI uri = new URI(imageUrl);
            URL url = uri.toURL();
            ImageIcon icon = new ImageIcon(
                    new ImageIcon(url).getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH)
            );
            imageLabel = new JLabel(icon);
        } catch (Exception e) {
            imageLabel = new JLabel("Image indisponible");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        comicPanel.add(imageLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        comicPanel.add(titleLabel, BorderLayout.SOUTH);

        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(fenetre);
                fenetreDetails.StartUpdate(comicId);
            }
        });

        return comicPanel;
    }

    private Border createStyledBorder() {
        Border outerBorder = new LineBorder(new Color(150, 150, 200), 1, true);
        Border innerBorder = new EmptyBorder(5, 5, 5, 5);
        return new CompoundBorder(outerBorder, innerBorder);
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

    public JScrollPane getLibraryScrollPane() {
        return scrollPane;
    }

    public JPanel getCentralPanel() {
        return centralPanel;
    }
}

