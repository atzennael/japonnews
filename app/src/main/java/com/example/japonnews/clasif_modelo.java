package com.example.japonnews;

public class clasif_modelo {
    private String titulo;
    private String detalle;
    private String imagen;

    public clasif_modelo() { } // Constructor vacío

    public clasif_modelo(String titulo, String detalle, String imagen) {
        this.titulo = titulo;
        this.detalle = detalle;
        this.imagen = imagen;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
