/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.prog.aborigym;

import com.prog.aborigym.Modelo.Asistencia;
import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author usuario
 */
public class Aborigym {

    public static void main(String[] args) {
        
        // Socio
        Socio ana = new Socio("30111222", "Ana", "Pérez", 28, "ana@mail.com", "2664000000", true);
        ana.setIdSocio(1);
        System.out.println("Socio: " + ana);
        
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
        Pase pase = new Pase(ana, 8, LocalDate.of(2026, 10, 1), 15000.0, true);
        pase.setIdPase(1);
        System.out.println("Pase: " + pase);
        System.out.println("  Inicio: " + pase.getFechaInicio() + " | Fin: " + pase.getFechaFin());
        System.out.println("  Titular: " + pase.getSocio());
        
        // Asistencia
        Asistencia asistencia = new Asistencia(ana, spinning, pase, LocalDate.now());
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
