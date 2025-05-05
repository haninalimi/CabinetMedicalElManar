// rmi/server/RendezVousServiceImpl.java
package server;

import interfaces.RendezVousService;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousServiceImpl extends UnicastRemoteObject implements RendezVousService {

    public RendezVousServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String demanderRendezVous(int patientId, String dateHeure, String motif) throws RemoteException {
        try (Connection con = DatabaseConnection.getConnection()) {
            // Vérifie si créneau déjà occupé
            String check = "SELECT COUNT(*) FROM rendezvous WHERE date_rdv = ? AND statut = 'prévu'";
            PreparedStatement stmt = con.prepareStatement(check);
            stmt.setString(1, dateHeure);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "refusee"; // créneau déjà pris
            }

            // Afficher la demande côté serveur
            System.out.println("Nouvelle demande de rendez-vous :");
            System.out.println("Patient ID: " + patientId);
            System.out.println("Date/Heure: " + dateHeure);
            System.out.println("Motif: " + motif);
            System.out.println("Tapez 'acceptee' ou 'refusee' :");

            // Attendre la décision de l’admin via Scanner
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String decision = scanner.nextLine().trim().toLowerCase();

            if (decision.equals("acceptee")) {
                String insert = "INSERT INTO rendezvous (patient_id, date_rdv, motif, statut) VALUES (?, ?, ?, 'prévu')";
                PreparedStatement ins = con.prepareStatement(insert);
                ins.setInt(1, patientId);
                ins.setString(2, dateHeure);
                ins.setString(3, motif);
                ins.executeUpdate();
                return "acceptee";
            } else {
                return "refusee";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "refusee";
        }
    }


    @Override
    public List<String> getRendezVousPatient(int patientId) throws RemoteException {
        List<String> rdvs = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT date_rdv FROM rendezvous WHERE patient_id = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rdvs.add(rs.getString("date_rdv"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rdvs;
    }
}
