package mx.edu.cbta.sistemaescolar.estructura.controller;

import mx.edu.cbta.sistemaescolar.estructura.dto.ImportarArchivoSiseemsDTO;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.*;

import mx.edu.cbta.sistemaescolar.estructura.service.CarreraTecnicaService;
import mx.edu.cbta.sistemaescolar.estructura.mapper.CarreraTecnicaMapper;
import mx.edu.cbta.sistemaescolar.estructura.dto.CarreraTecnicaDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carreras-tecnicas")
public class CarreraTecnicaController {

    @Autowired
    private CarreraTecnicaService carreraTecnicaService;

    @Autowired
    private CarreraTecnicaMapper carreraTecnicaMapper;

    @GetMapping
    public ResponseEntity<List<CarreraTecnicaDTO>> obtenerTodasCarrerasTecnicas() {
        List<CarreraTecnicaDTO> listaDTO = this.carreraTecnicaService.obtenerCarrerasTodas();
        return ResponseEntity.ok(listaDTO);
    }

    @PostMapping
    public ResponseEntity<CarreraTecnicaDTO> registrarCarreraTecnica(
            @Valid @RequestBody CarreraTecnicaDTO carreraTecnicaDTO
    ) throws RegistrarCarreraTecnicaException, CarreraTecnicaDuplicadaException {
        CarreraTecnicaDTO registradoDTO = this.carreraTecnicaService.registrarCarreraTecnica(carreraTecnicaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registradoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCarreraTecnica(@PathVariable("id") Long id)
            throws EliminarCarreraTecnicaException, CarreraTecnicaNoEncontradaException
    {
        this.carreraTecnicaService.eliminarCarreraTecnicaPorId(id);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "La carrera técnica se eliminó con éxito.");

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public CarreraTecnicaDTO obtenerCarreraTecnicaPorId(@PathVariable Long id) throws CarreraTecnicaNoEncontradaException {
        return this.carreraTecnicaService.obtenerCarreraTecnicaPorId(id);
    }

    @PostMapping("/importar")
    @PreAuthorize("hasRole('CARRERAS_TECNICAS_CREATE')")
    public ResponseEntity<?> importarCarrerasTecnicasArchivoSISEEMS(@ModelAttribute ImportarArchivoSiseemsDTO dto)
            throws ImportarCarrerasTecnicasSISEEMSException {
        this.carreraTecnicaService.importarCarrerasTecnicasSISEEMS(dto.getDocumento());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Se importaron las carreras técnicas correctamente.");

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
