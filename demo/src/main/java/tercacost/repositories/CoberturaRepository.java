package tercacost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import tercacost.model.Cobertura;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura, Integer> {
    Optional<Cobertura> findByProjetoId(Integer projetoId);
}
