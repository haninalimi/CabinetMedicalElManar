// rmi/client/RmiClient.java
package client;

import interfaces.RendezVousService;

import java.rmi.Naming;

public class RmiClient {
    public static RendezVousService getService() {
        try {
            return (RendezVousService) Naming.lookup("rmi://localhost:1099/rendezvous");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
