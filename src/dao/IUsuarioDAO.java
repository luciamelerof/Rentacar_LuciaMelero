package dao;

import model.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    Usuario validar(String username, String password);
    void registrarCliente(model.Cliente cliente);
    void registrarEmpleado(model.Empleado empleado);
    List<Usuario> listarTodos();
    void actualizar(Usuario usuario);
    void eliminar(int id);
}