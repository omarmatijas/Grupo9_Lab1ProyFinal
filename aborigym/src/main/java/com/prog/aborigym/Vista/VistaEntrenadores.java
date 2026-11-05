/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Disciplinas;
import com.prog.aborigym.Modelo.Entrenador;
import com.prog.aborigym.Persistencia.Conexion;
import com.prog.aborigym.Persistencia.EntrenadorData;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Gurpo9
 */
public class VistaEntrenadores extends javax.swing.JInternalFrame {

    private EntrenadorData entrenadorData;
    private DefaultTableModel modeloTabla;

    /**
     * Creates new form VistaEntrenadores
     */
    public VistaEntrenadores(Conexion con) {
        entrenadorData = new EntrenadorData(con);
        initComponents();
        agruparRadios();
        cargarEspecialidades();
        cargarDisciplinaBuscar();
        armarTabla();
        cargarTabla();
        limpiar();
        habilitarEdicion(false);
    }

    private void agruparRadios() {
        ButtonGroup estado = new ButtonGroup();
        estado.add(radioTodos);
        estado.add(radioActivos);
        estado.add(radioInactivos);
    }

    private void cargarEspecialidades() {
        cboEspecialidad.removeAllItems();
        for (String disciplina : Disciplinas.LISTA) {
            cboEspecialidad.addItem(disciplina);
        }
    }

    private void cargarDisciplinaBuscar() {
        cboDisciplinaBuscar.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Todas");
                }
                return this;
            }
        });
        cboDisciplinaBuscar.removeAllItems();
        cboDisciplinaBuscar.addItem(null);
        for (String disciplina : Disciplinas.LISTA) {
            cboDisciplinaBuscar.addItem(disciplina);
        }
        cboDisciplinaBuscar.setSelectedIndex(0);
    }

    private void armarTabla() {
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("DNI");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Especialidad");
        modeloTabla.addColumn("Estado");
        tablaEntrenadores.setModel(modeloTabla);
    }

    private void cargarTabla() {
        actualizarTabla(false);
    }

    private void actualizarTabla(boolean avisarSiVacio) {
        String disciplina = (String) cboDisciplinaBuscar.getSelectedItem();
        String texto = txtBuscarEntrenador.getText().trim().toLowerCase();
        boolean soloActivos = radioActivos.isSelected();
        boolean soloInactivos = radioInactivos.isSelected();

        modeloTabla.setRowCount(0);
        int visibles = 0;
        try {
            for (Entrenador e : entrenadorData.listarEntrenadores()) {
                if (!pasaFiltro(e, disciplina, texto, soloActivos, soloInactivos)) {
                    continue;
                }
                modeloTabla.addRow(new Object[]{
                    e.getIdEntrenador(),
                    e.getDni(),
                    e.getApellido(),
                    e.getNombre(),
                    e.getEspecialidad(),
                    e.isEstado() ? "Activo" : "Inactivo"
                });
                visibles++;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores: " + ex.getMessage());
            return;
        }

        tablaEntrenadores.clearSelection();
        boolean hayFiltro = disciplina != null || !texto.isEmpty() || soloActivos || soloInactivos;
        if (avisarSiVacio && visibles == 0 && hayFiltro) {
            JOptionPane.showMessageDialog(this, "No hay entrenadores con esos filtros.");
        }
    }

    private boolean pasaFiltro(Entrenador e, String disciplina, String texto, boolean soloActivos, boolean soloInactivos) {
        if (disciplina != null && (e.getEspecialidad() == null || !e.getEspecialidad().equalsIgnoreCase(disciplina))) {
            return false;
        }
        if (soloActivos && !e.isEstado()) {
            return false;
        }
        if (soloInactivos && e.isEstado()) {
            return false;
        }
        if (!texto.isEmpty() && !coincideEntrenador(e, texto)) {
            return false;
        }
        return true;
    }

    private boolean coincideEntrenador(Entrenador e, String texto) {
        return contiene(e.getDni(), texto)
                || contiene(e.getNombre(), texto)
                || contiene(e.getApellido(), texto);
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }
    
     private void mostrarEntrenador(Entrenador e) {
        txtId.setText(String.valueOf(e.getIdEntrenador()));
        txtDni.setText(e.getDni());
        txtNombre.setText(e.getNombre());
        txtApellido.setText(e.getApellido());
        cboEspecialidad.setSelectedItem(e.getEspecialidad());
        chkActivo.setSelected(e.isEstado());
    }

    private void limpiar() {
        txtId.setText("");
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        cboEspecialidad.setSelectedIndex(-1); 
        chkActivo.setSelected(true);
        tablaEntrenadores.clearSelection();
    }

    private void habilitarEdicion(boolean editando) {
        txtId.setEditable(false);
        txtDni.setEditable(editando);
        txtNombre.setEditable(editando);
        txtApellido.setEditable(editando);
        cboEspecialidad.setEditable(false);
        cboEspecialidad.setEnabled(editando);
        chkActivo.setEnabled(editando);

        btnGuardar.setEnabled(editando);
        btnCancelar.setEnabled(editando);

        btnNuevo.setEnabled(!editando);
        btnEditar.setEnabled(!editando && !txtId.getText().trim().isEmpty());
        btnEliminar.setEnabled(!editando && !txtId.getText().trim().isEmpty());

        tablaEntrenadores.setEnabled(!editando);
        tablaEntrenadores.setRowSelectionAllowed(!editando);

        cboDisciplinaBuscar.setEnabled(!editando);
        txtBuscarEntrenador.setEditable(!editando);
        radioTodos.setEnabled(!editando);
        radioActivos.setEnabled(!editando);
        radioInactivos.setEnabled(!editando);
        btnBuscar.setEnabled(!editando);
    }
        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */ 
        @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnNuevo = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaEntrenadores = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblDni = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        cboEspecialidad = new javax.swing.JComboBox<>();
        txtId = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblEspecialidades = new javax.swing.JLabel();
        chkActivo = new javax.swing.JCheckBox();
        txtNombre = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblID = new javax.swing.JLabel();
        panelBuscar = new javax.swing.JPanel();
        lblDisciplina1 = new javax.swing.JLabel();
        cboDisciplinaBuscar = new javax.swing.JComboBox<>();
        radioTodos = new javax.swing.JRadioButton();
        radioActivos = new javax.swing.JRadioButton();
        radioInactivos = new javax.swing.JRadioButton();
        txtBuscarEntrenador = new javax.swing.JTextField();
        lblTituloSocios1 = new javax.swing.JLabel();
        lblEntrenador = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();

        setBackground(new java.awt.Color(204, 204, 204));
        setTitle("Gestión de entrenadores");
        setToolTipText("");

        btnNuevo.setBackground(new java.awt.Color(153, 255, 153));
        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(255, 0, 0));
        btnEliminar.setText("Eliminar");
        btnEliminar.setBorderPainted(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        tablaEntrenadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "DNI", "Nombre", "Apellido", "Especialidades", "Estado"
            }
        ));
        tablaEntrenadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaEntrenadoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaEntrenadores);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDni.setText("DNI");

        lblApellido.setText("Apellido");

        cboEspecialidad.setEditable(true);

        lblNombre.setText("Nombre");

        lblEspecialidades.setText("Especialidad");

        chkActivo.setText("Activo");
        chkActivo.setEnabled(false);
        chkActivo.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        chkActivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActivoActionPerformed(evt);
            }
        });

        btnGuardar.setBackground(new java.awt.Color(102, 204, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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

        lblID.setText("ID");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(86, Short.MAX_VALUE)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDni)
                            .addComponent(lblNombre)
                            .addComponent(lblApellido)
                            .addComponent(lblEspecialidades)
                            .addComponent(lblID))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtApellido, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                    .addComponent(txtNombre)
                                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboEspecialidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(chkActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDni)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblApellido)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEspecialidades))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkActivo)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBuscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDisciplina1.setText("Disciplina");

        radioTodos.setSelected(true);
        radioTodos.setText("Todos");

        radioActivos.setText("Activos");

        radioInactivos.setText("Inactivos");

        txtBuscarEntrenador.setToolTipText("DNI, nombre o apellido");
        txtBuscarEntrenador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarEntrenadorActionPerformed(evt);
            }
        });

        lblTituloSocios1.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblTituloSocios1.setForeground(new java.awt.Color(102, 102, 102));
        lblTituloSocios1.setText("Busca por nombre, apellido o DNI");

        lblEntrenador.setText("Entrenador");

        btnBuscar.setBackground(new java.awt.Color(153, 102, 255));
        btnBuscar.setText("Buscar ");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBuscarLayout = new javax.swing.GroupLayout(panelBuscar);
        panelBuscar.setLayout(panelBuscarLayout);
        panelBuscarLayout.setHorizontalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblEntrenador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBuscarEntrenador, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTituloSocios1)
                            .addComponent(cboDisciplinaBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(radioActivos)
                            .addComponent(radioInactivos))))
                .addGap(10, 10, 10))
            .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBuscarLayout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addComponent(lblDisciplina1)
                    .addContainerGap(289, Short.MAX_VALUE)))
        );
        panelBuscarLayout.setVerticalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioTodos)
                    .addComponent(cboDisciplinaBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarEntrenador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEntrenador)
                    .addComponent(radioActivos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTituloSocios1)
                    .addComponent(radioInactivos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscar)
                .addContainerGap())
            .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBuscarLayout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addComponent(lblDisciplina1)
                    .addContainerGap(89, Short.MAX_VALUE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                            .addComponent(btnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(275, 275, 275))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnNuevo)
                                    .addComponent(btnEditar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String dni = txtDni.getText().trim();
        if (!dni.matches("\\d{7,8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe tener 7 u 8 números.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y el apellido son obligatorios.");
            return;
        }
        if (nombre.length() > 50 || apellido.length() > 50) {
            JOptionPane.showMessageDialog(this, "El nombre y el apellido no pueden superar los 50 caracteres.");
            return;
        }
        String especialidad = (String) cboEspecialidad.getSelectedItem();
        if (especialidad == null) {
            JOptionPane.showMessageDialog(this, "Elegí una especialidad.");
            return;
        }
        try {
            if (txtId.getText().isEmpty()) {
                Entrenador nuevo = new Entrenador(dni, nombre, apellido, especialidad, chkActivo.isSelected());
                entrenadorData.guardarEntrenador(nuevo);
                JOptionPane.showMessageDialog(this, "Entrenador guardado con ID " + nuevo.getIdEntrenador());
            } else {
                int id = Integer.parseInt(txtId.getText());
                Entrenador e = new Entrenador(id, dni, nombre, apellido, especialidad, chkActivo.isSelected());
                entrenadorData.actualizarEntrenador(e);
                entrenadorData.cambiarActivo(id, chkActivo.isSelected());
                JOptionPane.showMessageDialog(this, "Entrenador actualizado.");
            }
            cargarTabla();
            limpiar();
            habilitarEdicion(false);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        String especialidadFiltro = (String) cboDisciplinaBuscar.getSelectedItem();
        limpiar();
        if (especialidadFiltro != null) {
            cboEspecialidad.setSelectedItem(especialidadFiltro);
        }
        chkActivo.setSelected(true);
        tablaEntrenadores.clearSelection();
        habilitarEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
      if (txtId.getText().isEmpty()){
          JOptionPane.showMessageDialog(this, "Selecciona un entrrenador de la tabla.");
          return;
      }
      int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar definitivamente al entrenador?", "confirmar", JOptionPane.YES_NO_OPTION);
      if (resp != JOptionPane.YES_OPTION) {
      return;
    }
      try {
          entrenadorData.borrarEntrenador(Integer.parseInt(txtId.getText()));
          JOptionPane.showMessageDialog(this, "Entrenador eliminado.");
          cargarTabla();
          limpiar();
          habilitarEdicion(false);
      }catch (SQLException ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage());
      }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void chkActivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkActivoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        actualizarTabla(true);
        limpiar();
        habilitarEdicion(false);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void tablaEntrenadoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaEntrenadoresMouseClicked
        if (!tablaEntrenadores.isEnabled()) {
            return;
        }
        int fila = tablaEntrenadores.getSelectedRow();
        if (fila >= 0) {
            int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
            try {
                Entrenador e = entrenadorData.obtenerEntrenador(id);
                if (e != null) {
                    mostrarEntrenador(e);
                    habilitarEdicion(false);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar el entrenador: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_tablaEntrenadoresMouseClicked

    private void txtBuscarEntrenadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarEntrenadorActionPerformed
        actualizarTabla(true);
        limpiar();
        habilitarEdicion(false);
    }//GEN-LAST:event_txtBuscarEntrenadorActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiar();
        tablaEntrenadores.clearSelection();
        habilitarEdicion(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un entrenador de la tabla.");
            return;
        }
        habilitarEdicion(true);
    }//GEN-LAST:event_btnEditarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cboDisciplinaBuscar;
    private javax.swing.JComboBox<String> cboEspecialidad;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblDisciplina1;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEntrenador;
    private javax.swing.JLabel lblEspecialidades;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTituloSocios1;
    private javax.swing.JPanel panelBuscar;
    private javax.swing.JRadioButton radioActivos;
    private javax.swing.JRadioButton radioInactivos;
    private javax.swing.JRadioButton radioTodos;
    private javax.swing.JTable tablaEntrenadores;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscarEntrenador;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
