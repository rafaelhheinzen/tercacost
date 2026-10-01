package tercacost.engine;

import tercacost.model.PerfilEstrutural;

public class VerificadorELS {

    /**
     * Calcula a flecha máxima (deformação) no Estado Limite de Serviço (ELS)
     * e verifica se atende ao limite normativo de L/200 (NBR 14762).
     *
     * @param perfil Perfil estrutural
     * @param qServicoCargaLinear Carga linear sem majoradores (kN/mm ou kN/m)
     * @param comprimentoTercaMM Comprimento do vão L em mm
     * @return true se a flecha real for menor ou igual à flecha limite
     */
    public static boolean verificarFlechaLimite(PerfilEstrutural perfil, double qServicoKNm, double comprimentoTercaMM) {
        double E = perfil.getModuloElasticidade(); // MPa (N/mm²)
        double Ix = perfil.calcularInerciaX();     // mm⁴
        double L = comprimentoTercaMM;            // mm

        // Converte qServico de kN/m para N/mm (1 kN/m = 1 N/mm)
        double q = qServicoKNm;

        // Flecha real para viga biapoiada: delta = (5 * q * L⁴) / (384 * E * Ix)
        double flechaReal = (5.0 * q * Math.pow(L, 4)) / (384.0 * E * Ix);

        // Flecha limite normativa L/200
        double flechaLimite = L / 200.0;

        return flechaReal <= flechaLimite;
    }

    public static double calcularFlechaReal(PerfilEstrutural perfil, double qServicoKNm, double comprimentoTercaMM) {
        double E = perfil.getModuloElasticidade();
        double Ix = perfil.calcularInerciaX();
        double q = qServicoKNm;
        return (5.0 * q * Math.pow(comprimentoTercaMM, 4)) / (384.0 * E * Ix);
    }
}