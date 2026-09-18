package mx.edu.cbta.sistemaescolar.estructura.repository;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.CarreraTecnica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraTecnicaRepository extends JpaRepository<CarreraTecnica, Long> {
    CarreraTecnica findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
