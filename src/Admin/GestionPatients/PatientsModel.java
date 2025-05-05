package Admin.GestionPatients;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PatientsModel {
    private Connection connection;

    public PatientsModel(Connection connection) {
        this.connection = connection;
    }

    public void chargerPatients(DefaultTableModel tableModel, JComponent parentComponent) {
        tableModel.setRowCount(0);
        try (Statement stmt = connection.createStatement()) {
            String sql = "SELECT u.id, u.nom, u.prenom, u.email, " +
                    "p.adresse, p.telephone, p.date_naissance " +
                    "FROM users u JOIN patients p ON u.id = p.user_id " +
                    "WHERE u.role = 'patient'";

            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("adresse"),
                            rs.getString("telephone"),
                            rs.getDate("date_naissance")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            showMessage(parentComponent, "Erreur lors du chargement: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void preparerModification(PatientsView view) {
        int selectedRow = view.getPatientsTable().getSelectedRow();
        if (selectedRow == -1) {
            showMessage(view, "Veuillez sélectionner un patient à modifier", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = view.getPatientsTable().convertRowIndexToModel(selectedRow);
        int currentPatientId = (int) view.getTableModel().getValueAt(modelRow, 0);

        try {
            String sql = "SELECT u.nom, u.prenom, u.email, p.adresse, p.telephone, p.date_naissance " +
                    "FROM users u JOIN patients p ON u.id = p.user_id WHERE u.id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, currentPatientId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    view.setCurrentPatientId(currentPatientId);
                    view.getNomField().setText(rs.getString("nom"));
                    view.getPrenomField().setText(rs.getString("prenom"));
                    view.getEmailField().setText(rs.getString("email"));
                    view.getPasswordField().setText("");
                    view.getAdresseField().setText(rs.getString("adresse"));
                    view.getTelephoneField().setText(rs.getString("telephone"));
                    view.getDateNaissanceField().setText(rs.getDate("date_naissance").toString());
                    view.getSaveButton().setText("Mettre à jour");
                }
            }
        } catch (SQLException e) {
            showMessage(view, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void enregistrerPatient(int currentPatientId, String nom, String prenom, String email,
                                   char[] password, String adresse, String telephone,
                                   String dateNaissance, PatientsView view) {
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() ||
                password.length == 0 || dateNaissance.isEmpty()) {
            showMessage(view, "Veuillez remplir tous les champs obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            connection.setAutoCommit(false);

            if (currentPatientId == -1) {
                // Mode création
                String userSql = "INSERT INTO users (nom, prenom, email, mot_de_passe, role) VALUES (?, ?, ?, ?, 'patient')";
                try (PreparedStatement userStmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                    userStmt.setString(1, nom);
                    userStmt.setString(2, prenom);
                    userStmt.setString(3, email);
                    userStmt.setString(4, new String(password));
                    userStmt.executeUpdate();

                    int userId;
                    try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            userId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Échec de la création, aucun ID obtenu.");
                        }
                    }

                    String patientSql = "INSERT INTO patients (user_id, adresse, telephone, date_naissance) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement patientStmt = connection.prepareStatement(patientSql)) {
                        patientStmt.setInt(1, userId);
                        patientStmt.setString(2, adresse);
                        patientStmt.setString(3, telephone);
                        patientStmt.setString(4, dateNaissance);
                        patientStmt.executeUpdate();
                    }
                }
                connection.commit();
                chargerPatients(view.getTableModel(), view);
                view.viderChamps();
                showMessage(view, "Patient enregistré avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Mode modification
                String updateUserSql = "UPDATE users SET nom = ?, prenom = ?, email = ? WHERE id = ?";
                try (PreparedStatement userStmt = connection.prepareStatement(updateUserSql)) {
                    userStmt.setString(1, nom);
                    userStmt.setString(2, prenom);
                    userStmt.setString(3, email);
                    userStmt.setInt(4, currentPatientId);
                    userStmt.executeUpdate();
                }

                String updatePatientSql = "UPDATE patients SET adresse = ?, telephone = ?, date_naissance = ? WHERE user_id = ?";
                try (PreparedStatement patientStmt = connection.prepareStatement(updatePatientSql)) {
                    patientStmt.setString(1, adresse);
                    patientStmt.setString(2, telephone);
                    patientStmt.setString(3, dateNaissance);
                    patientStmt.setInt(4, currentPatientId);
                    patientStmt.executeUpdate();
                }

                if (password.length > 0) {
                    String updatePasswordSql = "UPDATE users SET mot_de_passe = ? WHERE id = ?";
                    try (PreparedStatement passwordStmt = connection.prepareStatement(updatePasswordSql)) {
                        passwordStmt.setString(1, new String(password));
                        passwordStmt.setInt(2, currentPatientId);
                        passwordStmt.executeUpdate();
                    }
                }

                connection.commit();
                chargerPatients(view.getTableModel(), view);
                view.annulerModification();
                showMessage(view, "Patient mis à jour avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            gererErreurSQL(view, e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void supprimerPatient(PatientsView view) {
        int selectedRow = view.getPatientsTable().getSelectedRow();
        if (selectedRow == -1) {
            showMessage(view, "Veuillez sélectionner un patient à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = view.getPatientsTable().convertRowIndexToModel(selectedRow);
        int patientId = (int) view.getTableModel().getValueAt(modelRow, 0);
        String nomPatient = view.getTableModel().getValueAt(modelRow, 1) + " " + view.getTableModel().getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer le patient " + nomPatient + "?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                connection.setAutoCommit(false);

                String deletePatientSql = "DELETE FROM patients WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deletePatientSql)) {
                    stmt.setInt(1, patientId);
                    stmt.executeUpdate();
                }

                String deleteUserSql = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deleteUserSql)) {
                    stmt.setInt(1, patientId);
                    stmt.executeUpdate();
                }
                connection.commit();
                chargerPatients(view.getTableModel(), view);

                if (view.getCurrentPatientId() == patientId) {
                    view.annulerModification();
                }

                showMessage(view, "Patient supprimé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                showMessage(view, "Erreur lors de la suppression: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void supprimerTousPatients(PatientsView view) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer TOUS les patients?\nCette action est irréversible!",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                connection.setAutoCommit(false);

                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("DELETE FROM patients");
                }

                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("DELETE FROM users WHERE role = 'patient'");
                }

                connection.commit();
                view.getTableModel().setRowCount(0);
                view.annulerModification();

                showMessage(view, "Tous les patients ont été supprimés", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                showMessage(view, "Erreur lors de la suppression: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rechercherPatient(String email, DefaultTableModel tableModel, JComponent parentComponent) {
        if (email.isEmpty()) {
            chargerPatients(tableModel, parentComponent);
            return;
        }

        try {
            String sql = "SELECT u.id, u.nom, u.prenom, u.email, " +
                    "p.adresse, p.telephone, p.date_naissance " +
                    "FROM users u JOIN patients p ON u.id = p.user_id " +
                    "WHERE u.role = 'patient' AND u.email LIKE ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, "%" + email + "%");
                ResultSet rs = stmt.executeQuery();

                tableModel.setRowCount(0);
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("adresse"),
                            rs.getString("telephone"),
                            rs.getDate("date_naissance")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            showMessage(parentComponent, "Erreur lors de la recherche: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gererErreurSQL(JComponent parentComponent, SQLException e) {
        if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
            showMessage(parentComponent, "Cet email est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
        } else {
            showMessage(parentComponent, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(JComponent parentComponent, String message, String title, int messageType) {
        JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
    }
}