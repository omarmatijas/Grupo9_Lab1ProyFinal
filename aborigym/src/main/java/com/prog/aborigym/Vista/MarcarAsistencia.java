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
public class MarcarAsistencia extends javax.swing.JInternalFrame {

    private SocioData socioData;
    private ClaseGymData claseGymData;
    private EntrenadorData entrenadorData;
    private PaseData paseData;
    private AsistenciaData asistenciaData;

    private DefaultTableModel modeloSocios;
    private DefaultTableModel modeloClases;
    private List<Socio> sociosVisibles;
    private List<ClaseGym> clasesVisibles;
    private Set<Integer> sociosInscriptos;
    private Socio socioSeleccionado;
    private ClaseGym claseSeleccionada;
    private LocalDate fechaValida;
    private boolean ajustandoFecha;

    public MarcarAsistencia(Conexion con) {
        socioData = new SocioData(con);
        claseGymData = new ClaseGymData(con);
        entrenadorData = new EntrenadorData(con);
        paseData = new PaseData(con);
        asistenciaData = new AsistenciaData(con);
        sociosVisibles = new ArrayList<>();
        clasesVisibles = new ArrayList<>();
        sociosInscriptos = new HashSet<>();
        initComponents();
        armarTablas();
        agruparRadios();
        prepararFecha();
        cargarEntrenadores();
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
        modeloSocios.addColumn("Inscripto");
        tablaSocios.setModel(modeloSocios);
        tablaSocios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSocios.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tablaSocios.getColumnModel().getColumn(0).setPreferredWidth(75);
        tablaSocios.getColumnModel().getColumn(0).setMaxWidth(100);
        tablaSocios.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaSocios.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaSocios.getColumnModel().getColumn(3).setPreferredWidth(70);
        tablaSocios.getColumnModel().getColumn(3).setMaxWidth(85);

        modeloClases = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        modeloClases.addColumn("Disciplina");
        modeloClases.addColumn("Entrenador");
        modeloClases.addColumn("Horario");
        modeloClases.addColumn("Capacidad");
        modeloClases.addColumn("Libres");
        tablaClases.setModel(modeloClases);
        tablaClases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClases.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tablaClases.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaClases.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaClases.getColumnModel().getColumn(2).setPreferredWidth(62);
        tablaClases.getColumnModel().getColumn(2).setMaxWidth(72);
        tablaClases.getColumnModel().getColumn(3).setPreferredWidth(72);
        tablaClases.getColumnModel().getColumn(3).setMaxWidth(90);
        tablaClases.getColumnModel().getColumn(4).setPreferredWidth(58);
        tablaClases.getColumnModel().getColumn(4).setMaxWidth(70);
    }

    private void cargarSocios(boolean avisarSiFaltaClase) {
        int idSocio = socioSeleccionado == null ? 0 : socioSeleccionado.getIdSocio();
        sociosVisibles = new ArrayList<>();
        sociosInscriptos = new HashSet<>();
        modeloSocios.setRowCount(0);
        socioSeleccionado = null;
        tablaSocios.clearSelection();

        if (claseSeleccionada == null) {
            actualizarBotones();
            if (avisarSiFaltaClase) {
                JOptionPane.showMessageDialog(this, "Seleccione una clase para ver los socios.");
            }
            return;
        }

        LocalDate fecha = fechaElegida();
        String errorFecha = mensajeFecha(fecha);
        if (errorFecha != null) {
            actualizarBotones();
            return;
        }

        String texto = txtBuscarSocio.getText().trim().toLowerCase();
        int filaSeleccion = -1;
        try {
            sociosInscriptos.addAll(asistenciaData.listarSociosConAsistencia(claseSeleccionada.getIdClase(), fecha));
            for (Socio s : socioData.listarSocios()) {
                if (!s.isEstado()) {
                    continue;
                }
                if (!texto.isEmpty() && !coincideSocio(s, texto)) {
                    continue;
                }
                sociosVisibles.add(s);
            }
            ordenarSocios(sociosVisibles);
            for (Socio s : sociosVisibles) {
                boolean inscripto = sociosInscriptos.contains(s.getIdSocio());
                modeloSocios.addRow(new Object[]{
                    s.getDni(),
                    s.getApellido(),
                    s.getNombre(),
                    inscripto ? "Sí" : "No"
                });
                if (s.getIdSocio() == idSocio) {
                    socioSeleccionado = s;
                    filaSeleccion = sociosVisibles.indexOf(s);
                }
            }
            if (filaSeleccion >= 0) {
                tablaSocios.setRowSelectionInterval(filaSeleccion, filaSeleccion);
            }
            if (!texto.isEmpty() && sociosVisibles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay socios activos con ese dato.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los socios. " + ex.getMessage());
        }
        actualizarBotones();
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

    private void actualizarBotones() {
        boolean inscripto = socioSeleccionado != null && sociosInscriptos.contains(socioSeleccionado.getIdSocio());
        btnMarcar.setEnabled(socioSeleccionado != null && claseSeleccionada != null && !inscripto);
        btnMarcar1.setEnabled(socioSeleccionado != null && claseSeleccionada != null && inscripto);
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
        boolean soloActivas = radioActivos.isSelected();
        boolean soloInactivas = radioInactivos.isSelected();
        List<ClaseGym> filtradas;
        List<Integer> lugaresLibres = new ArrayList<>();
        try {
            Map<Integer, Integer> cantidades = asistenciaData.contarAsistenciaClases(
                    fecha, txtDisciplinaBuscar.getText(), entrenador, horario, soloActivas, soloInactivas);
            filtradas = FiltroClases.filtrar(
                    claseGymData.listarClasesGym(),
                    txtDisciplinaBuscar.getText(),
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
                if (radioActivos1.isSelected() && libres == 0) {
                    continue;
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
                c.getCapacidad(),
                lugaresLibres.get(i)
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

        ButtonGroup vacantes = new ButtonGroup();
        vacantes.add(radioActivos1);
        vacantes.add(radioTodos1);
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
                    JOptionPane.showMessageDialog(MarcarAsistencia.this, error);
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
            return "No puedes marcar asistencia a futuro";
        }
        if (fecha.isBefore(hoy.minusDays(7))) {
            return "Solo puedes marcar asistencia de la última semana";
        }
        return null;
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
        lblTituloSocios1 = new javax.swing.JLabel();
        lblTituloSocios2 = new javax.swing.JLabel();
        btnMarcar1 = new javax.swing.JButton();

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

        btnMarcar.setBackground(new java.awt.Color(102, 204, 255));
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
                        .addComponent(txtDisciplinaBuscar)
                        .addComponent(txtHorarioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        lblTituloSocios1.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblTituloSocios1.setForeground(new java.awt.Color(102, 102, 102));
        lblTituloSocios1.setText("Busca por nombre, apellido o DNI");

        lblTituloSocios2.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblTituloSocios2.setForeground(new java.awt.Color(102, 102, 102));
        lblTituloSocios2.setText("Solo socios activos");

        btnMarcar1.setBackground(new java.awt.Color(255, 153, 153));
        btnMarcar1.setText("Anular asistencia");
        btnMarcar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarcar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTituloClases)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panelBuscar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(lblTituloSocios)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblTituloSocios1)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(lblTituloSocios2))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPaneClases, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                                    .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnMarcar1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnMarcar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFecha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(164, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFecha)
                    .addComponent(spnFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(lblTituloClases)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneClases, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(panelBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarSocio)
                            .addComponent(lblTituloSocios))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloSocios1)
                        .addGap(1, 1, 1)
                        .addComponent(lblTituloSocios2))
                    .addComponent(jScrollPaneSocios, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMarcar1)
                    .addComponent(btnMarcar))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    }//GEN-LAST:event_tablaSociosMouseClicked

    private void tablaClasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClasesMouseClicked
        int fila = tablaClases.getSelectedRow();
        if (fila < 0 || fila >= clasesVisibles.size()) {
            return;
        }
        claseSeleccionada = clasesVisibles.get(fila);
        cargarSocios(false);
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
        String errorFecha = mensajeFecha(fecha);
        if (errorFecha != null) {
            JOptionPane.showMessageDialog(this, errorFecha);
            ponerFecha(fechaValida);
            return;
        }

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
            JOptionPane.showMessageDialog(this, "Asistencia registrada. Al socio le quedan " + pase.getCantidadEntradas() + " entradas.");
            cargarClases(false);
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

    private void txtHorarioBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHorarioBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHorarioBuscarActionPerformed

    private void btnMarcar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarcar1ActionPerformed
        if (socioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio.");
            return;
        }
        if (claseSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase.");
            return;
        }

        LocalDate fecha = fechaElegida();
        String errorFecha = mensajeFecha(fecha);
        if (errorFecha != null) {
            JOptionPane.showMessageDialog(this, errorFecha);
            ponerFecha(fechaValida);
            return;
        }

        try {
            int idPase = asistenciaData.obtenerIdPaseDeAsistencia(
                    socioSeleccionado.getIdSocio(), claseSeleccionada.getIdClase(), fecha);
            if (idPase == 0) {
                JOptionPane.showMessageDialog(this, "El socio no tiene asistencia en esa clase el " + fecha + ".");
                return;
            }
            Pase pase = paseData.obtenerPase(idPase);
            if (pase == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el pase de esa asistencia.");
                return;
            }

            asistenciaData.eliminarAsistencia(socioSeleccionado.getIdSocio(), claseSeleccionada.getIdClase(), fecha);
            pase.anularAsistencia();
            paseData.actualizarCantidadEntradas(pase);
            JOptionPane.showMessageDialog(this, "Asistencia anulada. Al socio le quedan " + pase.getCantidadEntradas() + " entradas.");
            cargarClases(false);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_btnMarcar1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscarSocio;
    private javax.swing.JButton btnMarcar;
    private javax.swing.JButton btnMarcar1;
    private javax.swing.JComboBox<Entrenador> cboEntrenadorBuscar;
    private javax.swing.JScrollPane jScrollPaneClases;
    private javax.swing.JScrollPane jScrollPaneSocios;
    private javax.swing.JLabel lblDisciplina1;
    private javax.swing.JLabel lblEntrenador1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHorario1;
    private javax.swing.JLabel lblTituloClases;
    private javax.swing.JLabel lblTituloSocios;
    private javax.swing.JLabel lblTituloSocios1;
    private javax.swing.JLabel lblTituloSocios2;
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
