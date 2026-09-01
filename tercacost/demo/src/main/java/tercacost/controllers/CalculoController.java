package tercacost.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tercacost.dto.CalculoRequest;
import tercacost.dto.ResultadoVerificacaoDTO; 
import tercacost.model.*;                  
import tercacost.engine.*;     

@RestController
@RequestMapping("/api/calculo")
@CrossOrigin(origins = "*")
public class CalculoController {

    @PostMapping("/verificar-perfil")
    public ResponseEntity<ResultadoVerificacaoDTO> verificar(@RequestBody CalculoRequest dados) {
        
        // 1. Instanciar Perfil com base no tipo
        PerfilEstrutural perfil;
        switch (dados.getTipoPerfil().toUpperCase()) {
            case "U":
                perfil = new PerfilU(dados.getAlturaAlma(), dados.getLarguraAba(), dados.getEspessuraChapa(), dados.getFy(), dados.getFu(), dados.getE(), dados.getG());
                break;
            case "C":
                perfil = new PerfilC(dados.getAlturaAlma(), dados.getLarguraAba(), dados.getLarguraEnrijecedor(), dados.getEspessuraChapa(), dados.getFy(), dados.getFu(), dados.getE(), dados.getG());
                break;
            case "Z":
                perfil = new PerfilZ(dados.getAlturaAlma(), dados.getLarguraAba(), dados.getLarguraEnrijecedor(), dados.getEspessuraChapa(), dados.getFy(), dados.getFu(), dados.getE(), dados.getG());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        // 2. [NOVO] Chamar o Calculador de Cargas NBR 8681 para gerar MSD e VSD automaticamente
        CalculadorCargasNBR8681.Solicitacoes solicitacoes = CalculadorCargasNBR8681.calcularSolicitacoesGravitacionais(
                dados.getCargaPermanente(),
                dados.getSobrecarga(),
                dados.getVento(),
                dados.getEspacamento(),
                dados.getVao()
        );

        double msdCalculado = solicitacoes.getMsd();
        double vsdCalculado = solicitacoes.getVsd();

        // 3. Executar o cálculo de resistências mecânicas (ELU)
        double mrd = VerificadorMRD.calcularMomentoResistenteFinal(perfil, dados.getLb(), dados.getCb());
        double vrd = VerificadorMRD.calcularCortanteResistenteFinal(perfil);

        // 4. [NOVO] Executar os cálculos de Deformação e Flechas (ELS)
        // Convertendo carga linear permanente de kg/m² para kN/m para o cálculo do ELS de serviço básico
        double qServicoCargaLinear = (dados.getCargaPermanente() * 0.00981) * dados.getEspacamento();
        double vaoEmMM = dados.getVao() * 1000.0;

        double flechaReal = VerificadorELS.calcularFlechaReal(perfil, qServicoCargaLinear, vaoEmMM);
        double flechaLimite = vaoEmMM / 200.0;

        // 5. [NOVO] Gerar o bloco descritivo completo do diagnóstico
        String relatorio = GeradorDiagnostico.gerarRelatorioCompleto(
                perfil, mrd, msdCalculado, vrd, vsdCalculado, 
                flechaReal, flechaLimite, dados.getLb(), vaoEmMM
        );

        // 6. Enviar tudo compilado no DTO de Retorno
        ResultadoVerificacaoDTO resultado = new ResultadoVerificacaoDTO(
                mrd, vrd, msdCalculado, vsdCalculado, 
                perfil.calcularArea(), perfil.calcularPesoPorMetro(),
                flechaReal, flechaLimite, relatorio
        );

        return ResponseEntity.ok(resultado);
    }
}
