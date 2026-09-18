package mx.edu.cbta.sistemaescolar.grupos.repository;

import mx.edu.cbta.sistemaescolar.grupos.domain.model.Grupo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findByCicloEscolarId(Long idCicloEscolar);

    Grupo findBySemestreAndLetraAndCicloEscolarId(int semestre, Character letra, Long cicloEscolarId);

    boolean existsBySemestreAndLetraAndCicloEscolarId(int semestre, Character letra, Long cicloEscolarId);
}
