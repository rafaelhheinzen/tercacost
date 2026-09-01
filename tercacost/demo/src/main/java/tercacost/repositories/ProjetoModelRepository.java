package tercacost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import tercacost.model.Projeto;

@Repository
public interface ProjetoModelRepository extends JpaRepository<Projeto, Integer> {
    // Rota rápida para buscar os projetos direto do MySQL baseados no usuário logado
    List<Projeto> findByUsuarioId(Long usuarioId);
}
