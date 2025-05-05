package Admin.GestionRendezVous;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RendezvousController {
    private RendezvousModel model;
    private RendezvousView view;
    private int currentRdvId = -1;

    public RendezvousController(RendezvousModel model, RendezvousView view) {
        this.model = model;
        this.view = view;
    }

    public void initialize() {
        try {
            view.loadPatients(model.getPatients());
            view.loadRendezVous(model.getRendezVous());
            view.initHeureMinuteCombos();

            setupListeners();
        } catch (SQLException e) {
            showError("Erreur d'initialisation: " + e.getMessage());
        }
    }

    private void setupListeners() {
        // Table selection listener
        view.getRdvTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectionChanged();
                }
            }
        });

        // Button listeners
        view.getSaveButton().addActionListener(e -> enregistrerRendezVous());
        view.getCancelButton().addActionListener(e -> annulerModification());
        view.getRefreshButton().addActionListener(e -> actualiserPage());
        view.getSearchButton().addActionListener(e -> rechercherRendezVous());
        view.getEditMenuItem().addActionListener(e -> preparerModification());
        view.getDeleteMenuItem().addActionListener(e -> supprimerRendezVous());
        view.getDeleteAllMenuItem().addActionListener(e -> supprimerTousRendezVous());
    }

    private void selectionChanged() {
        int selectedRow = view.getRdvTable().getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = view.getRdvTable().convertRowIndexToModel(selectedRow);
            currentRdvId = (Integer) view.getTableModel().getValueAt(modelRow, 0);

            // Patient
            String patient = (String) view.getTableModel().getValueAt(modelRow, 1);
            view.getPatientCombo().setSelectedItem(patient);

            // Date et heure
            String dateHeure = (String) view.getTableModel().getValueAt(modelRow, 2);
            try {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = displayFormat.parse(dateHeure);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

                view.getDateField().setText(dateFormat.format(date));
                String[] timeParts = timeFormat.format(date).split(":");
                view.getHeureCombo().setSelectedItem(timeParts[0]);
                view.getMinuteCombo().setSelectedItem(timeParts[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Autres champs
            view.getMotifField().setText((String) view.getTableModel().getValueAt(modelRow, 3));
            view.getStatutCombo().setSelectedItem(view.getTableModel().getValueAt(modelRow, 4));

            view.getSaveButton().setText("Mettre à jour");
        }
    }

    private void enregistrerRendezVous() {
        // Validation des données
        if (view.getPatientCombo().getSelectedItem() == null || view.getDateField().getText().trim().isEmpty()) {
            showError("Patient et date sont obligatoires");
            return;
        }

        // Vérification du statut pour les nouveaux rendez-vous
        String statut = (String) view.getStatutCombo().getSelectedItem();
        if (currentRdvId == -1 && !"prévu".equals(statut)) {
            showError("Vous ne pouvez pas créer un rendez-vous directement avec le statut 'annulé' ou 'terminé'.\n" +
                    "Créez-le d'abord avec le statut 'prévu', puis modifiez-le si nécessaire.");
            return;
        }

        // Construction de la date/heure
        String dateTimeStr = view.getDateField().getText() + " " +
                view.getHeureCombo().getSelectedItem() + ":" +
                view.getMinuteCombo().getSelectedItem();

        try {
            model.saveRendezVous(
                    currentRdvId,
                    (String) view.getPatientCombo().getSelectedItem(),
                    dateTimeStr,
                    view.getMotifField().getText(),
                    statut
            );

            view.loadRendezVous(model.getRendezVous());
            annulerModification();
            showMessage("Rendez-vous enregistré avec succès");
        } catch (SQLException e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    private void annulerModification() {
        currentRdvId = -1;
        view.getPatientCombo().setSelectedIndex(-1);
        view.getDateField().setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        view.getHeureCombo().setSelectedItem(new SimpleDateFormat("HH").format(new Date()));
        view.getMinuteCombo().setSelectedItem(new SimpleDateFormat("mm").format(new Date()));
        view.getMotifField().setText("");
        view.getStatutCombo().setSelectedItem("prévu");
        view.getSaveButton().setText("Enregistrer");
        view.getRdvTable().clearSelection();
    }


private void actualiserPage() {
    try {
        // Actualiser les deux listes
        view.loadPatients(model.getPatients());
        view.loadRendezVous(model.getRendezVous());

        // Réinitialiser l'interface
        annulerModification();
        view.getSearchField().setText("");

        showMessage("Toutes les données ont été actualisées");
    } catch (SQLException e) {
        showError("Erreur lors de l'actualisation: " + e.getMessage());
    }
}

    private void rechercherRendezVous() {
        String searchText = view.getSearchField().getText().trim();
        if (searchText.isEmpty()) {
            showError("Veuillez entrer un patient pour la recherche.");
            return;
        }

        try {
            view.loadRendezVous(model.searchRendezVous(searchText));
        } catch (SQLException e) {
            showError("Erreur lors de la recherche: " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    private void preparerModification() {
        if (view.getRdvTable().getSelectedRow() == -1) {
            showWarning("Veuillez sélectionner un rendez-vous");
        }
    }

    private void supprimerRendezVous() {
        int selectedRow = view.getRdvTable().getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Veuillez sélectionner un rendez-vous");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Voulez-vous vraiment supprimer ce rendez-vous?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int modelRow = view.getRdvTable().convertRowIndexToModel(selectedRow);
                int rdvId = (Integer) view.getTableModel().getValueAt(modelRow, 0);

                model.deleteRendezVous(rdvId);
                view.loadRendezVous(model.getRendezVous());
                annulerModification();

                showMessage("Rendez-vous supprimé avec succès");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void supprimerTousRendezVous() {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Voulez-vous vraiment supprimer TOUS les rendez-vous?\nCette action est irréversible!",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                model.deleteAllRendezVous();
                view.getTableModel().setRowCount(0);
                annulerModification();

                showMessage("Tous les rendez-vous ont été supprimés");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }


    private void showWarning(String message) {
        JOptionPane.showMessageDialog(view, message, "Avertissement", JOptionPane.WARNING_MESSAGE);
    }
}