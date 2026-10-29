package com.prog.aborigym.Persistencia;

import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

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
        return new Pase(
                rs.getInt("id_pase"),
                socio,
                rs.getInt("cantidad_entradas"),
                rs.getObject("fecha_inicio", LocalDate.class),
                rs.getDouble("costo"),
                rs.getBoolean("estado")
        );
    }
}
