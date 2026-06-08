package dao;

import db.ConexionDB;
import dto.AlquilerDTO;
import model.Alquiler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAOImpl implements IAlquilerDAO {

    // Insertar un alquiler en la BD
    @Override
    public void insertar(Alquiler alquiler) {
        String sql = "INSERT INTO alquileres (cliente_id, vehiculo_id, empleado_id, fecha_inicio, fecha_fin, precio_total, estado) VALUES (?,?,?,?,?,?,?)";

        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, alquiler.getClienteId());
                ps.setInt(2, alquiler.getVehiculoId());
                if (alquiler.getEmpleadoId() != null) {
                    ps.setInt(3, alquiler.getEmpleadoId());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setDate(4, Date.valueOf(alquiler.getFechaInicio()));
                ps.setDate(5, Date.valueOf(alquiler.getFechaFin()));
                ps.setDouble(6, alquiler.getPrecioTotal());
                ps.setString(7, alquiler.getEstado());
                ps.executeUpdate();
            }

            // Marcar vehículo como no disponible
            String sqlVehiculo = "UPDATE vehiculos SET disponible = FALSE WHERE id = ?";
            try (PreparedStatement ps2 = con.prepareStatement(sqlVehiculo)) {
                ps2.setInt(1, alquiler.getVehiculoId());
                ps2.executeUpdate();
            }

            con.commit();

        } catch (SQLException e) {
            System.err.println("Error al insertar alquiler: " + e.getMessage());
            e.printStackTrace();
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

    // Actualizar alquiler
    @Override
    public void actualizar(Alquiler alquiler) {
        String sql = "UPDATE alquileres SET cliente_id=?, vehiculo_id=?, empleado_id=?, fecha_inicio=?, fecha_fin=?, precio_total=?, estado=? WHERE id=?";

        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, alquiler.getClienteId());
                ps.setInt(2, alquiler.getVehiculoId());
                ps.setInt(3, alquiler.getEmpleadoId());
                ps.setDate(4, Date.valueOf(alquiler.getFechaInicio()));
                ps.setDate(5, Date.valueOf(alquiler.getFechaFin()));
                ps.setDouble(6, alquiler.getPrecioTotal());
                ps.setString(7, alquiler.getEstado());
                ps.setInt(8, alquiler.getId());
                ps.executeUpdate();
            }

            // Si el alquiler se finaliza o cancela, liberar el vehículo
            if (alquiler.getEstado().equals("finalizado") || alquiler.getEstado().equals("cancelado")) {
                String sqlVehiculo = "UPDATE vehiculos SET disponible = TRUE WHERE id = ?";
                try (PreparedStatement ps2 = con.prepareStatement(sqlVehiculo)) {
                    ps2.setInt(1, alquiler.getVehiculoId());
                    ps2.executeUpdate();
                }
            }

            con.commit();

        } catch (SQLException e) {
            System.err.println("Error al actualizar alquiler: " + e.getMessage());
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

    // Eliminar alquiler: si se elimina el alquiler, se libera el vehículo
    @Override
    public void eliminar(int id) {
        Connection con = null;
        try {
            con = ConexionDB.getConnection();
            con.setAutoCommit(false);

            // Primero recuperar el vehiculo_id antes de borrar
            int vehiculoId = -1;
            String sqlBuscar = "SELECT vehiculo_id FROM alquileres WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        vehiculoId = rs.getInt("vehiculo_id");
                    }
                }
            }

            // Borrar el alquiler
            String sqlDelete = "DELETE FROM alquileres WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Liberar el vehículo
            if (vehiculoId != -1) {
                String sqlVehiculo = "UPDATE vehiculos SET disponible = TRUE WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlVehiculo)) {
                    ps.setInt(1, vehiculoId);
                    ps.executeUpdate();
                }
            }

            con.commit();

        } catch (SQLException e) {
            System.err.println("Error al eliminar alquiler: " + e.getMessage());
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

    // Este listarTodos(), en lugar de devolver IDs numéricos, devuelve
    // los nombres reales de cliente, vehículo y empleado usando JOINs.

    // Se usa AlquilerDTO porque en la interfaz solo se quiere mostrar
    // nombres legibles, no los IDs del vehículo, cliente...
    @Override
    public List<AlquilerDTO> listarTodos() {
        List<AlquilerDTO> lista = new ArrayList<>();
        String sql = """
                    SELECT a.id,
                           CONCAT(uc.nombre, ' ', uc.apellidos) AS nombre_cliente,
                           CONCAT(v.marca, ' ', v.modelo, ' (', v.matricula, ')') AS vehiculo,
                           COALESCE(CONCAT(ue.nombre, ' ', ue.apellidos), 'Sin empleado') AS nombre_empleado,
                           a.fecha_inicio, a.fecha_fin, a.precio_total, a.estado
                    FROM alquileres a
                    JOIN clientes c    ON a.cliente_id  = c.usuario_id
                    JOIN usuarios uc   ON c.usuario_id  = uc.id
                    JOIN vehiculos v   ON a.vehiculo_id = v.id
                    LEFT JOIN empleados e  ON a.empleado_id = e.usuario_id
                    LEFT JOIN usuarios ue  ON e.usuario_id  = ue.id
                """;

        try (Connection con = ConexionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new AlquilerDTO(
                        rs.getInt("id"),
                        rs.getString("nombre_cliente"),
                        rs.getString("vehiculo"),
                        rs.getString("nombre_empleado"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getDouble("precio_total"),
                        rs.getString("estado")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar alquileres: " + e.getMessage());
        }
        return lista;
    }

    // Buscar un alquiler por ID, se usa en el listener de mostrarFormAlquiler
    // en VentanaPrincipal para que devuelva en los paneles los datos del alquiler
    // seleccionado.
    @Override
    public Alquiler buscarPorId(int id) {
        String sql = "SELECT * FROM alquileres WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Alquiler(
                            rs.getInt("id"),
                            rs.getInt("cliente_id"),
                            rs.getInt("vehiculo_id"),
                            rs.getInt("empleado_id"),
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin").toLocalDate(),
                            rs.getDouble("precio_total"),
                            rs.getString("estado"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
