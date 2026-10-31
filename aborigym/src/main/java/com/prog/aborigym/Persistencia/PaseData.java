package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Grupo9
 */
public class PaseData {

    private final Conexion conexion;
    private final SocioData socioData;

    public PaseData(Conexion conexion) {
        this.conexion = conexion;
        this.socioData = new SocioData(conexion);
    }

    public Pase obtenerPase(int idPase) throws SQLException {
        String sql = "SELECT * FROM pase WHERE id_pase = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idPase);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Socio socio = socioData.obtenerSocio(rs.getInt("id_socio"));
                    return parsearRS(rs, socio);
                }
                return null;
            }
        }
    }

    public List<Integer> listarSociosConPaseVigente(LocalDate fecha) throws SQLException {
        String sql = "SELECT DISTINCT id_socio FROM pase WHERE estado = ? AND cantidad_entradas > 0 "
                + "AND fecha_inicio <= ? AND fecha_fin >= ?";
        List<Integer> socios = new ArrayList<>();

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setObject(2, fecha);
            ps.setObject(3, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    socios.add(rs.getInt("id_socio"));
                }
            }
        }
        return socios;
    }

    public Pase obtenerPaseActivoConEntradas(int idSocio, LocalDate fecha) throws SQLException {
        String sql = "SELECT * FROM pase WHERE id_socio = ? AND estado = ? AND cantidad_entradas > 0 "
                + "AND fecha_inicio <= ? AND fecha_fin >= ? ORDER BY fecha_fin LIMIT 1";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            ps.setBoolean(2, true);
            ps.setObject(3, fecha);
            ps.setObject(4, fecha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Socio socio = socioData.obtenerSocio(idSocio);
                    return parsearRS(rs, socio);
                }
                return null;
            }
        }
    }

    public List<Pase> listarPasesPorSocio(int idSocio) throws SQLException {
        String sql = "SELECT * FROM pase WHERE id_socio = ? ORDER BY fecha_fin DESC, id_pase DESC";
        List<Pase> pases = new ArrayList<>();
        Socio socio = socioData.obtenerSocio(idSocio);

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, idSocio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pases.add(parsearRS(rs, socio));
                }
            }
        }
        return pases;
    }

    public int guardarPase(Pase pase) throws SQLException {
        validarAlta(pase);
        String sql = "INSERT INTO pase(id_socio, cantidad_entradas, fecha_inicio, fecha_fin, costo, estado) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pase.getSocio().getIdSocio());
            ps.setInt(2, pase.getCantidadEntradas());
            ps.setObject(3, pase.getFechaInicio());
            ps.setObject(4, pase.getFechaFin());
            ps.setDouble(5, pase.getCosto());
            ps.setBoolean(6, pase.isEstado());

            int filas;
            try {
                filas = ps.executeUpdate();
            } catch (SQLException ex) {
                if (Conexion.SQLSTATE_VIOLACION_INTEGRIDAD.equals(ex.getSQLState())) {
                    throw new SQLException("No se pudo crear el pase: el socio no existe.", ex);
                }
                throw new SQLException("No se pudo crear el pase.", ex);
            }

            if (filas != 1) {
                throw new SQLException("No se pudo crear el pase.");
            }

            int idPase = 0;
            boolean hayId = false;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idPase = rs.getInt(1);
                    hayId = !rs.wasNull() && idPase != 0;
                }
            } catch (SQLException ex) {
                throw new SQLException("No se pudo obtener el ID generado del pase.", ex);
            }

            if (!hayId) {
                throw new SQLException("No se pudo obtener el ID generado del pase.");
            }

            pase.setIdPase(idPase);
            return idPase;
        }
    }

    public void actualizarPase(Pase pase) throws SQLException {
        if (pase.getCantidadEntradas() < 0) {
            throw new SQLException("Las entradas no pueden ser negativas.");
        }
        validarFechas(pase);

        String sql = "UPDATE pase SET cantidad_entradas = ?, fecha_inicio = ?, fecha_fin = ?, costo = ? WHERE id_pase = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, pase.getCantidadEntradas());
            ps.setObject(2, pase.getFechaInicio());
            ps.setObject(3, pase.getFechaFin());
            ps.setDouble(4, pase.getCosto());
            ps.setInt(5, pase.getIdPase());

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo actualizar el pase " + pase.getIdPase() + ", no existe.");
            }
        }
    }

    public void cambiarActivo(int idPase, boolean activo) throws SQLException {
        Pase pase = obtenerPase(idPase);
        String accion = activo ? "reactivar" : "dar de baja";

        if (pase == null) {
            throw new SQLException("No se pudo " + accion + " el pase, el ID " + idPase + " no existe.");
        }

        if (pase.isEstado() == activo) {
            return;
        }

        String sql = "UPDATE pase SET estado = ? WHERE id_pase = ?";
        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idPase);

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo " + accion + " el pase, el ID " + idPase + " no existe.");
            }
        }
    }

    private void validarAlta(Pase pase) throws SQLException {
        int entradas = pase.getCantidadEntradas();
        if (entradas != 8 && entradas != 12 && entradas != 20) {
            throw new SQLException("El pase debe tener 8, 12 o 20 entradas.");
        }
        if (pase.getSocio() == null || pase.getSocio().getIdSocio() == 0) {
            throw new SQLException("El pase debe pertenecer a un socio.");
        }
        validarFechas(pase);
    }

    private void validarFechas(Pase pase) throws SQLException {
        if (pase.getFechaInicio() == null || pase.getFechaFin() == null) {
            throw new SQLException("El pase debe tener fecha de inicio y de fin.");
        }
        if (pase.getFechaFin().isBefore(pase.getFechaInicio())) {
            throw new SQLException("La fecha de fin no puede ser anterior a la de inicio.");
        }
    }

    public void actualizarCantidadEntradas(Pase pase) throws SQLException {
        String sql = "UPDATE pase SET cantidad_entradas = ? WHERE id_pase = ?";

        try (PreparedStatement ps = conexion.buscarConexion().prepareStatement(sql)) {
            ps.setInt(1, pase.getCantidadEntradas());
            ps.setInt(2, pase.getIdPase());

            int filas = ps.executeUpdate();
            if (filas != 1) {
                throw new SQLException("No se pudo actualizar las entradas del pase " + pase.getIdPase() + ".");
            }
        }
    }

    public static Pase parsearRS(ResultSet rs, Socio socio) throws SQLException {
        Pase pase = new Pase(
                rs.getInt("id_pase"),
                socio,
                rs.getInt("cantidad_entradas"),
                rs.getObject("fecha_inicio", LocalDate.class),
                rs.getDouble("costo"),
                rs.getBoolean("estado")
        );
        LocalDate fin = rs.getObject("fecha_fin", LocalDate.class);
        if (fin != null) {
            pase.setFechaFin(fin);
        }
        return pase;
    }
}
