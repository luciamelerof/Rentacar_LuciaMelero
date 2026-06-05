package view;

import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JButton btnRegistro;

    public Login() {
        initComponents();
    }

    private void initComponents() {
        setTitle("RentaCar - Iniciar Sesión");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(30, 30, 46));

        // Panel título
        JLabel lblTitulo = new JLabel("🚗 RentaCar", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(137, 180, 250));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Panel formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(new Color(30, 30, 46));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(lblUser, gbc);

        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0;
        formulario.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(lblPass, gbc);

        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1;
        formulario.add(txtPassword, gbc);

        panel.add(formulario, BorderLayout.CENTER);

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(new Color(30, 30, 46));

        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEntrar.setBackground(new Color(137, 180, 250));
        btnEntrar.setForeground(new Color(30, 30, 46));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setPreferredSize(new Dimension(110, 35));

        btnRegistro = new JButton("Registrarse");
        btnRegistro.setFont(new Font("Arial", Font.PLAIN, 13));
        btnRegistro.setBackground(new Color(49, 50, 68));
        btnRegistro.setForeground(Color.WHITE);
        btnRegistro.setFocusPainted(false);
        btnRegistro.setPreferredSize(new Dimension(110, 35));

        panelBotones.add(btnEntrar);
        panelBotones.add(btnRegistro);
        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel);

        // Acción botón Entrar
        btnEntrar.addActionListener(e -> login());

        // Entrar también con Enter
        txtPassword.addActionListener(e -> login());

        // Acción botón Registro
        btnRegistro.addActionListener(e -> {
            new Registro().setVisible(true);
            dispose();
        });
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor rellena todos los campos.",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAOImpl dao = new UsuarioDAOImpl();
        Usuario usuario = dao.validar(username, password);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this,
                "Bienvenido/a, " + usuario.getNombre() + "!",
                "Acceso correcto", JOptionPane.INFORMATION_MESSAGE);
            new VentanaPrincipal(usuario).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos.",
                "Error de acceso", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}