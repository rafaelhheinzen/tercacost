package tercacost.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tercacost.model.Projeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    List<Projeto> findByUsuarioId(Long usuarioId);
}