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
        double xcg = calcularDistanciaCentroGravidade();

        double iyAlma = (bw * Math.pow(t, 3) / 12.0) + (bw * t) * Math.pow(xcg - (t / 2.0), 2);

        double iyAbas = 2.0 * ((t * Math.pow(bf, 3) / 12.0) + (bf * t) * Math.pow((bf / 2.0) - xcg, 2));
        
        return (iyAlma + iyAbas);
    }

    @Override
    public double calcularModuloResistenteX() {
        return (calcularInerciaX()) / (getAlturaAlma() / 2.0);
    }

    @Override
    public double calcularModuloResistenteY() {
        double xcg = calcularDistanciaCentroGravidade();
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
        double xcg = calcularDistanciaCentroGravidade();
        double ex = (3 * Math.pow(bf, 2)) / (bw + 6 * bf);
        return -(xcg + ex);
    }

    @Override
    public double calcularMomentoCriticoLocal() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double t = getEspessuraChapa();
        double E = getModuloElasticidade();
        double nu = 0.3; // Coeficiente de Poisson para o aço

        // Tensão crítica de flambagem local da Alma (k = 23.9)
        double sigmaAlma = 23.9 * (Math.pow(Math.PI, 2) * E / (12.0 * (1.0 - Math.pow(nu, 2)))) * Math.pow(t / bw, 2);
        // Tensão crítica de flambagem local da Aba em balanço (k = 0.425)
        double sigmaAba = 0.425 * (Math.pow(Math.PI, 2) * E / (12.0 * (1.0 - Math.pow(nu, 2)))) * Math.pow(t / bf, 2);
        // k = 23.9 para flexão simples na alma (NBR 14762)
        double sigmaCriticoLocal = Math.min(sigmaAlma, sigmaAba);
        return calcularModuloResistenteX() * sigmaCriticoLocal;
    }

    @Override
    public double calcularMomentoCriticoDistorcional() {
        // Perfil U Simples não possui enrijecedor, logo não sofre flambagem distorcional
        return Double.POSITIVE_INFINITY;
    }
}