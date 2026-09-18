package mx.edu.cbta.sistemaescolar.gruposparaescolares.controller;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.*;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.*;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.service.GrupoParaescolarService;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.mapper.GrupoParaescolarMapper;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/paraescolares/grupos")
public class GrupoParaescolarController {

    private GrupoParaescolarService grupoParaescolarService;
    private GrupoParaescolarMapper grupoParaescolarMapper;

    public GrupoParaescolarController(GrupoParaescolarService grupoParaescolarService,
            GrupoParaescolarMapper grupoParaescolarMapper) {
        this.grupoParaescolarMapper = grupoParaescolarMapper;
        this.grupoParaescolarService = grupoParaescolarService;
    }

    @GetMapping
    @PreAuthorize("hasRole('GRUPOS_PARAESCOLARES_READ')")
    public ResponseEntity<Page<GrupoParaescolarDTO>> listarGruposPaginados(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        Page<GrupoParaescolarDTO> gruposPage = this.grupoParaescolarService.obtenerGruposParaescolares(pageable);
        return ResponseEntity.ok(gruposPage);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<GrupoParaescolarDTO>> listarGruposActivosPaginados() throws GrupoParaescolarException {
        List<GrupoParaescolarDTO> dtoPage = this.grupoParaescolarService.obtenerGruposParaescolaresActivos();
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoParaescolarDTO> obtenerGrupoPorId(@PathVariable Long id)
            throws GrupoParaescolarNoEncontradoException {
        GrupoParaescolarDTO grupoDTO = this.grupoParaescolarService.obtenerGrupoPorId(id);
        return ResponseEntity.ok(grupoDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('GRUPOS_PARAESCOLARES_CREATE')")
    public ResponseEntity<?> crearGrupoParaescolar(
            @Valid @RequestBody CrearGrupoParaescolarDTO crearGrupoParaescolarDTO)
            throws CrearGrupoParaescolarException {

        this.grupoParaescolarService.crearGrupo(this.grupoParaescolarMapper.toDTO(crearGrupoParaescolarDTO));

        Map<String, String> body = new HashMap<>();
        body.put("message", "Grupo paraescolar creado con éxito.");

        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GRUPOS_PARAESCOLARES_DELETE')")
    public ResponseEntity<?> eliminarGrupoParaescolar(@PathVariable("id") Long grupoId)
            throws GrupoParaescolarNoEncontradoException {
        this.grupoParaescolarService.eliminarGrupoParaescolar(grupoId);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Grupo paraescolar eliminado con éxito.");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/inscripciones")
    @PreAuthorize("hasRole('ALUMNOS_INSCRIPCIONES')")
    public ResponseEntity<?> inscribirAlumnoAParaescolar(
            @Valid @RequestBody     InscribirAlumnoParaescolarDTO inscripcionDTO)
            throws InscribirAlumnoParaescolarException, GrupoParaescolarNoEncontradoException {
        this.grupoParaescolarService.inscribirAlumnoAGrupoParaescolar(inscripcionDTO.getMatricula(),
                inscripcionDTO.getGrupoId());

        Map<String, String> body = new HashMap<>();
        body.put("mensaje", "Se ha inscrito correctamente al alumno en el grupo.");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/bajas")
    @PreAuthorize("hasRole('ALUMNOS_BAJAS_PARAESCOLARES')")
    public ResponseEntity<?> darDeBajaAlumno(
            @Valid @RequestBody InscribirAlumnoParaescolarDTO bajaDTO)
            throws BajaAlumnoParaescolarException, GrupoParaescolarNoEncontradoException {

        this.grupoParaescolarService.darDeBajaAlumnoDeGrupoParaescolar(
                bajaDTO.getMatricula(),
                bajaDTO.getGrupoId()
        );

        Map<String, String> body = new HashMap<>();
        body.put("mensaje", "El alumno ha sido dado de baja del grupo correctamente.");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/inscripciones-masivas")
    @PreAuthorize("hasRole('ALUMNOS_INSCRIPCIONES')")
    public ResponseEntity<?> inscribirMasivamente(
            @Valid @RequestBody InscripcionMasivaAlumnosDTO dto)
            throws InscribirAlumnoParaescolarException, GrupoParaescolarNoEncontradoException {

        this.grupoParaescolarService.inscribirMasivamenteAlumnosAGrupoParaescolar(dto);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Proceso de inscripción masiva finalizado con éxito.");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/bajas-masivas")
    @PreAuthorize("hasRole('ALUMNOS_BAJAS_PARAESCOLARES')")
    public ResponseEntity<?> darDeBajaMasivamente(
            @Valid @RequestBody BajaMasivaAlumnosDTO dto)
            throws BajaAlumnoParaescolarException, GrupoParaescolarNoEncontradoException {

        this.grupoParaescolarService.darBajaMasivamenteAlumnosDeGrupoParaescolar(dto);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Proceso de baja masiva finalizado con éxito.");
        return ResponseEntity.ok(body);
    }
}