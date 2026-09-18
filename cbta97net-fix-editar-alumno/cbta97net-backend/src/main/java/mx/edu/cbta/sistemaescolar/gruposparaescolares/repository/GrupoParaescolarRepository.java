package mx.edu.cbta.sistemaescolar.gruposparaescolares.repository;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model.GrupoParaescolar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoParaescolarRepository extends JpaRepository<GrupoParaescolar, Long> {

    /*
    @Query("SELECT COUNT(a) FROM AlumnoInscritoParaescolar a JOIN a.grupoParaescolarId g WHERE g.actividadParaescolarId = :idGrupoParaescolar")
    long countAlumnosInscritosPorActividadParaescolar(Long idGrupoParaescolar);
    */
    @Query("SELECT COUNT(a) FROM AlumnoInscritoParaescolar a, GrupoParaescolar g " +
            "WHERE a.grupoParaescolarId = g.id " +
            "AND g.actividadParaescolarId = :idGrupoParaescolar")
    long countAlumnosInscritosPorActividadParaescolar(@Param("idGrupoParaescolar") Long idGrupoParaescolar);

    /**
     * Obtiene todos los grupos escolares que un docente llevó en un ciclo escolar determinado.
     * @param idDocente Docente a cargo del grupo paraescolar.
     * @param idCicloEscolar Ciclo escolar a buscar.
     * @return
     */
    List<GrupoParaescolar> findGrupoParaescolarByDocenteIdAndCicloEscolarId(Long idDocente, Long idCicloEscolar);

    /**
     * Obtiene una página (Page) de grupos paraescolares que pertenecen a un ciclo escolar específico.
     *
     * @param idCicloEscolar El identificador único del ciclo escolar por el cual se desean filtrar los grupos.
     * @param pageable El objeto {@code Pageable} que contiene la información de paginación (número de página,
     * tamaño de página y criterios de ordenamiento).
     * @return Un objeto {@code Page<GrupoParaescolar>} que contiene la lista de grupos encontrados
     * para la página solicitada, junto con metadatos de paginación (ej. total de elementos,
     * total de páginas).
     */
    Page<GrupoParaescolar> findGrupoParaescolarByCicloEscolarId(Long idCicloEscolar, Pageable pageable);

    /**
     * Obtiene todos los grupos paraescolares que pertenecen a un ciclo escolar específico.
     *
     * @param idCicloEscolar El identificador único del ciclo escolar por el cual se desean filtrar los grupos.
     * @return Un objeto {@code Page<GrupoParaescolar>} que contiene la lista de grupos encontrados
     * para la página solicitada, junto con metadatos de paginación (ej. total de elementos,
     * total de páginas).
     */
    List<GrupoParaescolar> findGrupoParaescolarByCicloEscolarId(Long idCicloEscolar);

    //Page<GrupoParaescolar> findByActividadParaescolar_NombreContainingIgnoreCaseAndCicloEscolarId(String nombre, Long idCiclo, Pageable pageable);

    /**
     * Calcula los espacios disponibles restando el cupo máximo menos los alumnos inscritos activos.
     * Se asume que AlumnoInscritoParaescolar tiene el campo grupoParaescolarId.
     */
    @Query("SELECT (g.maximoEspacios - (SELECT COUNT(a) FROM AlumnoInscritoParaescolar a " +
            "WHERE a.grupoParaescolarId = g.id AND a.fechaBaja IS NULL)) " +
            "FROM GrupoParaescolar g WHERE g.id = :idGrupo")
    Integer consultarEspaciosDisponibles(Long idGrupo);
}
