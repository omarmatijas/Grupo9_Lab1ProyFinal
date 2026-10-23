package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Asistencia;
import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import com.prog.aborigym.Persistencia.Conexion;
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
    private static final String PORT = "3306";
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
            
        } catch(Exception e){
            
        } finally {
            conexion.cerrarConexion();
        }
    }
    
    public static void pruebaDatos(Conexion conexion) throws SQLException {
        
        SocioData socioData = new SocioData(conexion);
        
        // Socios
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
        
        // Entrenador
        Entrenador juan = new Entrenador("25333444", "Juan", "Gómez", "Spinning", true);
        juan.setIdEntrenador(1);
        System.out.println("Entrenador: " + juan);
        
        // Clase
        ClaseGym spinning = new ClaseGym("Spinning", juan, LocalTime.of(18, 0), 20, true);
        spinning.setIdClase(1);
        System.out.println("Clase: " + spinning);
        System.out.println("  Entrenador de la clase: " + spinning.getEntrenador().getNombre());
        
        // Pase
        Pase pase = new Pase(omar, 8, LocalDate.of(2026, 10, 1), 15000.0, true);
        pase.setIdPase(1);
        System.out.println("Pase: " + pase);
        System.out.println("  Inicio: " + pase.getFechaInicio() + " | Fin: " + pase.getFechaFin());
        System.out.println("  Titular: " + pase.getSocio());
        
        // Asistencia
        Asistencia asistencia = new Asistencia(omar, spinning, pase, LocalDate.now());
        asistencia.setIdAsistencia(1);
        System.out.println("Asistencia: " + asistencia);
        
        // Ana consume una entrada
        System.out.println("Entradas antes: " + pase.getCantidadEntradas());
        pase.registrarAsistencia();
        System.out.println("Entradas después: " + pase.getCantidadEntradas());
        
        // Relaciones
        System.out.println("El socio " + asistencia.getSocio().getNombre()
                + " asistió a " + asistencia.getClase().getDisciplina() 
                + " con " + asistencia.getClase().getEntrenador().getNombre());
    }
}
