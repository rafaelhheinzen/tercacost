package Perfil;

public class perfil {
    private enum Tipo {U, Z, Uenrijecido, Sigma};
    private Tipo tipo;
    private double comprimento;
    private double espessura;

    //Getters e Setters
    public Tipo getTipo() {
        return tipo;
    }
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
    public double getComprimento() {
        return comprimento;
    }

    public void setComprimento(double comprimento) {
        this.comprimento = comprimento;
    }

    public double getEspessura() {
        return espessura;
    }

    public void setEspessura(double espessura) {
        this.espessura = espessura;
    }

    public perfil(Tipo tipo, double comprimento, double espessura) {
        this.tipo = tipo;
        this.comprimento = comprimento;
        this.espessura = espessura;
    }
}