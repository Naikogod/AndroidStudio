package com.example.orgalife;

public class Comentario {
    private String nombreUsuario;
    private String comentario;

    public Comentario(String nombreUsuario, String comentario) {
        this.nombreUsuario = nombreUsuario;
        this.comentario = comentario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getComentario() {
        return comentario;
    }
}
