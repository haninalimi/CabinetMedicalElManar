package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Méthode pour obtenir la connexion à la base de données
    public static Connection getConnection() throws SQLException {
        // Remplace les informations par celles de ton propre environnement
        String url = "jdbc:mysql://localhost:3306/cabinet_medical";
        String user = "root";  // Nom d'utilisateur de ta base de données
        String password = "root";  // Mot de passe de ta base de données

        // Retourner la connexion à la base de données
        return DriverManager.getConnection(url, user, password);
    }

    // Méthode pour fermer la connexion à la base de données
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
