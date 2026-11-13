package backend;

import java.sql.*;

public class ConexionBD {

    private static final String URL      = System.getenv().getOrDefault("GYM_DB_URL",
        "jdbc:mysql://localhost:3306/gimnasio_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true");
    private static final String USUARIO  = System.getenv().getOrDefault("GYM_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("GYM_DB_PASSWORD", "");

    private static Connection conexion;

    private ConexionBD() {}

    public static Connection getConexion() throws SQLException {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. Agrega mysql-connector-j.jar al proyecto.");
        }
        return conexion;
    }

    public static void cerrar() {
        try { if (conexion != null && !conexion.isClosed()) conexion.close(); }
        catch (SQLException e) { System.err.println("Error al cerrar: " + e.getMessage()); }
    }
}
