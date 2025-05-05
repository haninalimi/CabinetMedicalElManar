package Patients;

import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MonProfilPanel extends JPanel {

    public MonProfilPanel(String prenom, String nom, String email) {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));

        // Titre du profil
        JLabel titleLabel = new JLabel("Mon Profil", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // Panneau d'information avec espacement et bordure
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout()); // GridBagLayout pour plus de flexibilité
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les éléments
        gbc.anchor = GridBagConstraints.WEST;

        // Panneau pour contenir les informations avec bordure
        JPanel borderedPanel = new JPanel();
        borderedPanel.setLayout(new BorderLayout());
        borderedPanel.setBackground(Color.WHITE);
        borderedPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2)); // Bordure propre

        // Charger les données depuis la base de données
        try {
            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT p.telephone, p.adresse, p.date_naissance, u.email " +
                    "FROM patients p " +
                    "JOIN users u ON p.user_id = u.id " +
                    "WHERE u.email = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, email); // Utilisation de l'email pour récupérer les informations du patient

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String telephone = rs.getString("telephone");
                String adresse = rs.getString("adresse");
                String dateNaissance = (rs.getDate("date_naissance") != null) ? rs.getDate("date_naissance").toString() : "Non renseignée";

                // Ajout des informations dans le panneau
                gbc.gridx = 0;
                gbc.gridy = 0;
                infoPanel.add(createFieldLabel("Nom:"), gbc);

                gbc.gridx = 1;
                infoPanel.add(createValueLabel(nom), gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                infoPanel.add(createFieldLabel("Prénom:"), gbc);

                gbc.gridx = 1;
                infoPanel.add(createValueLabel(prenom), gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                infoPanel.add(createFieldLabel("Email:"), gbc);

                gbc.gridx = 1;
                infoPanel.add(createValueLabel(email), gbc);

                gbc.gridx = 0;
                gbc.gridy = 3;
                infoPanel.add(createFieldLabel("Téléphone:"), gbc);

                gbc.gridx = 1;
                infoPanel.add(createValueLabel(telephone != null ? telephone : "Non renseigné"), gbc);

                gbc.gridx = 0;
                gbc.gridy = 4;
                infoPanel.add(createFieldLabel("Adresse:"), gbc);

                gbc.gridx = 1;
                infoPanel.add(createValueLabel(adresse != null ? adresse : "Non renseignée"), gbc);

                gbc.gridx = 0;
                gbc.gridy = 5;
                infoPanel.add(createFieldLabel("Date de naissance:"), gbc);

                gbc.gridx = 1;
                infoPanel.add(createValueLabel(dateNaissance), gbc);

            } else {
                infoPanel.add(new JLabel("Aucune information trouvée pour cet utilisateur."));
            }

            DatabaseConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
            infoPanel.add(new JLabel("Erreur lors du chargement du profil."));
        }

        // Ajouter le panneau d'informations avec bordure dans le panneau principal
        borderedPanel.add(infoPanel, BorderLayout.CENTER);
        add(borderedPanel, BorderLayout.CENTER);
    }

    // Création d'un label pour le champ
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.DARK_GRAY);
        label.setHorizontalAlignment(SwingConstants.RIGHT); // Alignement à droite pour plus de lisibilité
        return label;
    }

    // Création d'un label pour la valeur avec du style
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(Color.BLACK);
        label.setBackground(new Color(245, 245, 245)); // Fond léger pour les valeurs
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Espacement autour des valeurs
        return label;
    }
}
