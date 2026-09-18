package mx.edu.cbta.sistemaescolar.estructura.controller;

import jakarta.validation.Valid;

import mx.edu.cbta.sistemaescolar.estructura.service.AreaPropedeuticaService;
import mx.edu.cbta.sistemaescolar.estructura.mapper.AreaPropedeuticaMapper;
import mx.edu.cbta.sistemaescolar.estructura.dto.AreaPropedeuticaDTO;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AreaPropedeuticaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AreaPropedeuticaDuplicadaException;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarAreaPropedeuticaException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/areas-propedeuticas")
public class AreaPropedeuticaController {

    private final AreaPropedeuticaService areaPropedeuticaService;
    private final AreaPropedeuticaMapper areaPropedeuticaMapper;

    public AreaPropedeuticaController(
            AreaPropedeuticaService areaPropedeuticaService,
            AreaPropedeuticaMapper areaPropedeuticaMapper
    ) {
        this.areaPropedeuticaService = areaPropedeuticaService;
        this.areaPropedeuticaMapper = areaPropedeuticaMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('AREAS_PROPEDEUTICAS_CREATE')")
    public ResponseEntity<AreaPropedeuticaDTO> crearAreaPropedeutica(
            @Valid @RequestBody AreaPropedeuticaDTO areaPropedeuticaDTO
    ) throws AreaPropedeuticaDuplicadaException {
        AreaPropedeuticaDTO areaCreadaDTO = areaPropedeuticaService.registrarAreaPropedeutica(areaPropedeuticaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(areaCreadaDTO);
    }

    @GetMapping
    public ResponseEntity<List<AreaPropedeuticaDTO>> obtenerAreasPropedeuticas() {
        List<AreaPropedeuticaDTO> areasDTO = this.areaPropedeuticaService.obtenerAreasPropedeuticas();
        return ResponseEntity.ok(areasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaPropedeuticaDTO> obtenerAreaPorId(@PathVariable("id") Long id) throws AreaPropedeuticaNoEncontradaException {
        AreaPropedeuticaDTO areaDTO = this.areaPropedeuticaService.obtenerAreaPropedeuticaPorId(id);
        return ResponseEntity.ok(areaDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AREAS_PROPEDEUTICAS_DELETE')")
    public ResponseEntity<?> eliminarAreaPropedeutica(@PathVariable("id") Long id) throws EliminarAreaPropedeuticaException, AreaPropedeuticaNoEncontradaException {
        this.areaPropedeuticaService.eliminarAreaPorId(id);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "El área propedéutica se eliminó con éxito.");

        return ResponseEntity.ok(body);
    }
}