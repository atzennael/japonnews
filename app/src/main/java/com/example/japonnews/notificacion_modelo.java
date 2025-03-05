package com.example.japonnews;

import java.security.Timestamp;

public class notificacion_modelo {
    private String mensaje;
    private Timestamp fecha;

    public notificacion_modelo() {} // Constructor vacío para Firebase

    public notificacion_modelo(String mensaje, Timestamp fecha) {
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getMensaje() { return mensaje; }
    public Timestamp getFecha() { return fecha; }
}

