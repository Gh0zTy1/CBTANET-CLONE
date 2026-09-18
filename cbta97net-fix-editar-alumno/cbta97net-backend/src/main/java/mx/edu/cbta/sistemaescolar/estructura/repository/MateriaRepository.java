package mx.edu.cbta.sistemaescolar.estructura.repository;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
    List<Materia> findByCarreraTecnicaId(Long carreraTecnicaId);

    Materia findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Materia> findBySemestre(int semestre);

    @Query("SELECT m FROM Materia m WHERE m.areaPropedeutica.id = :areaId")
    List<Materia> findByAreaPropedeuticaId(@Param("areaId") Long areaId);

    List<Materia> findBySemestreAndCarreraTecnicaId(int semestre, Long carreraTecnicaId);

    List<Materia> findBySemestreAndCarreraTecnicaIdAndAreaPropedeuticaId(int semestre, Long carreraTecnicaId, Long areaPropedeuticaId);
}
