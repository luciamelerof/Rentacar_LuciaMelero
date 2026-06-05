package dao;

import db.ConexionDB;
import dto.AlquilerDTO;
import model.Alquiler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAOImpl implements IAlquilerDAO {

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
                ps.setInt(3, alquiler.getEmpleadoId());
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
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

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
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM alquileres WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar alquiler: " + e.getMessage());
        }
    }

    @Override
    public List<AlquilerDTO> listarTodos() {
        List<AlquilerDTO> lista = new ArrayList<>();
        String sql = """
                SELECT a.id,
                       CONCAT(u1.nombre, ' ', u1.apellidos) AS cliente,
                       CONCAT(v.marca, ' ', v.modelo, ' (', v.matricula, ')') AS vehiculo,
                       CONCAT(u2.nombre, ' ', u2.apellidos) AS empleado,
                       a.fecha_inicio, a.fecha_fin,
                       a.precio_total, a.estado
                FROM alquileres a
                JOIN clientes   c  ON a.cliente_id  = c.usuario_id
                JOIN usuarios   u1 ON c.usuario_id  = u1.id
                JOIN vehiculos  v  ON a.vehiculo_id  = v.id
                JOIN empleados  e  ON a.empleado_id  = e.usuario_id
                JOIN usuarios   u2 ON e.usuario_id   = u2.id
                ORDER BY a.fecha_inicio DESC
                """;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new AlquilerDTO(
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("vehiculo"),
                    rs.getString("empleado"),
                    rs.getDate("fecha_inicio").toLocalDate(),
                    rs.getDate("fecha_fin").toLocalDate(),
                    rs.getDouble("precio_total"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar alquileres: " + e.getMessage());
        }
        return lista;
    }
}
