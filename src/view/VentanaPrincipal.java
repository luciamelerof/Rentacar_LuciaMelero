package view;

import dao.AlquilerDAOImpl;
import dao.VehiculoDAOImpl;
import dao.UsuarioDAOImpl;
import dto.AlquilerDTO;
import model.Usuario;
import model.Vehiculo;
import model.Alquiler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class VentanaPrincipal extends JFrame {

    // * Variables de instancia:
    // usuarioActual: usuario que inició sesión
    // tabla: componente visual de la tabla
    // modeloTabla: datos dentro de la tabla
    // panelFormulario: panel derecho donde aparece el formulario
    // moduloActivo: String que guarda el módulo en el que se encuentra,
    // por defecto: vehículos.

    private Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JPanel panelFormulario;
    private String moduloActivo = "vehiculos";

    // Variable de instancia para que no se acumulen ListSelectionListener
    private javax.swing.event.ListSelectionListener listenerTabla = null;

    // DAOs
    private final VehiculoDAOImpl vehiculoDAO = new VehiculoDAOImpl();
    private final AlquilerDAOImpl alquilerDAO = new AlquilerDAOImpl();
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    // Declaración de colores

    private static final Color COLOR_FONDO_OSCURO = new Color(30, 30, 46);
    private static final Color COLOR_FONDO_PANEL = new Color(24, 24, 37);
    private static final Color COLOR_FONDO_TABLA = new Color(49, 50, 68);
    private static final Color COLOR_ACENTO_AZUL = new Color(137, 180, 250);
    private static final Color COLOR_ACENTO_VERDE = new Color(166, 227, 161);
    private static final Color COLOR_ACENTO_ROJO = new Color(243, 139, 168);
    private static final Color COLOR_ACENTO_NARANJA = new Color(250, 179, 135);

    // Recibe objeto Usuario del Login y lo guarda, luego llama a
    // initComponents() que es donde se construye la interfaz.
    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
    }

    // Construcción de la interfaz
    private void initComponents() {
        setTitle("RentaCar - Panel Principal");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // * MENÚ
        
        // JMenuBar -> barra entera del menú
        // JMenu -> cada desplegable (sesión, tema)
        // JMenuItem -> cada opción dentro del desplegable
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_FONDO_OSCURO);

        JMenu menuSesion = new JMenu("Sesión");
        menuSesion.setForeground(Color.WHITE);

        JMenuItem itemCambiarPass = new JMenuItem("Cambiar contraseña");
        JMenuItem itemCerrar = new JMenuItem("Cerrar sesión");

        // Cambiar contraseña dentro de Sesión
        itemCambiarPass.addActionListener(e -> cambiarPassword());
        itemCerrar.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });

        menuSesion.add(itemCambiarPass);
        menuSesion.add(itemCerrar);
        menuBar.add(menuSesion);

        JMenu menuTema = new JMenu("Tema");
        menuTema.setForeground(Color.WHITE);
        JMenuItem itemOscuro = new JMenuItem("Modo oscuro");
        JMenuItem itemClaro = new JMenuItem("Modo claro");
        itemOscuro.addActionListener(e -> aplicarTema(true));
        itemClaro.addActionListener(e -> aplicarTema(false));
        menuTema.add(itemOscuro);
        menuTema.add(itemClaro);
        menuBar.add(menuTema);

        setJMenuBar(menuBar);

        // LAYOUT PRINCIPAL
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO_OSCURO);

        // PANEL LATERAL NAVEGACIÓN 
        JPanel panelNav = new JPanel();
        // Y_AXIS: apila los componentes verticalmente uno debajo de otro
        panelNav.setLayout(new BoxLayout(panelNav, BoxLayout.Y_AXIS));
        panelNav.setBackground(COLOR_FONDO_PANEL);
        // Ancho 180px, alto 0px porque en BorderLayout ya se configuró 
        // que el lado izquierdo ocupase todo el espacio disponible automáticamente.
        panelNav.setPreferredSize(new Dimension(180, 0));
        // Padding interior: 20px arriba y abajo, 10px izq y derecha.
        panelNav.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblMenu = new JLabel("MÓDULOS");
        lblMenu.setForeground(COLOR_ACENTO_AZUL);
        lblMenu.setFont(new Font("Garamond", Font.BOLD, 12));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNav.add(lblMenu);
        panelNav.add(Box.createVerticalStrut(15));

        JButton btnVehiculos = crearBotonNav("Vehículos");
        JButton btnAlquileres = crearBotonNav("Alquileres");
        JButton btnUsuarios = crearBotonNav("Usuarios");

        btnVehiculos.addActionListener(e -> cambiarModulo("vehiculos"));
        btnAlquileres.addActionListener(e -> cambiarModulo("alquileres"));
        btnUsuarios.addActionListener(e -> cambiarModulo("usuarios"));

        panelNav.add(btnVehiculos);
        // createVerticalStrut -> espacio vacío de 8px entre botones.
        panelNav.add(Box.createVerticalStrut(8));
        panelNav.add(btnAlquileres);
        panelNav.add(Box.createVerticalStrut(8));

        // El módulo Usuarios solo es visible para empleados
        if (usuarioActual.getRol().equals("empleado")) {
            panelNav.add(Box.createVerticalStrut(8));
            panelNav.add(btnUsuarios);
        }

        // Info usuario

        // Box.createVerticalGlue: espacio elástico que empuja todo lo que viene
        // después hacia abajo.
        panelNav.add(Box.createVerticalGlue());
        JLabel lblUsuario = new JLabel("<html><center>" + usuarioActual.getNombre()
                + "<br><small>" + usuarioActual.getRol() + "</small></center></html>");
        lblUsuario.setForeground(COLOR_ACENTO_VERDE);
        lblUsuario.setFont(new Font("Garamond", Font.PLAIN, 12));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNav.add(lblUsuario);

        panelPrincipal.add(panelNav, BorderLayout.WEST);

        // PANEL CENTRAL (tabla)

        // Se devuelve false para que las celdas no sean editables directamente
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setBackground(COLOR_FONDO_TABLA);
        tabla.setForeground(Color.WHITE);
        tabla.setFont(new Font("Garamond", Font.PLAIN, 13));
        tabla.setRowHeight(26);
        tabla.getTableHeader().setBackground(COLOR_ACENTO_AZUL);
        tabla.getTableHeader().setForeground(COLOR_FONDO_OSCURO);
        // Colores cuando la fila estña seleccionada
        tabla.setSelectionBackground(COLOR_ACENTO_AZUL);
        tabla.setSelectionForeground(COLOR_FONDO_OSCURO);

        // Si hay muchas filas, permite hacer scroll
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(COLOR_FONDO_TABLA);
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        // PANEL FORMULARIO DERECHO
        panelFormulario = new JPanel();
        panelFormulario.setBackground(COLOR_FONDO_PANEL);
        panelFormulario.setPreferredSize(new Dimension(230, 0));
        panelPrincipal.add(panelFormulario, BorderLayout.EAST);

        add(panelPrincipal);

        // Cargar módulo por defecto
        cambiarModulo("vehiculos");
    }

    // CAMBIAR MÓDULO
    private void cambiarModulo(String modulo) {
        this.moduloActivo = modulo;
        switch (modulo) {
            case "vehiculos" -> {
                cargarTablaVehiculos();
                mostrarFormVehiculo(null);
            }
            case "alquileres" -> {
                cargarTablaAlquileres();
                mostrarFormAlquiler(null);
            }
            case "usuarios" -> {
                cargarTablaUsuarios();
                mostrarFormUsuario(null);
            }
        }
    }

    // VEHÍCULOS
    private void cargarTablaVehiculos() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Matrícula");
        modeloTabla.addColumn("Marca");
        modeloTabla.addColumn("Modelo");
        modeloTabla.addColumn("Año");
        modeloTabla.addColumn("Categoría");
        modeloTabla.addColumn("€/día");
        modeloTabla.addColumn("Disponible");

        for (Vehiculo v : vehiculoDAO.listarTodos()) {
            modeloTabla.addRow(new Object[] {
                    v.getId(), v.getMatricula(), v.getMarca(), v.getModelo(),
                    v.getAnio(), v.getCategoria(), v.getPrecioDia(),
                    v.isDisponible() ? "SÍ" : "NO"
            });
        }
    }

    // Mostrar Form de Vehículos
    private void mostrarFormVehiculo(Vehiculo v) {

        // Elimina los componentes que estuvieran antes
        panelFormulario.removeAll();
        panelFormulario.setLayout(new GridBagLayout());

        // Grids para controlar cómo se colocan los campos
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel lblTitulo = new JLabel("Gestión Vehículos", SwingConstants.CENTER);
        lblTitulo.setForeground(COLOR_ACENTO_AZUL);
        lblTitulo.setFont(new Font("Garamond", Font.BOLD, 14));
        gbc.gridy = 0;
        panelFormulario.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        // Llamada a campo: añade una etiqueta y un campo de texto al panel,
        // y devuelve un JTextField para poder leerlo después.
        // el número es la fila en el GridBag.
        JTextField txtMatricula = campo(gbc, panelFormulario, 1, "Matrícula:");
        JTextField txtMarca = campo(gbc, panelFormulario, 2, "Marca:");
        JTextField txtModelo = campo(gbc, panelFormulario, 3, "Modelo:");
        JTextField txtAnio = campo(gbc, panelFormulario, 4, "Año:");
        JTextField txtCategoria = campo(gbc, panelFormulario, 5, "Categoría:");
        JTextField txtPrecio = campo(gbc, panelFormulario, 6, "€/día:");

        // Si hay fila seleccionada, rellenar con los datos
        registrarListenerTabla(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                txtMatricula.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtMarca.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtModelo.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtAnio.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtCategoria.setText(modeloTabla.getValueAt(fila, 5).toString());
                txtPrecio.setText(modeloTabla.getValueAt(fila, 6).toString());
            }
        });

        // IMPORTANTE: Botones
        gbc.gridwidth = 2;
        JButton btnNuevo = crearBotonAccion("Nuevo", COLOR_ACENTO_AZUL);
        JButton btnGuardar = crearBotonAccion("Guardar", COLOR_ACENTO_VERDE);
        JButton btnEliminar = crearBotonAccion("Eliminar", COLOR_ACENTO_ROJO);

        // Restricciones: desactiva el botón eliminar si es cliente y guardar vehículo
        btnEliminar.setEnabled(usuarioActual.getRol().equals("empleado"));
        btnGuardar.setEnabled(usuarioActual.getRol().equals("empleado"));
        
        gbc.gridy = 7;
        panelFormulario.add(btnNuevo, gbc);
        gbc.gridy = 8;
        panelFormulario.add(btnGuardar, gbc);
        gbc.gridy = 9;
        panelFormulario.add(btnEliminar, gbc);

        btnNuevo.addActionListener(e -> {
            txtMatricula.setText("");
            txtMarca.setText("");
            txtModelo.setText("");
            txtAnio.setText("");
            txtCategoria.setText("");
            txtPrecio.setText("");
            tabla.clearSelection();
            cargarTablaVehiculos();
        });

        // GUARDAR: crea objeto Vehículo, comprueba si hay fila seleccionada,
        // si la hay -> se llama a actualizar,
        // sino, se llama a insertar y MySQL genera el ID del vehículo solo.

        btnGuardar.addActionListener(e -> {
            try {
                Vehiculo nv = new Vehiculo(0,
                        txtMatricula.getText().trim(),
                        txtMarca.getText().trim(),
                        txtModelo.getText().trim(),
                        Integer.parseInt(txtAnio.getText().trim()),
                        txtCategoria.getText().trim(),
                        Double.parseDouble(txtPrecio.getText().trim()),
                        true);

                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    nv.setId((int) modeloTabla.getValueAt(fila, 0));
                    vehiculoDAO.actualizar(nv);
                    JOptionPane.showMessageDialog(this, "Vehículo actualizado.");
                } else {
                    vehiculoDAO.insertar(nv);
                    JOptionPane.showMessageDialog(this, "Vehículo añadido.");
                }

                // Refresca la tabla para que el tiempo se vea inmediatamente.
                cargarTablaVehiculos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ELIMINAR
        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un vehículo.");
                return;
            }
            int id = (int) modeloTabla.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar vehículo?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                vehiculoDAO.eliminar(id);
                cargarTablaVehiculos();
                JOptionPane.showMessageDialog(this, "Vehículo eliminado.");
            }
        });

        // Panel conversor de divisas
        gbc.gridwidth = 2;
        // Celda con la conversión
        JLabel lblConversor = new JLabel("── Conversor divisas ──", SwingConstants.CENTER);
        lblConversor.setForeground(COLOR_ACENTO_NARANJA);
        lblConversor.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridy = 10;
        panelFormulario.add(lblConversor, gbc);

        // Unidades a cambiar disponibles
        String[] divisas = { "USD", "GBP", "JPY", "CHF", "MXN" };
        JComboBox<String> cmbDivisa = new JComboBox<>(divisas);
        cmbDivisa.setBackground(COLOR_FONDO_TABLA);
        cmbDivisa.setForeground(Color.WHITE);
        gbc.gridy = 11;
        panelFormulario.add(cmbDivisa, gbc);

        JLabel lblResultado = new JLabel("Selecciona un vehículo", SwingConstants.CENTER);
        lblResultado.setForeground(COLOR_ACENTO_VERDE);
        lblResultado.setFont(new Font("Garamond", Font.BOLD, 12));
        gbc.gridy = 12;
        panelFormulario.add(lblResultado, gbc);

        // Botón para convertir
        JButton btnConvertir = crearBotonAccion("Convertir precio", COLOR_ACENTO_NARANJA);
        gbc.gridy = 13;
        panelFormulario.add(btnConvertir, gbc);

        btnConvertir.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un vehículo primero.");
                return;
            }

            double precioDia = Double.parseDouble(
                    modeloTabla.getValueAt(fila, 6).toString());
            String divisa = (String) cmbDivisa.getSelectedItem();
            lblResultado.setText("Calculando...");

            // Llamada en hilo separado para no bloquear la UI
            new Thread(() -> {
                double resultado = api.ExchangeRateService.convertir(precioDia, divisa);
                // Actualiza el label con el resultado
                SwingUtilities.invokeLater(() -> {
                    if (resultado > 0) {
                        lblResultado.setText(precioDia + " € = " + resultado + " " + divisa);
                    } else {
                        lblResultado.setText("Error al obtener tasa");
                    }
                });
            }).start();
        });

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    // ALQUILERES
    private void cargarTablaAlquileres() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Cliente");
        modeloTabla.addColumn("Vehículo");
        modeloTabla.addColumn("Empleado");
        modeloTabla.addColumn("Inicio");
        modeloTabla.addColumn("Fin");
        modeloTabla.addColumn("Total €");
        modeloTabla.addColumn("Estado");

        for (AlquilerDTO a : alquilerDAO.listarTodos()) {
            modeloTabla.addRow(new Object[] {
                    a.getId(), a.getNombreCliente(), a.getVehiculo(),
                    a.getNombreEmpleado(), a.getFechaInicio(), a.getFechaFin(),
                    a.getPrecioTotal(), a.getEstado()
            });
        }
    }

    private void mostrarFormAlquiler(AlquilerDTO a) {
        panelFormulario.removeAll();
        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel lblTitulo = new JLabel("Gestión Alquileres", SwingConstants.CENTER);
        lblTitulo.setForeground(COLOR_ACENTO_AZUL);
        lblTitulo.setFont(new Font("Garamond", Font.BOLD, 14));
        gbc.gridy = 0;
        panelFormulario.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        JTextField txtClienteId = campo(gbc, panelFormulario, 1, "ID Cliente:");
        JTextField txtVehiculoId = campo(gbc, panelFormulario, 2, "ID Vehículo:");
        JTextField txtEmpleadoId = campo(gbc, panelFormulario, 3, "ID Empleado:");
        JTextField txtInicio = campo(gbc, panelFormulario, 4, "Inicio(yyyy-mm-dd):");
        JTextField txtFin = campo(gbc, panelFormulario, 5, "Fin(yyyy-mm-dd):");
        JTextField txtTotal = campo(gbc, panelFormulario, 6, "Total €:");

        String[] estados = { "activo", "finalizado", "cancelado" };
        JComboBox<String> cmbEstado = new JComboBox<>(estados);
        cmbEstado.setBackground(COLOR_FONDO_TABLA);
        cmbEstado.setForeground(Color.WHITE);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("Garamond", Font.PLAIN, 12));
        panelFormulario.add(lblEstado, gbc);
        gbc.gridx = 1;
        panelFormulario.add(cmbEstado, gbc);

        registrarListenerTabla(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0 && moduloActivo.equals("alquileres")) {
                // Recuperar el ID del alquiler para buscar los datos completos
                int idAlquiler = (int) modeloTabla.getValueAt(fila, 0);
                Alquiler alq = alquilerDAO.buscarPorId(idAlquiler);
                if (alq != null) {
                    txtClienteId.setText(String.valueOf(alq.getClienteId()));
                    txtVehiculoId.setText(String.valueOf(alq.getVehiculoId()));
                    txtEmpleadoId.setText(String.valueOf(alq.getEmpleadoId()));
                }
                txtInicio.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtFin.setText(modeloTabla.getValueAt(fila, 5).toString());
                txtTotal.setText(modeloTabla.getValueAt(fila, 6).toString());
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 7).toString());
            }
        });

        // Mejora: Calcular precio automáticamente cuando cambian las fechas
        Runnable calcularPrecio = () -> {
            try {
                double precioDia = 0;

                String idTexto = txtVehiculoId.getText().trim();
                if (!idTexto.isEmpty()) {
                    int idVehiculo = Integer.parseInt(idTexto);
                    for (Vehiculo v : vehiculoDAO.listarTodos()) {
                        if (v.getId() == idVehiculo) {
                            precioDia = v.getPrecioDia();
                            break;
                        }
                    }
                } else {
                    int fila = tabla.getSelectedRow();
                    if (fila >= 0) {
                        String matriculaYModelo = modeloTabla.getValueAt(fila, 2).toString();
                        for (Vehiculo v : vehiculoDAO.listarTodos()) {
                            if (matriculaYModelo.contains(v.getMatricula())) {
                                precioDia = v.getPrecioDia();
                                break;
                            }
                        }
                    }
                }

                LocalDate inicio = LocalDate.parse(txtInicio.getText().trim());
                LocalDate fin = LocalDate.parse(txtFin.getText().trim());
                long dias = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin);
                if (dias > 0 && precioDia > 0) {
                    txtTotal.setText(String.valueOf(dias * precioDia));
                }
            } catch (Exception ignored) {
            }
        };

        // Los tres FocusListeners van aquí, fuera del Runnable
        txtVehiculoId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                calcularPrecio.run();
            }
        });
        txtInicio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                calcularPrecio.run();
            }
        });
        txtFin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                calcularPrecio.run();
            }
        });

        gbc.gridwidth = 2;
        JButton btnNuevo = crearBotonAccion("Nuevo", COLOR_ACENTO_AZUL);
        JButton btnGuardar = crearBotonAccion("Guardar", COLOR_ACENTO_VERDE);
        JButton btnEliminar = crearBotonAccion("Eliminar", COLOR_ACENTO_ROJO);

        // Restricción: desactiva el botón eliminar si es cliente
        btnEliminar.setEnabled(usuarioActual.getRol().equals("empleado"));

        gbc.gridy = 8;
        panelFormulario.add(btnNuevo, gbc);
        gbc.gridy = 9;
        panelFormulario.add(btnGuardar, gbc);
        gbc.gridy = 10;
        panelFormulario.add(btnEliminar, gbc);

        btnNuevo.addActionListener(e -> {
            txtClienteId.setText("");
            txtVehiculoId.setText("");
            txtEmpleadoId.setText("");
            txtInicio.setText(LocalDate.now().toString());
            txtFin.setText("");
            txtTotal.setText("");
            tabla.clearSelection();
            cargarTablaAlquileres();
        });

        btnGuardar.addActionListener(e -> {
            try {
                int fila = tabla.getSelectedRow();
                Alquiler alq = new Alquiler(
                        fila >= 0 ? (int) modeloTabla.getValueAt(fila, 0) : 0,
                        Integer.parseInt(txtClienteId.getText().trim()),
                        Integer.parseInt(txtVehiculoId.getText().trim()),
                        Integer.parseInt(txtEmpleadoId.getText().trim()),
                        LocalDate.parse(txtInicio.getText().trim()),
                        LocalDate.parse(txtFin.getText().trim()),
                        Double.parseDouble(txtTotal.getText().trim()),
                        (String) cmbEstado.getSelectedItem());
                if (fila >= 0) {
                    alquilerDAO.actualizar(alq);
                    JOptionPane.showMessageDialog(this, "Alquiler actualizado.");
                } else {
                    alquilerDAO.insertar(alq);
                    JOptionPane.showMessageDialog(this, "Alquiler creado.");
                }
                cargarTablaAlquileres();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un alquiler.");
                return;
            }
            int id = (int) modeloTabla.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar alquiler?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                alquilerDAO.eliminar(id);
                cargarTablaAlquileres();
                JOptionPane.showMessageDialog(this, "Alquiler eliminado.");
            }
        });

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    // USUARIOS
    private void cargarTablaUsuarios() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Username");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Apellidos");
        modeloTabla.addColumn("Email");
        modeloTabla.addColumn("DNI");
        modeloTabla.addColumn("Rol");

        for (Usuario u : usuarioDAO.listarTodos()) {
            modeloTabla.addRow(new Object[] {
                    u.getId(), u.getUsername(), u.getNombre(),
                    u.getApellidos(), u.getEmail(), u.getDni(), u.getRol()
            });
        }
    }

    private void mostrarFormUsuario(Usuario u) {
        panelFormulario.removeAll();
        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel lblTitulo = new JLabel("Gestión Usuarios", SwingConstants.CENTER);
        lblTitulo.setForeground(COLOR_ACENTO_AZUL);
        lblTitulo.setFont(new Font("Garamond", Font.BOLD, 14));
        gbc.gridy = 0;
        panelFormulario.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        JTextField txtNombre = campo(gbc, panelFormulario, 1, "Nombre:");
        JTextField txtApellidos = campo(gbc, panelFormulario, 2, "Apellidos:");
        JTextField txtEmail = campo(gbc, panelFormulario, 3, "Email:");
        JTextField txtDni = campo(gbc, panelFormulario, 4, "DNI:");

        registrarListenerTabla(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0 && moduloActivo.equals("usuarios")) {
                txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtApellidos.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtEmail.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtDni.setText(modeloTabla.getValueAt(fila, 5).toString());
            }
        });

        gbc.gridwidth = 2;
        JButton btnGuardar = crearBotonAccion("Guardar", COLOR_ACENTO_VERDE);
        JButton btnEliminar = crearBotonAccion("Eliminar", COLOR_ACENTO_ROJO);

        btnGuardar.setEnabled(usuarioActual.getRol().equals("empleado"));
        btnEliminar.setEnabled(usuarioActual.getRol().equals("empleado"));

        gbc.gridy = 5;
        panelFormulario.add(btnGuardar, gbc);
        gbc.gridy = 6;
        panelFormulario.add(btnEliminar, gbc);

        btnGuardar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario.");
                return;
            }
            Usuario editado = new Usuario(
                    (int) modeloTabla.getValueAt(fila, 0),
                    modeloTabla.getValueAt(fila, 1).toString(),
                    "", txtEmail.getText().trim(),
                    txtNombre.getText().trim(),
                    txtApellidos.getText().trim(),
                    txtDni.getText().trim(),
                    modeloTabla.getValueAt(fila, 6).toString());
            usuarioDAO.actualizar(editado);
            cargarTablaUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario actualizado.");
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario.");
                return;
            }
            int id = (int) modeloTabla.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                usuarioDAO.eliminar(id);
                cargarTablaUsuarios();
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
            }
        });

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    // MENÚ PEQUEÑO 

    // CAMBIAR PASSWORD
    private void cambiarPassword() {
        JPasswordField txtNueva = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this,
                new Object[] { "Nueva contraseña:", txtNueva },
                "Cambiar contraseña", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String nueva = new String(txtNueva.getPassword()).trim();
            if (!nueva.isEmpty()) {
                usuarioDAO.actualizarPassword(usuarioActual.getId(), nueva);
                JOptionPane.showMessageDialog(this, "Contraseña actualizada.");
            }
        }
    }

    // TEMA
    private void aplicarTema(boolean oscuro) {
        Color fondo = oscuro ? COLOR_FONDO_OSCURO : new Color(240, 240, 245);
        Color texto = oscuro ? Color.WHITE : Color.BLACK;
        tabla.setBackground(oscuro ? COLOR_FONDO_TABLA : Color.WHITE);
        tabla.setForeground(texto);
        getContentPane().setBackground(fondo);
        repaint();
    }

    // Método para registrar el Listener de la tabla
    private void registrarListenerTabla(javax.swing.event.ListSelectionListener nuevo) {
        if (listenerTabla != null) {
            tabla.getSelectionModel().removeListSelectionListener(listenerTabla);
        }
        listenerTabla = nuevo;
        tabla.getSelectionModel().addListSelectionListener(listenerTabla);
    }

    // HELPERS
    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Garamond", Font.PLAIN, 13));
        btn.setBackground(COLOR_FONDO_TABLA);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(160, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Garamond", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(COLOR_FONDO_OSCURO);
        btn.setFocusPainted(false);
        return btn;
    }

    private JTextField campo(GridBagConstraints gbc, JPanel panel, int fila, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Garamond", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(lbl, gbc);
        JTextField txt = new JTextField(10);
        txt.setBackground(COLOR_FONDO_TABLA);
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = fila;
        panel.add(txt, gbc);
        return txt;
    }
}