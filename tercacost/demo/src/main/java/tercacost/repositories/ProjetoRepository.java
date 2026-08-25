package tercacost.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tercacost.entities.Projeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long>{
	
}