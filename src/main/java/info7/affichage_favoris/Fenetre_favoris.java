package info7.affichage_favoris;

import javax.swing.*;


import info7.affichage_comics.Fenetre_affichage_comics;
import info7.connexion.Database;
import info7.page_accueil.Fenetre;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class Fenetre_favoris {
    private Fenetre fenetre;

    public Fenetre_favoris(Fenetre fenetre) {
        this.fenetre = fenetre;
    }

    public JScrollPane createFavoritesPanel(int userId) {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(new Color(20, 20, 20)); // Fond sombre

        // ** Titre "Mes Favoris" avec icônes **
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(20, 20, 20)); // Même couleur que le fond
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Espacement autour du titre

        // Icône gauche
        JLabel leftIcon = new JLabel(loadIcon("info7/sparkles.png", 50, 50));
        titlePanel.add(leftIcon);

        // Titre
        JLabel titleLabel = new JLabel("Mes Favoris");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Icône droite
        JLabel rightIcon = new JLabel(loadIcon("info7/sparkles.png", 50, 50));
        titlePanel.add(rightIcon);

        containerPanel.add(titlePanel, BorderLayout.NORTH);

        // ** Section des favoris **
        JPanel mainPanel = new JPanel(new GridLayout(0, 5, 20, 20)); // 5 comics par ligne avec espacement
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(20, 20, 20)); // Fond sombre

        // Charger les favoris de l'utilisateur connecté
        List<String> favorites = Database.getFavorites(userId);

        if (favorites.isEmpty()) {
            JLabel noFavoritesLabel = new JLabel("Vous n'avez pas encore de favoris.");
            noFavoritesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noFavoritesLabel.setFont(new Font("Arial", Font.BOLD, 20));
            noFavoritesLabel.setForeground(Color.WHITE);
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(noFavoritesLabel, BorderLayout.CENTER);
        } else {
        	for (String comicId : favorites) {
                String[] comicDetails = Database.getComicDetailsById(comicId);
                if (comicDetails == null) continue;

                String title = comicDetails[0];
                String imageUrl = comicDetails[1];

                JPanel comicPanel = createComicPanel(comicId, title, imageUrl);
                mainPanel.add(comicPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Pas de scroll horizontal
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Défilement fluide
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return new JScrollPane(containerPanel);
    }

    private JPanel createComicPanel(String comicId, String title, String imageUrl) {
        JPanel comicPanel = new JPanel(new BorderLayout());
        comicPanel.setPreferredSize(new Dimension(180, 260)); // Taille par défaut du panel
        comicPanel.setBackground(new Color(30, 30, 30));
        comicPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marge interne
        comicPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Curseur "main"

        JLabel imageLabel = new JLabel();
        try {
            ImageIcon icon = loadImage(imageUrl, 180, 220);
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            imageLabel.setText("Image indisponible");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setForeground(Color.WHITE);
        }
        imageLabel.setPreferredSize(new Dimension(180, 220));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        comicPanel.add(imageLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        comicPanel.add(titleLabel, BorderLayout.SOUTH);

        // Animation et interactions
        comicPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                comicPanel.setBackground(new Color(50, 50, 50)); // Changer la couleur de fond
                animateComicPanel(imageLabel, imageUrl, true); // Agrandir l'image uniformément
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                comicPanel.setBackground(new Color(30, 30, 30)); // Réinitialiser la couleur
                animateComicPanel(imageLabel, imageUrl, false); // Réduire l'image uniformément
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Fenetre_affichage_comics fenetreDetails = new Fenetre_affichage_comics(fenetre);
                fenetreDetails.StartUpdate(String.valueOf(comicId));
            }
        });

        return comicPanel;
    }

    private void animateComicPanel(JLabel imageLabel, String imageUrl, boolean enlarge) {
        int targetWidth = enlarge ? 200 : 180; // Taille augmentée
        int targetHeight = enlarge ? 240 : 220;

        try {
            ImageIcon icon = loadImage(imageUrl, targetWidth, targetHeight);
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageIcon loadImage(String imageUrl, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(new java.net.URL(imageUrl));
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            return null;
        }
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

    
}

