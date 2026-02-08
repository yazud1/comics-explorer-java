/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info7.connexion;

import javax.swing.*;
import javax.swing.border.Border;

import info7.page_accueil.Fenetre;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;


// Classe représentant un utilisateur
class User {
    String pseudo;
    String nom;
    String prenom;
    String email;
    String hashedPassword;

    public User(String pseudo, String nom, String prenom, String email, String hashedPassword) {
        this.pseudo = pseudo;
        this.nom = nom; 
        this.prenom = prenom;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public String toString() {
        return "Pseudo: " + pseudo + ", Nom: " + nom + ", Prénom: " + prenom + ", Email: " + email;
    }
}



public class UserRegistrationForm {

    private static final String EAST = null;

	private static ArrayList<User> users = new ArrayList<>();
    
    public static List<String> userFavorites = new ArrayList<>(); // Liste des favoris de l'utilisateur connecté
    public static List<String> userPurchase = new ArrayList<>();

    public static void Connexion(Fenetre fenetre) {
        // Fenêtre principale
        JFrame frame = new JFrame("Créer un compte");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 700);
        frame.setAlwaysOnTop(true); // Force la fenêtre au-dessus
        

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(50, 50, 50)); // Fond beige clair

     // Panneau pour le logo et le titre
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(50, 50, 50)); // Fond gris foncé

        // Logo
        try {
            URL resource = new UserRegistrationForm().getClass().getClassLoader().getResource("info7/logo.png");
            if (resource != null) {
                ImageIcon logoIcon = new ImageIcon(resource);
                Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Redimensionner le logo
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
                headerPanel.add(logoLabel);
            }
        } catch (Exception e) {
            System.out.println("Logo non trouvé.");
        }

        // Ajouter un espace entre le logo et le titre
        headerPanel.add(Box.createRigidArea(new Dimension(20, 0))); // Espacement horizontal

        // Titre
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(200, 200, 200)); // Texte gris clair
        headerPanel.add(titleLabel);

        // Ajouter le header au panneau principal
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espacement vertical entre le header et les champs


     // Panneau principal pour chaque champ avec label
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(new Color(70, 70, 70)); // Fond gris foncé pour le panneau

        // Label pour le champ Nom
        JLabel nameLabel = new JLabel("Nom");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBackground(new Color(70, 70, 70)); // Fond gris foncé
        nameLabel.setForeground(Color.WHITE); // Texte blanc
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Espacement sous le label
        nameLabel.setOpaque(true); // Rendre le fond visible
        namePanel.add(nameLabel, BorderLayout.NORTH); // Ajout du label au panneau

        // Champ Nom
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30));
        nameField.setBackground(new Color(60, 60, 60)); // Fond gris
        nameField.setForeground(Color.WHITE); // Texte blanc
        nameField.setCaretColor(Color.WHITE); // Curseur blanc
        nameField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2)); // Bordure noire
        namePanel.add(nameField, BorderLayout.CENTER); // Ajout du champ au panneau

        // Ajouter le panneau principal pour Nom au panneau principal
        mainPanel.add(namePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement vertical

        // Répéter la même structure pour Prénom
        JPanel firstNamePanel = new JPanel(new BorderLayout());
        firstNamePanel.setBackground(new Color(70, 70, 70));

        JLabel firstNameLabel = new JLabel("Prénom");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        firstNameLabel.setBackground(new Color(70, 70, 70));
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        firstNameLabel.setOpaque(true);
        firstNamePanel.add(firstNameLabel, BorderLayout.NORTH);

        JTextField firstNameField = new JTextField();
        firstNameField.setPreferredSize(new Dimension(200, 30));
        firstNameField.setBackground(new Color(60, 60, 60));
        firstNameField.setForeground(Color.WHITE);
        firstNameField.setCaretColor(Color.WHITE);
        firstNameField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        firstNamePanel.add(firstNameField, BorderLayout.CENTER);

        mainPanel.add(firstNamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Répéter pour Pseudo
        JPanel pseudoPanel = new JPanel(new BorderLayout());
        pseudoPanel.setBackground(new Color(70, 70, 70));

        JLabel pseudoLabel = new JLabel("Pseudo");
        pseudoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pseudoLabel.setBackground(new Color(70, 70, 70));
        pseudoLabel.setForeground(Color.WHITE);
        pseudoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        pseudoLabel.setOpaque(true);
        pseudoPanel.add(pseudoLabel, BorderLayout.NORTH);

        JTextField pseudoField = new JTextField();
        pseudoField.setPreferredSize(new Dimension(200, 30));
        pseudoField.setBackground(new Color(60, 60, 60));
        pseudoField.setForeground(Color.WHITE);
        pseudoField.setCaretColor(Color.WHITE);
        pseudoField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        pseudoPanel.add(pseudoField, BorderLayout.CENTER);

        mainPanel.add(pseudoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Répéter pour Email
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setBackground(new Color(70, 70, 70));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setBackground(new Color(70, 70, 70));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        emailLabel.setOpaque(true);
        emailPanel.add(emailLabel, BorderLayout.NORTH);

        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        emailField.setBackground(new Color(60, 60, 60));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        emailPanel.add(emailField, BorderLayout.CENTER);

        mainPanel.add(emailPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement vertical

        
        
     // // Panneau principal pour le mot de passe
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BorderLayout());
        passwordPanel.setBackground(new Color(70, 70, 70)); // Couleur d'arrière-plan

        // Label pour le mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setBackground(new Color(70, 70, 70)); // Fond gris foncé
        passwordLabel.setForeground(Color.WHITE); // Texte blanc
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Espacement sous le label
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordLabel.setOpaque(true); // Rendre le fond visible


        // Sous-panneau pour le champ de mot de passe et le bouton
        JPanel passwordFieldPanel = new JPanel(new BorderLayout());
        passwordFieldPanel.setBackground(new Color(50, 50, 50)); // Couleur d'arrière-plan

        // Champ de mot de passe
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setBackground(new Color(60, 60, 60)); // Fond gris
        passwordField.setForeground(Color.WHITE); // Texte blanc
        passwordField.setCaretColor(Color.WHITE); // Curseur blanc
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2)); // Noir
        passwordField.setEchoChar('\u2022'); // Caractère pour masquer le texte

        // Bouton pour afficher/masquer le mot de passe
        JButton togglePasswordButton = new JButton();
        togglePasswordButton.setBackground(new Color(90, 90, 90)); // Couleur gris foncé
        togglePasswordButton.setPreferredSize(new Dimension(30, 30));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setBorderPainted(true);
        togglePasswordButton.setContentAreaFilled(true);

        // Charger l'icône "œil"
        try {
            URL resource = new UserRegistrationForm().getClass().getClassLoader().getResource("info7/eyes_mdp_page_co.png");
            if (resource != null) {
                ImageIcon eyeIcon = new ImageIcon(resource);
                Image scaledImage = eyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                togglePasswordButton.setIcon(new ImageIcon(scaledImage));
            } else {
                System.out.println("Icône non trouvée, affichage par défaut.");
                togglePasswordButton.setText("👁"); // Emoji comme alternative
            }
        } catch (Exception e) {
            togglePasswordButton.setText("👁"); // Emoji comme alternative
            e.printStackTrace();
        }

        // Action pour basculer la visibilité du mot de passe
        togglePasswordButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == '\u2022') {
                passwordField.setEchoChar((char) 0); // Afficher le texte
            } else {
                passwordField.setEchoChar('\u2022'); // Masquer le texte
            }
        });

        // Ajouter le champ de mot de passe et le bouton dans le sous-panneau
        passwordFieldPanel.add(passwordField, BorderLayout.CENTER); // Champ au centre
        passwordFieldPanel.add(togglePasswordButton, BorderLayout.EAST); // Bouton à droite

        // Ajouter le sous-panneau au panneau principal
        passwordPanel.add(passwordFieldPanel, BorderLayout.CENTER);

        
        // Critères de mot de passe
        JPanel criteriaPanel = new JPanel();
        criteriaPanel.setLayout(new BoxLayout(criteriaPanel, BoxLayout.Y_AXIS));
        criteriaPanel.setBackground(new Color(50, 50, 50)); 

        JCheckBox lengthCheck = new JCheckBox("Au moins 8 caractères");
        lengthCheck.setEnabled(false); // Désactiver la case à cocher
        lengthCheck.setBackground(new Color(50, 50, 50)); // Fond beige clair
        lengthCheck.setForeground(new Color(70, 70, 70)); // Texte gris foncé

        JCheckBox uppercaseCheck = new JCheckBox("Au moins une majuscule");
        uppercaseCheck.setEnabled(false); // Désactiver la case à cocher
        uppercaseCheck.setBackground(new Color(50, 50, 50)); // Fond beige clair
        uppercaseCheck.setForeground(new Color(70, 70, 70)); // Texte gris foncé

        JCheckBox specialCharCheck = new JCheckBox("Au moins un caractère spécial");
        specialCharCheck.setEnabled(false); // Désactiver la case à cocher
        specialCharCheck.setBackground(new Color(50, 50, 50)); // Fond beige clair
        specialCharCheck.setForeground(new Color(70, 70, 70)); // Texte gris foncé

        criteriaPanel.add(lengthCheck);
        criteriaPanel.add(uppercaseCheck);
        criteriaPanel.add(specialCharCheck);

        mainPanel.add(passwordPanel);
        mainPanel.add(criteriaPanel);

     // Bouton "Créer un compte" (Jaune)
        JButton registerButton = createModernButton(
            "Créer un compte",
            new Color(255, 223, 0), // Jaune
            new Color(255, 200, 0), // Jaune plus foncé au survol
            Color.BLACK // Texte noir
        );
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bouton "S'identifier" (Vert)
        JButton loginButton = createModernButton(
            "S'identifier",
            new Color(50, 205, 50), // Vert
            new Color(34, 139, 34), // Vert foncé au survol
            Color.WHITE // Texte blanc
        );
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label pour messages
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(registerButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(messageLabel);

        // Ajout au frame
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        
    

        // Action dynamique pour vérifier le mot de passe
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passwordField.getPassword());

                // Vérifie les critères de mot de passe
                lengthCheck.setSelected(password.length() >= 8);
                uppercaseCheck.setSelected(password.matches(".*[A-Z].*"));
                specialCharCheck.setSelected(password.matches(".*[!@#$%^&*(),.?\":{}|<>].*"));
            }
        });

        // Actions pour le bouton "Créer un compte"
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String firstName = firstNameField.getText();
                String pseudo = pseudoField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (name.isEmpty() || firstName.isEmpty() || pseudo.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Veuillez remplir tous les champs.");
                    messageLabel.setForeground(Color.RED); // Afficher le message en rouge
                } else if (!isValidEmail(email)) {
                    messageLabel.setText("Email non valide.");
                    messageLabel.setForeground(Color.RED); // Afficher le message en rouge
                } else if (!lengthCheck.isSelected() || !uppercaseCheck.isSelected() || !specialCharCheck.isSelected()) {
                    messageLabel.setText("Le mot de passe ne respecte pas les critères.");
                    messageLabel.setForeground(Color.RED); // Afficher le message en rouge
                } else if (isPseudoOrEmailExists(pseudo, email)) {
                    messageLabel.setText("Le pseudo ou l'email est déjà utilisé.");
                    messageLabel.setForeground(Color.RED); // Afficher le message en rouge
                } else {
                    String hashedPassword = hashPassword(password);
                    addUserToDatabase(pseudo, name, firstName, email, hashedPassword);
                    customizeOptionPane();
                    JOptionPane.showMessageDialog(
                    	    frame,
                    	    "Compte créé avec succès ! Bienvenue " + pseudo + ".",
                    	    "Succès",
                    	    JOptionPane.INFORMATION_MESSAGE,
                    	    createStyledIcon()
                    );
                    messageLabel.setForeground(new Color(0, 128, 0)); // Afficher le message en vert

                    // Réinitialiser les champs
                    nameField.setText("");
                    firstNameField.setText("");
                    pseudoField.setText("");
                    emailField.setText("");
                    passwordField.setText("");

                    // Réinitialiser les cases à cocher
                    lengthCheck.setSelected(false);
                    uppercaseCheck.setSelected(false);
                    specialCharCheck.setSelected(false);
                }
            }
        });

        

     // Action pour le bouton "S'identifier"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Créer une fenêtre pour la connexion
            	JDialog loginDialog = new JDialog(frame, "Connexion", true);
            	loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            	loginDialog.setSize(400, 250);
            	loginDialog.setLocationRelativeTo(frame);
            	loginDialog.setLayout(new BorderLayout());

            	// Panel pour le haut avec le logo
            	JPanel headerPanel = new JPanel(new BorderLayout());
            	headerPanel.setBackground(new Color(50, 50, 50));

            	try {
            	    URL resource = UserRegistrationForm.class.getClassLoader().getResource("info7/logo.png");
            	    if (resource != null) {
            	        ImageIcon logoIcon = new ImageIcon(resource);
            	        Image scaledLogo = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            	        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            	        headerPanel.add(logoLabel, BorderLayout.WEST);
            	    }
            	} catch (Exception e1) {
            	    e1.printStackTrace();
            	}
            	
            	JLabel titleLabel = new JLabel("Connexion", SwingConstants.CENTER);
            	titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            	titleLabel.setForeground(Color.WHITE);
            	headerPanel.add(titleLabel, BorderLayout.CENTER);
            	loginDialog.add(headerPanel, BorderLayout.NORTH);

                // Panel pour les champs
            	JPanel loginPanel = new JPanel();
            	loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
            	loginPanel.setBackground(new Color(50, 50, 50));
            	loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


                // Champ pour le pseudo
            	JLabel pseudoLabel = new JLabel("Pseudo :");
            	pseudoLabel.setForeground(Color.WHITE);
            	pseudoLabel.setFont(new Font("Arial", Font.BOLD, 14));

            	JTextField pseudoField = new JTextField();
            	pseudoField.setPreferredSize(new Dimension(200, 30));
            	pseudoField.setBackground(new Color(60, 60, 60));
            	pseudoField.setForeground(Color.WHITE);
            	pseudoField.setCaretColor(Color.WHITE);
            	pseudoField.setBorder(BorderFactory.createLineBorder(new Color(70,70,70), 2));
            	

                // Champ pour le mot de passe
            	JLabel passwordLabel = new JLabel("Mot de passe :");
            	passwordLabel.setForeground(Color.WHITE);
            	passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

            	JPasswordField passwordField = new JPasswordField();
            	passwordField.setPreferredSize(new Dimension(200, 30));
            	passwordField.setBackground(new Color(60, 60, 60));
            	passwordField.setForeground(Color.WHITE);
            	passwordField.setCaretColor(Color.WHITE);
            	passwordField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));

                // Bouton pour afficher/masquer le mot de passe
                JButton togglePasswordButton = new JButton();
                togglePasswordButton.setBackground(new Color(90, 90, 90)); // Couleur gris foncé
                togglePasswordButton.setPreferredSize(new Dimension(30, 30));
                togglePasswordButton.setFocusPainted(false);
                togglePasswordButton.setBorderPainted(true);
                togglePasswordButton.setContentAreaFilled(true);

                try {
                    URL resource = new UserRegistrationForm().getClass().getClassLoader().getResource("info7/eyes_mdp_page_co.png");
                    if (resource != null) {
                        ImageIcon eyeIcon = new ImageIcon(resource);
                        Image scaledImage = eyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                        togglePasswordButton.setIcon(new ImageIcon(scaledImage));
                    } else {
                        togglePasswordButton.setText("👁");
                    }
                } catch (Exception ex) {
                    togglePasswordButton.setText("👁");
                }

                // Action pour afficher/masquer le mot de passe
                togglePasswordButton.addActionListener(event -> {
                    if (passwordField.getEchoChar() == '\u2022') {
                        passwordField.setEchoChar((char) 0); // Afficher le texte
                    } else {
                        passwordField.setEchoChar('\u2022'); // Masquer le texte
                    }
                });

                // Panel pour le champ de mot de passe et le bouton œil
                JPanel passwordPanel = new JPanel(new BorderLayout());
                passwordPanel.add(passwordField, BorderLayout.CENTER);
                passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

                // Ajouter les champs au panel
                loginPanel.add(pseudoLabel);
                loginPanel.add(pseudoField);
                loginPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacement
                loginPanel.add(passwordLabel);
                loginPanel.add(passwordPanel);

             // Ajouter le panel des champs
                loginDialog.add(loginPanel, BorderLayout.CENTER);

                // Bouton Se connecter
                JButton submitButton = new JButton("Se connecter");
                submitButton.setBackground(new Color(255, 223, 0));
                submitButton.setForeground(Color.BLACK);
                submitButton.setFont(new Font("Arial", Font.BOLD, 14));
                submitButton.setFocusPainted(false);
                submitButton.addActionListener(event -> {
                    String pseudo = pseudoField.getText();
                    String password = new String(passwordField.getPassword());
                    String hashedPassword = hashPassword(password);

                    if (checkCredentials(pseudo, hashedPassword)) {
                        customizeOptionPane(); // Applique le style personnalisé
                        JOptionPane.showMessageDialog(
                            loginDialog,
                            "Bienvenue " + pseudo + " !",
                            "Connexion réussie",
                            JOptionPane.INFORMATION_MESSAGE,
                            createStyledIcon1()
                        );
                        loginDialog.dispose();
                        fenetre.refreshSidebar(); // Rafraîchir la barre latérale après connexion
                        frame.dispose(); // Fermer la fenêtre principale
                    } else {
                        customizeOptionPane(); // Applique le style personnalisé
                        JOptionPane.showMessageDialog(
                            loginDialog,
                            "Identifiants incorrects.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE,
                            createStyledIcon2()
                        );
                    }


                });

                // Ajouter bouton en bas
                loginDialog.add(submitButton, BorderLayout.SOUTH);
                loginDialog.setVisible(true);
            }
        });

    }
    // Méthode pour vérifier si un email est valide
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
    

    public static int connectedUserId = -1; // ID de l'utilisateur connecté

    private static boolean checkCredentials(String pseudo, String hashedPassword) {
        String query = "SELECT id FROM users WHERE pseudo = ? AND hashed_password = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, pseudo);
            stmt.setString(2, hashedPassword);
    
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    connectedUserId = rs.getInt("id"); // Récupère l'ID si les identifiants sont corrects
                    userFavorites = Database.getFavorites(connectedUserId); // Charger les favoris
                    System.out.println("Connexion réussie pour l'utilisateur ID: " + connectedUserId);
                    return true;
                } else {
                    System.out.println("Aucun utilisateur trouvé avec ces identifiants.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Retourne faux si aucun utilisateur correspondant n'est trouvé
    }
    
    
    // Méthode pour hacher un mot de passe avec SHA-256
    
    private static boolean isPseudoOrEmailExists(String pseudo, String email) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE pseudo = ? OR email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, pseudo);
                stmt.setString(2, email);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void addUserToDatabase(String pseudo, String nom, String prenom, String email, String hashedPassword) {
        try (Connection connection = Database.getConnection()) {
            String query = "INSERT INTO users (pseudo, nom, prenom, email, hashed_password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, pseudo);
                stmt.setString(2, nom);
                stmt.setString(3, prenom);
                stmt.setString(4, email);
                stmt.setString(5, hashedPassword);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    // Méthode pour créer un champ de texte avec un label
    private static JTextField createLabeledTextField(JPanel panel, String labelText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBackground(new Color(245, 245, 245)); // Fond beige pour le label
        label.setOpaque(true);
        
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(textField, BorderLayout.CENTER);
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        return textField;
    }

    public static void logout() {
        connectedUserId = -1; // Réinitialise l'ID utilisateur
        userFavorites.clear(); // Vider la liste des favoris si nécessaire
        customizeOptionPane(); // Applique le style personnalisé
        JOptionPane.showMessageDialog(
            null,
            "Vous avez été déconnecté avec succès.",
            "Déconnexion",
            JOptionPane.INFORMATION_MESSAGE,
            createStyledIcon3()
        );
    }


    // Méthode pour créer une icône stylée
    private static ImageIcon createStyledIcon() {
        try {
            URL resource = UserRegistrationForm.class.getClassLoader().getResource("info7/logo.png");
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
 // Méthode pour créer une icône stylée
    private static ImageIcon createStyledIcon1() {
        try {
            URL resource = UserRegistrationForm.class.getClassLoader().getResource("info7/welcome.png");
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static ImageIcon createStyledIcon2() {
        try {
            URL resource = UserRegistrationForm.class.getClassLoader().getResource("info7/pblm.png");
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static ImageIcon createStyledIcon3() {
        try {
            URL resource = UserRegistrationForm.class.getClassLoader().getResource("info7/bye.png");
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static void customizeOptionPane() {
        UIManager.put("OptionPane.background", new Color(50, 50, 50)); // Fond gris foncé
        UIManager.put("Panel.background", new Color(50, 50, 50)); // Fond du panel interne
        UIManager.put("OptionPane.messageForeground", Color.WHITE); // Texte en blanc
        UIManager.put("Button.background", new Color(100, 149, 237)); // Couleur des boutons
        UIManager.put("Button.foreground", Color.WHITE); // Texte des boutons
        UIManager.put("Button.border", BorderFactory.createLineBorder(new Color(30, 30, 30), 2)); // Bordure des boutons
    }
    
    private static JButton createModernButton(String text, Color backgroundColor, Color hoverColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(hoverColor); // Couleur au survol
                } else {
                    g2.setColor(backgroundColor); // Couleur par défaut
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Bordure arrondie

                super.paintComponent(g);
            }
        };

        button.setFocusPainted(false); // Supprime l'effet de focus
        button.setOpaque(false); // Nécessaire pour appliquer le style personnalisé
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(textColor); // Couleur du texte
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Police du texte
        button.setPreferredSize(new Dimension(200, 40)); // Taille du bouton
        return button;
    }



    // Méthode pour créer des boutons stylisés
    private static JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
}
