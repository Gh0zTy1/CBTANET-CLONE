package mx.edu.cbta.sistemaescolar.gruposparaescolares.repository;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model.AlumnoInscritoParaescolar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoInscritoParaescolarRepository extends JpaRepository<AlumnoInscritoParaescolar, Long> {

    @Query("SELECT aip FROM AlumnoInscritoParaescolar aip " +
            "JOIN Alumno a ON aip.alumnoId = a.id " +
            "WHERE a.matricula = :matricula")
    List<AlumnoInscritoParaescolar> findAllByMatricula(@Param("matricula") String matricula);

    List<AlumnoInscritoParaescolar> findAllByGrupoParaescolarId(Long grupoId);

    Optional<AlumnoInscritoParaescolar> findByAlumnoIdAndGrupoParaescolarId(Long alumnoId, Long grupoId);
    
    Optional<AlumnoInscritoParaescolar> findByAlumnoIdAndGrupoParaescolarIdAndFechaBajaIsNull(Long alumnoId, Long grupoId);

    @Query("SELECT COUNT(aip) > 0 FROM AlumnoInscritoParaescolar aip " +
            "JOIN GrupoParaescolar gp ON aip.grupoParaescolarId = gp.id " +
            "JOIN Alumno a ON aip.alumnoId = a.id " +
            "WHERE a.matricula = :matricula " +
            "AND gp.cicloEscolarId = :idCiclo " +
            "AND aip.fechaBaja IS NULL")
    boolean existeInscripcionActivaEnCiclo(@Param("matricula") String matricula, @Param("idCiclo") Long idCiclo);
}
