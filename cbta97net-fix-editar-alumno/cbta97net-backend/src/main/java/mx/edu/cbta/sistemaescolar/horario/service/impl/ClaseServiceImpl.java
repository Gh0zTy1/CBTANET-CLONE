package mx.edu.cbta.sistemaescolar.horario.service.impl;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.*;
import mx.edu.cbta.sistemaescolar.horario.service.CoordinadorClasesService;
import mx.edu.cbta.sistemaescolar.horario.repository.ClaseRepository;
import mx.edu.cbta.sistemaescolar.horario.service.ClaseService;
import mx.edu.cbta.sistemaescolar.horario.mapper.ClaseMapper;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.Clase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaseServiceImpl implements ClaseService {

    private ClaseRepository claseRepository;

    private CoordinadorClasesService coordinadorClasesService;

    @Autowired
    private ClaseMapper claseMapper;

    /**
     * Constructor para facilitacion de pruebas.
     * @param claseRepository Repositorio de acceso a datos de Clases.
     * @param coordinadorClasesService Servicio para verificacion de disponibilidad.
     */
    public ClaseServiceImpl(
            ClaseRepository claseRepository,
            CoordinadorClasesService coordinadorClasesService
    ) {
        this.claseRepository = claseRepository;
        this.coordinadorClasesService = coordinadorClasesService;
    }

    @Override
    public List<ClaseDTO> obtenerClasesPorGrupo(Long idGrupo) throws ClaseGrupoNoEncontradoException {
        List<Clase> clases = this.claseRepository.findByGrupoId(idGrupo);

        if (clases.isEmpty()) {
            if (!this.claseRepository.existsByGrupoId(idGrupo)) {
                throw new ClaseGrupoNoEncontradoException("No se encontraron clases para el grupo: " + idGrupo);
            }
        }

        return clases.stream()
                .map(this.claseMapper::toDTO)
                .toList();
    }

    @Override
    public List<ClaseDTO> registrarClases(Long grupoId, List<ClaseDTO> listaClasesDTO) throws RegistrarClasesException {

        if (grupoId == null) {
            throw new RegistrarClasesException("El ID del grupo no puede ser nulo.");
        }

        try {
            List<Clase> clasesNuevas = listaClasesDTO.stream()
                    .map(dto -> {
                        Clase entidad = claseMapper.toEntity(dto);
                        entidad.setGrupoId(grupoId);
                        return entidad;
                    })
                    .toList();

            this.coordinadorClasesService.validarCreacionClasesSemestrales(listaClasesDTO);

            List<Clase> registradas = this.claseRepository.saveAll(clasesNuevas);

            return registradas.stream()
                    .map(claseMapper::toDTO)
                    .toList();

        } catch (Exception e) {
            throw new RegistrarClasesException(e.getMessage());
        }
    }

    @Override
    public List<ClaseDTO> obtenerClasesPorAula(Long aulaId) throws ClaseNoEncontradaException {
        List<Clase> encontradas = this.claseRepository.findByAulaId(aulaId);

        if (encontradas.isEmpty()) {
            throw new ClaseNoEncontradaException("No se encontraron clases impartidas en el aula dada.");
        }

        return encontradas.stream().map(c -> claseMapper.toDTO(c)).toList();
    }

    @Override
    public List<ClaseDTO> obtenerClasesVigentesDocente(Long docenteId) {
        return this.claseRepository.obtenerClasesVigentesDocente(docenteId, LocalDate.now()).stream()
                .map(claseMapper::toDTO)
                .toList();
    }

    @Override
    public void eliminarClasesPorGrupo(Long grupoId) throws EliminarClasesGrupoException, GrupoNoEncontradoException {
        try {

            this.claseRepository.deleteByGrupoId(grupoId);
        } catch (Exception e) {
            throw new EliminarClasesGrupoException("No se pudo eliminar las clases del grupo con ID: %s".formatted(grupoId));
        }
    }
}
