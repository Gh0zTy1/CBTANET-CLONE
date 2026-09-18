package mx.edu.cbta.sistemaescolar.personal.repository;

import mx.edu.cbta.sistemaescolar.personal.domain.model.Docente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    /**
     * Busca docentes asociados a una materia específica.
     * Se eliminó el FETCH de roles porque ahora es un campo @Transient
     * y no reside en la base de datos.
     */
    @Query("SELECT d FROM Docente d WHERE d.id IN " +
            "(SELECT c.docenteId FROM Clase c WHERE c.materiaId = :materiaId)")
    Page<Docente> findByMateriasId(@Param("materiaId") Long materiaId, Pageable pageable);


    boolean existsById(Long docenteId);

    boolean existsByCedulaProfesional(String cedula);

    boolean existsByUsuarioId(Long usuarioId);
}
