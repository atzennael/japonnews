package com.example.japonnews;

public class post_modelo {
    private String nombre, correo, telefono;

    public post_modelo() {
    }

    public post_modelo(String nombre, String correo, String telefono) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }
}

