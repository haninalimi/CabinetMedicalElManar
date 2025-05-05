package Admin.Dashboard;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardContentPanel extends JPanel {
    private Connection connection;

    public DashboardContentPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(new Color(245, 245, 245));

        try {
            statsPanel.add(createStatCard("Total Patients", String.valueOf(getTotalPatients()), new Color(100, 88, 255)));
            statsPanel.add(createStatCard("Rendez-vous", String.valueOf(getTotalAppointments()), new Color(0, 190, 190)));
            statsPanel.add(createStatCard("Annulations", String.valueOf(getTotalCancellations()), new Color(255, 77, 77)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        add(statsPanel, BorderLayout.NORTH);
        add(createNewPatientsSection(), BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNewPatientsSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(245, 245, 245, 204),2),
                "  Nouveaux Patients  ",
                TitledBorder.LEFT, // Titre aligné à gauche
                TitledBorder.TOP, // Position du titre en haut
                new Font("Segoe UI", Font.BOLD, 16) // Police du titre
        );
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        titledBorder,
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );

        try {
            ResultSet rs = getNewPatients();
            while (rs.next()) {
                panel.add(createPatientRow(rs.getString("nom") + " " + rs.getString("prenom"), rs.getString("role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createPatientRow(String name, String role) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel roleLabel = new JLabel(role);
        roleLabel.setOpaque(true);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        roleLabel.setBackground(role.equals("admin") ? new Color(155, 89, 182) : new Color(100, 88, 255));

        row.add(nameLabel);
        row.add(Box.createHorizontalStrut(10));
        row.add(roleLabel);
        return row;
    }

    private int getTotalPatients() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM patients");
        rs.next();
        return rs.getInt(1);
    }

    private int getTotalAppointments() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rendezvous");
        rs.next();
        return rs.getInt(1);
    }

    private int getTotalCancellations() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rendezvous WHERE statut = 'annulé'");
        rs.next();
        return rs.getInt(1);
    }

    private ResultSet getNewPatients() throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("SELECT u.nom, u.prenom, u.role FROM patients p JOIN users u ON p.user_id = u.id ORDER BY p.id DESC LIMIT 5");
    }
}
