package com.prog.aborigym.Modelo;

import java.time.LocalDate;

/**
 *
 * @author Grupo9
 */
public class Asistencia {

    private int idAsistencia;
    private Socio socio;
    private ClaseGym clase;
    private Pase pase;
    private LocalDate fechaAsistencia;

    // No es necesario el constructor vacío, por ahora public Asistencia() { } 
    public Asistencia(Socio socio, ClaseGym clase, Pase pase, LocalDate fechaAsistencia) {
        this.socio = socio;
        this.clase = clase;
        this.pase = pase;
        this.fechaAsistencia = fechaAsistencia;
    }

    public Asistencia(int idAsistencia, Socio socio, ClaseGym clase, Pase pase, LocalDate fechaAsistencia) {
        this.idAsistencia = idAsistencia;
        this.socio = socio;
        this.clase = clase;
        this.pase = pase;
        this.fechaAsistencia = fechaAsistencia;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public ClaseGym getClase() {
        return clase;
    }

    public void setClase(ClaseGym clase) {
        this.clase = clase;
    }

    public Pase getPase() {
        return pase;
    }

    public void setPase(Pase pase) {
        this.pase = pase;
    }

    public LocalDate getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(LocalDate fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    @Override
    public String toString() {
        return "Fecha Asistencia: " + fechaAsistencia + " - Socio: " + socio + " en " + clase;
    }
}
