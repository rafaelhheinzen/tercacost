package tercacost.model;
public class PerfilZ extends PerfilEstrutural {
    private double larguraEnrijecedor; // mm (dobra d)
    private double mlManual = 0.0; // Momento de inércia manual (kN.m)
    private double mdistManual = 0.0; // Momento de distorção manual (kN.m)

    public PerfilZ(double alturaAlma, double larguraAba, double larguraEnrijecedor, 
                   double espessuraChapa, double tensaoEscoamentoAco, 
                   double tensaoRuptura, double moduloElasticidade, 
                   double moduloElasticidadeTransversal) {
        
        super(alturaAlma, larguraAba, espessuraChapa, tensaoEscoamentoAco, 
              tensaoRuptura, moduloElasticidade, moduloElasticidadeTransversal);

        this.larguraEnrijecedor = larguraEnrijecedor;
    }
    public PerfilZ(double alturaAlma, double larguraAba, double larguraEnrijecedor, double espessuraChapa, double tensaoEscoamentoAco, double tensaoRuptura, double moduloElasticidade, double moduloElasticidadeTransversal, double mlManualKnm, double mdistManualKnm) {
    
        this(alturaAlma, larguraAba, larguraEnrijecedor, espessuraChapa, tensaoEscoamentoAco, tensaoRuptura, moduloElasticidade, moduloElasticidadeTransversal);
        setMlManual(mlManualKnm);
        setMdistManual(mdistManualKnm);
    }
    //Getters e Setters
    public double getMlManual() {
        return mlManual / 1.0e6; // Converte de N.mm para kN.m
    }
    public void setMlManual(double mlManualKnm) {
        this.mlManual = mlManualKnm * 1.0e6; // Converte kN.m para N.mm
    }
    public double getMdistManual() {
        return mdistManual / 1.0e6; // Converte de N.mm para kN.m
    }
    public void setMdistManual(double mdistManualKnm) {
        this.mdistManual = mdistManualKnm * 1.0e6; // Converte kN.m para N.mm
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
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double t = getEspessuraChapa();

        double iyAbas = 2 * ((Math.pow(bf, 3) / 12.0) + bf * Math.pow(bf / 2.0, 2));
        double iyDobras = 2 * d * Math.pow(bf, 2);

        return t * (iyAbas + iyDobras);
    }

    @Override
    public double calcularModuloResistenteX() {
        return (2 * calcularInerciaX()) / getAlturaAlma();
    }

    @Override
    public double calcularModuloResistenteY() {
        return (2 * calcularInerciaY()) / getLarguraAba();
    }

    @Override
    public double calcularConstanteTorcao() {
        return (Math.pow(getEspessuraChapa(), 3) / 3.0) * (getAlturaAlma() + 2 * getLarguraAba() + 2 * getLarguraEnrijecedor());
    }

    @Override
    public double calcularConstanteEmpenamento() {
        double bw = getAlturaAlma();
        double bf = getLarguraAba();
        double t = getEspessuraChapa();

        return (t * Math.pow(bw, 2) * Math.pow(bf, 3) / 12.0) * ((bf + 2 * bw) / (2 * bf + bw));
    }
    @Override
    public double calcularDistanciaCentroGravidade() {
        return 0.0;
    }
    @Override
    public double calcularDistanciaCentroCisalhamento() {
        return 0.0; 
    }

    @Override
    public double calcularMomentoCriticoLocal() {
        // Se você tiver uma variável mlManual configurada
        if (this.mlManual > 0.0) {
            return this.mlManual;
        }
        double bw = getAlturaAlma();
        double t = getEspessuraChapa();
        double E = getModuloElasticidade();
        
        // Fator de flambagem local da alma em flexão (NBR 14762)
        double tensaoCriticaLocal = 23.9 * (Math.pow(Math.PI, 2) * E / (12 * (1 - 0.09))) * Math.pow(t / bw, 2);
        
        double resultado = calcularModuloResistenteX() * tensaoCriticaLocal;
        return (resultado <= 0 || Double.isNaN(resultado)) ? 1.0e4 : resultado; // Proteção contra zero/NaN
    }


    @Override
    public double calcularMomentoCriticoDistorcional() {
        if (this.mdistManual > 0.0) {
            return this.mdistManual;
        }
        double bf = getLarguraAba();
        double d = getLarguraEnrijecedor();
        double t = getEspessuraChapa();
        double E = getModuloElasticidade();
        
        // Fórmula simplificada de transição para o perfil Z
        double tensaoCriticaDist = 0.5 * E * Math.pow(t / bf, 2) * Math.sqrt(d / bf);
        
        double resultado = calcularModuloResistenteX() * tensaoCriticaDist;
        
        // Se o cálculo falhar, retornar o momento de plastificação inicial (My) como segurança
        if (Double.isNaN(resultado) || Double.isInfinite(resultado) || resultado <= 0.0) {
            return calcularModuloResistenteX() * getTensaoEscoamentoAco();
        }
        return resultado;
    }

}