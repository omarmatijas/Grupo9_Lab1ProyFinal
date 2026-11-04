/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.prog.aborigym.Vista;

import com.prog.aborigym.Modelo.Asistencia;
import com.prog.aborigym.Modelo.ClaseGym;
import com.prog.aborigym.Modelo.Disciplinas;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class ConsultarAsistencia extends javax.swing.JInternalFrame {

    private ClaseGymData claseGymData;
    private EntrenadorData entrenadorData;
    private AsistenciaData asistenciaData;

    private DefaultTableModel modeloSocios;
    private DefaultTableModel modeloClases;
    private List<Socio> sociosInscriptos;
    private List<ClaseGym> clasesVisibles;
    private ClaseGym claseSeleccionada;
    private LocalDate fechaValida;
    private boolean ajustandoFecha;
    private boolean cargandoCombos;

    public ConsultarAsistencia(Conexion con) {
        claseGymData = new ClaseGymData(con);
        entrenadorData = new EntrenadorData(con);
        asistenciaData = new AsistenciaData(con);
        sociosInscriptos = new ArrayList<>();
        clasesVisibles = new ArrayList<>();
        initComponents();
        armarTablas();
        agruparRadios();
        prepararFecha();
        prepararFiltro();
        cargarClases(false);
    }

    private void armarTablas() {
        modeloSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloSocios.addColumn("DNI");
        modeloSocios.addColumn("Apellido");
        modeloSocios.addColumn("Nombre");
        modeloSocios.addColumn("Estado");
        tablaSocios.setModel(modeloSocios);
        tablaSocios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSocios.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tablaSocios.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaSocios.getColumnModel().getColumn(0).setMaxWidth(100);
        tablaSocios.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaSocios.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaSocios.getColumnModel().getColumn(3).setPreferredWidth(70);

        modeloClases = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloClases.addColumn("Disciplina");
        modeloClases.addColumn("Entrenador");
        modeloClases.addColumn("Horario");
        modeloClases.addColumn("Estado");
        tablaClases.setModel(modeloClases);
        tablaClases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClases.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tablaClases.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaClases.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaClases.getColumnModel().getColumn(2).setPreferredWidth(62);
        tablaClases.getColumnModel().getColumn(2).setMaxWidth(72);
        tablaClases.getColumnModel().getColumn(3).setPreferredWidth(75);
    }

    private void cargarSocios(boolean avisarSiFaltaClase) {
        sociosInscriptos = new ArrayList<>();
        modeloSocios.setRowCount(0);
        tablaSocios.clearSelection();

        if (claseSeleccionada == null) {
            lblTituloSelección.setText("Seleccione la fecha y la clase");
            lblTituloSocios.setText(" - Socios asistentes - ");
            jScrollPaneSocios.setVisible(true);
            if (avisarSiFaltaClase) {
                JOptionPane.showMessageDialog(this, "Seleccione una clase para ver los socios.");
            }
            return;
        }

        LocalDate fecha = fechaElegida();
        String errorFecha = mensajeFecha(fecha);
        if (errorFecha != null) {
            return;
        }

        String fechaTexto = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String horarioTexto = "";
        if (claseSeleccionada.getHorario() != null) {
            horarioTexto = " " + claseSeleccionada.getHorario().format(FiltroClases.HORA);
        }
        lblTituloSelección.setText(fechaTexto + " - " + claseSeleccionada.getDisciplina() + horarioTexto);

        int filaSeleccion = -1;
        try {
            sociosInscriptos.addAll(asistenciaData.listarSocios(claseSeleccionada.getIdClase(), fecha));
            ordenarSocios(sociosInscriptos);
            for (Socio s : sociosInscriptos) {
                modeloSocios.addRow(new Object[]{
                    s.getDni(),
                    s.getApellido(),
                    s.getNombre(),
                    s.isEstado() ? "Activo" : "Inactivo"
                });
            }
            if (filaSeleccion >= 0) {
                tablaSocios.setRowSelectionInterval(filaSeleccion, filaSeleccion);
            }
            if (sociosInscriptos.isEmpty()) {
                jScrollPaneSocios.setVisible(false);
                lblTituloSocios.setText("Sin socios asistentes para la clase y la fecha");
            } else {
                jScrollPaneSocios.setVisible(true);
                lblTituloSocios.setText("Socios asistentes");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los socios. " + ex.getMessage());
        }
    }

    private void ordenarSocios(List<Socio> socios) {
        Collections.sort(socios, new Comparator<Socio>() {
            @Override
            public int compare(Socio a, Socio b) {
                boolean inscriptoA = sociosInscriptos.contains(a.getIdSocio());
                boolean inscriptoB = sociosInscriptos.contains(b.getIdSocio());
                if (inscriptoA != inscriptoB) {
                    return inscriptoA ? -1 : 1;
                }
                int porApellido = texto(a.getApellido()).compareToIgnoreCase(texto(b.getApellido()));
                if (porApellido != 0) {
                    return porApellido;
                }
                return texto(a.getNombre()).compareToIgnoreCase(texto(b.getNombre()));
            }
        });
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private void cargarClases(boolean avisarSiVacio) {
        LocalTime horario;
        try {
            horario = FiltroClases.leerHorario(txtHorarioBuscar.getText());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, FiltroClases.MENSAJE_HORARIO);
            return;
        }

        LocalDate fecha = fechaElegida();
        String errorFecha = mensajeFecha(fecha);
        if (errorFecha != null) {
            JOptionPane.showMessageDialog(this, errorFecha);
            ponerFecha(fechaValida);
            return;
        }

        Entrenador entrenador = (Entrenador) cboEntrenadorBuscar.getSelectedItem();
        String disciplina = (String) cboDisciplinaBuscar.getSelectedItem();
        boolean soloActivas = radioActivos.isSelected();
        boolean soloInactivas = radioInactivos.isSelected();
        List<ClaseGym> filtradas;
        List<Integer> lugaresLibres = new ArrayList<>();
        try {
            Map<Integer, Integer> cantidades = asistenciaData.contarAsistenciaClases(
                    fecha, disciplina, entrenador, horario, soloActivas, soloInactivas);
            filtradas = FiltroClases.filtrar(
                    claseGymData.listarClasesGym(),
                    disciplina,
                    entrenador,
                    horario,
                    soloActivas,
                    soloInactivas
            );
            List<ClaseGym> visibles = new ArrayList<>();
            for (ClaseGym c : filtradas) {
                Integer tomadas = cantidades.get(c.getIdClase());
                int libres = c.getCapacidad() - (tomadas == null ? 0 : tomadas);
                if (libres < 0) {
                    libres = 0;
                }
                visibles.add(c);
                lugaresLibres.add(libres);
            }
            filtradas = visibles;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las clases. " + ex.getMessage());
            return;
        }

        int idClase = claseSeleccionada == null ? 0 : claseSeleccionada.getIdClase();
        clasesVisibles = filtradas;
        modeloClases.setRowCount(0);
        claseSeleccionada = null;
        tablaClases.clearSelection();

        for (int i = 0; i < filtradas.size(); i++) {
            ClaseGym c = filtradas.get(i);
            String nombreEntrenador = "";
            if (c.getEntrenador() != null) {
                nombreEntrenador = c.getEntrenador().toString();
            }
            String horarioTextoFila = "";
            if (c.getHorario() != null) {
                horarioTextoFila = c.getHorario().format(FiltroClases.HORA);
            }
            modeloClases.addRow(new Object[]{
                c.getDisciplina(),
                nombreEntrenador,
                horarioTextoFila,
                lugaresLibres.get(i) + "/" + c.getCapacidad(),
                c.isEstado() ? "Activo" : "Inactivo"
            });
            if (c.getIdClase() == idClase) {
                claseSeleccionada = c;
                tablaClases.setRowSelectionInterval(i, i);
            }
        }

        if (avisarSiVacio && filtradas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay clases con esos filtros.");
        }
        cargarSocios(false);
    }

    private void agruparRadios() {
        ButtonGroup estado = new ButtonGroup();
        estado.add(radioTodos);
        estado.add(radioActivos);
        estado.add(radioInactivos);
    }

    private void prepararFecha() {
        fechaValida = LocalDate.now();
        ponerFecha(fechaValida);
        spnFecha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                if (ajustandoFecha) {
                    return;
                }
                LocalDate fecha = fechaElegida();
                String error = mensajeFecha(fecha);
                if (error != null) {
                    JOptionPane.showMessageDialog(ConsultarAsistencia.this, error);
                    ponerFecha(fechaValida);
                    return;
                }
                fechaValida = fecha;
                cargarClases(false);
            }
        });
    }

    private void ponerFecha(LocalDate fecha) {
        ajustandoFecha = true;
        java.util.Calendar calendario = java.util.Calendar.getInstance();
        calendario.set(fecha.getYear(), fecha.getMonthValue() - 1, fecha.getDayOfMonth(), 0, 0, 0);
        calendario.set(java.util.Calendar.MILLISECOND, 0);
        spnFecha.setModel(new javax.swing.SpinnerDateModel(
                calendario.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spnFecha.setEditor(new javax.swing.JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        ajustandoFecha = false;
    }

    private String mensajeFecha(LocalDate fecha) {
        LocalDate hoy = LocalDate.now();
        if (fecha.isAfter(hoy)) {
            return "No consultar asistencia a futuro";
        }
        return null;
    }

    private LocalDate fechaElegida() {
        java.util.Date valor = (java.util.Date) spnFecha.getValue();
        return valor.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private void prepararFiltro() {
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
        cboDisciplinaBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (!cargandoCombos) {
                    cargarEntrenadores();
                }
            }
        });

        cargandoCombos = true;
        cboDisciplinaBuscar.removeAllItems();
        cboDisciplinaBuscar.addItem(null);
        for (String disciplina : Disciplinas.LISTA) {
            cboDisciplinaBuscar.addItem(disciplina);
        }
        cboDisciplinaBuscar.setSelectedIndex(0);
        cargandoCombos = false;
        cargarEntrenadores();
    }

    private void cargarEntrenadores() {
        Entrenador conservar = (Entrenador) cboEntrenadorBuscar.getSelectedItem();
        String disciplina = (String) cboDisciplinaBuscar.getSelectedItem();
        cargandoCombos = true;
        cboEntrenadorBuscar.removeAllItems();
        cboEntrenadorBuscar.addItem(null);

        try {
            List<Entrenador> lista;
            if (disciplina == null || disciplina.trim().isEmpty()) {
                lista = entrenadorData.listarEntrenadores();
            } else {
                lista = entrenadorData.listarEntrenadoresPorEspecialidad(disciplina);
            }
            for (Entrenador e : lista) {
                cboEntrenadorBuscar.addItem(e);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los entrenadores. " + ex.getMessage());
        }

        seleccionarEntrenador(conservar);
        if (cboEntrenadorBuscar.getSelectedIndex() < 0 && cboEntrenadorBuscar.getItemCount() > 0) {
            cboEntrenadorBuscar.setSelectedIndex(0);
        }
        cargandoCombos = false;
    }

    private void seleccionarEntrenador(Entrenador entrenador) {
        if (entrenador == null) {
            cboEntrenadorBuscar.setSelectedIndex(-1);
            return;
        }
        for (int i = 0; i < cboEntrenadorBuscar.getItemCount(); i++) {
            Entrenador item = cboEntrenadorBuscar.getItemAt(i);
            if (item != null && item.getIdEntrenador() == entrenador.getIdEntrenador()) {
                cboEntrenadorBuscar.setSelectedIndex(i);
                return;
            }
        }
        cboEntrenadorBuscar.setSelectedIndex(-1);
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

        jScrollPaneSocios = new javax.swing.JScrollPane();
        tablaSocios = new javax.swing.JTable();
        lblTituloClases = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        spnFecha = new javax.swing.JSpinner();
        panelBuscar = new javax.swing.JPanel();
        lblDisciplina1 = new javax.swing.JLabel();
        lblEntrenador1 = new javax.swing.JLabel();
        lblHorario1 = new javax.swing.JLabel();
        txtHorarioBuscar = new javax.swing.JTextField();
        cboEntrenadorBuscar = new javax.swing.JComboBox<>();
        cboDisciplinaBuscar = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();
        radioTodos = new javax.swing.JRadioButton();
        radioActivos = new javax.swing.JRadioButton();
        radioInactivos = new javax.swing.JRadioButton();
        jScrollPaneClases = new javax.swing.JScrollPane();
        tablaClases = new javax.swing.JTable();
        lblTituloSocios = new javax.swing.JLabel();
        lblTituloSelección = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Consultar asistencia");
        setPreferredSize(new java.awt.Dimension(980, 480));

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

        lblFecha.setText("Fecha:");

        panelBuscar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDisciplina1.setText("Disciplina");

        lblEntrenador1.setText("Entrenador");

        lblHorario1.setText("Horario");

        txtHorarioBuscar.setToolTipText("HH:mm");
        txtHorarioBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHorarioBuscarActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout panelBuscarLayout = new javax.swing.GroupLayout(panelBuscar);
        panelBuscar.setLayout(panelBuscarLayout);
        panelBuscarLayout.setHorizontalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelBuscarLayout.createSequentialGroup()
                                .addComponent(radioTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(radioActivos)
                                .addGap(18, 18, 18)
                                .addComponent(radioInactivos))
                            .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPaneClases, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(cboDisciplinaBuscar, 0, 260, Short.MAX_VALUE)
                        .addComponent(txtHorarioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(105, Short.MAX_VALUE)))
        );
        panelBuscarLayout.setVerticalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioTodos)
                    .addComponent(radioActivos)
                    .addComponent(radioInactivos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneClases, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBuscarLayout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblDisciplina1)
                        .addComponent(cboDisciplinaBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblEntrenador1)
                        .addComponent(cboEntrenadorBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblHorario1)
                        .addComponent(txtHorarioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(286, Short.MAX_VALUE)))
        );

        lblTituloSelección.setText("Seleccione la fecha y la clase");

        lblTituloSocios.setText(" - Socios asistentes - ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTituloClases)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFecha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloSelección)
                    .addComponent(lblTituloSocios)
                    .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFecha)
                            .addComponent(spnFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblTituloSelección)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTituloClases)
                    .addComponent(lblTituloSocios))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaSociosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaSociosMouseClicked
    }//GEN-LAST:event_tablaSociosMouseClicked

    private void tablaClasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClasesMouseClicked
        int fila = tablaClases.getSelectedRow();
        if (fila < 0 || fila >= clasesVisibles.size()) {
            return;
        }
        claseSeleccionada = clasesVisibles.get(fila);
        cargarSocios(false);
    }//GEN-LAST:event_tablaClasesMouseClicked

    private void cboEntrenadorBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboEntrenadorBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboEntrenadorBuscarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        cargarClases(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtHorarioBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHorarioBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHorarioBuscarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JComboBox<String> cboDisciplinaBuscar;
    private javax.swing.JComboBox<Entrenador> cboEntrenadorBuscar;
    private javax.swing.JScrollPane jScrollPaneClases;
    private javax.swing.JScrollPane jScrollPaneSocios;
    private javax.swing.JLabel lblDisciplina1;
    private javax.swing.JLabel lblEntrenador1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHorario1;
    private javax.swing.JLabel lblTituloClases;
    private javax.swing.JLabel lblTituloSelección;
    private javax.swing.JLabel lblTituloSocios;
    private javax.swing.JPanel panelBuscar;
    private javax.swing.JRadioButton radioActivos;
    private javax.swing.JRadioButton radioInactivos;
    private javax.swing.JRadioButton radioTodos;
    private javax.swing.JSpinner spnFecha;
    private javax.swing.JTable tablaClases;
    private javax.swing.JTable tablaSocios;
    private javax.swing.JTextField txtHorarioBuscar;
    // End of variables declaration//GEN-END:variables
}
