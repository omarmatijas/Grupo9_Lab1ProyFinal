package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.Asistencia;
import com.prog.aborigym.Modelo.Entrenador;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Integer, Integer> contarAsistenciaClases(LocalDate fecha, String disciplina, Entrenador entrenador,
            LocalTime horario, boolean soloActivas, boolean soloInactivas) throws SQLException {
        String sql = "SELECT c.id_clase, COUNT(a.id_asistencia) AS cantidad "
                + "FROM clasegym c LEFT JOIN asistencia a ON a.id_clase = c.id_clase AND a.fecha_asistencia = ?";
        String texto = disciplina == null ? "" : disciplina.trim();
        String where = "";

        if (!texto.isEmpty()) {
            where = where + " AND c.disciplina LIKE ?";
        }
        if (entrenador != null) {
            where = where + " AND c.id_entrenador = ?";
        }
        if (horario != null) {
            where = where + " AND HOUR(c.horario) = ? AND MINUTE(c.horario) = ?";
        }
        if (soloActivas) {
            where = where + " AND c.estado = ?";
        } else if (soloInactivas) {
            where = where + " AND c.estado = ?";
        }
        if (!where.isEmpty()) {
            sql = sql + " WHERE" + where.substring(4);
        }
        sql = sql + " GROUP BY c.id_clase";

        Map<Integer, Integer> cantidades = new HashMap<>();
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            int indice = 1;
            ps.setObject(indice, fecha);
            indice++;
            if (!texto.isEmpty()) {
                ps.setString(indice, "%" + texto + "%");
                indice++;
            }
            if (entrenador != null) {
                ps.setInt(indice, entrenador.getIdEntrenador());
                indice++;
            }
            if (horario != null) {
                ps.setInt(indice, horario.getHour());
                indice++;
                ps.setInt(indice, horario.getMinute());
                indice++;
            }
            if (soloActivas) {
                ps.setBoolean(indice, true);
            } else if (soloInactivas) {
                ps.setBoolean(indice, false);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cantidades.put(rs.getInt("id_clase"), rs.getInt("cantidad"));
                }
            }
        }
        return cantidades;
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

    public List<Integer> listarSociosConAsistencia(int idClase, LocalDate fecha) throws SQLException {
        String sql = "SELECT id_socio FROM asistencia WHERE id_clase = ? AND fecha_asistencia = ?";
        List<Integer> socios = new ArrayList<>();

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idClase);
            ps.setObject(2, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    socios.add(rs.getInt("id_socio"));
                }
            }
        }
        return socios;
    }

    public int obtenerIdPaseDeAsistencia(int idSocio, int idClase, LocalDate fecha) throws SQLException {
        String sql = "SELECT id_pase FROM asistencia WHERE id_socio = ? AND id_clase = ? AND fecha_asistencia = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            ps.setInt(2, idClase);
            ps.setObject(3, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_pase");
                }
                return 0;
            }
        }
    }

    public void eliminarAsistencia(int idSocio, int idClase, LocalDate fecha) throws SQLException {
        String sql = "DELETE FROM asistencia WHERE id_socio = ? AND id_clase = ? AND fecha_asistencia = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            ps.setInt(2, idClase);
            ps.setObject(3, fecha);

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo anular la asistencia.");
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
