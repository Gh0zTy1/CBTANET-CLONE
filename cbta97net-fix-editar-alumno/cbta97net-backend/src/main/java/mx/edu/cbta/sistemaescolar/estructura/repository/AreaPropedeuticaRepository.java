package mx.edu.cbta.sistemaescolar.estructura.repository;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.AreaPropedeutica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaPropedeuticaRepository extends JpaRepository<AreaPropedeutica, Long> {
	AreaPropedeutica findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
