package Admin.Dashboard;

import javax.swing.*;
import java.awt.*;

public class TopBarPanel extends JPanel {
    public TopBarPanel(String adminEmail) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        TimerPanel timerPanel = new TimerPanel();
        timerPanel.setBackground(Color.WHITE);
        add(timerPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);

        JLabel emailLabel = new JLabel("Connecté en tant que : " + adminEmail);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(100, 88, 255));
        rightPanel.add(emailLabel);

        add(rightPanel, BorderLayout.EAST);
    }
}
