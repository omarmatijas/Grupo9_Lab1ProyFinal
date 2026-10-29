package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import com.prog.aborigym.Persistencia.ClaseGymData;
import com.prog.aborigym.Persistencia.Conexion;
import com.prog.aborigym.Persistencia.EntrenadorData;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Grupo9
 */
public class VistaClases extends javax.swing.JInternalFrame {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private ClaseGymData claseGymData;
    private EntrenadorData entrenadorData;
    private DefaultTableModel modeloTabla;

    public VistaClases(Conexion con) {
        claseGymData = new ClaseGymData(con);
        entrenadorData = new EntrenadorData(con);
        initComponents();
        radioTodos.setSelected(true);
        armarTabla();
        cargarEntrenadores();
        cargarTabla();
        limpiar();
        habilitarEdicion(false);
    }

    private void armarTabla() {
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Disciplina");
        modeloTabla.addColumn("Entrenador");
        modeloTabla.addColumn("Horario");
        modeloTabla.addColumn("Capacidad");
        modeloTabla.addColumn("Estado");
        tablaClases.setModel(modeloTabla);

        tablaClases.getColumnModel().getColumn(0).setPreferredWidth(45);
        tablaClases.getColumnModel().getColumn(0).setMaxWidth(60);
        tablaClases.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaClases.getColumnModel().getColumn(2).setPreferredWidth(170);
        tablaClases.getColumnModel().getColumn(3).setPreferredWidth(75);
        tablaClases.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaClases.getColumnModel().getColumn(5).setPreferredWidth(75);
    }

    private void cargarEntrenadores() {
        cboEntrenador.removeAllItems();
        cboEntrenadorBuscar.removeAllItems();
        cboEntrenadorBuscar.addItem(null);
        cboEntrenadorBuscar.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Todos");
                }
                return this;
            }
        });

        try {
            for (Entrenador e : entrenadorData.listarEntrenadores()) {
                cboEntrenador.addItem(e);
                cboEntrenadorBuscar.addItem(e);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores. " + ex.getMessage());
        }

        cboEntrenador.setSelectedIndex(-1);
        cboEntrenadorBuscar.setSelectedIndex(0);
    }

    private void mostrarClase(ClaseGym c) {
        txtId.setText(String.valueOf(c.getIdClase()));
        txtDisciplina.setText(c.getDisciplina());
        seleccionarEntrenador(c.getEntrenador());
        if (c.getHorario() != null) {
            txtHorario.setText(c.getHorario().format(HORA));
        } else {
            txtHorario.setText("");
        }
        txtCapacidad.setText(String.valueOf(c.getCapacidad()));
        chkActivo.setSelected(c.isEstado());
    }

    private void seleccionarEntrenador(Entrenador entrenador) {
        if (entrenador == null) {
            cboEntrenador.setSelectedIndex(-1);
            return;
        }

        for (int i = 0; i < cboEntrenador.getItemCount(); i++) {
            Entrenador item = cboEntrenador.getItemAt(i);
            if (item.getIdEntrenador() == entrenador.getIdEntrenador()) {
                cboEntrenador.setSelectedIndex(i);
                return;
            }
        }

        cboEntrenador.setSelectedIndex(-1);
    }

    public void limpiar() {
        txtId.setText("");
        txtDisciplina.setText("");
        cboEntrenador.setSelectedIndex(-1);
        txtHorario.setText("");
        txtCapacidad.setText("");
        chkActivo.setSelected(false);
    }

    public void cargarTabla() {
        try {
            llenarTabla(claseGymData.listarClasesGym());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las clases. " + ex.getMessage());
        }
    }

    private void llenarTabla(List<ClaseGym> clases) {
        modeloTabla.setRowCount(0);

        for (ClaseGym c : clases) {
            String entrenador = "";
            if (c.getEntrenador() != null) {
                entrenador = c.getEntrenador().toString();
            }

            String horario = "";
            if (c.getHorario() != null) {
                horario = c.getHorario().format(HORA);
            }

            modeloTabla.addRow(new Object[]{
                c.getIdClase(),
                c.getDisciplina(),
                entrenador,
                horario,
                c.getCapacidad(),
                c.isEstado() ? "Activo" : "Inactivo"
            });
        }
    }

    private void habilitarEdicion(boolean editando) {
        txtId.setEditable(false);
        txtDisciplina.setEditable(editando);
        txtHorario.setEditable(editando);
        txtCapacidad.setEditable(editando);
        cboEntrenador.setEnabled(editando);
        chkActivo.setEnabled(editando);

        btnGuardar.setEnabled(editando);
        btnCancelar.setEnabled(editando);

        btnNuevo.setEnabled(!editando);
        btnEditar.setEnabled(!editando && !txtId.getText().trim().isEmpty());

        tablaClases.setEnabled(!editando);
        tablaClases.setRowSelectionAllowed(!editando);

        txtDisciplinaBuscar.setEditable(!editando);
        txtHorarioBuscar.setEditable(!editando);
        cboEntrenadorBuscar.setEnabled(!editando);
        radioTodos.setEnabled(!editando);
        radioActivos.setEnabled(!editando);
        radioInactivos.setEnabled(!editando);
        radioTodos1.setEnabled(!editando);
        radioActivos1.setEnabled(!editando);
        radioInactivos1.setEnabled(!editando);
        btnBuscar.setEnabled(!editando);
    }

    private List<ClaseGym> filtrar(List<ClaseGym> clases) {
        String disciplina = txtDisciplinaBuscar.getText().trim();
        String horarioTexto = txtHorarioBuscar.getText().trim();
        Entrenador entrenador = (Entrenador) cboEntrenadorBuscar.getSelectedItem();

        LocalTime horario = null;
        if (!horarioTexto.isEmpty()) {
            try {
                horario = LocalTime.parse(horarioTexto, HORA);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "El horario de búsqueda debe tener el formato HH:mm, por ejemplo 18:30.");
                return null;
            }
        }

        List<ClaseGym> filtradas = new ArrayList<>();
        for (ClaseGym c : clases) {
            if (!disciplina.isEmpty()) {
                String disciplinaClase = c.getDisciplina() == null ? "" : c.getDisciplina();
                if (!disciplinaClase.toLowerCase().contains(disciplina.toLowerCase())) {
                    continue;
                }
            }

            if (entrenador != null) {
                if (c.getEntrenador() == null || c.getEntrenador().getIdEntrenador() != entrenador.getIdEntrenador()) {
                    continue;
                }
            }

            if (horario != null) {
                LocalTime horarioClase = c.getHorario();
                if (horarioClase == null || horarioClase.getHour() != horario.getHour() || horarioClase.getMinute() != horario.getMinute()) {
                    continue;
                }
            }

            if (radioActivos.isSelected() && !c.isEstado()) {
                continue;
            }
            if (radioInactivos.isSelected() && c.isEstado()) {
                continue;
            }

            filtradas.add(c);
        }
        return filtradas;
    }

    private ClaseGym leerFormulario() {
        String disciplina = txtDisciplina.getText().trim();
        if (disciplina.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la disciplina.");
            return null;
        }
        if (disciplina.length() > 50) {
            JOptionPane.showMessageDialog(this, "La disciplina puede tener hasta 50 caracteres.");
            return null;
        }

        Entrenador entrenador = (Entrenador) cboEntrenador.getSelectedItem();
        if (entrenador == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un entrenador.");
            return null;
        }

        LocalTime horario;
        try {
            horario = LocalTime.parse(txtHorario.getText().trim(), HORA);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "El horario debe tener el formato HH:mm, por ejemplo 18:30.");
            return null;
        }

        int capacidad;
        try {
            capacidad = Integer.parseInt(txtCapacidad.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La capacidad debe ser un número entero.");
            return null;
        }
        if (capacidad <= 0) {
            JOptionPane.showMessageDialog(this, "La capacidad debe ser mayor a cero.");
            return null;
        }

        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            return new ClaseGym(disciplina, entrenador, horario, capacidad, chkActivo.isSelected());
        }

        try {
            int id = Integer.parseInt(idTexto);
            return new ClaseGym(id, disciplina, entrenador, horario, capacidad, chkActivo.isSelected());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupActivoFiltro = new javax.swing.ButtonGroup();
        buttonGroupVacantesFiltro = new javax.swing.ButtonGroup();
        btnNuevo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClases = new javax.swing.JTable();
        panelDetalle = new javax.swing.JPanel();
        lblID = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblDisciplina = new javax.swing.JLabel();
        txtDisciplina = new javax.swing.JTextField();
        lblEntrenador = new javax.swing.JLabel();
        cboEntrenador = new javax.swing.JComboBox<>();
        lblHorario = new javax.swing.JLabel();
        txtHorario = new javax.swing.JTextField();
        lblCapacidad = new javax.swing.JLabel();
        txtCapacidad = new javax.swing.JTextField();
        chkActivo = new javax.swing.JCheckBox();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelBuscar = new javax.swing.JPanel();
        lblDisciplina1 = new javax.swing.JLabel();
        lblEntrenador1 = new javax.swing.JLabel();
        lblHorario1 = new javax.swing.JLabel();
        txtHorarioBuscar = new javax.swing.JTextField();
        cboEntrenadorBuscar = new javax.swing.JComboBox<>();
        txtDisciplinaBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        radioTodos = new javax.swing.JRadioButton();
        radioActivos = new javax.swing.JRadioButton();
        radioInactivos = new javax.swing.JRadioButton();
        radioInactivos1 = new javax.swing.JRadioButton();
        radioActivos1 = new javax.swing.JRadioButton();
        radioTodos1 = new javax.swing.JRadioButton();
        btnEditar = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión de clases");
        setPreferredSize(new java.awt.Dimension(950, 400));

        btnNuevo.setBackground(new java.awt.Color(153, 255, 153));
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        tablaClases.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tablaClases.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaClasesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaClases);

        panelDetalle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblID.setText("ID");

        txtId.setEditable(false);

        lblDisciplina.setText("Disciplina");

        lblEntrenador.setText("Entrenador");

        lblHorario.setText("Horario");

        txtHorario.setToolTipText("HH:mm");

        lblCapacidad.setText("Capacidad");

        chkActivo.setText("Activo");
        chkActivo.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

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

        javax.swing.GroupLayout panelDetalleLayout = new javax.swing.GroupLayout(panelDetalle);
        panelDetalle.setLayout(panelDetalleLayout);
        panelDetalleLayout.setHorizontalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(chkActivo))
                    .addGroup(panelDetalleLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelDetalleLayout.createSequentialGroup()
                                .addComponent(btnCancelar)
                                .addGap(27, 27, 27)
                                .addComponent(btnGuardar))
                            .addGroup(panelDetalleLayout.createSequentialGroup()
                                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCapacidad, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblHorario, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblEntrenador, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDisciplina, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblID, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboEntrenador, 0, 260, Short.MAX_VALUE)
                                    .addComponent(txtHorario, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDisciplina)
                                    .addComponent(txtCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        panelDetalleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCapacidad, txtHorario});

        panelDetalleLayout.setVerticalGroup(
            panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblID)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDisciplina)
                    .addComponent(txtDisciplina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEntrenador)
                    .addComponent(cboEntrenador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHorario)
                    .addComponent(txtHorario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCapacidad)
                    .addComponent(txtCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkActivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnGuardar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBuscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDisciplina1.setText("Disciplina");

        lblEntrenador1.setText("Entrenador");

        lblHorario1.setText("Horario");

        txtHorarioBuscar.setToolTipText("HH:mm");

        cboEntrenadorBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboEntrenadorBuscarActionPerformed(evt);
            }
        });

        btnBuscar.setBackground(new java.awt.Color(204, 153, 255));
        btnBuscar.setText("Filtrar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        buttonGroupActivoFiltro.add(radioTodos);
        radioTodos.setSelected(true);
        radioTodos.setText("Todos");

        buttonGroupActivoFiltro.add(radioActivos);
        radioActivos.setText("Activas");

        buttonGroupActivoFiltro.add(radioInactivos);
        radioInactivos.setText("Inactivas");

        buttonGroupVacantesFiltro.add(radioInactivos1);
        radioInactivos1.setText("Completos");

        buttonGroupVacantesFiltro.add(radioActivos1);
        radioActivos1.setText("Con vacantes");

        buttonGroupVacantesFiltro.add(radioTodos1);
        radioTodos1.setSelected(true);
        radioTodos1.setText("Todos");

        javax.swing.GroupLayout panelBuscarLayout = new javax.swing.GroupLayout(panelBuscar);
        panelBuscar.setLayout(panelBuscarLayout);
        panelBuscarLayout.setHorizontalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                .addContainerGap(361, Short.MAX_VALUE)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(radioActivos)
                            .addComponent(radioInactivos))
                        .addGap(37, 37, 37)
                        .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioTodos1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(radioActivos1)
                            .addComponent(radioInactivos1))))
                .addGap(21, 21, 21))
            .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBuscarLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblHorario1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblEntrenador1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblDisciplina1, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cboEntrenadorBuscar, 0, 260, Short.MAX_VALUE)
                        .addComponent(txtHorarioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDisciplinaBuscar))
                    .addContainerGap(203, Short.MAX_VALUE)))
        );
        panelBuscarLayout.setVerticalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                        .addComponent(radioTodos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioActivos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioInactivos))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                        .addComponent(radioTodos1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioActivos1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioInactivos1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscar)
                .addContainerGap())
            .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBuscarLayout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDisciplina1)
                        .addComponent(txtDisciplinaBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblEntrenador1)
                        .addComponent(cboEntrenadorBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHorario1)
                        .addComponent(txtHorarioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(33, Short.MAX_VALUE)))
        );

        btnEditar.setBackground(new java.awt.Color(255, 255, 153));
        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEditar)
                                .addGap(18, 18, 18)
                                .addComponent(btnNuevo)))))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEditar)
                            .addComponent(btnNuevo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        ClaseGym clase = leerFormulario();
        if (clase == null) {
            return;
        }

        try {
            if (clase.getIdClase() == 0) {
                claseGymData.guardarClaseGym(clase);
                JOptionPane.showMessageDialog(this, "Clase guardada con ID " + clase.getIdClase() + ".");
            } else {
                claseGymData.actualizarClaseGym(clase);
                claseGymData.cambiarActivo(clase.getIdClase(), clase.isEstado());
                JOptionPane.showMessageDialog(this, "Clase actualizada.");
            }
            cargarTabla();
            limpiar();
            habilitarEdicion(false);
            tablaClases.clearSelection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String disciplina = txtDisciplinaBuscar.getText().trim();
        String horarioTexto = txtHorarioBuscar.getText().trim();
        boolean hayFiltro = !disciplina.isEmpty() || !horarioTexto.isEmpty()
                || cboEntrenadorBuscar.getSelectedItem() != null
                || radioActivos.isSelected() || radioInactivos.isSelected();

        try {
            List<ClaseGym> filtradas = filtrar(claseGymData.listarClasesGym());
            if (filtradas == null) {
                return;
            }

            llenarTabla(filtradas);
            tablaClases.clearSelection();
            limpiar();
            habilitarEdicion(false);

            if (filtradas.isEmpty() && hayFiltro) {
                JOptionPane.showMessageDialog(this, "No hay clases con esos filtros.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo buscar. " + ex.getMessage());
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiar();
        chkActivo.setSelected(true);
        tablaClases.clearSelection();
        habilitarEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void tablaClasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClasesMouseClicked
        if (!tablaClases.isEnabled()) {
            return;
        }

        int fila = tablaClases.getSelectedRow();

        if (fila >= 0) {
            int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

            try {
                ClaseGym c = claseGymData.obtenerClaseGym(id);
                if (c != null) {
                    mostrarClase(c);
                    habilitarEdicion(false);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la clase: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_tablaClasesMouseClicked

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiar();
        tablaClases.clearSelection();
        habilitarEdicion(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void cboEntrenadorBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEntrenadorBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboEntrenadorBuscarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase de la tabla.");
            return;
        }
        habilitarEdicion(true);
    }//GEN-LAST:event_btnEditarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.ButtonGroup buttonGroupActivoFiltro;
    private javax.swing.ButtonGroup buttonGroupVacantesFiltro;
    private javax.swing.JComboBox<Entrenador> cboEntrenador;
    private javax.swing.JComboBox<Entrenador> cboEntrenadorBuscar;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCapacidad;
    private javax.swing.JLabel lblDisciplina;
    private javax.swing.JLabel lblDisciplina1;
    private javax.swing.JLabel lblEntrenador;
    private javax.swing.JLabel lblEntrenador1;
    private javax.swing.JLabel lblHorario;
    private javax.swing.JLabel lblHorario1;
    private javax.swing.JLabel lblID;
    private javax.swing.JPanel panelBuscar;
    private javax.swing.JPanel panelDetalle;
    private javax.swing.JRadioButton radioActivos;
    private javax.swing.JRadioButton radioActivos1;
    private javax.swing.JRadioButton radioInactivos;
    private javax.swing.JRadioButton radioInactivos1;
    private javax.swing.JRadioButton radioTodos;
    private javax.swing.JRadioButton radioTodos1;
    private javax.swing.JTable tablaClases;
    private javax.swing.JTextField txtCapacidad;
    private javax.swing.JTextField txtDisciplina;
    private javax.swing.JTextField txtDisciplinaBuscar;
    private javax.swing.JTextField txtHorario;
    private javax.swing.JTextField txtHorarioBuscar;
    private javax.swing.JTextField txtId;
    // End of variables declaration//GEN-END:variables
}
