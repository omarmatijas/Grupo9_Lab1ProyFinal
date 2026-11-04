/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import com.prog.aborigym.Persistencia.AsistenciaData;
import com.prog.aborigym.Persistencia.Conexion;
import com.prog.aborigym.Persistencia.PaseData;
import com.prog.aborigym.Persistencia.SocioData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author usuario
 */
public class VistaPases extends javax.swing.JInternalFrame {

    private PaseData paseData;
    private SocioData socioData;
    private AsistenciaData asistenciaData;
    private DefaultTableModel modeloTabla;
    private DefaultTableModel modeloSocios;
    private List<Socio> sociosVisibles;
    private Socio socioSeleccionado;
    private boolean ajustandoFecha;
    private boolean ajustandoEstado;

    /**
     * Creates new form VistaPases
     */
    public VistaPases(Conexion con) {
        paseData = new PaseData(con);
        socioData = new SocioData(con);
        asistenciaData = new AsistenciaData(con);
        sociosVisibles = new ArrayList<>();
        initComponents();
        armarTablas();
        prepararFechas();
        cargarSocios(false);
        habilitarEdicion(false);
    }

    private void armarTablas() {
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Disponibles");
        modeloTabla.addColumn("Utilizadas");
        modeloTabla.addColumn("Inicio");
        modeloTabla.addColumn("Fin");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Estado");
        tablaPases.setModel(modeloTabla);
        tablaPases.getColumnModel().getColumn(0).setPreferredWidth(45);
        tablaPases.getColumnModel().getColumn(0).setMaxWidth(60);

        modeloSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloSocios.addColumn("DNI");
        modeloSocios.addColumn("Apellido");
        modeloSocios.addColumn("Nombre");
        modeloSocios.addColumn("PASE");
        tablaSocios.setModel(modeloSocios);
        tablaSocios.getColumnModel().getColumn(3).setPreferredWidth(70);
        tablaSocios.getColumnModel().getColumn(3).setMaxWidth(90);
    }

    private void prepararFechas() {
        ponerFecha(spnFechaInicio, LocalDate.now());
        ponerFecha(spnFechaFin, LocalDate.now().plusDays(30));
        spnFechaInicio.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                if (ajustandoFecha) {
                    return;
                }
                if (spnFechaInicio.getValue() instanceof java.util.Date) {
                    ponerFecha(spnFechaFin, leerFecha(spnFechaInicio).plusDays(30));
                }
                refrescarEstado();
            }
        });
        spnFechaFin.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                if (!ajustandoFecha) {
                    refrescarEstado();
                }
            }
        });
        txtEntradasDisponibles.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent evt) {
                refrescarEstado();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent evt) {
                refrescarEstado();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent evt) {
                refrescarEstado();
            }
        });
        java.awt.event.ItemListener estadoManual = new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED && !ajustandoEstado) {
                    refrescarEstado();
                }
            }
        };
        radioEstadoActivo.addItemListener(estadoManual);
        radioEstadoInactivo.addItemListener(estadoManual);
        txtEntradasUtilizadas.setEditable(false);
        refrescarEstado();
    }

    private void ponerFecha(javax.swing.JSpinner spinner, LocalDate fecha) {
        ajustandoFecha = true;
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        calendario.set(fecha.getYear(), fecha.getMonthValue() - 1, fecha.getDayOfMonth(), 0, 0, 0);
        calendario.set(java.util.Calendar.MILLISECOND, 0);
        spinner.setModel(new javax.swing.SpinnerDateModel(
                calendario.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spinner.setEditor(new javax.swing.JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        ajustandoFecha = false;
    }

    private LocalDate leerFecha(javax.swing.JSpinner spinner) {
        java.util.Date valor = (java.util.Date) spinner.getValue();
        return valor.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private void mostrarPase(Pase pase) throws SQLException {
        txtId.setText(String.valueOf(pase.getIdPase()));
        txtEntradasDisponibles.setText(String.valueOf(pase.getCantidadEntradas()));
        txtEntradasUtilizadas.setText(String.valueOf(asistenciaData.contarAsistenciasPorPase(pase.getIdPase())));
        ponerFecha(spnFechaInicio, pase.getFechaInicio());
        ponerFecha(spnFechaFin, pase.getFechaFin());
        txtPrecio.setText(String.valueOf(pase.getCosto()));
        if (pase.isEstado()) {
            radioEstadoActivo.setSelected(true);
        } else {
            radioEstadoInactivo.setSelected(true);
        }
        refrescarEstado();
    }

    private void limpiar() {
        txtId.setText("");
        txtEntradasDisponibles.setText("");
        txtEntradasUtilizadas.setText("0");
        txtPrecio.setText("");
        radioEstadoInactivo.setSelected(true);
        ponerFecha(spnFechaInicio, LocalDate.now());
        ponerFecha(spnFechaFin, LocalDate.now().plusDays(30));
        refrescarEstado();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        if (socioSeleccionado == null) {
            return;
        }

        try {
            Map<Pase, Integer> pases = paseData.listarPasesConConsumo(socioSeleccionado);
            for (Pase pase : pases.keySet()) {
                int usadas = pases.get(pase);
                modeloTabla.addRow(new Object[]{
                    pase.getIdPase(),
                    pase.getCantidadEntradas(),
                    usadas,
                    pase.getFechaInicio(),
                    pase.getFechaFin(),
                    pase.getCosto(),
                    textoEstado(pase.isEstado(), pase.getCantidadEntradas(), pase.getFechaFin())
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los pases. " + ex.getMessage());
        }
    }

    private void habilitarEdicion(boolean editando) {
        txtId.setEditable(false);
        txtEntradasDisponibles.setEditable(editando);
        txtEntradasUtilizadas.setEditable(false);
        txtPrecio.setEditable(editando);
        spnFechaInicio.setEnabled(editando);
        spnFechaFin.setEnabled(editando);
        radioEstadoActivo.setEnabled(editando);
        radioEstadoInactivo.setEnabled(editando);
        radioEstadoFinalizado.setEnabled(false);

        btnGuardar.setEnabled(editando);
        btnCancelar.setEnabled(editando);
        btnNuevo.setEnabled(!editando && socioSeleccionado != null);
        btnEditar.setEnabled(!editando && !txtId.getText().trim().isEmpty());

        tablaPases.setEnabled(!editando);
        tablaPases.setRowSelectionAllowed(!editando);
        tablaSocios.setEnabled(!editando);
        txtBuscarSocio.setEditable(!editando);
        btnBuscarSocio.setEnabled(!editando);
    }

    private Pase leerFormulario() {
        String idTexto = txtId.getText().trim();
        boolean nuevo = idTexto.isEmpty();
        String error = validarFormulario(nuevo);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error);
            return null;
        }

        int entradas = Integer.parseInt(txtEntradasDisponibles.getText().trim());
        double costo = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
        LocalDate inicio = leerFecha(spnFechaInicio);
        LocalDate fin = leerFecha(spnFechaFin);
        Pase pase;
        if (nuevo) {
            pase = new Pase(socioSeleccionado, entradas, inicio, costo, esActivoManual());
        } else {
            try {
                int id = Integer.parseInt(idTexto);
                pase = new Pase(id, socioSeleccionado, entradas, inicio, costo, esActivoManual());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.");
                return null;
            }
        }
        pase.setFechaFin(fin);
        return pase;
    }

    private String validarFormulario(boolean nuevo) {
        if (socioSeleccionado == null) {
            return "Seleccione un socio.";
        }

        String entradasTexto = txtEntradasDisponibles.getText().trim();
        if (entradasTexto.isEmpty()) {
            return "Ingrese las entradas disponibles.";
        }
        int entradas;
        try {
            entradas = Integer.parseInt(entradasTexto);
        } catch (NumberFormatException ex) {
            return "Las entradas disponibles deben ser un número entero.";
        }
        if (entradas < 0) {
            return "Las entradas no pueden ser negativas.";
        }
        if (nuevo && entradas != 8 && entradas != 12 && entradas != 20) {
            return "Un pase nuevo tiene 8, 12 o 20 entradas.";
        }

        String precioTexto = txtPrecio.getText().trim();
        if (precioTexto.isEmpty()) {
            return "Ingrese el precio abonado.";
        }
        try {
            double costo = Double.parseDouble(precioTexto.replace(',', '.'));
            if (costo < 0) {
                return "El precio no puede ser negativo.";
            }
        } catch (NumberFormatException ex) {
            return "El precio debe ser un número.";
        }

        if (!(spnFechaInicio.getValue() instanceof java.util.Date)
                || !(spnFechaFin.getValue() instanceof java.util.Date)) {
            return "Ingrese la fecha de inicio y la de fin.";
        }
        LocalDate inicio = leerFecha(spnFechaInicio);
        LocalDate fin = leerFecha(spnFechaFin);
        if (fin.isBefore(inicio)) {
            return "La fecha de fin no puede ser anterior a la de inicio.";
        }
        return null;
    }

    private String textoEstado(boolean manualActivo, int disponibles, LocalDate fin) {
        if (!manualActivo) {
            return "Inactivo";
        }
        if (inactivoAutomatico(disponibles, fin)) {
            return "Finalizado";
        }
        return "Activo";
    }

    private boolean confirmarSiSeSuperpone(Pase nuevo) throws SQLException {
        LocalDate hoy = LocalDate.now();
        for (Pase existente : paseData.listarPasesPorSocio(nuevo.getSocio().getIdSocio())) {
            if (!existente.isEstado() || existente.getCantidadEntradas() <= 0) {
                continue;
            }
            if (existente.getFechaFin() == null || existente.getFechaFin().isBefore(hoy)) {
                continue;
            }
            if (seSuperpone(nuevo.getFechaInicio(), nuevo.getFechaFin(), existente.getFechaInicio(), existente.getFechaFin())) {
                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "Este nuevo pase se superpone con otro que aún tiene entradas disponibles. ¿Creamos igualmente este nuevo pase?",
                        "Pase superpuesto",
                        JOptionPane.YES_NO_OPTION
                );
                return respuesta == JOptionPane.YES_OPTION;
            }
        }
        return true;
    }

    private boolean seSuperpone(LocalDate inicioNuevo, LocalDate finNuevo, LocalDate inicioExistente, LocalDate finExistente) {
        if (inicioNuevo == null || finNuevo == null || inicioExistente == null || finExistente == null) {
            return false;
        }
        return !inicioNuevo.isAfter(finExistente) && !inicioExistente.isAfter(finNuevo);
    }

    private boolean esActivoManual() {
        return !radioEstadoInactivo.isSelected();
    }

    private boolean inactivoAutomatico(int disponibles, LocalDate fin) {
        if (disponibles <= 0) {
            return true;
        }
        return fin != null && fin.isBefore(LocalDate.now());
    }

    private void refrescarEstado() {
        int disponibles = 1;
        String texto = txtEntradasDisponibles.getText().trim();
        if (!texto.isEmpty()) {
            try {
                disponibles = Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                disponibles = 1;
            }
        }
        LocalDate fin = null;
        if (spnFechaFin.getValue() instanceof java.util.Date) {
            fin = leerFecha(spnFechaFin);
        }
        ajustandoEstado = true;
        if (!esActivoManual()) {
            radioEstadoInactivo.setSelected(true);
        } else if (inactivoAutomatico(disponibles, fin)) {
            radioEstadoFinalizado.setSelected(true);
        } else {
            radioEstadoActivo.setSelected(true);
        }
        ajustandoEstado = false;
    }

    private void cargarSocios(boolean avisarSiVacio) {
        String texto = txtBuscarSocio.getText().trim().toLowerCase();
        sociosVisibles = new ArrayList<>();
        modeloSocios.setRowCount(0);
        socioSeleccionado = null;
        tablaSocios.clearSelection();

        try {
            Map<Integer, int[]> conPaseVigente = paseData.listarEntradasPaseActivo(LocalDate.now());
            List<Socio> conPase = new ArrayList<>();
            List<Socio> sinPase = new ArrayList<>();
            for (Socio socio : socioData.listarSocios()) {
                if (!socio.isEstado()) {
                    continue;
                }
                if (!texto.isEmpty() && !coincideSocio(socio, texto)) {
                    continue;
                }
                if (conPaseVigente.containsKey(socio.getIdSocio())) {
                    conPase.add(socio);
                } else {
                    sinPase.add(socio);
                }
            }
            sociosVisibles.addAll(conPase);
            sociosVisibles.addAll(sinPase);
            for (Socio socio : sociosVisibles) {
                String pase = "";
                int[] entradas = conPaseVigente.get(socio.getIdSocio());
                if (entradas != null) {
                    int restantes = entradas[0];
                    int total = entradas[0] + entradas[1];
                    pase = restantes + " de " + total;
                }
                modeloSocios.addRow(new Object[]{
                    socio.getDni(),
                    socio.getApellido(),
                    socio.getNombre(),
                    pase
                });
            }
            if (avisarSiVacio && sociosVisibles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay socios activos con ese dato.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los socios. " + ex.getMessage());
        }
        actualizarBotones();
    }

    private boolean coincideSocio(Socio socio, String texto) {
        return contiene(socio.getDni(), texto)
                || contiene(socio.getNombre(), texto)
                || contiene(socio.getApellido(), texto);
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    private void actualizarBotones() {
        limpiar();
        tablaPases.clearSelection();
        habilitarEdicion(false);
        cargarTabla();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupEstado = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaPases = new javax.swing.JTable();
        btnEditar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        panelDetalle = new javax.swing.JPanel();
        lblID = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblCantidadEntradas = new javax.swing.JLabel();
        txtEntradasDisponibles = new javax.swing.JTextField();
        lblCantidadEntradasUsadas = new javax.swing.JLabel();
        lblInicio = new javax.swing.JLabel();
        lblFin = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtPrecio = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        spnFechaFin = new javax.swing.JSpinner();
        spnFechaInicio = new javax.swing.JSpinner();
        txtEntradasUtilizadas = new javax.swing.JTextField();
        radioEstadoActivo = new javax.swing.JRadioButton();
        radioEstadoInactivo = new javax.swing.JRadioButton();
        radioEstadoFinalizado = new javax.swing.JRadioButton();
        panelSocio = new javax.swing.JPanel();
        lblTituloSocios = new javax.swing.JLabel();
        btnBuscarSocio = new javax.swing.JButton();
        txtBuscarSocio = new javax.swing.JTextField();
        lblTituloSocios1 = new javax.swing.JLabel();
        lblTituloSocios2 = new javax.swing.JLabel();
        jScrollPaneSocios = new javax.swing.JScrollPane();
        tablaSocios = new javax.swing.JTable();
        lblTituloPases = new javax.swing.JLabel();

        setTitle("Gestión de pases");

        tablaPases.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaPases.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaPasesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaPases);

        btnEditar.setBackground(new java.awt.Color(255, 255, 153));
        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnNuevo.setBackground(new java.awt.Color(153, 255, 153));
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        panelDetalle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblID.setText("ID");

        txtId.setEditable(false);

        lblCantidadEntradas.setText("Entradas disponibles");

        lblCantidadEntradasUsadas.setText("Entradas utilizadas");

        lblInicio.setText("Fecha de inicio");

        lblFin.setText("Fecha de finalización");

        btnGuardar.setBackground(new java.awt.Color(102, 204, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(255, 153, 153));
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        lblPrecio.setText("Precio abonado");

        txtEntradasUtilizadas.setEditable(false);

        buttonGroupEstado.add(radioEstadoActivo);
        radioEstadoActivo.setText("Activo");
        radioEstadoActivo.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        buttonGroupEstado.add(radioEstadoInactivo);
        radioEstadoInactivo.setText("Inactivo");
        radioEstadoInactivo.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        buttonGroupEstado.add(radioEstadoFinalizado);
        radioEstadoFinalizado.setText("Finalizado");
        radioEstadoFinalizado.setEnabled(false);
        radioEstadoFinalizado.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        radioEstadoFinalizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioEstadoFinalizadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDetalleLayout = new javax.swing.GroupLayout(panelDetalle);
        panelDetalle.setLayout(panelDetalleLayout);
        panelDetalleLayout.setHorizontalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDetalleLayout.createSequentialGroup()
                                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblFin)
                                    .addComponent(lblInicio)
                                    .addComponent(lblCantidadEntradasUsadas)
                                    .addComponent(lblCantidadEntradas)
                                    .addComponent(lblPrecio))
                                .addGap(18, 18, 18)
                                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(spnFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spnFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(panelDetalleLayout.createSequentialGroup()
                                            .addComponent(txtEntradasDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblID))
                                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtEntradasUtilizadas, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btnCancelar, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGuardar)
                            .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(radioEstadoInactivo, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(radioEstadoFinalizado, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(panelDetalleLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(radioEstadoActivo)))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        panelDetalleLayout.setVerticalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCantidadEntradas)
                    .addComponent(txtEntradasDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblID)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCantidadEntradasUsadas))
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(txtEntradasUtilizadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblInicio)
                            .addComponent(spnFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFin)
                            .addComponent(spnFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrecio)))
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addComponent(radioEstadoActivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioEstadoInactivo)
                        .addGap(3, 3, 3)
                        .addComponent(radioEstadoFinalizado)))
                .addGap(8, 8, 8)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnGuardar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelSocio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTituloSocios.setText("Buscar socio");

        btnBuscarSocio.setBackground(new java.awt.Color(204, 153, 255));
        btnBuscarSocio.setText("Filtrar");
        btnBuscarSocio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarSocioActionPerformed(evt);
            }
        });

        txtBuscarSocio.setToolTipText("DNI, nombre o apellido");
        txtBuscarSocio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarSocioActionPerformed(evt);
            }
        });

        lblTituloSocios1.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblTituloSocios1.setForeground(new java.awt.Color(102, 102, 102));
        lblTituloSocios1.setText("Busca por nombre, apellido o DNI");

        lblTituloSocios2.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblTituloSocios2.setForeground(new java.awt.Color(102, 102, 102));
        lblTituloSocios2.setText("Solo socios activos");

        tablaSocios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaSocios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaSociosMouseClicked(evt);
            }
        });
        jScrollPaneSocios.setViewportView(tablaSocios);

        javax.swing.GroupLayout panelSocioLayout = new javax.swing.GroupLayout(panelSocio);
        panelSocio.setLayout(panelSocioLayout);
        panelSocioLayout.setHorizontalGroup(
            panelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSocioLayout.createSequentialGroup()
                .addGroup(panelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloSocios)
                    .addGroup(panelSocioLayout.createSequentialGroup()
                        .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelSocioLayout.createSequentialGroup()
                                .addComponent(txtBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblTituloSocios1)
                            .addComponent(lblTituloSocios2))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelSocioLayout.setVerticalGroup(
            panelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSocioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloSocios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSocioLayout.createSequentialGroup()
                        .addGroup(panelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarSocio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloSocios1)
                        .addGap(1, 1, 1)
                        .addComponent(lblTituloSocios2))
                    .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTituloPases.setText("Pases");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTituloPases)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnNuevo))
                            .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(panelSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTituloPases)
                    .addComponent(btnEditar)
                    .addComponent(btnNuevo))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaPasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaPasesMouseClicked
        if (!tablaPases.isEnabled()) {
            return;
        }

        mostrarPaseDeFila(tablaPases.getSelectedRow());
    }//GEN-LAST:event_tablaPasesMouseClicked

    private void mostrarPaseDeFila(int fila) {
        if (fila < 0 || fila >= modeloTabla.getRowCount()) {
            return;
        }

        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

        try {
            Pase pase = paseData.obtenerPase(id);
            if (pase != null) {
                mostrarPase(pase);
                habilitarEdicion(false);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar el pase: " + ex.getMessage());
        }
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un pase de la tabla.");
            return;
        }
        habilitarEdicion(true);
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiar();
        radioEstadoActivo.setSelected(true);
        refrescarEstado();
        tablaPases.clearSelection();
        habilitarEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void txtBuscarSocioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarSocioActionPerformed
        cargarSocios(true);
    }//GEN-LAST:event_txtBuscarSocioActionPerformed

    private void btnBuscarSocioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarSocioActionPerformed
        cargarSocios(true);
    }//GEN-LAST:event_btnBuscarSocioActionPerformed

    private void tablaSociosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaSociosMouseClicked
        int fila = tablaSocios.getSelectedRow();
        if (fila < 0 || fila >= sociosVisibles.size()) {
            return;
        }
        socioSeleccionado = sociosVisibles.get(fila);
        actualizarBotones();
        if (modeloTabla.getRowCount() > 0 && tablaPases.isEnabled()) {
            tablaPases.setRowSelectionInterval(0, 0);
            tablaPases.scrollRectToVisible(tablaPases.getCellRect(0, 0, true));
            mostrarPaseDeFila(0);
        }
    }//GEN-LAST:event_tablaSociosMouseClicked

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiar();
        tablaPases.clearSelection();
        habilitarEdicion(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        Pase pase = leerFormulario();
        if (pase == null) {
            return;
        }

        try {
            if (pase.getIdPase() == 0) {
                if (!confirmarSiSeSuperpone(pase)) {
                    return;
                }
                paseData.guardarPase(pase);
                JOptionPane.showMessageDialog(this, "Pase guardado con ID " + pase.getIdPase() + ".");
            } else {
                paseData.actualizarPase(pase);
                paseData.cambiarActivo(pase.getIdPase(), pase.isEstado());
                JOptionPane.showMessageDialog(this, "Pase actualizado.");
            }
            cargarTabla();
            limpiar();
            habilitarEdicion(false);
            tablaPases.clearSelection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void radioEstadoFinalizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioEstadoFinalizadoActionPerformed
    }//GEN-LAST:event_radioEstadoFinalizadoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarSocio;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.ButtonGroup buttonGroupEstado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneSocios;
    private javax.swing.JLabel lblCantidadEntradas;
    private javax.swing.JLabel lblCantidadEntradasUsadas;
    private javax.swing.JLabel lblFin;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblInicio;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblTituloPases;
    private javax.swing.JLabel lblTituloSocios;
    private javax.swing.JLabel lblTituloSocios1;
    private javax.swing.JLabel lblTituloSocios2;
    private javax.swing.JPanel panelDetalle;
    private javax.swing.JPanel panelSocio;
    private javax.swing.JRadioButton radioEstadoActivo;
    private javax.swing.JRadioButton radioEstadoFinalizado;
    private javax.swing.JRadioButton radioEstadoInactivo;
    private javax.swing.JSpinner spnFechaFin;
    private javax.swing.JSpinner spnFechaInicio;
    private javax.swing.JTable tablaPases;
    private javax.swing.JTable tablaSocios;
    private javax.swing.JTextField txtBuscarSocio;
    private javax.swing.JTextField txtEntradasDisponibles;
    private javax.swing.JTextField txtEntradasUtilizadas;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
