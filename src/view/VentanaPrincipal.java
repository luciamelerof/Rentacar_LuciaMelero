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

    private Usuario usuarioActual;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JPanel panelFormulario;
    private String moduloActivo = "vehiculos";

    // DAOs
    private final VehiculoDAOImpl vehiculoDAO = new VehiculoDAOImpl();
    private final AlquilerDAOImpl alquilerDAO = new AlquilerDAOImpl();
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
    }

    private void initComponents() {
        setTitle("RentaCar - Panel Principal");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── MENÚ ──────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 46));

        JMenu menuSesion = new JMenu("Sesión");
        menuSesion.setForeground(Color.WHITE);

        JMenuItem itemCambiarPass = new JMenuItem("Cambiar contraseña");
        JMenuItem itemCerrar = new JMenuItem("Cerrar sesión");

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

        // ── LAYOUT PRINCIPAL ──────────────────────────────
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 46));

        // ── PANEL LATERAL NAVEGACIÓN ──────────────────────
        JPanel panelNav = new JPanel();
        panelNav.setLayout(new BoxLayout(panelNav, BoxLayout.Y_AXIS));
        panelNav.setBackground(new Color(24, 24, 37));
        panelNav.setPreferredSize(new Dimension(180, 0));
        panelNav.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblMenu = new JLabel("MÓDULOS");
        lblMenu.setForeground(new Color(137, 180, 250));
        lblMenu.setFont(new Font("Arial", Font.BOLD, 12));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNav.add(lblMenu);
        panelNav.add(Box.createVerticalStrut(15));

        JButton btnVehiculos = crearBotonNav("🚗 Vehículos");
        JButton btnAlquileres = crearBotonNav("📋 Alquileres");
        JButton btnUsuarios = crearBotonNav("👥 Usuarios");

        btnVehiculos.addActionListener(e -> cambiarModulo("vehiculos"));
        btnAlquileres.addActionListener(e -> cambiarModulo("alquileres"));
        btnUsuarios.addActionListener(e -> cambiarModulo("usuarios"));

        panelNav.add(btnVehiculos);
        panelNav.add(Box.createVerticalStrut(8));
        panelNav.add(btnAlquileres);
        panelNav.add(Box.createVerticalStrut(8));
        panelNav.add(btnUsuarios);

        // Info usuario
        panelNav.add(Box.createVerticalGlue());
        JLabel lblUsuario = new JLabel("<html><center>" + usuarioActual.getNombre()
                + "<br><small>" + usuarioActual.getRol() + "</small></center></html>");
        lblUsuario.setForeground(new Color(166, 227, 161));
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNav.add(lblUsuario);

        panelPrincipal.add(panelNav, BorderLayout.WEST);

        // ── PANEL CENTRAL (tabla) ──────────────────────────
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setBackground(new Color(49, 50, 68));
        tabla.setForeground(Color.WHITE);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(26);
        tabla.getTableHeader().setBackground(new Color(137, 180, 250));
        tabla.getTableHeader().setForeground(new Color(30, 30, 46));
        tabla.setSelectionBackground(new Color(137, 180, 250));
        tabla.setSelectionForeground(new Color(30, 30, 46));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(49, 50, 68));
        panelPrincipal.add(scroll, BorderLayout.CENTER);

        // ── PANEL FORMULARIO DERECHO ───────────────────────
        panelFormulario = new JPanel();
        panelFormulario.setBackground(new Color(24, 24, 37));
        panelFormulario.setPreferredSize(new Dimension(230, 0));
        panelPrincipal.add(panelFormulario, BorderLayout.EAST);

        add(panelPrincipal);

        // Cargar módulo por defecto
        cambiarModulo("vehiculos");
    }

    // ── CAMBIAR MÓDULO ─────────────────────────────────────
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

    // ── VEHÍCULOS ──────────────────────────────────────────
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
                    v.isDisponible() ? "✅" : "❌"
            });
        }
    }

    private void mostrarFormVehiculo(Vehiculo v) {
        panelFormulario.removeAll();
        panelFormulario.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel lblTitulo = new JLabel("Gestión Vehículos", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(137, 180, 250));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 0;
        panelFormulario.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        JTextField txtMatricula = campo(gbc, panelFormulario, 1, "Matrícula:");
        JTextField txtMarca = campo(gbc, panelFormulario, 2, "Marca:");
        JTextField txtModelo = campo(gbc, panelFormulario, 3, "Modelo:");
        JTextField txtAnio = campo(gbc, panelFormulario, 4, "Año:");
        JTextField txtCategoria = campo(gbc, panelFormulario, 5, "Categoría:");
        JTextField txtPrecio = campo(gbc, panelFormulario, 6, "€/día:");

        // Si hay fila seleccionada, rellenar
        tabla.getSelectionModel().addListSelectionListener(e -> {
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

        // Botones
        gbc.gridwidth = 2;
        JButton btnNuevo = crearBotonAccion("Nuevo", new Color(137, 180, 250));
        JButton btnGuardar = crearBotonAccion("Guardar", new Color(166, 227, 161));
        JButton btnEliminar = crearBotonAccion("Eliminar", new Color(243, 139, 168));

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
        });

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
                cargarTablaVehiculos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    // ── ALQUILERES ─────────────────────────────────────────
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
        lblTitulo.setForeground(new Color(137, 180, 250));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
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
        cmbEstado.setBackground(new Color(49, 50, 68));
        cmbEstado.setForeground(Color.WHITE);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        panelFormulario.add(lblEstado, gbc);
        gbc.gridx = 1;
        panelFormulario.add(cmbEstado, gbc);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0 && moduloActivo.equals("alquileres")) {
                txtClienteId.setText("");
                txtVehiculoId.setText("");
                txtEmpleadoId.setText("");
                txtInicio.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtFin.setText(modeloTabla.getValueAt(fila, 5).toString());
                txtTotal.setText(modeloTabla.getValueAt(fila, 6).toString());
                cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 7).toString());
            }
        });

        gbc.gridwidth = 2;
        JButton btnNuevo = crearBotonAccion("Nuevo", new Color(137, 180, 250));
        JButton btnGuardar = crearBotonAccion("Guardar", new Color(166, 227, 161));
        JButton btnEliminar = crearBotonAccion("Eliminar", new Color(243, 139, 168));

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

    // ── USUARIOS ───────────────────────────────────────────
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
        lblTitulo.setForeground(new Color(137, 180, 250));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 0;
        panelFormulario.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        JTextField txtNombre = campo(gbc, panelFormulario, 1, "Nombre:");
        JTextField txtApellidos = campo(gbc, panelFormulario, 2, "Apellidos:");
        JTextField txtEmail = campo(gbc, panelFormulario, 3, "Email:");
        JTextField txtDni = campo(gbc, panelFormulario, 4, "DNI:");

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0 && moduloActivo.equals("usuarios")) {
                txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtApellidos.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtEmail.setText(modeloTabla.getValueAt(fila, 4).toString());
                txtDni.setText(modeloTabla.getValueAt(fila, 5).toString());
            }
        });

        gbc.gridwidth = 2;
        JButton btnGuardar = crearBotonAccion("Guardar", new Color(166, 227, 161));
        JButton btnEliminar = crearBotonAccion("Eliminar", new Color(243, 139, 168));

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

    // ── CAMBIAR PASSWORD ───────────────────────────────────
    private void cambiarPassword() {
        JPasswordField txtNueva = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this,
                new Object[] { "Nueva contraseña:", txtNueva },
                "Cambiar contraseña", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String nueva = new String(txtNueva.getPassword()).trim();
            if (!nueva.isEmpty()) {
                usuarioActual.setPassword(nueva);
                usuarioDAO.actualizar(usuarioActual);
                JOptionPane.showMessageDialog(this, "Contraseña actualizada.");
            }
        }
    }

    // ── TEMA ───────────────────────────────────────────────
    private void aplicarTema(boolean oscuro) {
        Color fondo = oscuro ? new Color(30, 30, 46) : new Color(240, 240, 245);
        Color texto = oscuro ? Color.WHITE : Color.BLACK;
        tabla.setBackground(oscuro ? new Color(49, 50, 68) : Color.WHITE);
        tabla.setForeground(texto);
        getContentPane().setBackground(fondo);
        repaint();
    }

    // ── HELPERS ────────────────────────────────────────────
    private JButton crearBotonNav(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(new Color(49, 50, 68));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(160, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(new Color(30, 30, 46));
        btn.setFocusPainted(false);
        return btn;
    }

    private JTextField campo(GridBagConstraints gbc, JPanel panel, int fila, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(lbl, gbc);
        JTextField txt = new JTextField(10);
        txt.setBackground(new Color(49, 50, 68));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = fila;
        panel.add(txt, gbc);
        return txt;
    }
}