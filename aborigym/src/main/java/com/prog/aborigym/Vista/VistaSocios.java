package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Socio;
import com.prog.aborigym.Persistencia.Conexion;
import com.prog.aborigym.Persistencia.SocioData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Grupo9
 */
public class VistaSocios extends javax.swing.JInternalFrame {

    private SocioData socioData;
    private DefaultTableModel modeloTabla;

    public VistaSocios(Conexion con) {
        socioData = new SocioData(con);
        initComponents();
        armarTabla();
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
        modeloTabla.addColumn("DNI");
        modeloTabla.addColumn("Apellido");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Edad");
        modeloTabla.addColumn("Correo");
        modeloTabla.addColumn("Teléfono");
        tablaSocios.setModel(modeloTabla);
        
        tablaSocios.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaSocios.getColumnModel().getColumn(1).setPreferredWidth(75);
        tablaSocios.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaSocios.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaSocios.getColumnModel().getColumn(4).setPreferredWidth(40);
        tablaSocios.getColumnModel().getColumn(5).setPreferredWidth(150);
        tablaSocios.getColumnModel().getColumn(6).setPreferredWidth(80);
    }

    private void mostrarSocio(Socio s) {
        txtId.setText(String.valueOf(s.getIdSocio()));
        txtDni.setText(s.getDni());
        txtNombre.setText(s.getNombre());
        txtApellido.setText(s.getApellido());
        txtEdad.setText(String.valueOf(s.getEdad()));
        txtCorreo.setText(s.getCorreo());
        txtTelefono.setText(s.getTelefono());
        chkActivo.setSelected(s.isEstado());
    }

    public void limpiar() {
        txtId.setText("");
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEdad.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        chkActivo.setSelected(false);
    }

    private String textoONull(String texto) {
        String t = texto.trim();
        return t.isEmpty() ? null : t;
    }

    public void cargarTabla() {
        try {
            llenarTabla(socioData.listarSocios());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las clases. " + ex.getMessage());
        }
    }
    
    private void habilitarEdicion(boolean editable) {
        txtId.setEditable(false);
        txtDni.setEditable(editable);
        txtNombre.setEditable(editable);
        txtApellido.setEditable(editable);
        txtEdad.setEditable(editable);
        txtCorreo.setEditable(editable);
        txtTelefono.setEditable(editable);
        chkActivo.setEnabled(editable);

        btnGuardar.setEnabled(editable);
        btnCancelar.setEnabled(editable);

        btnNuevo.setEnabled(!editable);
        btnEditar.setEnabled(!editable && !txtId.getText().trim().isEmpty());

        tablaSocios.setEnabled(!editable);
        tablaSocios.setRowSelectionAllowed(!editable);

        txtDniBuscar.setEditable(!editable);
        txtNombreBuscar.setEditable(!editable);
        txtApellidoBuscar.setEditable(!editable);
        radioTodos.setEnabled(!editable);
        radioActivos.setEnabled(!editable);
        radioInactivos.setEnabled(!editable);
        btnBuscar.setEnabled(!editable);
    }
    
    public void llenarTabla(List<Socio> socios) {
        modeloTabla.setRowCount(0);

        for (Socio s : socios) {
            modeloTabla.addRow(new Object[] {
                s.getIdSocio(),
                s.getDni(),
                s.getApellido(),
                s.getNombre(),
                s.getEdad(),
                s.getCorreo(),
                s.getTelefono(),
                s.isEstado() ? "Activo" : "Inactivo"
            });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpActivar = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaSocios = new javax.swing.JTable();
        panelBuscar = new javax.swing.JPanel();
        btnBuscar = new javax.swing.JButton();
        lblDniBuscar = new javax.swing.JLabel();
        txtDniBuscar = new javax.swing.JTextField();
        lblNombreBuscar = new javax.swing.JLabel();
        txtNombreBuscar = new javax.swing.JTextField();
        lblApellidoBuscar = new javax.swing.JLabel();
        txtApellidoBuscar = new javax.swing.JTextField();
        radioTodos = new javax.swing.JRadioButton();
        radioActivos = new javax.swing.JRadioButton();
        radioInactivos = new javax.swing.JRadioButton();
        btnLimpiar = new javax.swing.JButton();
        panelABM = new javax.swing.JPanel();
        lblTelefono = new javax.swing.JLabel();
        chkActivo = new javax.swing.JCheckBox();
        lblID = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        txtId = new javax.swing.JTextField();
        txtCorreo = new javax.swing.JTextField();
        lblTelefono1 = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        btnNuevo = new javax.swing.JButton();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblEdad = new javax.swing.JLabel();
        txtEdad = new javax.swing.JTextField();
        btnEditar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setTitle("Gestión de socios");
        setPreferredSize(new java.awt.Dimension(950, 400));

        tablaSocios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI", "Apellido", "Nombre", "Edad", "Correo", "Teléfono"
            }
        ));
        tablaSocios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaSociosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaSocios);
        if (tablaSocios.getColumnModel().getColumnCount() > 0) {
            tablaSocios.getColumnModel().getColumn(0).setPreferredWidth(10);
            tablaSocios.getColumnModel().getColumn(1).setPreferredWidth(30);
            tablaSocios.getColumnModel().getColumn(4).setPreferredWidth(10);
        }

        panelBuscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnBuscar.setBackground(new java.awt.Color(204, 153, 255));
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        lblDniBuscar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDniBuscar.setText("DNI");

        lblNombreBuscar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombreBuscar.setText("Nombre");

        lblApellidoBuscar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblApellidoBuscar.setText("Apellido");

        btnGrpActivar.add(radioTodos);
        radioTodos.setSelected(true);
        radioTodos.setText("Todos");

        btnGrpActivar.add(radioActivos);
        radioActivos.setText("Activos");
        radioActivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioActivosActionPerformed(evt);
            }
        });

        btnGrpActivar.add(radioInactivos);
        radioInactivos.setText("Inactivos");

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBuscarLayout = new javax.swing.GroupLayout(panelBuscar);
        panelBuscar.setLayout(panelBuscarLayout);
        panelBuscarLayout.setHorizontalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblNombreBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblApellidoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblDniBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDniBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellidoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioActivos)
                    .addComponent(radioInactivos)
                    .addComponent(radioTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(btnLimpiar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelBuscarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBuscar, btnLimpiar});

        panelBuscarLayout.setVerticalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDniBuscar)
                    .addComponent(txtDniBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreBuscar)
                    .addComponent(txtNombreBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblApellidoBuscar)
                    .addComponent(txtApellidoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnLimpiar))
                .addContainerGap())
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(radioActivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioInactivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioTodos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelABM.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTelefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelefono.setText("Teléfono");

        chkActivo.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkActivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkActivoActionPerformed(evt);
            }
        });

        lblID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblID.setText("ID");

        txtTelefono.setEditable(false);
        txtTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefonoActionPerformed(evt);
            }
        });

        txtId.setEditable(false);

        txtCorreo.setEditable(false);

        lblTelefono1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelefono1.setText("Activo");

        lblDni.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDni.setText("DNI");

        lblCorreo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCorreo.setText("Correo");

        txtDni.setEditable(false);

        btnGuardar.setBackground(new java.awt.Color(102, 204, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        lblNombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombre.setText("Nombre");

        txtNombre.setEditable(false);

        btnNuevo.setBackground(new java.awt.Color(153, 255, 153));
        btnNuevo.setText("Nuevo");
        btnNuevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNuevoMouseClicked(evt);
            }
        });
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        lblApellido.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblApellido.setText("Apellido");

        txtApellido.setEditable(false);

        lblEdad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEdad.setText("Edad");

        txtEdad.setEditable(false);

        btnEditar.setBackground(new java.awt.Color(255, 255, 153));
        btnEditar.setText("Editar");
        btnEditar.setPreferredSize(new java.awt.Dimension(70, 28));
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(255, 153, 153));
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelABMLayout = new javax.swing.GroupLayout(panelABM);
        panelABM.setLayout(panelABMLayout);
        panelABMLayout.setHorizontalGroup(
            panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelABMLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelABMLayout.createSequentialGroup()
                        .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelABMLayout.createSequentialGroup()
                                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblDni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblApellido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblEdad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblCorreo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblTelefono, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblTelefono1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelABMLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkActivo)
                                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(panelABMLayout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(panelABMLayout.createSequentialGroup()
                                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(113, 113, 113))
                    .addGroup(panelABMLayout.createSequentialGroup()
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112))))
        );

        panelABMLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtApellido, txtCorreo, txtDni, txtEdad, txtNombre, txtTelefono});

        panelABMLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblApellido, lblCorreo, lblDni, lblEdad, lblID, lblNombre, lblTelefono, lblTelefono1});

        panelABMLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancelar, btnEditar, btnGuardar, btnNuevo});

        panelABMLayout.setVerticalGroup(
            panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelABMLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblID)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDni)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblApellido)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEdad)
                    .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCorreo)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelefono)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkActivo)
                    .addComponent(lblTelefono1))
                .addGap(16, 16, 16)
                .addGroup(panelABMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelABM, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelABM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonoActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String dni = (String) txtDniBuscar.getText().trim();
        String apellido = txtApellidoBuscar.getText().trim();
        String nombre = txtNombreBuscar.getText().trim();
        
        boolean hayFiltro = !dni.isEmpty() || !apellido.isEmpty() || !nombre.isEmpty()
                || radioActivos.isSelected() || radioInactivos.isSelected();
        
        try {
            List<Socio> socios = filtrar(socioData.listarSocios(), dni, nombre, 
                apellido, radioActivos.isSelected(), radioInactivos.isSelected());
            
            if (socios == null) {
                return;
            }
            
            llenarTabla(socios);
            tablaSocios.clearSelection();
            limpiar();
            habilitarEdicion(false);
            
            if (socios.isEmpty() && hayFiltro) {
                JOptionPane.showMessageDialog(this, "No hay socios con esos filtros.");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo buscar. " + ex.getMessage());
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void chkActivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkActivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkActivoActionPerformed

    private void tablaSociosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaSociosMouseClicked
        if (!tablaSocios.isEnabled()) {
            return;
        }
        
        int fila = tablaSocios.getSelectedRow();

        if (fila >= 0) {
            int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());

            try {
                Socio s = socioData.obtenerSocio(id);
                if (s != null) {
                    mostrarSocio(s);
                    habilitarEdicion(false);
                }
            } catch (SQLException ex) {
                JOptionPane.showConfirmDialog(this, "No se pudo cargar el socio: " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_tablaSociosMouseClicked

    public static List<Socio> filtrar(List<Socio> socios, String dni, String nombre,
        String apellido, boolean soloActivas, boolean soloInactivas) {
        
        String textoDni = dni == null ? "" : dni.trim().toLowerCase();
        String textoNombre = nombre == null ? "" : nombre.trim().toLowerCase();
        String textoApellido = apellido == null ? "" : apellido.trim().toLowerCase();
        
        List<Socio> filtradas = new ArrayList<>();

        for (Socio s : socios) {
            if (soloActivas && !s.isEstado()) {
                continue;
            }
            if (soloInactivas && s.isEstado()) {
                continue;
            }
            if (!textoDni.isEmpty()) {
                String dniSocio = s.getDni() == null ? "" : s.getDni();
                if (!dniSocio.contains(textoDni)) {
                    continue;
                }
            }
            
            if (!textoNombre.isEmpty()) {
                String nombreSocio = s.getNombre() == null ? "" : s.getNombre();
                if (!nombreSocio.toLowerCase().contains(textoNombre)) {
                    continue;
                }
            }
            
            if (!textoApellido.isEmpty()) {
                String apellidoSocio = s.getApellido()== null ? "" : s.getApellido();
                if (!apellidoSocio.toLowerCase().contains(textoApellido)) {
                    continue;
                }
            }
            filtradas.add(s);
        }

        //ordenar(filtradas);
        return filtradas;
    }
    
    private void btnNuevoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNuevoMouseClicked
        // TODO add your handling code here:
        limpiar();
        chkActivo.setSelected(true);
    }//GEN-LAST:event_btnNuevoMouseClicked

    private void radioActivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioActivosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioActivosActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiar();
        chkActivo.setSelected(true);
        tablaSocios.clearSelection();
        habilitarEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        Socio socio = leerFormulario();
        if (socio == null) {
            return;
        }

        try {
            if (socio.getIdSocio()== 0) {
                socioData.guardarSocio(socio);
                JOptionPane.showMessageDialog(this, "Socio guardado con ID " + socio.getIdSocio()+ ".");
            } else {
                socioData.actualizarSocio(socio);
                socioData.cambiarActivo(socio.getIdSocio(), socio.isEstado());
                JOptionPane.showMessageDialog(this, "Socio modificado.");
            }
            cargarTabla();
            limpiar();
            habilitarEdicion(false);
            tablaSocios.clearSelection();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private Socio leerFormulario() {

        String dni = (String) txtDni.getText().trim();
        if (dni == null || dni.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el DNI.");
            return null;
        }

        String nombre = (String) txtNombre.getText().trim();
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre.");
            return null;
        }

        String apellido = (String) txtApellido.getText().trim();
        if (apellido == null || apellido.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el apellido.");
            return null;
        }

        int edad;
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero.");
            return null;
        }
        if (edad <= 10) {
            JOptionPane.showMessageDialog(this, "La edad debe ser mayor a 8.");
            return null;
        }
        
        String correo = (String) txtCorreo.getText().trim();
        if (correo == null || correo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el correo.");
            return null;
        }

        String telefono = (String) txtTelefono.getText().trim();
        if (telefono == null || telefono.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el teléfono.");
            return null;
        }

        String idSocio = txtId.getText().trim();
        if (idSocio.isEmpty()) {
            return new Socio(dni, nombre, apellido, edad, correo, telefono, chkActivo.isSelected());
        }

        try {
            int id = Integer.parseInt(idSocio);
            return new Socio(id, dni, nombre, apellido, edad, correo, telefono, chkActivo.isSelected());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.");
            return null;
        }
    }
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiar();
        chkActivo.setSelected(true);
        tablaSocios.clearSelection();
        habilitarEdicion(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtDniBuscar.setText("");
        txtNombreBuscar.setText("");
        txtApellidoBuscar.setText("");
        radioTodos.setSelected(true);
        cargarTabla();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio de la tabla.");
            return;
        }
        habilitarEdicion(true);
    }//GEN-LAST:event_btnEditarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditar;
    private javax.swing.ButtonGroup btnGrpActivar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblApellidoBuscar;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblDniBuscar;
    private javax.swing.JLabel lblEdad;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreBuscar;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTelefono1;
    private javax.swing.JPanel panelABM;
    private javax.swing.JPanel panelBuscar;
    private javax.swing.JRadioButton radioActivos;
    private javax.swing.JRadioButton radioInactivos;
    private javax.swing.JRadioButton radioTodos;
    private javax.swing.JTable tablaSocios;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtApellidoBuscar;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtDniBuscar;
    private javax.swing.JTextField txtEdad;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreBuscar;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
