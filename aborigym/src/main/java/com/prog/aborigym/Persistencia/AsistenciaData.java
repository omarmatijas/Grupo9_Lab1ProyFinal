package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.Asistencia;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 *
 * @author Grupo9
 */
public class AsistenciaData {

    private final Conexion conexion;

    public AsistenciaData(Conexion conexion) {
        this.conexion = conexion;
    }

    public int guardarAsistencia(Asistencia asistencia) throws SQLException {
        String sql = "INSERT INTO asistencia(id_socio, id_clase, id_pase, fecha_asistencia) VALUES (?,?,?,?)";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idSocio(asistencia));
            ps.setInt(2, idClase(asistencia));
            ps.setInt(3, idPase(asistencia));
            ps.setObject(4, asistencia.getFechaAsistencia());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (SQLException ex) {
                if (Conexion.SQLSTATE_VIOLACION_INTEGRIDAD.equals(ex.getSQLState())) {
                    throw new SQLException("El socio ya tiene asistencia en esa clase ese día.", ex);
                }
                throw new SQLException("No se pudo registrar la asistencia.", ex);
            }

            if (filas != 1) {
                throw new SQLException("No se pudo registrar la asistencia.");
            }

            int idAsistencia = 0;
            boolean hayId = false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idAsistencia = rs.getInt(1);
                    hayId = !rs.wasNull() && idAsistencia != 0;
                }
            } catch (SQLException ex) {
                throw new SQLException("No se pudo obtener el ID generado de la asistencia.", ex);
            }

            if (!hayId) {
                throw new SQLException("No se pudo obtener el ID generado de la asistencia.");
            }

            asistencia.setIdAsistencia(idAsistencia);
            return idAsistencia;
        }
    }

    public int contarAsistencias(int idClase, LocalDate fecha) throws SQLException {
        String sql = "SELECT COUNT(*) FROM asistencia WHERE id_clase = ? AND fecha_asistencia = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idClase);
            ps.setObject(2, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    public boolean existeAsistencia(int idSocio, int idClase, LocalDate fecha) throws SQLException {
        String sql = "SELECT id_asistencia FROM asistencia WHERE id_socio = ? AND id_clase = ? AND fecha_asistencia = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            ps.setInt(2, idClase);
            ps.setObject(3, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int idSocio(Asistencia asistencia) throws SQLException {
        if (asistencia.getSocio() == null || asistencia.getSocio().getIdSocio() == 0) {
            throw new SQLException("No se pudo registrar la asistencia: falta el socio.");
        }
        return asistencia.getSocio().getIdSocio();
    }

    private int idClase(Asistencia asistencia) throws SQLException {
        if (asistencia.getClase() == null || asistencia.getClase().getIdClase() == 0) {
            throw new SQLException("No se pudo registrar la asistencia: falta la clase.");
        }
        return asistencia.getClase().getIdClase();
    }

    private int idPase(Asistencia asistencia) throws SQLException {
        if (asistencia.getPase() == null || asistencia.getPase().getIdPase() == 0) {
            throw new SQLException("No se pudo registrar la asistencia: falta el pase.");
        }
        return asistencia.getPase().getIdPase();
    }
}
