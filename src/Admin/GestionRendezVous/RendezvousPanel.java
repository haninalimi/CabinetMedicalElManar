package Admin.GestionRendezVous;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class RendezvousPanel extends JPanel {
    private RendezvousModel model;
    private RendezvousView view;
    private RendezvousController controller;

    public RendezvousPanel(Connection connection) {
        setLayout(new BorderLayout());
        model = new RendezvousModel();
        view = new RendezvousView();
        controller = new RendezvousController(model, view);

        add(view, BorderLayout.CENTER);
        controller.initialize();
    }
}