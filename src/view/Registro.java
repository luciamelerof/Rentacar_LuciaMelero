package view;

import dao.UsuarioDAOImpl;
import model.Cliente;
import model.Empleado;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class Registro extends JFrame {

    private JTextField txtUsername, txtEmail, txtNombre, txtApellidos, txtDni;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;

    // Campos solo cliente
    private JLabel lblTelefono, lblDireccion, lblCarnet;
    private JTextField txtTelefono, txtDireccion, txtCarnet;

    // Campos solo empleado
    private JLabel lblSalario, lblFechaAlta;
    private JTextField txtSalario, txtFechaAlta;

    public Registro() {
        initComponents();
    }

    private void initComponents() {
        setTitle("RentaCar - Registro");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 46));

        // Título
        JLabel lblTitulo = new JLabel("Nuevo Usuario", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(137, 180, 250));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(30, 30, 46));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 15, 6, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campos comunes ---
        txtUsername  = new JTextField(18);
        txtPassword  = new JPasswordField(18);
        txtEmail     = new JTextField(18);
        txtNombre    = new JTextField(18);
        txtApellidos = new JTextField(18);
        txtDni       = new JTextField(18);
        cmbRol       = new JComboBox<>(new String[]{"cliente", "empleado"});

        agregarFila(form, gbc, 0, "Usuario:",    txtUsername);
        agregarFila(form, gbc, 1, "Contraseña:", txtPassword);
        agregarFila(form, gbc, 2, "Email:",      txtEmail);
        agregarFila(form, gbc, 3, "Nombre:",     txtNombre);
        agregarFila(form, gbc, 4, "Apellidos:",  txtApellidos);
        agregarFila(form, gbc, 5, "DNI:",        txtDni);
        agregarFila(form, gbc, 6, "Rol:",        cmbRol);

        // --- Campos cliente ---
        txtTelefono  = new JTextField(18);
        txtDireccion = new JTextField(18);
        txtCarnet    = new JTextField(18);
        lblTelefono  = crearLabel("Teléfono:");
        lblDireccion = crearLabel("Dirección:");
        lblCarnet    = crearLabel("Nº Carnet:");

        agregarFilaDinamica(form, gbc, 7,  lblTelefono,  txtTelefono);
        agregarFilaDinamica(form, gbc, 8,  lblDireccion, txtDireccion);
        agregarFilaDinamica(form, gbc, 9,  lblCarnet,    txtCarnet);

        // --- Campos empleado ---
        txtSalario   = new JTextField(18);
        txtFechaAlta = new JTextField(18);
        txtFechaAlta.setText(LocalDate.now().toString());
        lblSalario   = crearLabel("Salario (€):");
        lblFechaAlta = crearLabel("Fecha alta:");

        agregarFilaDinamica(form, gbc, 10, lblSalario,   txtSalario);
        agregarFilaDinamica(form, gbc, 11, lblFechaAlta, txtFechaAlta);

        panel.add(form, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(new Color(30, 30, 46));

        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(166, 227, 161));
        btnGuardar.setForeground(new Color(30, 30, 46));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(110, 35));

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 13));
        btnVolver.setBackground(new Color(49, 50, 68));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setPreferredSize(new Dimension(110, 35));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnVolver);
        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel);

        // Mostrar campos según rol seleccionado
        actualizarCampos("cliente");
        cmbRol.addActionListener(e -> actualizarCampos((String) cmbRol.getSelectedItem()));

        btnGuardar.addActionListener(e -> registrar());
        btnVolver.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void actualizarCampos(String rol) {
        boolean esCliente = rol.equals("cliente");

        lblTelefono.setVisible(esCliente);
        txtTelefono.setVisible(esCliente);
        lblDireccion.setVisible(esCliente);
        txtDireccion.setVisible(esCliente);
        lblCarnet.setVisible(esCliente);
        txtCarnet.setVisible(esCliente);

        lblSalario.setVisible(!esCliente);
        txtSalario.setVisible(!esCliente);
        lblFechaAlta.setVisible(!esCliente);
        txtFechaAlta.setVisible(!esCliente);

        revalidate();
        repaint();
    }

    private void registrar() {
        String username  = txtUsername.getText().trim();
        String password  = new String(txtPassword.getPassword());
        String email     = txtEmail.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni       = txtDni.getText().trim();
        String rol       = (String) cmbRol.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()
                || nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor rellena todos los campos obligatorios.",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAOImpl dao = new UsuarioDAOImpl();

        if (rol.equals("cliente")) {
            Cliente c = new Cliente(0, username, password, email, nombre, apellidos, dni,
                    txtTelefono.getText().trim(),
                    txtDireccion.getText().trim(),
                    txtCarnet.getText().trim());
            dao.registrarCliente(c);
        } else {
            double salario = 1500.0;
            try { salario = Double.parseDouble(txtSalario.getText().trim()); }
            catch (NumberFormatException ignored) {}

            LocalDate fecha = LocalDate.now();
            try { fecha = LocalDate.parse(txtFechaAlta.getText().trim()); }
            catch (Exception ignored) {}

            Empleado emp = new Empleado(0, username, password, email, nombre, apellidos, dni,
                    salario, fecha);
            dao.registrarEmpleado(emp);
        }

        JOptionPane.showMessageDialog(this,
            "Usuario registrado correctamente.",
            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        new Login().setVisible(true);
        dispose();
    }

    // --- Helpers ---
    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, String texto, JComponent campo) {
        JLabel lbl = crearLabel(texto);
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.gridy = fila;
        panel.add(campo, gbc);
    }

    private void agregarFilaDinamica(JPanel panel, GridBagConstraints gbc, int fila, JLabel lbl, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.gridy = fila;
        panel.add(campo, gbc);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        return lbl;
    }
}