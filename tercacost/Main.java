import Model.PerfilEstrutural;
import Model.PerfilU;
import Model.PerfilC;
import Model.PerfilZ;
import Engine.VerificadorMRD;
import Service.ResultadoVerificacaoDTO;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== TESTE COMPLETO DOS PERFIS (NBR 14762:2010) ===\n");

        // 1. DADOS GEOMÉTRICOS E DE MATERIAL
        double alturaAlma = 300.0;
        double larguraAba = 85.0;
        double larguraEnrijecedor = 25.0;
        double espessuraChapa = 4.75;

        double fy = 300.0;
        double fu = 400.0;
        double E = 200000.0;
        double G = 77000.0;

        // Solicitações da Terça
        double Msd = 27.57;
        double Vsd = 16.54;
        double Lb = 1500.0;
        double Cb = 2.381;

        // 2. CREAÇÃO DOS TRÊS TIPOS DE PERFIS
        PerfilEstrutural perfilU = new PerfilU(
            alturaAlma, larguraAba, espessuraChapa, 
            fy, fu, E, G
        );

        PerfilEstrutural perfilC = new PerfilC(
            alturaAlma, larguraAba, larguraEnrijecedor, espessuraChapa, 
            fy, fu, E, G
        );

        PerfilEstrutural perfilZ = new PerfilZ(
            alturaAlma, larguraAba, larguraEnrijecedor, espessuraChapa, 
            fy, fu, E, G
        );

        // Array com todos os perfis para rodar o teste no loop
        PerfilEstrutural[] listaPerfis = { perfilU, perfilC, perfilZ };

        // 3. ITERAÇÃO E CÁLCULO
        for (PerfilEstrutural perfil : listaPerfis) {
            String nomePerfil = perfil.getClass().getSimpleName();
            
            double mrd = VerificadorMRD.calcularMomentoResistenteFinal(perfil, Lb, Cb);
            double vrd = VerificadorMRD.calcularCortanteResistenteFinal(perfil);

            ResultadoVerificacaoDTO resultado = new ResultadoVerificacaoDTO(mrd, vrd, Msd, Vsd);

            System.out.println("----------------------------------------");
            System.out.println("RESULTADOS PARA: " + nomePerfil);
            System.out.printf("Área (Ag): %.2f mm² | Peso: %.2f kg/m\n", perfil.calcularArea(), perfil.calcularPesoPorMetro());
            System.out.printf("Mrd: %.2f kN.m (Solicitado: %.2f kN.m) -> %s\n", resultado.getMrd(), Msd, resultado.isAprovadoMomento() ? "OK" : "FALHOU");
            System.out.printf("Vrd: %.2f kN   (Solicitado: %.2f kN)   -> %s\n", resultado.getVrd(), Vsd, resultado.isAprovadoCortante() ? "OK" : "FALHOU");
        }
    }
}