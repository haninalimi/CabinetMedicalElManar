// rmi/server/RmiServer.java
package server;

import interfaces.RendezVousService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RmiServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099); // démarrer registre RMI
            RendezVousService service = new RendezVousServiceImpl();
            Naming.rebind("rmi://localhost:1099/rendezvous", service);
            System.out.println("Serveur RMI lancé !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
