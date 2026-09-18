package mx.edu.cbta.sistemaescolar.horario.repository;

import mx.edu.cbta.sistemaescolar.horario.domain.model.Clase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {

    @Query("SELECT c FROM Clase c WHERE c.grupoId IN :grupoIds AND c.aulaId = :aulaId")
    List<Clase> findByGrupoIdsAndAula(@Param("grupoIds") List<Long> grupoIds, @Param("aulaId") Long aulaId);

    List<Clase> findByDocenteId(Long docenteId);

    List<Clase> findByGrupoId(Long grupoId);

    //long countByGrupoId(Long grupoId);

    boolean existsByGrupoId(Long grupoId);

    List<Clase> findByAulaId(Long aulaId);

    @Query("SELECT c FROM Clase c, CicloEscolar ce " +
            "WHERE c.cicloEscolarId = ce.id " +
            "AND c.docenteId = :docenteId " +
            "AND :fechaActual BETWEEN ce.fechaInicio AND ce.fechaFin")
    List<Clase> obtenerClasesVigentesDocente(@Param("docenteId") Long docenteId, @Param("fechaActual") LocalDate fechaActual);

    List<Clase> findByDocenteIdAndCicloEscolarId(Long docenteId, Long cicloEscolarId);

    List<Clase> findByAulaIdAndCicloEscolarId(Long aulaId, Long cicloEscolarId);

    void deleteByGrupoId(Long grupoId);

    //List<Clase> findAllByCicloEscolarAndAula(Long idCicloEscolar, Long idAula);
}
