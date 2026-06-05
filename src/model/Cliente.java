package model;

public class Cliente extends Usuario {
    private String telefono;
    private String direccion;
    private String carnetConducir;

    public Cliente() {
    }

    public Cliente(int id, String username, String password, String email,
            String nombre, String apellidos, String dni,
            String telefono, String direccion, String carnetConducir) {
        super(id, username, password, email, nombre, apellidos, dni, "cliente");
        this.telefono = telefono;
        this.direccion = direccion;
        this.carnetConducir = carnetConducir;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCarnetConducir() {
        return carnetConducir;
    }

    public void setCarnetConducir(String carnetConducir) {
        this.carnetConducir = carnetConducir;
    }
}