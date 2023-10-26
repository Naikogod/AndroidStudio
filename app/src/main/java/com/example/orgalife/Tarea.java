package com.example.orgalife;

public class Tarea {
    private String nombre;
    private String descripcion;
    private String etiqueta;
    private String imageUrl;

    // Constructor
    public Tarea(String nombre, String descripcion, String etiqueta, String imageUrl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.etiqueta = etiqueta;
        this.imageUrl = imageUrl;
    }

    // Getter y Setter para el nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter y Setter para la descripción
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Getter y Setter para la etiqueta
    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // Getter y Setter para la URL de la imagen
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
