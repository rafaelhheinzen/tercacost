package tercacost.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tercacost.dto.CalculoRequest;
import tercacost.dto.ResultadoVerificacaoDTO;
import tercacost.model.Projeto;
import tercacost.entities.Usuario;
import tercacost.model.Cobertura;
import tercacost.model.PerfilTerca;
import tercacost.repositories.CoberturaRepository;
import tercacost.repositories.PerfilTercaRepository;
import tercacost.repositories.ProjetoRepository;

@Service
public class ProjetoService {
	
	@Autowired private tercacost.repositories.ProjetoModelRepository projetoModelRepository;

	    @Autowired
	    private CoberturaRepository coberturaRepository;

	    @Autowired
	    private PerfilTercaRepository perfilTercaRepository;

    @Autowired
    private ProjetoRepository repository;

    public tercacost.model.Projeto salvar(tercacost.model.Projeto projeto) {
        // Validação simples de identificação do novo modelo MySQL
        if (projeto.getNome() == null || projeto.getNome().trim().isEmpty()) {
            throw new RuntimeException("O nome/descrição do projeto deve ser informado.");
        }
        
        // Salva diretamente na tabela oficial 'projetos' do MySQL Workbench
        return projetoModelRepository.save(projeto);
    }


    public List<Projeto> consultar() {
        return repository.findAll();
    }

    public Projeto getUm(Long id) {
        Optional<Projeto> opt = repository.findById(id);
        return opt.orElseThrow(() -> new RuntimeException("Projeto não encontrado com o ID: " + id));
    }

    public Projeto getUmPorUsuario(Long id, Long usuarioId) {
        Projeto proj = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));

        if (proj.getUsuario() == null || !proj.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado: Este projeto pertence a outro usuário.");
        }

        return proj;
    }

    public void excluirPorUsuario(Long id, Long usuarioId) {
        Projeto proj = getUmPorUsuario(id, usuarioId);
        repository.delete(proj);
    }

    public tercacost.model.Projeto alterar(Long id, tercacost.model.Projeto projeto) {
        // Valida se o projeto existe na nova tabela do MySQL Workbench
        tercacost.model.Projeto proj = projetoModelRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));

        // Atualiza apenas o nome/descrição principal na tabela de projetos
        proj.setNome(projeto.getNome());

        return projetoModelRepository.save(proj);
    }

    
    public void excluir(Long id) {
        repository.deleteById(id);
    }
    
    
    public List<Projeto> consultarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }
    
    
  

    @Transactional
    public void salvarCalculoCompleto(CalculoRequest request, ResultadoVerificacaoDTO resultado, tercacost.entities.Usuario usuarioAtivo) {
        
        tercacost.model.Projeto proj;
        tercacost.model.Cobertura cob;
        PerfilTerca perf;

        // 1. SE FOR EDIÇÃO, CARREGA OS DADOS DO BANCO. SE FOR NOVO, CRIA LIMPO.
        if (request.getId() != null && request.getId() > 0) {
            proj = projetoModelRepository.findById(request.getId().intValue())
                    .orElse(new tercacost.model.Projeto());
            cob = coberturaRepository.findById(request.getId().intValue()).orElse(new tercacost.model.Cobertura());
            perf = perfilTercaRepository.findById(request.getId().intValue()).orElse(new PerfilTerca());
        } else {
            proj = new tercacost.model.Projeto();
            cob = new tercacost.model.Cobertura();
            perf = new PerfilTerca();
        }

        // Popula as informações básicas do Projeto Unificado
        proj.setNome(request.getDescricao());
        proj.setUsuario(usuarioAtivo);
        projetoModelRepository.save(proj); // Salva e gera o ID autoincremento do MySQL

        // 2. POPULA E SALVA A COBERTURA VINCULADA
        cob.setProjeto(proj); 
        cob.setComprimentoTerca(request.getVao());
        cob.setEspacamentoTercas(request.getEspacamento());
        cob.setLb(request.getLb());
        cob.setCargaTelha(request.getCargaPermanente());
        cob.setSobrecargaPadrao(request.getSobrecarga());
        cob.setVentoPressao(request.getVento());
        coberturaRepository.save(cob);

        // 3. POPULA E SALVA O PERFIL E TAXAS DA NBR 14762
        perf.setCobertura(cob);
        perf.setTipoPerfil(request.getTipoPerfil());
        perf.setAlturaAlma(request.getAlturaAlma());
        perf.setLarguraAba(request.getLarguraAba());
        perf.setLarguraEnrijecedor(request.getLarguraEnrijecedor());
        perf.setEspessuraChapa(request.getEspessuraChapa());
        perf.setFy(request.getFy());
        perf.setFu(request.getFu());
        perf.setE(request.getE());
        perf.setG(request.getG());
        perf.setCb(request.getCb());
        
        perf.setMrd(resultado.getMrd());
        perf.setVrd(resultado.getVrd());
        perf.setFlechaReal(resultado.getFlechaReal());
        perf.setTaxaMomento((resultado.getMsd() / resultado.getMrd()) * 100);
        perf.setTaxaCortante((resultado.getVsd() / resultado.getVrd()) * 100);
        perf.setTaxaFlecha((resultado.getFlechaReal() / resultado.getFlechaLimite()) * 100);
        perf.setAprovado(resultado.isAprovadoGeral());
        
        perfilTercaRepository.save(perf);
    }




}