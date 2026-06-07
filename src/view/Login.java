package view;

import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

/* 
FLUJO DE VENTANAS:
    1ª opción: 
        1. Se abre Login, 
        ¿credenciales incorrectas? -> se queda en el Login
        ¿credenciales correctas? ->
        2. abre VentanaPrincipal  y se cierra el Login.

    2ª opción: 
        1. botón Registrarse -> que abre Registro y cierra Login.
        2. Registro completado -> vuelve a la ventana Login. 
*/

public class Login extends JFrame {

    // Declaración de campos de texto y botones
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JButton btnRegistro;

    public Login() {
        initComponents();
    }

    // initComponents() -> método privado donde se construye la
    // interfaz. Es una convención para separarlo del constructor
    // y tener el código más organizado.

    private void initComponents() {
        setTitle("RentaCar - Iniciar Sesión");
        setSize(420, 320);
        // EXIT_ON_CLOSE: cuando el usuario cierra la ventana,
        // termina el programa.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Centra la ventana en la pantalla
        setLocationRelativeTo(null);
        // No se puede cambiar el tamaño de la ventana
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(30, 30, 46));

        // Panel título
        JLabel lblTitulo = new JLabel("RentaCar - Alquiler", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Garamond", Font.BOLD, 35));
        lblTitulo.setForeground(new Color(137, 180, 250));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // Panel formulario, tipo Grid (el más flexible).
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(new Color(30, 30, 46));
        // GridBagConstraints: objeto que define cómo se coloca
        // cada componente (fila, columna, espacio...).
        GridBagConstraints gbc = new GridBagConstraints();
        // Márgenes alrededor de los componentes
        gbc.insets = new Insets(8, 15, 8, 15);
        // El componente se estira horizontalmente para ocupar
        // todo el ancho disponible.
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Garamond", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(lblUser, gbc);

        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Garamond", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formulario.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(new Font("Garamond", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(lblPass, gbc);

        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Garamond", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formulario.add(txtPassword, gbc);

        panel.add(formulario, BorderLayout.CENTER);

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(new Color(30, 30, 46));

        btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Garamond", Font.BOLD, 14));
        btnEntrar.setBackground(new Color(137, 180, 250));
        btnEntrar.setForeground(new Color(30, 30, 46));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setPreferredSize(new Dimension(110, 35));

        btnRegistro = new JButton("Registrarse");
        btnRegistro.setFont(new Font("Garamond", Font.PLAIN, 13));
        btnRegistro.setBackground(new Color(49, 50, 68));
        btnRegistro.setForeground(Color.WHITE);
        btnRegistro.setFocusPainted(false);
        btnRegistro.setPreferredSize(new Dimension(110, 35));

        panelBotones.add(btnEntrar);
        panelBotones.add(btnRegistro);
        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel);

        // ActionListeners
        // e -> es una lambda para acortar la forma de escribir
        // una función anónima.

        // Acción botón Entrar, cuando se hace clic, llama al método login()
        btnEntrar.addActionListener(e -> login());

        // Entrar también con Enter desde el campo contraseña
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
                    "¡Bienvenido/a, " + usuario.getNombre() + "!",
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