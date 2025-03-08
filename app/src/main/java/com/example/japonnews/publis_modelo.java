package com.example.japonnews;


import java.util.List;

public class publis_modelo {
    private String titulo;
    private String detalle;
    private String userId;
    private String clasifId;
    private String fecha;
    private String tipoPublicacion;
    private String imagen;

    public publis_modelo() {
        // Constructor vacío requerido por Firestore
    }

    public publis_modelo(String titulo, String detalle, String userId, String clasifId, String fecha,
                         String tipoPublicacion, String imagen) {
        this.titulo = titulo;
        this.detalle = detalle;
        this.userId = userId;
        this.clasifId = clasifId;
        this.tipoPublicacion = tipoPublicacion;
        this.imagen = imagen;
    }


    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getClasifId() { return clasifId; }
    public void setClasifId(String clasifId) { this.clasifId = clasifId; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getTipoPublicacion() { return tipoPublicacion;
    }
    public void setTipoPublicacion(String tipoPublicacion) {this.tipoPublicacion=tipoPublicacion;}
}
