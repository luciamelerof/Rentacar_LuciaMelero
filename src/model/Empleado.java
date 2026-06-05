package model;

import java.time.LocalDate;

public class Empleado extends Usuario {
    private double salario;
    private LocalDate fechaAlta;

    public Empleado() {
    }

    public Empleado(int id, String username, String password, String email,
            String nombre, String apellidos, String dni,
            double salario, LocalDate fechaAlta) {
        super(id, username, password, email, nombre, apellidos, dni, "empleado");
        this.salario = salario;
        this.fechaAlta = fechaAlta;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}