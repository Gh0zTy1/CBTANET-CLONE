package mx.edu.cbta.sistemaescolar.estructura.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;
import mx.edu.cbta.sistemaescolar.estructura.mapper.ActividadParaescolarMapper;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ModificarParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CrearParaescolarException;
import mx.edu.cbta.sistemaescolar.estructura.repository.ActividadParaescolarRepository;
import mx.edu.cbta.sistemaescolar.estructura.service.ActividadParaescolarService;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.ActividadParaescolar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Slf4j
@Service
public class ActividadParaescolarServiceImpl implements ActividadParaescolarService {

    private final ActividadParaescolarRepository actividadParaescolarRepository;

    @Autowired
    private ActividadParaescolarMapper paraescolarMapper;

    public ActividadParaescolarServiceImpl(ActividadParaescolarRepository repository) {
        this.actividadParaescolarRepository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadParaescolarDTO> obtenerParaescolares() {
        return actividadParaescolarRepository.findAll().stream()
                .map(act -> this.paraescolarMapper.toDTO(act))
                .toList();
    }

    @Override
    @Transactional
    public ActividadParaescolarDTO crearActividadParaescolar(ActividadParaescolarDTO paraescolarDTO) throws CrearParaescolarException {
        Optional<ActividadParaescolar> existente = actividadParaescolarRepository.findByNombre(paraescolarDTO.getNombre());

        if (existente.isPresent()) {
            throw new CrearParaescolarException("Ya existe otra actividad con ese nombre.");
        }

        ActividadParaescolar paraescolar = this.paraescolarMapper.toEntity(paraescolarDTO);

        ActividadParaescolar registrado = actividadParaescolarRepository.save(paraescolar);

        return this.paraescolarMapper.toDTO(registrado);
    }

    @Override
    @Transactional
    public ActividadParaescolarDTO modificarParaescolar(Long id, ActividadParaescolarDTO paraescolarDatosDTO)
            throws ParaescolarNoEncontradaException, ModificarParaescolarException
    {

        ActividadParaescolar actividadEncontrada = this.actividadParaescolarRepository.findById(id).get();

        if (this.actividadParaescolarRepository.existsByNombreAndIdNot(paraescolarDatosDTO.getNombre(), id)) {
            throw new ModificarParaescolarException("Ya existe otra actividad con ese nombre.");
        }

        actividadEncontrada.setNombre(paraescolarDatosDTO.getNombre());
        actividadEncontrada.setDescripcion(paraescolarDatosDTO.getDescripcion());

        ActividadParaescolar actualizado = this.actividadParaescolarRepository.save(actividadEncontrada);

        return this.paraescolarMapper.toDTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminarParaescolar(Long id) throws ParaescolarNoEncontradaException, EliminarParaescolarException {

        ActividadParaescolar paraescolar = this.actividadParaescolarRepository
                .findById(id)
                .orElseThrow(() -> new ParaescolarNoEncontradaException("No se encontró la actividad con el ID dado."));

        try {

            if (paraescolar.isActivo()) {
                paraescolar.setActivo(false);
                this.actividadParaescolarRepository.save(paraescolar);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EliminarParaescolarException(
                    "No se pudo eliminar la actividad paraescolar. Porfavor intente más tarde."
            );
        }
    }

    @Override
    public ActividadParaescolarDTO obtenerParaescolarPorId(Long id) throws ParaescolarNoEncontradaException {
        Optional<ActividadParaescolar> encontrado = this.actividadParaescolarRepository.findById(id);

        if (encontrado.isEmpty()) {
            throw new ParaescolarNoEncontradaException(String.format("No se encontró la actividad paraescolar con el ID especificado (ID: %d).", id));
        }

        return this.paraescolarMapper.toDTO(encontrado.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActividadParaescolarDTO> obtenerActividadesParaescolaresPorNombre(String nombre, Pageable pageable) {
        Page<ActividadParaescolar> actividadesPage = this.actividadParaescolarRepository
                .findByNombreContainingIgnoreCase(nombre, pageable);
        return actividadesPage.map(actividad -> this.paraescolarMapper.toDTO(actividad));
    }
}