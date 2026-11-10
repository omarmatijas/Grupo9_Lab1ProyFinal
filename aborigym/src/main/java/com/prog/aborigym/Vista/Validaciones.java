package com.prog.aborigym.Vista;

import java.awt.Component;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * Clase para unificar, centralizar las validaciones y evitar repetir el código en las diferentes vistas
 *
 * @author Grupo9
 */
public final class Validaciones {

    private Validaciones() {
    }

    public static boolean revisarDni(Component ventana, JTextField campo) {
        String dni = campo.getText().trim();
        if (dni.isEmpty()) {
            return true;
        }
        for (int i = 0; i < dni.length(); i++) {
            if (!Character.isDigit(dni.charAt(i))) {
                JOptionPane.showMessageDialog(ventana, "El DNI solo admite números.");
                campo.requestFocus();
                return false;
            }
        }
        if (dni.length() > 8 || Long.parseLong(dni) > 99999999L) {
            JOptionPane.showMessageDialog(ventana, "El DNI no puede ser mayor a 99999999.");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean revisarLetras(Component ventana, JTextField campo, String etiqueta) {
        String texto = campo.getText().trim();
        if (texto.length() > 40) {
            JOptionPane.showMessageDialog(ventana, etiqueta + " no puede superar los 40 caracteres.");
            campo.requestFocus();
            return false;
        }
        if (!texto.matches("[\\p{L} ]*")) {
            JOptionPane.showMessageDialog(ventana, etiqueta + " solo admite letras.");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean revisarBusqueda(Component ventana, JTextField campo) {
        String texto = campo.getText().trim();
        if (texto.length() > 40) {
            JOptionPane.showMessageDialog(ventana, "La búsqueda no puede superar los 40 caracteres.");
            campo.requestFocus();
            return false;
        }
        if (!texto.matches("[\\p{L}\\p{N} ]*")) {
            JOptionPane.showMessageDialog(ventana, "La búsqueda solo admite letras y números.");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean revisarCorreo(Component ventana, JTextField campo) {
        String correo = campo.getText().trim();
        if (correo.isEmpty()) {
            return true;
        }
        if (correo.length() > 100 || !correo.matches("[A-Za-z0-9._+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            JOptionPane.showMessageDialog(ventana, "El correo debe tener la forma nombre@dominio.com y no superar los 100 caracteres.");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean revisarTelefono(Component ventana, JTextField campo) {
        String telefono = campo.getText().trim();
        if (telefono.isEmpty()) {
            return true;
        }
        if (!telefono.matches("\\d{1,30}")) {
            JOptionPane.showMessageDialog(ventana, "El teléfono solo admite números, hasta 30 dígitos.");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean revisarEdad(Component ventana, JTextField campo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty() || !texto.matches("\\d+")) {
            JOptionPane.showMessageDialog(ventana, "La edad debe ser un número entero.");
            campo.requestFocus();
            return false;
        }
        int edad = Integer.parseInt(texto);
        if (edad <= 10) {
            JOptionPane.showMessageDialog(ventana, "La edad debe ser mayor a 10.");
            campo.requestFocus();
            return false;
        }
        if (edad > 120) {
            JOptionPane.showMessageDialog(ventana, "La edad debe ser realista... :P");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean revisarPrecio(Component ventana, JTextField campo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Ingrese el precio abonado.");
            campo.requestFocus();
            return false;
        }
        try {
            double costo = Double.parseDouble(texto.replace(',', '.'));
            if (costo < 0) {
                JOptionPane.showMessageDialog(ventana, "El precio no puede ser negativo.");
                campo.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ventana, "El precio debe ser un número.");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    public static LocalTime leerHorario(Component ventana, JTextField campo, boolean obligatorio) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            if (obligatorio) {
                JOptionPane.showMessageDialog(ventana, "Ingrese el horario.");
                campo.requestFocus();
            }
            return null;
        }
        try {
            return FiltroClases.leerHorario(texto);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(ventana, FiltroClases.MENSAJE_HORARIO);
            campo.requestFocus();
            return null;
        }
    }

    public static boolean revisarFecha(Component ventana, JSpinner spinner, String etiqueta) {
        try {
            spinner.commitEdit();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(ventana, etiqueta + " debe tener el formato dd/MM/yyyy.");
            return false;
        }
        if (!(spinner.getValue() instanceof java.util.Date)) {
            JOptionPane.showMessageDialog(ventana, "Ingrese " + etiqueta.toLowerCase() + ".");
            return false;
        }
        return true;
    }
}
