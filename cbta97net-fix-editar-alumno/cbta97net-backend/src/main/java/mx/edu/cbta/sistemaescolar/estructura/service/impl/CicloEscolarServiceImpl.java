package mx.edu.cbta.sistemaescolar.estructura.service.impl;

import mx.edu.cbta.sistemaescolar.estructura.domain.model.CicloEscolar;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.repository.CicloEscolarRepository;
import mx.edu.cbta.sistemaescolar.estructura.service.CicloEscolarService;
import mx.edu.cbta.sistemaescolar.estructura.mapper.CicloEscolarMapper;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CrearCicloEscolarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CicloEscolarServiceImpl implements CicloEscolarService {
    private final CicloEscolarRepository cicloEscolarRepository;

    @Autowired
    private CicloEscolarMapper cicloEscolarMapper;

    public CicloEscolarServiceImpl(CicloEscolarRepository cicloEscolarRepository) {
        this.cicloEscolarRepository = cicloEscolarRepository;
    }

    public CicloEscolarDTO obtenerCicloEscolarActivo() throws CicloEscolarNoEncontradoException {
        LocalDate hoy = LocalDate.now();
        return Optional.ofNullable(cicloEscolarRepository.obtenerCicloEscolarActivo(hoy))
                .map(cicloEscolarMapper::toDTO)
                .orElseThrow(() -> new CicloEscolarNoEncontradoException("No hay ciclo escolar vigente"));
    }

    @Override
    public CicloEscolarDTO obtenerCicloEscolarPorId(Long id) throws CicloEscolarNoEncontradoException {
        return this.cicloEscolarRepository.findById(id)
                .map(cicloEscolarMapper::toDTO)
                .orElseThrow(() -> new CicloEscolarNoEncontradoException("No se encontro el ciclo escolar."));
    }

    @Override
    public CicloEscolarDTO crearCicloEscolar(CicloEscolarDTO nuevoCicloDTO) throws CrearCicloEscolarException {
        if (this.cicloEscolarRepository.existsByFechaInicioAndFechaFin(nuevoCicloDTO.getFechaInicio(), nuevoCicloDTO.getFechaFin())) {
            throw new CrearCicloEscolarException("La fecha de finalización del ciclo escolar no puede ser anterior a la de inicio.");
        }

        boolean existeCicloEscolarActivo = this.cicloEscolarRepository.obtenerCicloEscolarActivo(LocalDate.now()) != null;

        if (existeCicloEscolarActivo) {
            throw new CrearCicloEscolarException("Ya existe un ciclo escolar activo.");
        }

        CicloEscolar cicloCreado = this.cicloEscolarRepository.save(this.cicloEscolarMapper.toEntity(nuevoCicloDTO));

        return this.cicloEscolarMapper.toDTO(cicloCreado);
    }
}
