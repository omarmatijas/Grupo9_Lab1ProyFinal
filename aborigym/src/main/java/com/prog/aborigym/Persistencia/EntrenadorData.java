package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.Entrenador;
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
public class EntrenadorData {

    private final Conexion conexion;

    public EntrenadorData(Conexion conexion) {
        this.conexion = conexion;
    }

    public int guardarEntrenador(Entrenador entrenador) throws SQLException {
        String sql = "INSERT INTO entrenador(dni, nombre, apellido, especialidad, estado) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entrenador.getDni());
            ps.setString(2, entrenador.getNombre());
            ps.setString(3, entrenador.getApellido());
            ps.setString(4, entrenador.getEspecialidad());
            ps.setBoolean(5, entrenador.isEstado());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (SQLException ex) {
                if (Conexion.SQLSTATE_VIOLACION_INTEGRIDAD.equals(ex.getSQLState())) {
                    throw new SQLException("No se pudo crear el entrenador: el DNI ya existe.", ex);
                }
                throw new SQLException("No se pudo crear el entrenador.", ex);
            }

            if (filas != 1) {
                throw new SQLException("No se pudo crear el entrenador.");
            }

            int idEntrenador = 0;
            boolean hayId = false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idEntrenador = rs.getInt(1);
                    hayId = !rs.wasNull() && idEntrenador != 0;
                }
            } catch (SQLException ex) {
                throw new SQLException("No se pudo obtener el ID generado del entrenador.", ex);
            }

            if (!hayId) {
                throw new SQLException("No se pudo obtener el ID generado del entrenador.");
            }

            entrenador.setIdEntrenador(idEntrenador);
            return idEntrenador;
        }
    }

    public void actualizarEntrenador(Entrenador entrenador) throws SQLException {
        String sql = "UPDATE entrenador SET dni = ?, nombre = ?, apellido = ?, especialidad = ? WHERE id_entrenador = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setString(1, entrenador.getDni());
            ps.setString(2, entrenador.getNombre());
            ps.setString(3, entrenador.getApellido());
            ps.setString(4, entrenador.getEspecialidad());
            ps.setInt(5, entrenador.getIdEntrenador());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (SQLException ex) {
                if (Conexion.SQLSTATE_VIOLACION_INTEGRIDAD.equals(ex.getSQLState())) {
                    throw new SQLException("No se pudo actualizar el entrenador: el DNI ya existe.", ex);
                }
                throw new SQLException("No se pudo actualizar el entrenador.", ex);
            }

            if (filas != 1) {
                throw new SQLException("No se pudo actualizar el entrenador " + entrenador.getIdEntrenador() + ", no existe.");
            }
        }
    }

    public Entrenador obtenerEntrenador(int idEntrenador) throws SQLException {
        String sql = "SELECT * FROM entrenador WHERE id_entrenador = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idEntrenador);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return parsearRS(rs);
                }
                return null;
            }
        }
    }

    public Entrenador buscarEntrenadorPorDNI(String dni) throws SQLException {
        String sql = "SELECT * FROM entrenador WHERE dni = ?";

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

    public List<Entrenador> listarEntrenadores() throws SQLException {
        String sql = "SELECT * FROM entrenador";
        return listar(sql);
    }

    public List<Entrenador> listarEntrenadoresPorEspecialidad(String especialidad) throws SQLException {
        String sql = "SELECT * FROM entrenador WHERE especialidad = ?";
        List<Entrenador> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setString(1, especialidad);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(parsearRS(rs));
                }
            }
        }
        return lista;
    }

    public void bajaLogicaEntrenador(int idEntrenador) throws SQLException {
        cambiarActivo(idEntrenador, false);
    }

    public void reactivacionLogicaEntrenador(int idEntrenador) throws SQLException {
        cambiarActivo(idEntrenador, true);
    }

    public void cambiarActivo(int idEntrenador, boolean activo) throws SQLException {
        Entrenador entrenador = obtenerEntrenador(idEntrenador);
        String accion = activo ? "reactivar" : "dar de baja";

        if (entrenador == null) {
            throw new SQLException("No se pudo " + accion + " el entrenador, el ID " + idEntrenador + " no existe.");
        }

        if (entrenador.isEstado() == activo) {
            return;
        }

        String sql = "UPDATE entrenador SET estado = ? WHERE id_entrenador = ?";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idEntrenador);

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo " + accion + " el entrenador, el ID = " + idEntrenador + " no existe.");
            }
        }
    }

    public static Entrenador parsearRS(ResultSet rs) throws SQLException {
        return new Entrenador(
                rs.getInt("id_entrenador"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("especialidad"),
                rs.getBoolean("estado")
        );
    }

    private List<Entrenador> listar(String sql) throws SQLException {
        List<Entrenador> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(parsearRS(rs));
                }
            }
        }
        return lista;
    }
}
