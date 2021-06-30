package Estructuras;

public class RecyclerItemHistorial {
    String NombreCompleto;
    String edadJub;
    String SBC;
    String Pension;

    public RecyclerItemHistorial() {
    }

    public RecyclerItemHistorial(String nombreCompleto, String pension, String SBC, String edadJub ) {
        NombreCompleto = nombreCompleto;
        this.edadJub = edadJub;
        this.SBC = SBC;
        Pension = pension;
    }

    public String getNombreCompleto() {
        return NombreCompleto;
    }

    public String getEdadJub() {
        return edadJub;
    }

    public String getSBC() {
        return SBC;
    }

    public String getPension() {
        return Pension;
    }

    public void setNombreCompleto(String nombreCompleto) {
        NombreCompleto = nombreCompleto;
    }

    public void setEdadJub(String edadJub) {
        this.edadJub = edadJub;
    }

    public void setSBC(String SBC) {
        this.SBC = SBC;
    }

    public void setPension(String pension) {
        Pension = pension;
    }
}
