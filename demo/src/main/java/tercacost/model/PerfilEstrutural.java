package tercacost.model;
public abstract class PerfilEstrutural {
    private double alturaAlma ;
    private double larguraAba ;
    private double espessuraChapa ;
    private double tensaoEscoamentoAco ; //MPa
    private double tensaoRuptura ;       //MPa
    private double moduloElasticidade ;   //MPa
    private double moduloElasticidadeTransversal ;    //MPa

    public PerfilEstrutural(double alturaAlma, double larguraAba, double espessuraChapa, double tensaoEscoamentoAco, double tensaoRuptura, double moduloElasticidade, double moduloElasticidadeTransversal) {
        this.alturaAlma = alturaAlma;
        this.larguraAba = larguraAba;
        this.espessuraChapa = espessuraChapa;
        this.tensaoEscoamentoAco = tensaoEscoamentoAco;
        this.tensaoRuptura = tensaoRuptura;
        this.moduloElasticidade = moduloElasticidade;
        this.moduloElasticidadeTransversal = moduloElasticidadeTransversal;
    }

    public abstract double calcularArea(); //Ag
    public abstract double calcularInerciaX();  //Ix
    public abstract double calcularInerciaY();  //Iy    
    public abstract double calcularModuloResistenteX(); //Wx
    public abstract double calcularModuloResistenteY(); //Wy
    public abstract double calcularConstanteTorcao();   //J 
    public abstract double calcularConstanteEmpenamento();  //Cw
    public abstract double calcularDistanciaCentroGravidade();  //xcg
    public abstract double calcularDistanciaCentroCisalhamento();   //x0
    public abstract double calcularMomentoCriticoLocal();   //Ml
    public abstract double calcularMomentoCriticoDistorcional();    //Mdist
    public double calcularRaioGiracaoX() { // rx
        return Math.sqrt(calcularInerciaX() / calcularArea());
    }
    public double calcularRaioGiracaoY() { // ry
        return Math.sqrt(calcularInerciaY() / calcularArea());
    }
    public double calcularPesoPorMetro(){
        // Densidade do aço aprox. 7850 kg/m³
        // Converte a área de mm² para m² multiplicando por 1e-6
        return this.calcularArea() * 1e-6 * 7850;
    }
    

    // --- GETTERS E SETTERS ---
    public double getAlturaAlma() {
        return alturaAlma;
    }

    public void setAlturaAlma(double alturaAlma) {
        this.alturaAlma = alturaAlma;
    }

    public double getLarguraAba() {
        return larguraAba;
    }

    public void setLarguraAba(double larguraAba) {
        this.larguraAba = larguraAba;
    }

    public double getEspessuraChapa() {
        return espessuraChapa;
    }

    public void setEspessuraChapa(double espessuraChapa) {
        this.espessuraChapa = espessuraChapa;
    }

    public double getTensaoEscoamentoAco() {
        return tensaoEscoamentoAco;
    }

    public void setTensaoEscoamentoAco(double tensaoEscoamentoAco) {
        this.tensaoEscoamentoAco = tensaoEscoamentoAco;
    }

    public double getTensaoRuptura() {
        return tensaoRuptura;
    }

    public void setTensaoRuptura(double tensaoRuptura) {
        this.tensaoRuptura = tensaoRuptura;
    }

    public double getModuloElasticidade() {
        return moduloElasticidade;
    }

    public void setModuloElasticidade(double moduloElasticidade) {
        this.moduloElasticidade = moduloElasticidade;
    }

    public double getModuloElasticidadeTransversal() {
        return moduloElasticidadeTransversal;
    }

    public void setModuloElasticidadeTransversal(double moduloElasticidadeTransversal) {
        this.moduloElasticidadeTransversal = moduloElasticidadeTransversal;
    }
}




