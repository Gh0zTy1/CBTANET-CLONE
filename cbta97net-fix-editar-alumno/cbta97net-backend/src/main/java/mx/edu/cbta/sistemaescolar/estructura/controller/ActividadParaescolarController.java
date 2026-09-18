package mx.edu.cbta.sistemaescolar.estructura.controller;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ModificarParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CrearParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.service.ActividadParaescolarService;
import mx.edu.cbta.sistemaescolar.estructura.mapper.ActividadParaescolarMapper;
import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/paraescolares")
public class ActividadParaescolarController {

    private final ActividadParaescolarService actividadParaescolarService;
    private final ActividadParaescolarMapper actividadParaescolarMapper;

    public ActividadParaescolarController(ActividadParaescolarService service, ActividadParaescolarMapper mapper) {
        this.actividadParaescolarService = service;
        this.actividadParaescolarMapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ACTIVIDADES_PARAESCOLARES_READ')")
    public ResponseEntity<Page<ActividadParaescolarDTO>> obtenerActividadesParaescolares(
            @PageableDefault(page = 0, size = 10, sort = "nombre") Pageable pageable,
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre
    ) {
        return ResponseEntity.ok(this.actividadParaescolarService.obtenerActividadesParaescolaresPorNombre(nombre, pageable));
    }

    /*
    @GetMapping
    @PreAuthorize("hasRole('ACTIVIDADES_PARAESCOLARES_READ')")
    public ResponseEntity<List<ActividadParaescolarDTO>> listar() {
        List<ActividadParaescolarDTO> lista = actividadParaescolarService.obtenerParaescolares();
        return ResponseEntity.ok(lista);
    }*/

    @PostMapping
    @PreAuthorize("hasRole('ACTIVIDADES_PARAESCOLARES_CREATE')")
    public ResponseEntity<ActividadParaescolarDTO> crear(@Valid @RequestBody ActividadParaescolarDTO actividadNuevaDTO) throws CrearParaescolarException {
        ActividadParaescolarDTO guardada = actividadParaescolarService.crearActividadParaescolar(actividadNuevaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ACTIVIDADES_PARAESCOLARES_UPDATE')")
    public ResponseEntity<ActividadParaescolarDTO> modificar(@PathVariable Long id, @Valid @RequestBody ActividadParaescolarDTO actividadParaescolarDTO)
            throws ParaescolarNoEncontradaException, ModificarParaescolarException {
        ActividadParaescolarDTO actualizada = actividadParaescolarService.modificarParaescolar(id, actividadParaescolarDTO);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ACTIVIDADES_PARAESCOLARES_DELETE')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) throws ParaescolarNoEncontradaException, EliminarParaescolarException {
        actividadParaescolarService.eliminarParaescolar(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "La actividad paraescolar ha sido eliminada con éxito.");
        return ResponseEntity.ok(response);
    }
}
