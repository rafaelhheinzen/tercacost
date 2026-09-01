package tercacost.engine;

public class CalculadorCargasNBR8681 {

    // Constante para converter kg/m² para kN/m² (g ≈ 9.81 m/s² -> 1 kg/m² = 0.00981 kN/m²)
    private static final double KG_PARA_KN = 0.00981;

    /**
     * DTO interno ou auxiliar para guardar as solicitações calculadas
     */
    public static class Solicitacoes {
        private final double msd; // kN.m
        private final double vsd; // kN

        public Solicitacoes(double msd, double vsd) {
            this.msd = msd;
            this.vsd = vsd;
        }

        public double getMsd() { return msd; }
        public double getVsd() { return vsd; }
    }

    /**
     * Calcula Msd e Vsd para o caso crítico de pressão (Atua para baixo)
     *
     * @param cargaPermanenteKgM2 Telhas + Estrutura + Painéis (kg/m²)
     * @param sobrecargaKgM2     Sobrecarga normativa de utilização (kg/m²)
     * @param ventoKgM2          Pressão do vento atuando para baixo (kg/m²)
     * @param espacamentoTercasM Espaçamento s em metros (m)
     * @param comprimentoTercaM  Vão livre L em metros (m)
     * @return Objeto Solicitacoes contendo Msd (kN.m) e Vsd (kN)
     */
    public static Solicitacoes calcularSolicitacoesGravitacionais(
            double cargaPermanenteKgM2,
            double sobrecargaKgM2,
            double ventoKgM2,
            double espacamentoTercasM,
            double comprimentoTercaM) {

        // 1. Converter kg/m² para kN/m²
        double g_k_area = cargaPermanenteKgM2 * KG_PARA_KN; // Carga Permanente (kN/m²)
        double q_sc_area = sobrecargaKgM2 * KG_PARA_KN;     // Sobrecarga Utilização (kN/m²)
        double q_v_area = ventoKgM2 * KG_PARA_KN;           // Vento Pressão (kN/m²)

        // 2. Transformar para Carga Linear na Terça (kN/m) multiplicando pela largura de influência
        double g_k = g_k_area * espacamentoTercasM;
        double q_sc = q_sc_area * espacamentoTercasM;
        double q_v = q_v_area * espacamentoTercasM;

        // 3. Combinação Última NBR 8681 (ELU)
        // Considerando Vento como ação variável principal e Sobrecarga com fator psi0 = 0.7
        double gamma_g = 1.35;
        double gamma_q = 1.50;
        double psi_0 = 0.7;

        // Carga de projeto linear no ELU (kN/m)
        double q_ud = (gamma_g * g_k) + (gamma_q * q_v) + (gamma_q * psi_0 * q_sc);

        // 4. Cálculo dos Esforços Solicitantes para Viga Biapoiada
        double msd = (q_ud * Math.pow(comprimentoTercaM, 2)) / 8.0; // kN.m
        double vsd = (q_ud * comprimentoTercaM) / 2.0;               // kN

        return new Solicitacoes(msd, vsd);
    }
}