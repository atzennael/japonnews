package com.example.japonnews;

public class publis_modelo {
    private String tituloP, detalleP, userIdP, clasifIdP;

    public publis_modelo(){

    }

    public publis_modelo(String tituloP, String detalleP, String userIdP, String clasifIdP){
        this.tituloP=tituloP;
        this.detalleP=detalleP;
        this.userIdP=userIdP;
        this.clasifIdP=clasifIdP;
    }

    public String getTituloP(){return tituloP; }

    public void setTituloP(String tituloP) {
        this.tituloP = tituloP;
    }

    public String getDetalleP() {
        return detalleP;
    }

    public void setDetalleP(String detalleP) {
        this.detalleP = detalleP;
    }

    public String getClasifIdP() {
        return clasifIdP;
    }
    public void setClasifIdP(String clasifIdP) {
        this.clasifIdP = clasifIdP;
    }

    public String userIdP() {
        return userIdP;
    }

    public void userIdP(String userIdP) {
        this.userIdP = userIdP;
    }
}
