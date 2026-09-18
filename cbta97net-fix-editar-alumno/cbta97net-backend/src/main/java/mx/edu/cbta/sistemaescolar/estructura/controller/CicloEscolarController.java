package mx.edu.cbta.sistemaescolar.estructura.controller;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CrearCicloEscolarException;
import mx.edu.cbta.sistemaescolar.estructura.service.CicloEscolarService;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/ciclos-escolares")
public class CicloEscolarController {
    private final CicloEscolarService cicloEscolarService;

    public CicloEscolarController(CicloEscolarService cicloEscolarService) {
        this.cicloEscolarService = cicloEscolarService;
    }

    @GetMapping("/activo")
    @PreAuthorize("hasRole('CICLOS_ESCOLARES_READ')")
    public ResponseEntity<CicloEscolarDTO> obtenerCicloEscolarActivo() throws CicloEscolarNoEncontradoException {

        CicloEscolarDTO cicloActivo = cicloEscolarService.obtenerCicloEscolarActivo();

        return ResponseEntity.ok(cicloActivo);
    }

    @PostMapping
    @PreAuthorize("hasRole('CICLOS_ESCOLARES_CREATE')")
    public ResponseEntity<?> crearNuevoCicloEscolar(@Valid @RequestBody CicloEscolarDTO cicloEscolarNuevoDTO) throws CrearCicloEscolarException {
        CicloEscolarDTO cicloEscolarCreado = this.cicloEscolarService.crearCicloEscolar(cicloEscolarNuevoDTO);
        return ResponseEntity.ok(cicloEscolarCreado);
    }
}
