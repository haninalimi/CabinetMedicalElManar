

package Admin.Dashboard;
import Admin.GestionPatients.PatientsPanel;
import Admin.GestionRendezVous.RendezvousPanel;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {
    private String adminEmail;
    private Connection connection;
    private JPanel mainPanel;

    public AdminDashboard(String email) throws SQLException {
        this.adminEmail = email;
        this.connection = DatabaseConnection.getConnection();

        setTitle("Cabinet El Manar");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new SidebarPanel(this), BorderLayout.WEST);
        add(new TopBarPanel(adminEmail), BorderLayout.NORTH);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(new DashboardContentPanel(connection), "Dashboard");
        mainPanel.add(new RendezvousPanel(connection), "Rendez-vous");
        mainPanel.add(new PatientsPanel( connection), "Gestion des Patients");

        add(mainPanel, BorderLayout.CENTER);
    }

    public void switchPanel(String panelName) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        if (panelName.equals("Déconnexion")) {
            dispose();
        } else {
            cl.show(mainPanel, panelName);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AdminDashboard("admin@example.com").setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
