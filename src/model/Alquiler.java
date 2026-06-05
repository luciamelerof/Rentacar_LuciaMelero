package model;

import java.time.LocalDate;

public class Alquiler {
    private int id;
    private int clienteId;
    private int vehiculoId;
    private int empleadoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioTotal;
    private String estado;

    public Alquiler() {
    }

    public Alquiler(int id, int clienteId, int vehiculoId, int empleadoId,
            LocalDate fechaInicio, LocalDate fechaFin,
            double precioTotal, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.vehiculoId = vehiculoId;
        this.empleadoId = empleadoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(int vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public int getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
