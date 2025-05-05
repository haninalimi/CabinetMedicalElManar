// rmi/interfaces/RendezVousService.java
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RendezVousService extends Remote {
    String demanderRendezVous(int patientId, String dateHeure, String motif) throws RemoteException;
    List<String> getRendezVousPatient(int patientId) throws RemoteException;
}
