package mx.edu.cbta.sistemaescolar.usuarios.repository;

import mx.edu.cbta.sistemaescolar.usuarios.dto.InformacionBasicaUsuarioDTO;
import mx.edu.cbta.sistemaescolar.usuarios.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u.id as id, u.curp as curp, u.nombre as nombre, " +
            "u.apellidoPaterno as apellidoPaterno, u.apellidoMaterno as apellidoMaterno " +
            "FROM Usuario u WHERE " +
            "u.nombre LIKE %:credenciales% OR " +
            "u.apellidoPaterno LIKE %:credenciales% OR " +
            "u.apellidoMaterno LIKE %:credenciales% OR " +
            "u.curp LIKE %:credenciales% OR " +
            "u.email LIKE %:credenciales% OR " +
            "u.id = :idLong")
    Page<InformacionBasicaUsuarioDTO> obtenerPorCredenciales(
            @Param("credenciales") String credenciales,
            @Param("idLong") Long idLong,
            Pageable pageable
    );

    Usuario findByEmail(String email);
    Usuario findByTelefono(String telefono);
    boolean existsByEmail(String email);
    boolean existsByTelefono(String telefono);
    boolean existsByCurp(String curp);
}
