package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Conexión con la Base de Datos SQL
public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/rentacar?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "admin";

    // Evitar que alguien haga new ConexionDB() desde fuera.
    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}