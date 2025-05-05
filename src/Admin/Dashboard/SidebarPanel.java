package Admin.Dashboard;

import Admin.Dashboard.AdminDashboard;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {
    private AdminDashboard dashboard;

    public SidebarPanel(AdminDashboard dashboard) {
        this.dashboard = dashboard;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(200, getHeight()));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel(new ImageIcon("assets/logoo.jpg"));
        add(logoLabel);

        JLabel title = new JLabel("Cabinet El Manar");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        add(title);
        add(Box.createVerticalStrut(30));

        add(createSidebarButton("Dashboard"));
        add(createSidebarButton("Rendez-vous"));
        add(createSidebarButton("Gestion des Patients"));
        add(createSidebarButton("Déconnexion"));
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

        button.addActionListener(e -> dashboard.switchPanel(name));

        return button;
    }
}
