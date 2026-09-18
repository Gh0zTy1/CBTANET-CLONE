package mx.edu.cbta.sistemaescolar.estructura.service.impl;

import mx.edu.cbta.sistemaescolar.estructura.dto.AreaPropedeuticaDTO;
import mx.edu.cbta.sistemaescolar.estructura.mapper.AreaPropedeuticaMapper;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.AreaPropedeutica;
import mx.edu.cbta.sistemaescolar.estructura.repository.AreaPropedeuticaRepository;
import mx.edu.cbta.sistemaescolar.estructura.service.AreaPropedeuticaService;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AreaPropedeuticaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AreaPropedeuticaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarAreaPropedeuticaException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaPropedeuticaServiceImpl implements AreaPropedeuticaService {

	private final AreaPropedeuticaRepository areaPropedeuticaRepository;

    @Autowired
    private AreaPropedeuticaMapper areaPropedeuticaMapper;

	public AreaPropedeuticaServiceImpl(AreaPropedeuticaRepository areaPropedeuticaRepository) {
		this.areaPropedeuticaRepository = areaPropedeuticaRepository;
	}

	@Override
	public AreaPropedeuticaDTO registrarAreaPropedeutica(AreaPropedeuticaDTO areaPropedeutica)
            throws AreaPropedeuticaDuplicadaException {

        if (this.areaPropedeuticaRepository.existsByNombre(areaPropedeutica.getNombre())) {
            throw new AreaPropedeuticaDuplicadaException(
                    "Ya existe un área propedéutica con el nombre '%s'.".formatted(areaPropedeutica.getNombre())
            );
        }

        AreaPropedeutica areaNueva = this.areaPropedeuticaMapper.toEntity(areaPropedeutica);

		AreaPropedeutica creada = areaPropedeuticaRepository.save(areaNueva);

		return this.areaPropedeuticaMapper.toDto(creada);
	}

	@Override
	public AreaPropedeuticaDTO obtenerAreaPropedeuticaPorId(Long id) throws AreaPropedeuticaNoEncontradaException {
		return this.areaPropedeuticaRepository.findById(id)
                .map(this.areaPropedeuticaMapper::toDto)
                .orElseThrow(() -> new AreaPropedeuticaNoEncontradaException(
                        "No se encontró el área propedéutica con ID: %s.".formatted(id))
                );
	}

	@Override
	public List<AreaPropedeuticaDTO> obtenerAreasPropedeuticas() {
		return this.areaPropedeuticaRepository.findAll()
                .stream()
                .map(this.areaPropedeuticaMapper::toDto)
                .toList();
	}

    @Override
    public void eliminarAreaPorId(Long areaId)
            throws AreaPropedeuticaNoEncontradaException, EliminarAreaPropedeuticaException {

        if (!this.areaPropedeuticaRepository.existsById(areaId)) {
            throw new AreaPropedeuticaNoEncontradaException(
                    "No se encontró el área propedéutica con ID: %s.".formatted(areaId)
            );
        }

        try {
            this.areaPropedeuticaRepository.deleteById(areaId);
        } catch (Exception e) {
            throw new EliminarAreaPropedeuticaException(
                    "No se pudo eliminar el área propedéutica. Intente de nuevo más tarde."
            );
        }
    }
}
