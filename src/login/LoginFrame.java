package login;

import Patients.PatientDashboard;
import Admin.Dashboard.AdminDashboard;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JPanel rightPanel;
    private JTextField tfEmail;
    private JPasswordField tfMotDePasse;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Connexion - Cabinet El Manar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        rightPanel = createLoginPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon("assets/logoo.jpg")); // Mets le bon chemin
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Cabinet El Manar");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalGlue());

        return leftPanel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre centré
        JLabel titleLabel = new JLabel("Connexion", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        tfEmail = new JTextField(20);
        styleInput(tfEmail);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(lblEmail, gbc);

        gbc.gridx = 1;
        panel.add(tfEmail, gbc);

        // Mot de passe
        JLabel lblMotDePasse = new JLabel("Mot de passe:");
        lblMotDePasse.setFont(new Font("Arial", Font.PLAIN, 16));
        tfMotDePasse = new JPasswordField(20);
        styleInput(tfMotDePasse);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblMotDePasse, gbc);

        gbc.gridx = 1;
        panel.add(tfMotDePasse, gbc);

        // Bouton de connexion
        btnLogin = new JButton("Se connecter");
        styleButton(btnLogin);

        btnLogin.addActionListener(e -> login());

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
    }

    private void styleInput(JComponent input) {
        input.setFont(new Font("Arial", Font.PLAIN, 16));
        input.setPreferredSize(new Dimension(250, 30));
        input.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2));
        input.setBackground(new Color(240, 240, 240));
    }

    private void login() {
        String email = tfEmail.getText();
        String motDePasse = new String(tfMotDePasse.getPassword());

        if (email.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, motDePasse);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String emailDb = rs.getString("email");

                JOptionPane.showMessageDialog(this, "Bienvenue " + nom + " !");

                if ("admin".equalsIgnoreCase(role)) {
                    new AdminDashboard(emailDb).setVisible(true);
                } else if ("patient".equalsIgnoreCase(role)) {
                    new PatientDashboard(nom, prenom, emailDb).setVisible(true);
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


}
