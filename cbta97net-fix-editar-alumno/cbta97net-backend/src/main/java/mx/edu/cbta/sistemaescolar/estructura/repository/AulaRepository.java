package mx.edu.cbta.sistemaescolar.estructura.repository;

import jakarta.transaction.Transactional;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    Aula findByClave(String clave);

    @Modifying
    @Transactional
    void deleteByClave(String clave);

    boolean existsByClave(String clave);
}
