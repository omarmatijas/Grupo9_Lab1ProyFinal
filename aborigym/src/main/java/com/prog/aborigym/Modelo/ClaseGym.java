package com.prog.aborigym.Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Grupo9
 */
public class ClaseGym {

    private int idClase;
    private String disciplina;
    private Entrenador entrenador;
    private LocalTime horario;
    private int capacidad;
    private boolean estado;

    // No es necesario el constructor vacío, por ahora public ClaseGym() { } 
    public ClaseGym(String disciplina, Entrenador entrenador, LocalTime horario, int capacidad, boolean estado) {
        this.disciplina = disciplina;
        this.entrenador = entrenador;
        this.horario = horario;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public ClaseGym(int idClase, String disciplina, Entrenador entrenador, LocalTime horario, int capacidad, boolean estado) {
        this.idClase = idClase;
        this.disciplina = disciplina;
        this.entrenador = entrenador;
        this.horario = horario;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Asistencia marcarAsistencia(Socio socio, LocalDate fecha, int asistenciasTomadas, Pase pase) {
        if (pase == null || pase.getCantidadEntradas() <= 0) {
            return null;
        }
        if (asistenciasTomadas >= capacidad) {
            return null;
        }
        pase.registrarAsistencia();
        return new Asistencia(socio, this, pase, fecha);
    }

    @Override
    public String toString() {
        return "Disciplina " + disciplina + " - Horario " + horario;
    }
}
