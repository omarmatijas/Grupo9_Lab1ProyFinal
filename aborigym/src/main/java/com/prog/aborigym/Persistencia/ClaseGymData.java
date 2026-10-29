package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Grupo9
 */
public class ClaseGymData {

    private final Conexion conexion;
    private final EntrenadorData entrenadorData;

    public ClaseGymData(Conexion conexion) {
        this.conexion = conexion;
        this.entrenadorData = new EntrenadorData(conexion);
    }

    public int guardarClaseGym(ClaseGym clase) throws SQLException {
        String sql = "INSERT INTO clasegym(disciplina, id_entrenador, horario, capacidad, estado) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, clase.getDisciplina());
            ps.setInt(2, idEntrenador(clase));
            ps.setObject(3, clase.getHorario());
            ps.setInt(4, clase.getCapacidad());
            ps.setBoolean(5, clase.isEstado());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (SQLException ex) {
                if (Conexion.SQLSTATE_VIOLACION_INTEGRIDAD.equals(ex.getSQLState())) {
                    throw new SQLException("No se pudo crear la clase: el entrenador no existe.", ex);
                }
                throw new SQLException("No se pudo crear la clase.", ex);
            }

            if (filas != 1) {
                throw new SQLException("No se pudo crear la clase.");
            }

            int idClase = 0;
            boolean hayId = false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idClase = rs.getInt(1);
                    hayId = !rs.wasNull() && idClase != 0;
                }
            } catch (SQLException ex) {
                throw new SQLException("No se pudo obtener el ID generado de la clase.", ex);
            }

            if (!hayId) {
                throw new SQLException("No se pudo obtener el ID generado de la clase.");
            }

            clase.setIdClase(idClase);
            return idClase;
        }
    }

    public void actualizarClaseGym(ClaseGym clase) throws SQLException {
        String sql = "UPDATE clasegym SET disciplina = ?, id_entrenador = ?, horario = ?, capacidad = ? WHERE id_clase = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setString(1, clase.getDisciplina());
            ps.setInt(2, idEntrenador(clase));
            ps.setObject(3, clase.getHorario());
            ps.setInt(4, clase.getCapacidad());
            ps.setInt(5, clase.getIdClase());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (SQLException ex) {
                if (Conexion.SQLSTATE_VIOLACION_INTEGRIDAD.equals(ex.getSQLState())) {
                    throw new SQLException("No se pudo actualizar la clase: el entrenador no existe.", ex);
                }
                throw new SQLException("No se pudo actualizar la clase.", ex);
            }

            if (filas != 1) {
                throw new SQLException("No se pudo actualizar la clase " + clase.getIdClase() + ", no existe.");
            }
        }
    }

    public ClaseGym obtenerClaseGym(int idClase) throws SQLException {
        String sql = "SELECT * FROM clasegym WHERE id_clase = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idClase);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return armarClase(rs);
                }
                return null;
            }
        }
    }

    public List<ClaseGym> listarClasesGym() throws SQLException {
        String sql = "SELECT * FROM clasegym";
        return listar(sql, null);
    }

    public List<ClaseGym> listarClasesGymPorDisciplina(String disciplina) throws SQLException {
        String sql = "SELECT * FROM clasegym WHERE disciplina = ?";
        return listar(sql, disciplina);
    }

    public List<ClaseGym> listarClasesGymPorEntrenador(int idEntrenador) throws SQLException {
        String sql = "SELECT * FROM clasegym WHERE id_entrenador = ?";
        List<ClaseGym> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idEntrenador);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(armarClase(rs));
                }
            }
        }
        return lista;
    }

    public void bajaLogicaClaseGym(int idClase) throws SQLException {
        cambiarActivo(idClase, false);
    }

    public void reactivacionLogicaClaseGym(int idClase) throws SQLException {
        cambiarActivo(idClase, true);
    }

    public void cambiarActivo(int idClase, boolean activo) throws SQLException {
        ClaseGym clase = obtenerClaseGym(idClase);
        String accion = activo ? "reactivar" : "dar de baja";

        if (clase == null) {
            throw new SQLException("No se pudo " + accion + " la clase, el ID " + idClase + " no existe.");
        }

        if (clase.isEstado() == activo) {
            return;
        }

        String sql = "UPDATE clasegym SET estado = ? WHERE id_clase = ?";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idClase);

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo " + accion + " la clase, el ID = " + idClase + " no existe.");
            }
        }
    }

    public int contarSociosInscriptos(int idClase) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT id_socio) FROM asistencia WHERE id_clase = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idClase);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    public static ClaseGym parsearRS(ResultSet rs, Entrenador entrenador) throws SQLException {
        return new ClaseGym(
                rs.getInt("id_clase"),
                rs.getString("disciplina"),
                entrenador,
                rs.getObject("horario", LocalTime.class),
                rs.getInt("capacidad"),
                rs.getBoolean("estado")
        );
    }

    private ClaseGym armarClase(ResultSet rs) throws SQLException {
        Entrenador entrenador = entrenadorData.obtenerEntrenador(rs.getInt("id_entrenador"));
        return parsearRS(rs, entrenador);
    }

    private int idEntrenador(ClaseGym clase) throws SQLException {
        if (clase.getEntrenador() == null || clase.getEntrenador().getIdEntrenador() == 0) {
            throw new SQLException("No se pudo guardar la clase: falta el entrenador.");
        }
        return clase.getEntrenador().getIdEntrenador();
    }

    private List<ClaseGym> listar(String sql, String disciplina) throws SQLException {
        List<ClaseGym> lista = new ArrayList<>();

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            if (disciplina != null) {
                ps.setString(1, disciplina);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(armarClase(rs));
                }
            }
        }
        return lista;
    }
}
