package dto;

import java.time.LocalDate;

// Clase para la interfaz para evitar que el Usuario pueda tener
// información de los alquileres no deseada, como los IDs.

public class AlquilerDTO {
    private int id;
    private String nombreCliente;
    private String vehiculo;
    private String nombreEmpleado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioTotal;
    private String estado;

    public AlquilerDTO(int id, String nombreCliente, String vehiculo,
            String nombreEmpleado, LocalDate fechaInicio,
            LocalDate fechaFin, double precioTotal, String estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.vehiculo = vehiculo;
        this.nombreEmpleado = nombreEmpleado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
