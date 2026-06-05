package dao;

import db.ConexionDB;
import model.Cliente;
import model.Empleado;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements IUsuarioDAO {

    @Override
    public Usuario validar(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection con = ConexionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("password");
                    System.out.println("Usuario encontrado: " + username);
                    System.out.println("Password BD: " + hashGuardado);
                    System.out.println("Password introducida: " + password);
                    System.out.println("¿Coinciden? " + hashGuardado.equals(password));

                    if (hashGuardado.equals(password)) {
                        return mapearUsuario(rs);
                    }
                } else {
                    System.out.println("Usuario NO encontrado en BD: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void registrarCliente(Cliente cliente) {
        String sqlUsuario = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?,?,?,?,?,?,'cliente')";
        String sqlCliente = "INSERT INTO clientes (usuario_id, telefono, direccion, carnet_conducir) VALUES (?,?,?,?)";

        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false);

            String hash = cliente.getPassword();

            try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cliente.getUsername());
                ps.setString(2, hash);
                ps.setString(3, cliente.getEmail());
                ps.setString(4, cliente.getNombre());
                ps.setString(5, cliente.getApellidos());
                ps.setString(6, cliente.getDni());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int idGenerado = keys.getInt(1);

                        try (PreparedStatement ps2 = con.prepareStatement(sqlCliente)) {
                            ps2.setInt(1, idGenerado);
                            ps2.setString(2, cliente.getTelefono());
                            ps2.setString(3, cliente.getDireccion());
                            ps2.setString(4, cliente.getCarnetConducir());
                            ps2.executeUpdate();
                        }
                    }
                }
            }

            con.commit();

        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void registrarEmpleado(Empleado empleado) {
        String sqlUsuario = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?,?,?,?,?,?,'empleado')";
        String sqlEmpleado = "INSERT INTO empleados (usuario_id, salario, fecha_alta) VALUES (?,?,?)";

        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false);

            String hash = empleado.getPassword();

            try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, empleado.getUsername());
                ps.setString(2, hash);
                ps.setString(3, empleado.getEmail());
                ps.setString(4, empleado.getNombre());
                ps.setString(5, empleado.getApellidos());
                ps.setString(6, empleado.getDni());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int idGenerado = keys.getInt(1);

                        try (PreparedStatement ps2 = con.prepareStatement(sqlEmpleado)) {
                            ps2.setInt(1, idGenerado);
                            ps2.setDouble(2, empleado.getSalario());
                            ps2.setDate(3, Date.valueOf(empleado.getFechaAlta()));
                            ps2.executeUpdate();
                        }
                    }
                }
            }

            con.commit();

        } catch (SQLException e) {
            System.err.println("Error al registrar empleado: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection con = ConexionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET email=?, nombre=?, apellidos=?, dni=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellidos());
            ps.setString(4, usuario.getDni());
            ps.setInt(5, usuario.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                rs.getString("rol"));
    }
}
