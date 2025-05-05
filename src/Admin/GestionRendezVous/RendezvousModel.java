package Admin.GestionRendezVous;

import utils.DatabaseConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RendezvousModel {
    public List<String> getPatients() throws SQLException {
        List<String> patients = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT u.id, u.nom, u.prenom FROM users u JOIN patients p ON u.id = p.user_id ORDER BY u.nom, u.prenom")) {

            while (rs.next()) {
                patients.add(rs.getString("nom") + " " + rs.getString("prenom"));
            }
        }
        return patients;
    }

    public List<Object[]> getRendezVous() throws SQLException {
        List<Object[]> rdvs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT r.id, r.date_rdv, r.motif, r.statut, 
                        u.nom, u.prenom
                 FROM rendezvous r
                 JOIN patients p ON r.patient_id = p.id
                 JOIN users u ON p.user_id = u.id
                 ORDER BY r.date_rdv DESC""")) {

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                Timestamp dateRdv = rs.getTimestamp("date_rdv");
                String dateHeure = displayFormat.format(dateRdv);

                rdvs.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        dateHeure,
                        rs.getString("motif"),
                        rs.getString("statut")
                });
            }
        }
        return rdvs;
    }

    public List<Object[]> searchRendezVous(String searchText) throws SQLException {
        List<Object[]> rdvs = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                 SELECT r.id, r.date_rdv, r.motif, r.statut, 
                        u.nom, u.prenom
                 FROM rendezvous r
                 JOIN patients p ON r.patient_id = p.id
                 JOIN users u ON p.user_id = u.id
                 WHERE CONCAT(u.nom, ' ', u.prenom) LIKE ?
                 ORDER BY r.date_rdv DESC""")) {

            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                Timestamp dateRdv = rs.getTimestamp("date_rdv");
                String dateHeure = displayFormat.format(dateRdv);

                rdvs.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        dateHeure,
                        rs.getString("motif"),
                        rs.getString("statut")
                });
            }
        }
        return rdvs;
    }

    public void saveRendezVous(int currentRdvId, String patientName, String dateTimeStr, String motif, String statut) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int patientId = getPatientId(conn, patientName);

            if (currentRdvId == -1) {
                checkRendezVousConflict(conn, dateTimeStr);
                insertRendezVous(conn, patientId, dateTimeStr, motif, statut);
            } else {
                updateRendezVous(conn, patientId, dateTimeStr, motif, statut, currentRdvId);
            }

            conn.commit();
        }
    }

    public void deleteRendezVous(int rdvId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM rendezvous WHERE id=?")) {
            stmt.setInt(1, rdvId);
            stmt.executeUpdate();
        }
    }

    public void deleteAllRendezVous() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM rendezvous");
        }
    }

    private int getPatientId(Connection conn, String patientName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT p.id FROM patients p JOIN users u ON p.user_id = u.id WHERE CONCAT(u.nom, ' ', u.prenom) = ?")) {
            stmt.setString(1, patientName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Patient non trouvé");
    }

    private void checkRendezVousConflict(Connection conn, String dateTimeStr) throws SQLException {
        try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM rendezvous WHERE date_rdv = ? AND statut = 'prévu'")) {
            checkStmt.setString(1, dateTimeStr);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Un rendez-vous est déjà prévu à cette date et heure");
            }
        }
    }

    private void insertRendezVous(Connection conn, int patientId, String dateTimeStr, String motif, String statut) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO rendezvous (patient_id, date_rdv, motif, statut) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, patientId);
            stmt.setString(2, dateTimeStr);
            stmt.setString(3, motif);
            stmt.setString(4, statut);
            stmt.executeUpdate();
        }
    }

    private void updateRendezVous(Connection conn, int patientId, String dateTimeStr, String motif, String statut, int rdvId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE rendezvous SET patient_id=?, date_rdv=?, motif=?, statut=? WHERE id=?")) {
            stmt.setInt(1, patientId);
            stmt.setString(2, dateTimeStr);
            stmt.setString(3, motif);
            stmt.setString(4, statut);
            stmt.setInt(5, rdvId);
            stmt.executeUpdate();
        }
    }
}