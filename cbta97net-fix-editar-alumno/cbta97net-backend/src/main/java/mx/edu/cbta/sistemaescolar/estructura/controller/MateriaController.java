package mx.edu.cbta.sistemaescolar.estructura.controller;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.SemestreMateriaNoValidoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ImportarMateriasSISEEMSException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.dto.ImportarArchivoSiseemsDTO;
import mx.edu.cbta.sistemaescolar.estructura.service.MateriaService;
import mx.edu.cbta.sistemaescolar.estructura.mapper.MateriaMapper;
import mx.edu.cbta.sistemaescolar.estructura.dto.MateriaDTO;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/materias")
public class MateriaController {

    private final MateriaService materiaService;
    private final MateriaMapper materiaMapper;

    public MateriaController(MateriaService materiaService, MateriaMapper materiaMapper) {
        this.materiaService = materiaService;
        this.materiaMapper = materiaMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaDTO> obtenerMateriaPorId(@PathVariable Long id) throws MateriaNoEncontradaException {
        MateriaDTO materiaDTO = materiaService.obtenerMateriaPorId(id);
        return ResponseEntity.ok(materiaDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('MATERIAS_CREATE')")
    public ResponseEntity<MateriaDTO> registrarMateria(@Valid @RequestBody MateriaDTO materiaDTO) throws MateriaDuplicadaException, SemestreMateriaNoValidoException {
        MateriaDTO materiaCreadaDTO = materiaService.registrarMateria(materiaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(materiaCreadaDTO);
    }

    @GetMapping
    public ResponseEntity<List<MateriaDTO>> obtenerTodasLasMaterias() {
        List<MateriaDTO> materiasDTO = materiaService.obtenerTodasLasMaterias();
        return ResponseEntity.ok(materiasDTO);
    }

    @GetMapping("/carrera/{carreraTecnicaId}")
    public ResponseEntity<List<MateriaDTO>> obtenerMateriasPorCarrera(@PathVariable Long carreraTecnicaId) {
        List<MateriaDTO> materiasDTO = materiaService.obtenerMateriasPorCarrera(carreraTecnicaId);
        return ResponseEntity.ok(materiasDTO);
    }

    @GetMapping("/area/{id}")
    public ResponseEntity<List<MateriaDTO>> obtenerMateriasPorAreaPropedeutica(@PathVariable Long id) throws MateriaNoEncontradaException {
        List<MateriaDTO> materiasDTO = materiaService.obtenerTodasPorAreaPropedeutica(id);
        return ResponseEntity.ok(materiasDTO);
    }

    @GetMapping("/grado/{grado}")
    public ResponseEntity<List<MateriaDTO>> obtenerMateriasPorGrado(@PathVariable Integer grado) {
        List<MateriaDTO> materiasDTO = materiaService.obtenerMateriasPorSemestre(grado);
        return ResponseEntity.ok(materiasDTO);
    }

    @GetMapping("/grado/{grado}/carrera/{carreraTecnicaId}")
    public ResponseEntity<List<MateriaDTO>> obtenerMateriasPorGradoYCarrera(
            @PathVariable Integer grado,
            @PathVariable Long carreraTecnicaId) {

        List<MateriaDTO> materiasDTO = materiaService.obtenerMateriasPorSemestreYCarrera(grado, carreraTecnicaId);
        return ResponseEntity.ok(materiasDTO);
    }

    @GetMapping("/grado/{grado}/carrera/{carreraTecnicaId}/area/{areaPropedeuticaId}")
    public ResponseEntity<List<MateriaDTO>> obtenerMateriasPorGradoYCarreraYAreaPropedeutica(
            @PathVariable Integer grado,
            @PathVariable Long carreraTecnicaId,
            @PathVariable Long areaPropedeuticaId
    ) {
        List<MateriaDTO> materiasDTO = materiaService
                .obtenerMateriasPorSemestreYCarreraYArea(grado, carreraTecnicaId, areaPropedeuticaId);
        return ResponseEntity.ok(materiasDTO);
    }

    @PostMapping("/importar")
    @PreAuthorize("hasRole('MATERIAS_CREATE')")
    public ResponseEntity<?> importarMateriasArchivoSISEEMS(@ModelAttribute ImportarArchivoSiseemsDTO dto)
            throws ImportarMateriasSISEEMSException
    {
        this.materiaService.importarMateriasSISEEMS(dto.getDocumento());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Se importaron las materias correctamente.");

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
