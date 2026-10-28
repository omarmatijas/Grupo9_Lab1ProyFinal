package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.Socio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Grupo9
 */
public class SocioData {

    private final Conexion conexion;

    public SocioData(Conexion conexion) {
        this.conexion = conexion;
    }

    public List<Socio> listarSocios() throws SQLException {
        String sql = "SELECT * FROM socio ORDER BY apellido, nombre";
        List<Socio> socios = new ArrayList<>();
        
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                socios.add(parsearRS(rs));
            }
        }
        return socios;
    }
    
    public int guardarSocio(Socio socio) throws SQLException {
        String sql = "INSERT INTO socio(dni, nombre, apellido, edad, correo, telefono, estado) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, socio.getDni());
            ps.setString(2, socio.getNombre());
            ps.setString(3, socio.getApellido());
            ps.setInt(4, socio.getEdad());
            ps.setString(5, socio.getCorreo());
            ps.setString(6, socio.getTelefono());
            ps.setBoolean(7, socio.isEstado());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (Exception ex) {
                throw new SQLException("No se pudo crear el socio.");
            }

            if (filas != 1) {
                throw new SQLException("No se pudo crear el socio.");
            }

            int idSocio = 0;
            boolean hayId = false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idSocio = rs.getInt(1);
                    hayId = !rs.wasNull() && idSocio != 0;
                }
            } catch (SQLException ex) {
                throw new SQLException("No se pudo obtener el ID generado del socio.", ex);
            }

            if (!hayId) {
                throw new SQLException("No se pudo obtener el ID generado del socio.");
            }

            socio.setIdSocio(idSocio);
            return idSocio;
        }
    }

    public void actualizarSocio(Socio socio) throws SQLException {
        String sql = "UPDATE socio SET dni = ?, nombre = ?, apellido = ?, edad = ?, correo = ?, telefono = ? WHERE id_socio = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setString(1, socio.getDni());
            ps.setString(2, socio.getNombre());
            ps.setString(3, socio.getApellido());
            ps.setInt(4, socio.getEdad());
            ps.setString(5, socio.getCorreo());
            ps.setString(6, socio.getTelefono());
            ps.setInt(7, socio.getIdSocio());

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException("No se pudo actualizar el socio " + socio.getIdSocio() + ", no existe.");
            }

        }
    }

    public Socio obtenerSocio(int idSocio) throws SQLException {
        String sql = "SELECT * FROM socio WHERE id_socio = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idSocio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return parsearRS(rs);
                }
                return null;
            }
        }
    }

    public Socio buscarSocioPorDNI(String dni) throws SQLException {
        String sql = "SELECT * FROM socio WHERE dni = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return parsearRS(rs);
                }
                return null;
            }
        }
    }

    public void bajaLogicaSocio(int idSocio) throws SQLException {
        cambiarActivo(idSocio, false);
    }

    public void reactivacionLogicaSocio(int idSocio) throws SQLException {
        cambiarActivo(idSocio, true);
    }

    public void cambiarActivo(int idSocio, boolean activo) throws SQLException {
        Socio socio = obtenerSocio(idSocio);
        String accion = activo ? "reactivar" : "dar de baja";

        if (socio == null) {
            throw new SQLException("No se pudo " + accion + " el socio, el ID " + idSocio + " no existe.");
        }

        if (socio.isEstado() == activo) {
            return;
        }

        String sql = "UPDATE socio SET estado = ? WHERE id_socio = ?";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idSocio);

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo " + accion + " el socio, el ID = " + idSocio + " no existe.");
            }
        }
    }

    public static Socio parsearRS(ResultSet rs) throws SQLException {
        return new Socio(
                rs.getInt("id_socio"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getInt("edad"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getBoolean("estado")
        );
    }
}
