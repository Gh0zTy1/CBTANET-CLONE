package mx.edu.cbta.sistemaescolar.estructura.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.estructura.dto.CarreraTecnicaDTO;
import mx.edu.cbta.sistemaescolar.estructura.mapper.CarreraTecnicaMapper;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.CarreraTecnica;
import mx.edu.cbta.sistemaescolar.estructura.repository.CarreraTecnicaRepository;
import mx.edu.cbta.sistemaescolar.estructura.service.CarreraTecnicaService;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CarreraTecnicaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarCarreraTecnicaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ImportarCarrerasTecnicasSISEEMSException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.RegistrarCarreraTecnicaException;

import mx.edu.cbta.sistemaescolar.estructura.util.LectorCarrerasTecnicasSISEEMS;
import mx.edu.cbta.sistemaescolar.estructura.util.impl.LectorCarrerasTecnicasSISEEMSImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CarreraTecnicaServiceImpl implements CarreraTecnicaService {

    private CarreraTecnicaRepository carreraTecnicaRepository;

    private LectorCarrerasTecnicasSISEEMS lectorCarrerasTecnicasSISEEMS;

    @Autowired
    private CarreraTecnicaMapper carreraTecnicaMapper;

    public CarreraTecnicaServiceImpl(CarreraTecnicaRepository carreraTecnicaRepository) {
        this.carreraTecnicaRepository = carreraTecnicaRepository;
        this.lectorCarrerasTecnicasSISEEMS = new LectorCarrerasTecnicasSISEEMSImpl();
    }

    @Override
    public CarreraTecnicaDTO obtenerCarreraTecnicaPorId(Long idCarrera) throws CarreraTecnicaNoEncontradaException {
        return this.carreraTecnicaRepository.findById(idCarrera)
                .map(this.carreraTecnicaMapper::toDto)
                .orElseThrow(() -> new CarreraTecnicaNoEncontradaException(
                        "No se encontró la carrera con ID: %s.".formatted(idCarrera)
                ));
    }

    @Override
    public List<CarreraTecnicaDTO> obtenerCarrerasTodas() {
        return this.carreraTecnicaRepository.findAll()
                .stream()
                .map(this.carreraTecnicaMapper::toDto)
                .toList();
    }

    @Override
    public CarreraTecnicaDTO registrarCarreraTecnica(CarreraTecnicaDTO carreraTecnicaDTO) throws RegistrarCarreraTecnicaException {

        if (this.carreraTecnicaRepository.existsByNombre(carreraTecnicaDTO.getNombre())) {
            throw new RegistrarCarreraTecnicaException("Ya existe una carrera con dicho nombre: %s".formatted(carreraTecnicaDTO.getNombre()));
        }

        CarreraTecnica registrada = this.carreraTecnicaRepository.save(this.carreraTecnicaMapper.toEntity(carreraTecnicaDTO));

        return this.carreraTecnicaMapper.toDto(registrada);
    }

    @Override
    public void eliminarCarreraTecnicaPorId(Long carreraId) throws CarreraTecnicaNoEncontradaException, EliminarCarreraTecnicaException {

        if (!this.carreraTecnicaRepository.existsById(carreraId)) {
            throw new CarreraTecnicaNoEncontradaException("No se encontró la carrera técnica con ID: %s.".formatted(carreraId));
        }

        try {
            this.carreraTecnicaRepository.deleteById(carreraId);
        } catch (Exception ex) {
            throw new EliminarCarreraTecnicaException("No se pudo eliminar la carrera técnica. Intente otra ve.");
        }
    }

    /**
     * Método para normalizar texto:
     * - Elimina acentos (Á -> A)
     * - Elimina caracteres especiales (solo deja letras y espacios)
     * - Convierte a MAYÚSCULAS
     */
    private String normalizarTexto(String texto) {
        if (texto == null) return null;

        String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return normalizado.replaceAll("[^a-zA-Z\\s]", "")
                .toUpperCase()
                .trim();
    }

    @Override
    public void importarCarrerasTecnicasSISEEMS(MultipartFile archivoSISEEMS)
            throws ImportarCarrerasTecnicasSISEEMSException {
        try {
            List<CarreraTecnica> carrerasTecnicasEncontradas = this.lectorCarrerasTecnicasSISEEMS
                    .obtenerCarrerasTecnicas(archivoSISEEMS.getInputStream());

            List<CarreraTecnica> carrerasTecnicasFiltradas = carrerasTecnicasEncontradas
                    .stream()
                    .peek(c -> c.setNombre(normalizarTexto(c.getNombre())))
                    .filter(c -> !carreraTecnicaRepository.existsByNombre(c.getNombre()))
                    .toList();

            long registrados = this.carreraTecnicaRepository.saveAll(carrerasTecnicasFiltradas).size();

            log.info("Carreras Tecnicas importadas desde el archivo de SISEEMS: %d".formatted(registrados));
        } catch (IOException e) {
            throw new ImportarCarrerasTecnicasSISEEMSException(e.getMessage());
        }
    }
}
