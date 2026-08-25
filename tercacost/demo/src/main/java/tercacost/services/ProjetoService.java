package tercacost.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tercacost.entities.Projeto;
import tercacost.repositories.ProjetoRepository;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository repository;

    public Projeto salvar(Projeto projeto) {
        if (projeto.getDescricao() == null || projeto.getDescricao().trim().isEmpty()) {
            throw new RuntimeException("A descrição deve ser informada.");
        }
        if (projeto.getMSD() < 0 || projeto.getVSD() < 0 || projeto.getLb() < 0 || projeto.getCb() < 0) {
            throw new RuntimeException("Os valores numéricos não podem ser menores que zero.");
        }

        return repository.save(projeto);
    }

    public List<Projeto> consultar() {
        return repository.findAll();
    }

    public Projeto getUm(Long id) {
        Optional<Projeto> opt = repository.findById(id);
        return opt.orElseThrow(() -> new RuntimeException("Projeto não encontrado com o ID: " + id));
    }

    public Projeto alterar(Long id, Projeto projeto) {
        // 1. Fetch the existing entity (which contains the database ID)
        Projeto proj = getUm(id);

        // 2. Update the existing entity with incoming values
        proj.setDescricao(projeto.getDescricao());
        proj.setNomedoPerfil(projeto.getNomedoPerfil());
        proj.setMSD(projeto.getMSD());
        proj.setVSD(projeto.getVSD());
        proj.setLb(projeto.getLb());
        proj.setCb(projeto.getCb());

        // 3. Save the updated entity (JPA triggers UPDATE instead of INSERT)
        return repository.save(proj);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}