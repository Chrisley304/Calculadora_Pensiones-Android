package Estructuras;

import java.io.Serializable;

public class Pensionado implements Serializable {

    String nombre;
    String apellidos;
    double SBC;
    double UMA_pensionado;
    int edadJubilacion;
    boolean conyuge;
    int hijos;
    int padres;
    int semanasCotizadas;
    double PENSION_FINAL;
    Pension pension = new Pension();

    public double getPENSION_FINAL() {
        return PENSION_FINAL;
    }

    public void setPENSION_FINAL(double PENSION_FINAL) {
        this.PENSION_FINAL = PENSION_FINAL;
    }

    public void setSBC(double SBC) {
        this.SBC = SBC;
    }

    public Pension getPension() {
        return pension;
    }

    public boolean isConyuge() {
        return conyuge;
    }

    public void setConyuge(boolean conyuge) {
        this.conyuge = conyuge;
    }

    public int getHijos() {
        return hijos;
    }

    public void setHijos(int hijos) {
        this.hijos = hijos;
    }

    public int getPadres() {
        return padres;
    }

    public void setPadres(int padres) {
        this.padres = padres;
    }

    public double getUMA_pensionado() {
        return UMA_pensionado;
    }

    public void setUMA_pensionado(double UMA_pensionado) {
        this.UMA_pensionado = UMA_pensionado;
    }

    public int getEdadJubilacion() {
        return edadJubilacion;
    }

    public void setEdadJubilacion(int edadJubilacion) {
        this.edadJubilacion = edadJubilacion;
    }

    public int getSemanasCotizadas() {
        return semanasCotizadas;
    }

    public void setSemanasCotizadas(int semanasCotizadas) {
        this.semanasCotizadas = semanasCotizadas;
    }

    public Pensionado(String nombre, String apellido, double SBC) {
        this.nombre = nombre;
        this.apellidos = apellido;
        this.SBC = SBC;
    }

    public Pensionado() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setPension(Pension pension) {
        this.pension = pension;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public double getSBC() {
        return SBC;
    }
}
