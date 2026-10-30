package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Filtro de clases compartido por Gestión de clases y Marcar asistencia.
 *
 * @author Grupo9
 */
public class FiltroClases {

    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    public static final String MENSAJE_HORARIO = "El horario de búsqueda debe tener el formato HH:mm, por ejemplo 18:30.";

    private FiltroClases() {
    }

    public static LocalTime leerHorario(String texto) throws DateTimeParseException {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(texto.trim(), HORA);
    }

    public static List<ClaseGym> filtrar(List<ClaseGym> clases, String disciplina, Entrenador entrenador,
            LocalTime horario, boolean soloActivas, boolean soloInactivas) {
        String texto = disciplina == null ? "" : disciplina.trim().toLowerCase();
        List<ClaseGym> filtradas = new ArrayList<>();

        for (ClaseGym c : clases) {
            if (soloActivas && !c.isEstado()) {
                continue;
            }
            if (soloInactivas && c.isEstado()) {
                continue;
            }
            if (!texto.isEmpty()) {
                String disciplinaClase = c.getDisciplina() == null ? "" : c.getDisciplina();
                if (!disciplinaClase.toLowerCase().contains(texto)) {
                    continue;
                }
            }
            if (entrenador != null && (c.getEntrenador() == null
                    || c.getEntrenador().getIdEntrenador() != entrenador.getIdEntrenador())) {
                continue;
            }
            if (horario != null) {
                LocalTime horarioClase = c.getHorario();
                if (horarioClase == null || horarioClase.getHour() != horario.getHour()
                        || horarioClase.getMinute() != horario.getMinute()) {
                    continue;
                }
            }
            filtradas.add(c);
        }

        ordenar(filtradas);
        return filtradas;
    }

    public static void ordenar(List<ClaseGym> clases) {
        Collections.sort(clases, new Comparator<ClaseGym>() {
            @Override
            public int compare(ClaseGym a, ClaseGym b) {
                LocalTime horaA = a.getHorario();
                LocalTime horaB = b.getHorario();
                if (horaA == null && horaB != null) {
                    return 1;
                }
                if (horaA != null && horaB == null) {
                    return -1;
                }
                if (horaA != null && horaB != null) {
                    int porHora = horaA.compareTo(horaB);
                    if (porHora != 0) {
                        return porHora;
                    }
                }
                String disciplinaA = a.getDisciplina() == null ? "" : a.getDisciplina();
                String disciplinaB = b.getDisciplina() == null ? "" : b.getDisciplina();
                return disciplinaA.compareToIgnoreCase(disciplinaB);
            }
        });
    }
}
