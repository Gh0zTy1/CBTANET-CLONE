package mx.edu.cbta.sistemaescolar.grupos.controller;

import mx.edu.cbta.sistemaescolar.grupos.domain.exception.*;
import mx.edu.cbta.sistemaescolar.grupos.dto.CrearNuevoGrupoDTO;
import mx.edu.cbta.sistemaescolar.grupos.service.GrupoService;
import mx.edu.cbta.sistemaescolar.grupos.mapper.GrupoMapper;
import mx.edu.cbta.sistemaescolar.grupos.dto.GrupoDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private GrupoService grupoService;

    @Autowired
    private GrupoMapper grupoMapper;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('GRUPOS_SEMESTRE_READ')")
    public ResponseEntity<?> registrarGrupo(@Valid @RequestBody CrearNuevoGrupoDTO nuevoGrupoDTO)
            throws GrupoException,
            RegistrarGrupoException,
            GrupoYaExistenteException
    {
        this.grupoService.registrarGrupo(this.grupoMapper.toDTO(nuevoGrupoDTO));

        Map<String, String> body = new HashMap<>();
        body.put("message", "El grupo se creó correctamente.");

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    @PreAuthorize("hasRole('GRUPOS_SEMESTRE_READ')")
    public ResponseEntity<Page<GrupoDTO>> obtenerGruposPaginado(Pageable pageable) {
        Page<GrupoDTO> gruposDTO = this.grupoService.obtenerGrupos(pageable);
        return ResponseEntity.ok(gruposDTO);
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('GRUPOS_SEMESTRE_READ')")
    public ResponseEntity<List<GrupoDTO>> obtenerGruposActivos() throws GrupoException {
        return ResponseEntity.ok(this.grupoService.obtenerGruposActivos());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GRUPOS_SEMESTRE_DELETE')")
    public ResponseEntity<?> eliminarGrupo(@PathVariable("id") Long id) throws EliminarGrupoSemestralException, GrupoNoEncontradoException {
        this.grupoService.eliminarGrupo(id);

        Map<String, String> body = new HashMap<>();
        body.put("message", "El grupo se eliminó correctamente.");

        return ResponseEntity.ok(body);
    }
}
