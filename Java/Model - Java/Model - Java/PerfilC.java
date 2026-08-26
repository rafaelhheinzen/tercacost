package Model;
public class PerfilC extends PerfilEstrutural{
    private double larguraEnrijecedor; //mm
 public PerfilC(double alturaAlma, double larguraAba, double larguraEnrijecedor, double espessuraChapa, double tensaoEscoamentoAco, double tensaoRuptura, double moduloElasticidade, double moduloElasticidadeTransversal) {
        super(alturaAlma, larguraAba, espessuraChapa, tensaoEscoamentoAco, tensaoRuptura, moduloElasticidade, moduloElasticidadeTransversal);
        this.larguraEnrijecedor = larguraEnrijecedor;
    }
    public double getLarguraEnrijecedor() {
        return larguraEnrijecedor;
    }

    public void setLarguraEnrijecedor(double larguraEnrijecedor) {
        this.larguraEnrijecedor = larguraEnrijecedor;
    }
    @Override
    public double calcularArea() {
        return getEspessuraChapa() * (getAlturaAlma() + 2 * getLarguraAba() + 2 * getLarguraEnrijecedor());
    }

    @Override
    public double calcularInerciaX() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double t = getEspessuraChapa();

        double ixAlma = Math.pow(bw, 3) / 12.0;
        double ixAbas = 2 * bf * Math.pow(bw / 2.0, 2);
        double ixDobras = 2 * ((Math.pow(d, 3) / 12.0) + d * Math.pow((bw / 2.0) - (d / 2.0), 2));

        return t * (ixAlma + ixAbas + ixDobras);
    }

    @Override
    public double calcularInerciaY() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double t = getEspessuraChapa();
        double xcg = calcularDistanciaCentroGravidade();

        double iyAlma = bw * Math.pow(xcg, 2);
        double iyAbas = 2 * ((Math.pow(bf, 3) / 12.0) + bf * Math.pow((bf / 2.0) - xcg, 2));
        double iyDobras = 2 * d * Math.pow(bf - xcg, 2);

        return t * (iyAlma + iyAbas + iyDobras);
    }

    @Override
    public double calcularModuloResistenteX() {
        return (2 * calcularInerciaX()) / getAlturaAlma();
    }

    @Override
    public double calcularModuloResistenteY() {
        double xcg = calcularDistanciaCentroGravidade();
        return calcularInerciaY() / (getLarguraAba() - xcg);
    }

    @Override
    public double calcularConstanteTorcao() {
        return (Math.pow(getEspessuraChapa(), 3) / 3.0) * (getAlturaAlma() + 2 * getLarguraAba() + 2 * getLarguraEnrijecedor());
    }

    @Override
    public double calcularConstanteEmpenamento() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double t = getEspessuraChapa();
        double x0 = calcularDistanciaCentroCisalhamento();
        double xcg = calcularDistanciaCentroGravidade();
        double ex = Math.abs(x0) - xcg;

        double termo1 = Math.pow(bf, 3) / 3.0;
        double termo2 = Math.pow(ex, 2) * (bw / 3.0);
        double termo3 = Math.pow(ex, 2) * bf;
        double termo4 = (Math.pow(d, 3) / 3.0) * Math.pow((2 * d / bw) - 1, 2);

        return (t * Math.pow(bw, 2) / 4.0) * (termo1 + termo2 + termo3 + termo4);
    }
    @Override
    public double calcularDistanciaCentroGravidade() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        return (Math.pow(bf, 2) + 2 * d * bf) / (bw + 2 * bf + 2 * d);
    }
    @Override
    public double calcularDistanciaCentroCisalhamento() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double xcg = calcularDistanciaCentroGravidade();

        double ex = (3 * Math.pow(bf, 2) + 6 * d * bf - (2 * Math.pow(d, 3) / bw)) / 
                    (bw + 6 * bf + 2 * d * (3 - (2 * d / bw)));

        return -(xcg + ex);
    }

    @Override
    public double calcularMomentoCriticoLocal() {
        double bw = getAlturaAlma();
        double t = getEspessuraChapa();
        double E = getModuloElasticidade();

        double tensaoCriticaLocal = 4.0 * (Math.pow(Math.PI, 2) * E / (12 * (1 - 0.09))) * Math.pow(t / bw, 2);
        return calcularModuloResistenteX() * tensaoCriticaLocal;
    }

    @Override
    public double calcularMomentoCriticoDistorcional() {
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double t = getEspessuraChapa();
        double E = getModuloElasticidade();

        // Aproximação analítica simplificada para a tensão distorcional
        double tensaoCriticaDist = 0.5 * E * Math.pow(t / bf, 2) * Math.sqrt(d / bf);

        return calcularModuloResistenteX() * tensaoCriticaDist;
    }
}
