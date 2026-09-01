package tercacost.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tercacost.dto.CalculoRequest;
import tercacost.dto.ResultadoVerificacaoDTO;
import tercacost.engine.CalculadorCargasNBR8681;
import tercacost.engine.VerificadorELS;
import tercacost.engine.VerificadorMRD;
import tercacost.model.PerfilC;
import tercacost.model.PerfilEstrutural;
import tercacost.model.PerfilU;
import tercacost.model.PerfilZ;
import tercacost.services.ProjetoService;
import tercacost.entities.Usuario;

@RestController
@RequestMapping("/projetos")
@CrossOrigin(origins = "*")
public class ProjetoController {

    @Autowired
    private ProjetoService service;

    @Autowired
    private tercacost.repositories.ProjetoModelRepository projetoModelRepository;

    @Autowired
    private tercacost.repositories.CoberturaRepository coberturaRepository;

    @Autowired
    private tercacost.repositories.PerfilTercaRepository perfilTercaRepository;

    // 1. Busca todos os projetos do modelo unificado do MySQL
    @GetMapping
    public ResponseEntity<List<tercacost.model.Projeto>> getProjetos() {
        return ResponseEntity.status(HttpStatus.OK).body(projetoModelRepository.findAll());
    }

    // 2. Alimenta o carrossel da index.html buscando a lista direto do MySQL por Usuário
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<tercacost.model.Projeto>> getProjetosPorUsuario(@PathVariable Long usuarioId) {
        List<tercacost.model.Projeto> projetosDoUsuario = projetoModelRepository.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(projetosDoUsuario);
    }

    @GetMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<?> getUmProjeto(@PathVariable Long id, @PathVariable Long usuarioId) {
        try {
            java.util.Map<String, Object> dadosCompletos = new java.util.HashMap<>();
            int idBuscaMySQL = id.intValue();
            
            // 1. Busca o projeto na tabela primária 'projetos'
            java.util.Optional<tercacost.model.Projeto> optProj = projetoModelRepository.findById(idBuscaMySQL);
            if (!optProj.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado no MySQL.");
            }
            
            tercacost.model.Projeto projeto = optProj.get();
            
            // 2. Validação rigorosa de posse (Compara os IDs numéricos puros)
            if (projeto.getUsuario() == null || projeto.getUsuario().getId().longValue() != usuarioId.longValue()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso não autorizado a este projeto.");
            }

            // Alimenta os dados básicos de cabeçalho
            dadosCompletos.put("id", projeto.getId());
            dadosCompletos.put("descricao", projeto.getNome());

            // 3. Busca os dados de carregamento e vão na tabela Coberturas
            java.util.Optional<tercacost.model.Cobertura> optCob = coberturaRepository.findByProjetoId(idBuscaMySQL);
            if (optCob.isPresent()) {
                tercacost.model.Cobertura cob = optCob.get();
                dadosCompletos.put("cargaPermanente", cob.getCargaTelha());
                dadosCompletos.put("sobrecarga", cob.getSobrecargaPadrao());
                dadosCompletos.put("vento", cob.getVentoPressao());
                dadosCompletos.put("espacamento", cob.getEspacamentoTercas());
                dadosCompletos.put("vao", cob.getComprimentoTerca());
                dadosCompletos.put("lb", cob.getLb());
                
                // 4. Busca as propriedades geométricas e o CB na tabela perfis_tercas
                java.util.Optional<tercacost.model.PerfilTerca> optPerf = perfilTercaRepository.findByCoberturaId(cob.getId());
                if (optPerf.isPresent()) {
                    tercacost.model.PerfilTerca perf = optPerf.get();
                    dadosCompletos.put("tipoPerfil", perf.getTipoPerfil());
                    dadosCompletos.put("alturaAlma", perf.getAlturaAlma());
                    dadosCompletos.put("larguraAba", perf.getLarguraAba());
                    dadosCompletos.put("larguraEnrijecedor", perf.getLarguraEnrijecedor());
                    dadosCompletos.put("espessuraChapa", perf.getEspessuraChapa());
                    dadosCompletos.put("fy", perf.getFy());
                    
                    // 🌟 CORREÇÃO: Captura o Cb real que foi calculado e salvo na tabela de perfis!
                    dadosCompletos.put("cb", perf.getCb()); 
                }
            } else {
                // Fallbacks de segurança para a tela não abrir em branco caso falte alguma amarração
                dadosCompletos.put("cargaPermanente", 15.0);
                dadosCompletos.put("sobrecarga", 25.0);
                dadosCompletos.put("vento", 45.0);
                dadosCompletos.put("espacamento", 1.50);
                dadosCompletos.put("vao", 6.00);
                dadosCompletos.put("lb", 1500.0);
                dadosCompletos.put("cb", 1.0);
            }
            
            return ResponseEntity.ok(dadosCompletos);
            
        } catch (Exception e) {
            e.printStackTrace(); // Cospe o erro real detalhado no console do Spring Boot para sabermos o que houve
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao ler memorial: " + e.getMessage());
        }
    }


    // 4. Deleta de forma direta e em cascata atômica controlada
    @DeleteMapping("/{id}/usuario/{usuarioId}")
    @jakarta.transaction.Transactional 
    public ResponseEntity<?> deletar(@PathVariable Long id, @PathVariable Long usuarioId) {
        try {
            java.util.Optional<tercacost.model.Projeto> optProj = projetoModelRepository.findById(id.intValue());
            
            if (optProj.isPresent()) {
                tercacost.model.Projeto projeto = optProj.get();
                
                if (projeto.getUsuario() == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Erro: Usuário do projeto inválido.");
                }

                long idDonoDoProjeto = projeto.getUsuario().getId().longValue();
                long idUsuarioLogado = usuarioId.longValue();

                if (idDonoDoProjeto != idUsuarioLogado) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado: Este projeto pertence a outro usuário.");
                }
                
                // Graças ao @Transactional e aos Cascades das Entidades, limpa tudo sem erros de FK
                projetoModelRepository.delete(projeto);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado no MySQL.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir: " + e.getMessage());
        }
    }

    // 5. Rota Inteligente de persistência e combinações da NBR 8681 (Restaurada do corte)
    @PostMapping("/salvar-calculado")
    public ResponseEntity<?> salvarProjetoComCalculo(@RequestBody CalculoRequest dados) {
        try {
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
                    return ResponseEntity.badRequest().body("Tipo de perfil desconhecido.");
            }

            CalculadorCargasNBR8681.Solicitacoes solicitacoes = CalculadorCargasNBR8681.calcularSolicitacoesGravitacionais(
                    dados.getCargaPermanente(), dados.getSobrecarga(), dados.getVento(), dados.getEspacamento(), dados.getVao()
            );

            double mrd = VerificadorMRD.calcularMomentoResistenteFinal(perfil, dados.getLb(), dados.getCb());
            double vrd = VerificadorMRD.calcularCortanteResistenteFinal(perfil);
            double qServico = (dados.getCargaPermanente() * 0.00981) * dados.getEspacamento();
            double vaoMM = dados.getVao() * 1000.0;
            double flechaReal = VerificadorELS.calcularFlechaReal(perfil, qServico, vaoMM);
            double flechaLimite = vaoMM / 200.0;

            ResultadoVerificacaoDTO resultado = new ResultadoVerificacaoDTO(
                    mrd, vrd, solicitacoes.getMsd(), solicitacoes.getVsd(),
                    perfil.calcularArea(), perfil.calcularPesoPorMetro(), flechaReal, flechaLimite, ""
            );

            tercacost.entities.Usuario usuarioAtivo = new tercacost.entities.Usuario();
            
            if (dados.getUsuarioId() != null) {
                usuarioAtivo.setId(dados.getUsuarioId()); 
            } else {
                usuarioAtivo.setId(1L); 
            }

            service.salvarCalculoCompleto(dados, resultado, usuarioAtivo);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"mensagem\": \"Estrutura gravada com sucesso no MySQL!\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na persistência: " + e.getMessage());
        }
    }
}
