package Admin.GestionPatients;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class PatientsPanel extends JPanel {
    private PatientsModel model;
    private PatientsView view;

    public PatientsPanel(Connection connection) {
        setLayout(new BorderLayout());
        model = new PatientsModel(connection);
        view = new PatientsView();

        add(view, BorderLayout.CENTER);
        initializeListeners();
        model.chargerPatients(view.getTableModel(), view); // Ajout du paramètre view
    }

    private void initializeListeners() {
        view.getSaveButton().addActionListener(e -> {
            model.enregistrerPatient(
                    view.getCurrentPatientId(),
                    view.getNomField().getText(),
                    view.getPrenomField().getText(),
                    view.getEmailField().getText(),
                    view.getPasswordField().getPassword(),
                    view.getAdresseField().getText(),
                    view.getTelephoneField().getText(),
                    view.getDateNaissanceField().getText(),
                    view
            );
        });

        view.getCancelButton().addActionListener(e -> view.annulerModification());

        view.getRefreshButton().addActionListener(e -> {
            model.chargerPatients(view.getTableModel(), view); // Ajout du paramètre view
            view.actualiserPage();
        });

        view.setEditListener(e -> model.preparerModification(view));
        view.setDeleteListener(e -> model.supprimerPatient(view));
        view.setDeleteAllListener(e -> model.supprimerTousPatients(view));
        view.setSearchListener(e -> model.rechercherPatient(
                view.getSearchField().getText(),
                view.getTableModel(),
                view // Ajout du paramètre view
        ));
    }
}