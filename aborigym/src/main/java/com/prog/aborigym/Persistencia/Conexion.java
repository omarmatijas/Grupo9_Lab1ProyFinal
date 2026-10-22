package com.prog.aborigym.Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Grupo9
 */
public class Conexion {
    
    /** SQLSTATE de violación de restricción de integridad (clave foránea, unique, etc.). */
    public static final String SQLSTATE_VIOLACION_INTEGRIDAD = "23000";

    private final String url;
    private final String usuario;
    private final String password;

    private static Connection conexion = null;

    public Conexion(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    public Connection buscarConexion() {
        try {
            // Se reconecta si es la primera vez o si la conexión se cerró
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.mariadb.jdbc.Driver");
                conexion = DriverManager.getConnection(url, usuario, password);
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("No se pudo cargar el driver de MariaDB. ¿Está la dependencia en el pom.xml? " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("No se pudo conectar a la base de datos: " + ex.getMessage());
        }
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error al cerrar la conexión: " + ex.getMessage());
        }
    }
}
