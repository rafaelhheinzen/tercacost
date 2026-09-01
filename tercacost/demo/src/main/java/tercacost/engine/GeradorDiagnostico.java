package tercacost.engine;

import tercacost.model.PerfilEstrutural;

public class GeradorDiagnostico {

    /**
     * Retorna um relatório completo em texto sobre a situação do perfil.
     */
    public static String gerarRelatorioCompleto(
            PerfilEstrutural perfil, 
            double mrd, double msd, 
            double vrd, double vsd, 
            double flechaRealMM, double flechaLimiteMM,
            double Lb, double comprimentoTotalMM) {

        StringBuilder sb = new StringBuilder();

        // 1. Taxas de Utilização
        double taxaMomento = (msd / mrd) * 100.0;
        double taxaCortante = (vsd / vrd) * 100.0;
        double taxaFlecha = (flechaRealMM / flechaLimiteMM) * 100.0;

        sb.append("==================================================\n");
        sb.append("DIAGNÓSTICO AUTOMÁTICO DE DIMENSIONAMENTO\n");
        sb.append("==================================================\n");

        // --- VERIFICAÇÃO DE FLEXÃO (Msd / Mrd) ---
        sb.append(String.format("[ELU] Flexão (Msd / Mrd): %.2f / %.2f kN.m (Uso: %.1f%%) -> %s\n",
                msd, mrd, taxaMomento, (msd <= mrd) ? "OK" : "FALHOU"));
        
        if (msd > mrd) {
            sb.append("  ↳ CAUSA DO ERRO: Capacidade de momento fletor insuficiente.\n");
            // Diagnóstico específico do motivo de falha
            if (Lb >= comprimentoTotalMM * 0.8) {
                sb.append("  ↳ DIAGNÓSTICO: Alta probabilidade de Flambagem Lateral por Torção (FLT) devido ao vão livre sem travamento.\n");
                sb.append("  ↳ SOLUÇÃO SUGERIDA: Adicione tirantes/correntões intermediários para reduzir 'Lb', ou aumente as abas/espessura.\n");
            } else {
                sb.append("  ↳ DIAGNÓSTICO: Flambagem Local/Distorcional ou plastificação do aço.\n");
                sb.append("  ↳ SOLUÇÃO SUGERIDA: Aumente a espessura da chapa (t) ou adicione/aumente o enrijecedor labial.\n");
            }
        }

        // --- VERIFICAÇÃO DE CORTANTE (Vsd / Vrd) ---
        sb.append(String.format("[ELU] Cortante (Vsd / Vrd): %.2f / %.2f kN (Uso: %.1f%%) -> %s\n",
                vsd, vrd, taxaCortante, (vsd <= vrd) ? "OK" : "FALHOU"));
        
        if (vsd > vrd) {
            sb.append("  ↳ CAUSA DO ERRO: A alma do perfil não suporta a reação nos apoios (cisalhamento).\n");
            sb.append("  ↳ SOLUÇÃO SUGERIDA: Aumente a espessura da chapa (t) ou a altura da alma.\n");
        }

        // --- VERIFICAÇÃO DE DEFORMAÇÃO / FLECHA (ELS) ---
        sb.append(String.format("[ELS] Flecha (Real / Limite): %.2f / %.2f mm (Uso: %.1f%%) -> %s\n",
                flechaRealMM, flechaLimiteMM, taxaFlecha, (flechaRealMM <= flechaLimiteMM) ? "OK" : "FALHOU"));
        
        if (flechaRealMM > flechaLimiteMM) {
            sb.append("  ↳ CAUSA DO ERRO: O perfil está deformando/selando mais do que o permitido por norma (L/200).\n");
            sb.append("  ↳ SOLUÇÃO SUGERIDA: Aumente a altura da alma (h) do perfil para ganhar inércia (Ix), ou reduza o espaçamento entre terças.\n");
        }

        sb.append("--------------------------------------------------\n");

        // Status Final Resumido
        boolean aprovadoGeral = (msd <= mrd) && (vsd <= vrd) && (flechaRealMM <= flechaLimiteMM);
        if (aprovadoGeral) {
            sb.append("STATUS FINAL: APROVADO - A terça atende com segurança a todas as exigências normativas.\n");
        } else {
            sb.append("STATUS FINAL: REPROVADO - Ajuste as variáveis indicadas acima.\n");
        }

        return sb.toString();
    }
}