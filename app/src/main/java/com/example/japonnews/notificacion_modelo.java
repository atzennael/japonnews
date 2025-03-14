package com.example.japonnews;
import java.util.Date;


public class notificacion_modelo {
    private String mensaje, id, titulo, id_receptor,id_usuario;
    private Date fecha;

    public notificacion_modelo() {} // Constructor vacío para Firebase

    public notificacion_modelo(String mensaje, Date fecha, String id, String titulo, String id_receptor, String id_usuario) {
        this.mensaje = mensaje;
        this.id=id;
        this.id_receptor=id_receptor;
        this.titulo=titulo;
        this.fecha = fecha;
        this.id_usuario=id_usuario;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje=mensaje; }
    public String getId() {return id;}
    public void setId(String id) { this.id=id;}
    public String getTitulo(){ return titulo; }
    public void setTitulo(String titulo){ this.titulo=titulo; }
    public String getId_receptor() { return id_receptor;}
    public void setId_receptor(String id_receptor){this.id_receptor=id_receptor;}
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha){this.fecha=fecha;}
    public String getId_usuario(){return id_usuario;}
    public void setId_usuario(String id_usuario){this.id_usuario=id_usuario;}
}

