package Patients;

import login.LoginFrame;
import utils.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientDashboard extends JFrame {
    private String nom;
    private String prenom;
    private String email;
    private JPanel mainPanel;

    public PatientDashboard(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;

        setTitle("Dashboard Patient");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(createAccueilPanel(), "Accueil");
        mainPanel.add(new MesRendezVousPanel(prenom, nom, email), "Mes Rendez-vous");
        mainPanel.add(new MonProfilPanel(prenom, nom,email), "Mon Profil");

        add(mainPanel, BorderLayout.CENTER);
        showPanel("Accueil");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.BLACK);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon("assets/logoo.jpg")); // Mets le bon chemin vers le logo
        sidebar.add(logoLabel);

        JLabel title = new JLabel("Cabinet El Manar");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createSidebarButton("Accueil"));
        sidebar.add(createSidebarButton("Mes Rendez-vous"));
        sidebar.add(createSidebarButton("Mon Profil"));
        sidebar.add(createSidebarButton("Déconnexion"));

        return sidebar;
    }

    private JButton createSidebarButton(String name) {
        JButton button = new JButton(name);
        button.setFocusPainted(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 10));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (name.equals("Déconnexion")) {
                int confirm = JOptionPane.showConfirmDialog(this, "Se déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true); // À créer ou adapter
                }
            } else {
                showPanel(name);
            }
        });

        return button;
    }

    private JPanel createTopBar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel nameLabel = new JLabel("Connecté : " + prenom + " " + nom);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(25, 118, 210));

        topbar.add(nameLabel, BorderLayout.EAST);
        return topbar;
    }
    private JPanel createAccueilPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(Color.WHITE);

        int totalPatients = getCount("SELECT COUNT(*) FROM patients");
        int totalRdv = getCount("SELECT COUNT(*) FROM rendezvous");
        int totalMedecins = 5; // Valeur fictive

        cardsPanel.add(createInfoPanel("Médecins", String.valueOf(totalMedecins), new Color(33, 150, 243), "🩺"));
        cardsPanel.add(createInfoPanel("Patients", String.valueOf(totalPatients), new Color(76, 175, 80), "👥"));
        cardsPanel.add(createInfoPanel("Rendez-vous", String.valueOf(totalRdv), new Color(255, 152, 0), "📅"));

        JPanel servicesPanel = new JPanel(new BorderLayout(15, 15));
        servicesPanel.setBackground(Color.WHITE);
        servicesPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(0, 102, 204), 2, true),
                "",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(0, 102, 204)
        ));

        JPanel servicesContent = new JPanel();
        servicesContent.setLayout(new BoxLayout(servicesContent, BoxLayout.Y_AXIS));
        servicesContent.setBackground(Color.WHITE);
        servicesContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Texte "Nos Services :"
        JLabel nosServicesLabel = new JLabel("Nos Services :");
        nosServicesLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nosServicesLabel.setForeground(new Color(0, 102, 204));
        nosServicesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        servicesContent.add(nosServicesLabel);

        servicesContent.add(Box.createVerticalStrut(15)); // Espace après "Nos Services :"

        // Liste des services
        String[] services = {
                "Consultations spécialisées",
                "Téléconsultations",
                "Urgences 24/7",
                "Analyses et laboratoires",
                "Soins à domicile",
                "Suivi médical personnalisé"
        };

        for (String service : services) {
            JLabel serviceLabel = new JLabel("- " + service);
            serviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            serviceLabel.setForeground(new Color(60, 60, 60));
            serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            servicesContent.add(serviceLabel);
            servicesContent.add(Box.createVerticalStrut(8)); // Petit espace entre les services
        }

        servicesPanel.add(servicesContent, BorderLayout.CENTER);

        container.add(cardsPanel, BorderLayout.NORTH);
        container.add(servicesPanel, BorderLayout.CENTER);

        return container;
    }


    private JPanel createInfoPanel(String title, String value, Color color, String icon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(150, 100));

        JLabel iconLabel = new JLabel(icon, SwingConstants.LEFT);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(color);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, name);
    }

    private int getCount(String query) {
        int count = 0;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}