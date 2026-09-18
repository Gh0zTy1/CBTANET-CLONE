package mx.edu.cbta.sistemaescolar.alumnado.repository;

import mx.edu.cbta.sistemaescolar.alumnado.dto.InformacionBasicaAlumnoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByCurp(String curp);
    Optional<Alumno> findByMatricula(String matricula);
    Optional<Alumno> findByNumeroSeguroSocial(String nss);
    boolean existsByMatricula(String matricula);

    @Query("SELECT a.id as id, a.matricula as matricula, a.nombre as nombre, " +
            "a.apellidoPaterno as apellido_paterno, a.apellidoMaterno as apellido_materno, " +
            "a.curp as curp FROM Alumno a WHERE " +
            "a.nombre LIKE %:credenciales% OR " +
            "a.apellidoPaterno LIKE %:credenciales% OR " +
            "a.apellidoMaterno LIKE %:credenciales% OR " +
            "a.curp LIKE %:credenciales% OR " +
            "a.matricula LIKE %:credenciales%")
    Page<InformacionBasicaAlumnoDTO> obtenerPorCredencialesOptimizado(@Param("credenciales") String credenciales, Pageable pageable);

    /*
    @Query("SELECT a FROM Alumno a WHERE " +
                  "a.nombre LIKE %:credenciales% OR " +
                  "a.apellidoPaterno LIKE %:credenciales% OR " +
                  "a.apellidoMaterno LIKE %:credenciales% OR " +
                  "a.curp LIKE %:credenciales% OR " +
                  "a.matricula LIKE %:credenciales%")
    Page<Alumno> obtenerPorCredenciales(@Param("credenciales") String credenciales, Pageable pageable);
     */

    @Transactional
    @Modifying
    void deleteByMatricula(String matricula);
}