package mx.edu.cbta.sistemaescolar.personal.controller;

import jakarta.validation.Valid;
import mx.edu.cbta.sistemaescolar.personal.dto.DocenteDTO;
import mx.edu.cbta.sistemaescolar.personal.dto.RegistrarDocenteDTO;
import mx.edu.cbta.sistemaescolar.personal.mapper.DocenteMapper;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteDuplicadoException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.RegistrarDocenteException;
import mx.edu.cbta.sistemaescolar.personal.service.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/docentes")
public class DocenteController {

    private final DocenteService docenteService;

    @Autowired
    private DocenteMapper docenteMapper;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCENTES_CREATE')")
    public ResponseEntity<DocenteDTO> registrarDocente(@Valid @RequestBody RegistrarDocenteDTO registrarDocenteDTO) throws DocenteDuplicadoException, RegistrarDocenteException {
        DocenteDTO registrado = this.docenteService.registrarDocente(this.docenteMapper.toDto(registrarDocenteDTO));
        return ResponseEntity.ok(registrado);
    }


    @GetMapping("/materia/{materiaId}")
    @PreAuthorize("hasRole('DOCENTES_READ')")
    public ResponseEntity<Page<DocenteDTO>> obtenerDocentesPorMateria(
            @PathVariable Long materiaId,
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) throws DocenteException {
        Page<DocenteDTO> docentes = docenteService.obtenerDocentePorMateria(materiaId, pageable);
        return ResponseEntity.ok(docentes);
    }

    @GetMapping
    @PreAuthorize("hasRole('DOCENTES_READ')")
    public ResponseEntity<Page<DocenteDTO>> obtenerTodosLosDocentes(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
    ) throws DocenteException {
        Page<DocenteDTO> docentes = docenteService.obtenerTodos(pageable);

        return ResponseEntity.ok(docentes);
    }
}
