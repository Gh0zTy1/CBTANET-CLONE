package mx.edu.cbta.sistemaescolar.estructura.repository;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.ActividadParaescolar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// [Diagrama de Secuencia] Lifeline: :ActividadParaescolarRepository
@Repository
public interface ActividadParaescolarRepository extends JpaRepository<ActividadParaescolar, Long> {

    //findByNombre(nombre)
    Optional<ActividadParaescolar> findByNombre(String nombre);


    // Auxiliar para validación en Modificar (no permitir duplicados en otros IDs)
    boolean existsByNombreAndIdNot(String nombre, Long id);

    Page<ActividadParaescolar> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}