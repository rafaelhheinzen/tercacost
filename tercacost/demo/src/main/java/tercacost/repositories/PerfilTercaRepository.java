package tercacost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import tercacost.model.PerfilTerca;

@Repository
public interface PerfilTercaRepository extends JpaRepository<PerfilTerca, Integer> {
    
    Optional<PerfilTerca> findByCoberturaId(Integer coberturaId);
}
