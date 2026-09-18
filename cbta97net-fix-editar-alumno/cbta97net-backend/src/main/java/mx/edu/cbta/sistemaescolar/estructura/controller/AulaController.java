package mx.edu.cbta.sistemaescolar.estructura.controller;


import jakarta.validation.Valid;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AulaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.service.AulaService;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarAulaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.RegistrarAulaException;
import mx.edu.cbta.sistemaescolar.estructura.dto.AulaDTO;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aulas")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('AULAS_CREATE')")
    public ResponseEntity<AulaDTO> registrarAula(@Valid @RequestBody AulaDTO aulaDTO) throws AulaDuplicadaException, RegistrarAulaException {

        AulaDTO aulaCreadaDTO = this.aulaService.registrarAula(aulaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(aulaCreadaDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AULAS_DELETE')")
    public ResponseEntity<?> eliminarAula(@PathVariable("id") Long id) throws EliminarAulaException {

        this.aulaService.eliminarAulaPorId(id);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "El aula se eliminó con éxito.");

        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/clave/{clave}")
    @PreAuthorize("hasRole('AULAS_DELETE')")
    public ResponseEntity<?> eliminarAula(@PathVariable("clave") String clave) throws EliminarAulaException {

        this.aulaService.eliminarAulaPorClave(clave);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "El aula se eliminó con éxito.");

        return ResponseEntity.ok(body);
    }

    @GetMapping
    //@PreAuthorize("hasRole('AULAS_DELETE')")
    public ResponseEntity<List<AulaDTO>> obtenerTodasLasAulas() {
        List<AulaDTO> aulasDTO = aulaService.obtenerTodasLasAulas();
        return ResponseEntity.ok(aulasDTO);
    }
}
