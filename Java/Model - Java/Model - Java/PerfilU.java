package Model;
public class PerfilU extends PerfilEstrutural {
    public PerfilU(double alturaAlma, double larguraAba, double espessuraChapa,
                   double tensaoEscoamentoAco, double tensaoRuptura,
                   double moduloElasticidade, double moduloElasticidadeTransversal) {
        super( alturaAlma, larguraAba, espessuraChapa, tensaoEscoamentoAco, 
              tensaoRuptura, moduloElasticidade, moduloElasticidadeTransversal);
    }

@Override
 public double calcularArea() {
    return getEspessuraChapa() * (getAlturaAlma() + 2 * getLarguraAba());
 }
    @Override
    public double calcularInerciaX() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double t = getEspessuraChapa();
        return t * ((Math.pow(bw, 3) / 12.0) + 2 * bf * Math.pow(bw / 2.0, 2));
    }

    @Override
    public double calcularInerciaY() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double t = getEspessuraChapa();
        double xcg = (Math.pow(bf, 2)) / (bw + 2 * bf);

        double iyAlma = bw * Math.pow(xcg, 2);
        double iyAbas = 2 * ((Math.pow(bf, 3) / 12.0) + bf * Math.pow((bf / 2.0) - xcg, 2));
        return t * (iyAlma + iyAbas);
    }

    @Override
    public double calcularModuloResistenteX() {
        return (2 * calcularInerciaX()) / getAlturaAlma();
    }

    @Override
    public double calcularModuloResistenteY() {
        double xcg = (Math.pow(getLarguraAba(), 2)) / (getAlturaAlma() + 2 * getLarguraAba());
        return calcularInerciaY() / (getLarguraAba() - xcg);
    }

    @Override
    public double calcularConstanteTorcao() {
        return (Math.pow(getEspessuraChapa(), 3) / 3.0) * (getAlturaAlma() + 2 * getLarguraAba());
    }

    @Override
    public double calcularConstanteEmpenamento() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double t = getEspessuraChapa();
        return (t * Math.pow(bf, 3) * Math.pow(bw, 2) / 12.0) * ((3 * bf + 2 * bw) / (6 * bf + bw));
    }
    @Override
    public double calcularDistanciaCentroGravidade() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        return Math.pow(bf, 2) / (bw + 2 * bf);
    }
    @Override
    public double calcularDistanciaCentroCisalhamento() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double xcg = (Math.pow(bf, 2)) / (bw + 2 * bf);
        double ex = (3 * Math.pow(bf, 2)) / (bw + 6 * bf);
        return -(xcg + ex);
    }

    @Override
    public double calcularMomentoCriticoLocal() {
        double bw = getAlturaAlma();
        double t = getEspessuraChapa();
        double E = getModuloElasticidade();
    
        double tensaoCriticaLocal = 3.615 * E * Math.pow(t / bw, 2);
        return calcularModuloResistenteX() * tensaoCriticaLocal;
    }

    @Override
    public double calcularMomentoCriticoDistorcional() {
        // Perfil U Simples não possui enrijecedor, logo não sofre flambagem distorcional
        return Double.POSITIVE_INFINITY;
    }
}