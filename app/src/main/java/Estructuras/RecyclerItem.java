package Estructuras;

public class RecyclerItem {
    String contenido;
    String titulo;
    int icon_path;


    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(int icon_path) {
        this.icon_path = icon_path;
    }

    public RecyclerItem(String contenido, String titulo, int icon_path) {
        this.contenido = contenido;
        this.titulo = titulo;
        this.icon_path = icon_path;
    }
}
