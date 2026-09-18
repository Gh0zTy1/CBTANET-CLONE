package mx.edu.cbta.sistemaescolar.horario.repository;

import mx.edu.cbta.sistemaescolar.horario.domain.model.ClaseParaescolar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClaseParaescolarRepository extends JpaRepository<ClaseParaescolar, Long> {

    List<ClaseParaescolar> findByDocenteId(Long docenteId);

    List<ClaseParaescolar> findByGrupoParaescolarId(Long grupoId);

    boolean existsByGrupoParaescolarId(Long grupoId);

    @Query("SELECT c FROM ClaseParaescolar c, CicloEscolar ce " +
            "WHERE c.cicloEscolarId = ce.id " +
            "AND c.docenteId = :docenteId " +
            "AND :fechaActual BETWEEN ce.fechaInicio AND ce.fechaFin")
    List<ClaseParaescolar> obtenerClasesVigentesDocente(@Param("docenteId") Long docenteId, @Param("fechaActual") LocalDate fechaActual);

    List<ClaseParaescolar> findByDocenteIdAndCicloEscolarId(Long docenteId, Long cicloEscolarId);

    List<ClaseParaescolar> findByCicloEscolarId(Long cicloEscolarId);

    void deleteByGrupoParaescolarId(Long grupoId);
}
