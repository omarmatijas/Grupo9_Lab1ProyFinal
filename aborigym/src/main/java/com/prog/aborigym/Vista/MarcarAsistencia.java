/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Asistencia;
import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Entrenador;
import com.prog.aborigym.Modelo.Pase;
import com.prog.aborigym.Modelo.Socio;
import com.prog.aborigym.Persistencia.AsistenciaData;
import com.prog.aborigym.Persistencia.ClaseGymData;
import com.prog.aborigym.Persistencia.Conexion;
import com.prog.aborigym.Persistencia.EntrenadorData;
import com.prog.aborigym.Persistencia.PaseData;
import com.prog.aborigym.Persistencia.SocioData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Omar
 */
public class MarcarAsistencia extends javax.swing.JInternalFrame {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private SocioData socioData;
    private ClaseGymData claseGymData;
    private EntrenadorData entrenadorData;
    private PaseData paseData;
    private AsistenciaData asistenciaData;

    private DefaultTableModel modeloSocios;
    private DefaultTableModel modeloClases;
    private List<Socio> sociosVisibles;
    private List<ClaseGym> clasesVisibles;
    private Socio socioSeleccionado;
    private ClaseGym claseSeleccionada;

    public MarcarAsistencia(Conexion con) {
        socioData = new SocioData(con);
        claseGymData = new ClaseGymData(con);
        entrenadorData = new EntrenadorData(con);
        paseData = new PaseData(con);
        asistenciaData = new AsistenciaData(con);
        sociosVisibles = new ArrayList<>();
        clasesVisibles = new ArrayList<>();
        initComponents();
        armarTablas();
        agruparRadios();
        prepararFecha();
        cargarEntrenadores();
        cargarSocios();
        cargarClases(false);
    }

    private void armarTablas() {
        modeloSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloSocios.addColumn("ID");
        modeloSocios.addColumn("DNI");
        modeloSocios.addColumn("Apellido");
        modeloSocios.addColumn("Nombre");
        tablaSocios.setModel(modeloSocios);
        tablaSocios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modeloClases = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloClases.addColumn("ID");
        modeloClases.addColumn("Disciplina");
        modeloClases.addColumn("Entrenador");
        modeloClases.addColumn("Horario");
        modeloClases.addColumn("Capacidad");
        tablaClases.setModel(modeloClases);
        tablaClases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarSocios() {
        String texto = txtBuscarSocio.getText().trim().toLowerCase();
        sociosVisibles = new ArrayList<>();
        modeloSocios.setRowCount(0);
        socioSeleccionado = null;

        try {
            for (Socio s : socioData.listarSocios()) {
                if (!s.isEstado()) {
                    continue;
                }
                if (!texto.isEmpty() && !coincideSocio(s, texto)) {
                    continue;
                }
                sociosVisibles.add(s);
                modeloSocios.addRow(new Object[]{
                    s.getIdSocio(),
                    s.getDni(),
                    s.getApellido(),
                    s.getNombre()
                });
            }
            if (!texto.isEmpty() && sociosVisibles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay socios activos con ese dato.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los socios. " + ex.getMessage());
        }
    }

    private void cargarClases(boolean avisarSiVacio) {
        String disciplina = txtDisciplinaBuscar.getText().trim().toLowerCase();
        String horarioTexto = txtHorarioBuscar.getText().trim();
        LocalTime horario = null;
        if (!horarioTexto.isEmpty()) {
            try {
                horario = LocalTime.parse(horarioTexto, HORA);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "El horario de búsqueda debe tener el formato HH:mm, por ejemplo 18:30.");
                return;
            }
        }

        Entrenador entrenador = (Entrenador) cboEntrenadorBuscar.getSelectedItem();
        LocalDate fecha = fechaElegida();
        boolean soloVacantes = radioActivos1.isSelected();
        List<ClaseGym> filtradas = new ArrayList<>();

        try {
            for (ClaseGym c : claseGymData.listarClasesGym()) {
                if (radioActivos.isSelected() && !c.isEstado()) {
                    continue;
                }
                if (radioInactivos.isSelected() && c.isEstado()) {
                    continue;
                }
                if (!disciplina.isEmpty() && !contiene(c.getDisciplina(), disciplina)) {
                    continue;
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
                if (soloVacantes) {
                    int tomadas = asistenciaData.contarAsistencias(c.getIdClase(), fecha);
                    if (tomadas >= c.getCapacidad()) {
                        continue;
                    }
                }
                filtradas.add(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las clases. " + ex.getMessage());
            return;
        }

        ordenarPorHorarioYDisciplina(filtradas);
        clasesVisibles = filtradas;
        modeloClases.setRowCount(0);
        claseSeleccionada = null;
        tablaClases.clearSelection();

        for (ClaseGym c : filtradas) {
            String nombreEntrenador = "";
            if (c.getEntrenador() != null) {
                nombreEntrenador = c.getEntrenador().toString();
            }
            String horarioTextoFila = "";
            if (c.getHorario() != null) {
                horarioTextoFila = c.getHorario().format(HORA);
            }
            modeloClases.addRow(new Object[]{
                c.getIdClase(),
                c.getDisciplina(),
                nombreEntrenador,
                horarioTextoFila,
                c.getCapacidad()
            });
        }

        if (avisarSiVacio && filtradas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay clases con esos filtros.");
        }
    }

    private void ordenarPorHorarioYDisciplina(List<ClaseGym> clases) {
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

    private void agruparRadios() {
        ButtonGroup estado = new ButtonGroup();
        estado.add(radioTodos);
        estado.add(radioActivos);
        estado.add(radioInactivos);

        ButtonGroup vacantes = new ButtonGroup();
        vacantes.add(radioActivos1);
        vacantes.add(radioTodos1);
    }

    private void prepararFecha() {
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        calendario.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendario.set(java.util.Calendar.MINUTE, 0);
        calendario.set(java.util.Calendar.SECOND, 0);
        calendario.set(java.util.Calendar.MILLISECOND, 0);
        spnFecha.setModel(new javax.swing.SpinnerDateModel(
                calendario.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spnFecha.setEditor(new javax.swing.JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        spnFecha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cargarClases(false);
            }
        });
    }

    private LocalDate fechaElegida() {
        java.util.Date valor = (java.util.Date) spnFecha.getValue();
        return valor.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private void cargarEntrenadores() {
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
                cboEntrenadorBuscar.addItem(e);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores. " + ex.getMessage());
        }
        cboEntrenadorBuscar.setSelectedIndex(0);
    }

    private boolean coincideSocio(Socio socio, String texto) {
        return contiene(socio.getDni(), texto)
                || contiene(socio.getNombre(), texto)
                || contiene(socio.getApellido(), texto);
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTituloSocios = new javax.swing.JLabel();
        txtBuscarSocio = new javax.swing.JTextField();
        btnBuscarSocio = new javax.swing.JButton();
        jScrollPaneSocios = new javax.swing.JScrollPane();
        tablaSocios = new javax.swing.JTable();
        lblTituloClases = new javax.swing.JLabel();
        jScrollPaneClases = new javax.swing.JScrollPane();
        tablaClases = new javax.swing.JTable();
        lblFecha = new javax.swing.JLabel();
        spnFecha = new javax.swing.JSpinner();
        btnMarcar = new javax.swing.JButton();
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
        radioActivos1 = new javax.swing.JRadioButton();
        radioTodos1 = new javax.swing.JRadioButton();

        setClosable(true);
        setTitle("Marcar asistencia");
        setPreferredSize(new java.awt.Dimension(980, 480));

        lblTituloSocios.setText("Socios");

        txtBuscarSocio.setToolTipText("DNI, nombre o apellido");
        txtBuscarSocio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarSocioActionPerformed(evt);
            }
        });

        btnBuscarSocio.setBackground(new java.awt.Color(204, 153, 255));
        btnBuscarSocio.setText("Filtrar");
        btnBuscarSocio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarSocioActionPerformed(evt);
            }
        });

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

        lblTituloClases.setText("Clases");

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
        jScrollPaneClases.setViewportView(tablaClases);

        lblFecha.setText("Fecha:");

        btnMarcar.setBackground(new java.awt.Color(153, 204, 255));
        btnMarcar.setText("Marcar asistencia");
        btnMarcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcarActionPerformed(evt);
            }
        });

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

        radioTodos.setSelected(true);
        radioTodos.setText("Todos");

        radioActivos.setText("Activas");

        radioInactivos.setText("Inactivas");

        radioActivos1.setSelected(true);
        radioActivos1.setText("Solo con vacantes");
        radioActivos1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioActivos1ActionPerformed(evt);
            }
        });

        radioTodos1.setText("Todos");

        javax.swing.GroupLayout panelBuscarLayout = new javax.swing.GroupLayout(panelBuscar);
        panelBuscar.setLayout(panelBuscarLayout);
        panelBuscarLayout.setHorizontalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addComponent(radioActivos1)
                        .addGap(18, 18, 18)
                        .addComponent(radioTodos1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addComponent(radioTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioActivos)
                        .addGap(18, 18, 18)
                        .addComponent(radioInactivos)))
                .addContainerGap(41, Short.MAX_VALUE))
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
                    .addContainerGap(7, Short.MAX_VALUE)))
        );
        panelBuscarLayout.setVerticalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioTodos)
                    .addComponent(radioActivos)
                    .addComponent(radioInactivos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioActivos1)
                    .addComponent(radioTodos1))
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
                    .addContainerGap(108, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelBuscar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMarcar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneClases, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTituloClases)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTituloSocios)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFecha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFecha)
                    .addComponent(spnFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(lblTituloClases)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneClases, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(panelBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addComponent(lblTituloSocios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBuscarSocio)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMarcar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarSocioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarSocioActionPerformed
        cargarSocios();
    }//GEN-LAST:event_txtBuscarSocioActionPerformed

    private void btnBuscarSocioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarSocioActionPerformed
        cargarSocios();
    }//GEN-LAST:event_btnBuscarSocioActionPerformed

    private void tablaSociosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaSociosMouseClicked
        int fila = tablaSocios.getSelectedRow();
        if (fila < 0 || fila >= sociosVisibles.size()) {
            return;
        }
        socioSeleccionado = sociosVisibles.get(fila);
    }//GEN-LAST:event_tablaSociosMouseClicked

    private void tablaClasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClasesMouseClicked
        int fila = tablaClases.getSelectedRow();
        if (fila < 0 || fila >= clasesVisibles.size()) {
            return;
        }
        claseSeleccionada = clasesVisibles.get(fila);
    }//GEN-LAST:event_tablaClasesMouseClicked

    private void btnMarcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcarActionPerformed
        if (socioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio.");
            return;
        }
        if (claseSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase.");
            return;
        }

        if (!claseSeleccionada.isEstado()) {
            JOptionPane.showMessageDialog(this, "La clase está inactiva.");
            return;
        }

        LocalDate fecha = fechaElegida();
        try {
            Pase pase = paseData.obtenerPaseActivoConEntradas(socioSeleccionado.getIdSocio(), fecha);
            if (pase == null || !pase.isEstado() || pase.getCantidadEntradas() <= 0) {
                JOptionPane.showMessageDialog(this, "El socio no tiene un pase activo con entradas.");
                return;
            }
            if (asistenciaData.existeAsistencia(socioSeleccionado.getIdSocio(), claseSeleccionada.getIdClase(), fecha)) {
                JOptionPane.showMessageDialog(this, "El socio ya tiene asistencia en esa clase el " + fecha + ".");
                return;
            }

            int tomadas = asistenciaData.contarAsistencias(claseSeleccionada.getIdClase(), fecha);
            Asistencia asistencia = claseSeleccionada.marcarAsistencia(socioSeleccionado, fecha, tomadas, pase);
            if (asistencia == null) {
                JOptionPane.showMessageDialog(this, "La clase no tiene cupo libre.");
                return;
            }

            asistenciaData.guardarAsistencia(asistencia);
            paseData.actualizarCantidadEntradas(pase);
            JOptionPane.showMessageDialog(this, "Asistencia registrada. Quedan " + pase.getCantidadEntradas() + " entradas.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnMarcarActionPerformed

    private void cboEntrenadorBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEntrenadorBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboEntrenadorBuscarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        cargarClases(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void radioActivos1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioActivos1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioActivos1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscarSocio;
    private javax.swing.JButton btnMarcar;
    private javax.swing.JComboBox<Entrenador> cboEntrenadorBuscar;
    private javax.swing.JScrollPane jScrollPaneClases;
    private javax.swing.JScrollPane jScrollPaneSocios;
    private javax.swing.JLabel lblDisciplina1;
    private javax.swing.JLabel lblEntrenador1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHorario1;
    private javax.swing.JLabel lblTituloClases;
    private javax.swing.JLabel lblTituloSocios;
    private javax.swing.JPanel panelBuscar;
    private javax.swing.JRadioButton radioActivos;
    private javax.swing.JRadioButton radioActivos1;
    private javax.swing.JRadioButton radioInactivos;
    private javax.swing.JRadioButton radioTodos;
    private javax.swing.JRadioButton radioTodos1;
    private javax.swing.JSpinner spnFecha;
    private javax.swing.JTable tablaClases;
    private javax.swing.JTable tablaSocios;
    private javax.swing.JTextField txtBuscarSocio;
    private javax.swing.JTextField txtDisciplinaBuscar;
    private javax.swing.JTextField txtHorarioBuscar;
    // End of variables declaration//GEN-END:variables
}
