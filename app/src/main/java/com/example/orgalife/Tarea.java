package com.example.orgalife;

public class Tarea {
    private String nombre;
    private String descripcion;
    private String etiqueta;
    private String imageUrl;
    private String nombreDocumento;

    public Tarea(String nombre, String descripcion, String etiqueta, String imageUrl, String nombreDocumento) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.etiqueta = etiqueta;
        this.imageUrl = imageUrl;
        this.nombreDocumento = nombreDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }
}
