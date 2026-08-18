package Telhado;

public class telhado {

    private tipoTrapezoidal tipoTrapezoidal;
    private double espessura;
    private int numeroDeApoios;
    private double distanciaApoio;
    private double peso;
    private enum tipoTrapezoidal {simples, duplo};
    //Getters e Setters
    public tipoTrapezoidal getTrapezoidal() {
        return tipoTrapezoidal;
    }
    public void setTrapezoidal() {
        this.tipoTrapezoidal = tipoTrapezoidal;
    }
    public double getEspessura() {  
        return espessura;
    }

    public void setEspessura(double espessura) {
        this.espessura = espessura;
    }

    public int getNumeroDeApoios() {
        return numeroDeApoios;
    }

    public void setNumeroDeApoios(int numeroDeApoios) {
        this.numeroDeApoios = numeroDeApoios;
    }

    public double getDistanciaApoio() {
        return distanciaApoio;
    }

    public void setDistanciaApoio(double distanciaApoio) {
        this.distanciaApoio = distanciaApoio;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    telhado(tipoTrapezoidal tipoTrapezoidal, double espessura, int numeroDeApoios, double distanciaApoio, double peso) {
        this.tipoTrapezoidal = tipoTrapezoidal;
        this.espessura = espessura;
        this.numeroDeApoios = numeroDeApoios;
        this.distanciaApoio = distanciaApoio;
        this.peso = peso;
    }
}
