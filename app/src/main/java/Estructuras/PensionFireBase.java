package Estructuras;

public class PensionFireBase {

    String nombre;
    String apellidos;
    Double SBC;
    Double UMA_pensionado;
    long edadJubilacion;
    Boolean conyuge;
    long hijos;
    long padres;
    long semanasCotizadas;
    Double PENSION_FINAL;

    public PensionFireBase() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Double getSBC() {
        return SBC;
    }

    public void setSBC(Double SBC) {
        this.SBC = SBC;
    }

    public Double getUMA_pensionado() {
        return UMA_pensionado;
    }

    public void setUMA_pensionado(Double UMA_pensionado) {
        this.UMA_pensionado = UMA_pensionado;
    }

    public long getEdadJubilacion() {
        return edadJubilacion;
    }

    public void setEdadJubilacion(long edadJubilacion) {
        this.edadJubilacion = edadJubilacion;
    }

    public Boolean getConyuge() {
        return conyuge;
    }

    public void setConyuge(Boolean conyuge) {
        this.conyuge = conyuge;
    }

    public long getHijos() {
        return hijos;
    }

    public void setHijos(long hijos) {
        this.hijos = hijos;
    }

    public long getPadres() {
        return padres;
    }

    public void setPadres(long padres) {
        this.padres = padres;
    }

    public long getSemanasCotizadas() {
        return semanasCotizadas;
    }

    public void setSemanasCotizadas(long semanasCotizadas) {
        this.semanasCotizadas = semanasCotizadas;
    }

    public Double getPENSION_FINAL() {
        return PENSION_FINAL;
    }

    public void setPENSION_FINAL(Double PENSION_FINAL) {
        this.PENSION_FINAL = PENSION_FINAL;
    }
}
