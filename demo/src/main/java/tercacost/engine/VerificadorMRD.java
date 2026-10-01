package tercacost.engine;

import org.springframework.ui.Model;

import tercacost.model.PerfilEstrutural;

public class VerificadorMRD {

    /**
     * Calcula o Momento Fletor Resistente Final (Mrd) em kN.m
     * Nota: Lb deve ser passado em milímetros (mm)!
     */
	public static double calcularMomentoResistenteFinal(PerfilEstrutural perfil, double Lb, double Cb) {
	    // 1. Obter Me, Ml e Mdist
	    double Me = calcularMomentoCriticoGlobal(perfil, Lb, Cb);
	    double Ml = perfil.calcularMomentoCriticoLocal();
	    double Mdist = perfil.calcularMomentoCriticoDistorcional();

	    // 2. Aplicar equações do MRD (NBR 14762) para MRe, MRl e MRdist
	    double MRe = aplicarFlambagemGlobal(perfil, Me);
	    double MRl = aplicarFlambagemLocal(perfil, MRe, Ml);
	    double MRdist = aplicarFlambagemDistorcional(perfil, Mdist);

	    // 🔍 LINHA DE RASTREAMENTO COMPLEMENTAR: Imprime no console preto do Eclipse/STS
	    System.out.println("=== DIAGNÓSTICO INTERNO DOS MOMENTOS (N.mm) ===");
	    System.out.println("MRe (Global): " + MRe);
	    System.out.println("MRl (Local): " + MRl);
	    System.out.println("MRdist (Distorcional): " + MRdist);
	    System.out.println("===============================================");

	    // Proteção preventiva: Se algum cálculo falhar e retornar zero/inválido, 
	    // assume o momento de escoamento básico para não quebrar a divisão
	    if (MRe <= 0 || Double.isNaN(MRe)) MRe = perfil.calcularModuloResistenteX() * perfil.getTensaoEscoamentoAco();
	    if (MRl <= 0 || Double.isNaN(MRl)) MRl = MRe;
	    if (MRdist <= 0 || Double.isNaN(MRdist)) MRdist = MRe;

	    // 3. Menor valor em N.mm dividido por gamma (1.10)
	    double MrNmm = Math.min(MRe, Math.min(MRl, MRdist)) / 1.10;

	    // Converte de N.mm para kN.m dividindo por 1.0e6
	    return MrNmm / 1.0e6;
	}


    /**
     * Calcula a Força Cortante Resistente Final (Vrd) em kN
     */
    public static double calcularCortanteResistenteFinal(PerfilEstrutural perfil) {
        double h = perfil.getAlturaAlma();
        double t = perfil.getEspessuraChapa();
        double fy = perfil.getTensaoEscoamentoAco();
        double E = perfil.getModuloElasticidade();

        double Aw = h * t;
        double ht = h / t;
        double kv = 5.0; // Coeficiente para alma sem enrijecedores intermediários

        double limite1 = 1.08 * Math.sqrt((E * kv) / fy);
        double limite2 = 1.40 * Math.sqrt((E * kv) / fy);

        double Vvr; // em Newtons (N)

        if (ht <= limite1) {
            Vvr = 0.60 * Aw * fy;
        } else if (ht <= limite2) {
            Vvr = 0.65 * t * t * Math.sqrt(fy * E * kv);
        } else {
            Vvr = (0.905 * E * kv * Math.pow(t, 3)) / h;
        }

        double VrdN = Vvr / 1.10;

        // Converte de N para kN dividindo por 1000
        return VrdN / 1000.0;
    }

    // --- MÉTODOS AUXILIARES ---

    private static double calcularMomentoCriticoGlobal(PerfilEstrutural perfil, double Lb, double Cb) {
        double E = perfil.getModuloElasticidade();
        double G = perfil.getModuloElasticidadeTransversal();
        double Iy = perfil.calcularInerciaY();
        double J = perfil.calcularConstanteTorcao();
        double Cw = perfil.calcularConstanteEmpenamento();

        double termo1 = (Math.PI * Math.PI * E * Iy) / (Lb * Lb);
        double termo2 = (G * J) + ((Math.PI * Math.PI * E * Cw) / (Lb * Lb));

        if (perfil instanceof tercacost.model.PerfilZ) {
            return 0.5 * Cb * Math.sqrt(termo1 * termo2);
        }
        
        return Cb * Math.sqrt(termo1 * termo2);
    }

    private static double aplicarFlambagemGlobal(PerfilEstrutural perfil, double Me) {
        double My = perfil.calcularModuloResistenteX() * perfil.getTensaoEscoamentoAco();
        
        // Se Me for zero, ou se My for zero, lambda0 vira Infinity ou NaN
        if (Me <= 0 || Double.isNaN(Me)) return My; 
        
        double lambda0 = Math.sqrt(My / Me);

        if (lambda0 <= 0.6) {
            return My;
        } else if (lambda0 < 1.358) {
            return 1.11 * (1 - 0.228 * Math.pow(lambda0, 2)) * My;
        } else {
            return (0.877 / Math.pow(lambda0, 2)) * My;
        }
    }

    private static double aplicarFlambagemLocal(PerfilEstrutural perfil, double MRe, double Ml) {
        double lambdaL = Math.sqrt(MRe / Ml);

        if (lambdaL <= 0.776) {
            return MRe;
        } else {
            return (1 - 0.15 / Math.pow(lambdaL, 0.8)) * (1 / Math.pow(lambdaL, 0.8)) * MRe;
        }
    }

    private static double aplicarFlambagemDistorcional(PerfilEstrutural perfil, double Mdist) {
        double My = perfil.calcularModuloResistenteX() * perfil.getTensaoEscoamentoAco();

        if (Double.isInfinite(Mdist) || Double.isNaN(Mdist) || Mdist <= 0.0) {
            return My;
        }

        double lambdaDist = Math.sqrt(My / Mdist);

        if (lambdaDist <= 0.561) {
            return My;
        } else {
            return (1 - 0.25 / Math.pow(lambdaDist, 1.2)) * (1 / Math.pow(lambdaDist, 1.2)) * My;
        }
    }
}