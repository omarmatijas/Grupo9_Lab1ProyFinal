package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Asistencia;
import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import com.prog.aborigym.Persistencia.ClaseGymData;
import com.prog.aborigym.Persistencia.Conexion;
import com.prog.aborigym.Persistencia.EntrenadorData;
import com.prog.aborigym.Persistencia.SocioData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Grupo9
 */
public class Aborigym {

    private static final String HOST = "localhost";
    private static final String PORT = "3307";
    private static final String DB_NAME = "grupo9_aborigym";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws SQLException {

        String url = "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DB_NAME;
        Conexion conexion = new Conexion(
                url,
                USER,
                PASSWORD
        );
        try {
            if (conexion.buscarConexion() == null) {
                throw new SQLException("Error de conexion: verifique usuario, contraseña, nombre de bbdd, host y puerto (tal vez alternar entre 3306 y 3307)");
            }

            pruebaDatos(conexion);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            conexion.cerrarConexion();
        }
    }

    public static void pruebaDatos(Conexion conexion) throws SQLException {
        Socio omar = pruebasSocios(conexion);
        Entrenador juan = pruebaEntrenador(conexion);
        ClaseGym spinning = pruebaClase(conexion, juan);
        Pase pase = pruebaPase(omar);
        pruebaAsistencia(omar, spinning, pase);
    }

    public static Socio pruebasSocios(Conexion conexion) throws SQLException {
        SocioData socioData = new SocioData(conexion);

        Socio omar = new Socio("30334915", "Omar", "Matijas", 43, "omarmatijas@gmail.com", "2665103445", true);
        int idOmar = socioData.guardarSocio(omar);
        omar.setIdSocio(idOmar);
        System.out.println("Socio: " + omar);

        Socio gonzalo = new Socio("35915707", "Gonzalo", "Martin", 35, "gonzamartin91@gmail.com", "2657242483", true);
        int idGonzalo = socioData.guardarSocio(gonzalo);
        gonzalo.setIdSocio(idGonzalo);
        System.out.println("Socio: " + gonzalo);

        Socio franco = new Socio("34421846", "Franco", "Magallanes", 36, "magallanesfranco694@gmail.com", "2657621806", true);
        int idFranco = socioData.guardarSocio(franco);
        franco.setIdSocio(idFranco);
        System.out.println("Socio: " + franco);

        Socio jessica = new Socio("34877066", "Jessica", "Auriol", 37, "jessica.auriol@gmail.com", "2665039381", true);
        int idJessica = socioData.guardarSocio(jessica);
        jessica.setIdSocio(idJessica);
        System.out.println("Socio: " + jessica);

        return omar;
    }

    public static Entrenador pruebaEntrenador(Conexion conexion) throws SQLException {
        EntrenadorData entrenadorData = new EntrenadorData(conexion);
        Entrenador juan = new Entrenador("25333444", "Juan", "Gómez", "Spinning", true);
        int idJuan = entrenadorData.guardarEntrenador(juan);
        juan.setIdEntrenador(idJuan);
        System.out.println("Entrenador: " + juan);
        System.out.println("Entrenador por DNI: " + entrenadorData.buscarEntrenadorPorDNI("25333444"));
        return juan;
    }

    public static ClaseGym pruebaClase(Conexion conexion, Entrenador entrenador) throws SQLException {
        ClaseGymData claseGymData = new ClaseGymData(conexion);
        ClaseGym spinning = new ClaseGym("Spinning", entrenador, LocalTime.of(18, 0), 20, true);
        int idSpinning = claseGymData.guardarClaseGym(spinning);
        spinning.setIdClase(idSpinning);
        System.out.println("Clase: " + spinning);
        System.out.println("  Entrenador de la clase: " + spinning.getEntrenador().getNombre());

        System.out.println("Clases de Spinning:");
        for (ClaseGym clase : claseGymData.listarClasesGymPorDisciplina("Spinning")) {
            System.out.println("  " + clase + " - " + clase.getEntrenador());
        }
        return spinning;
    }

    public static Pase pruebaPase(Socio socio) {
        Pase pase = new Pase(socio, 8, LocalDate.of(2026, 10, 1), 15000.0, true);
        pase.setIdPase(1);
        System.out.println("Pase: " + pase);
        System.out.println("  Inicio: " + pase.getFechaInicio() + " | Fin: " + pase.getFechaFin());
        System.out.println("  Titular: " + pase.getSocio());
        return pase;
    }

    public static void pruebaAsistencia(Socio socio, ClaseGym clase, Pase pase) {
        Asistencia asistencia = new Asistencia(socio, clase, pase, LocalDate.now());
        asistencia.setIdAsistencia(1);
        System.out.println("Asistencia: " + asistencia);

        System.out.println("Entradas antes: " + pase.getCantidadEntradas());
        pase.registrarAsistencia();
        System.out.println("Entradas después: " + pase.getCantidadEntradas());

        System.out.println("El socio " + asistencia.getSocio().getNombre()
                + " asistió a " + asistencia.getClase().getDisciplina()
                + " con " + asistencia.getClase().getEntrenador().getNombre());
    }
}
