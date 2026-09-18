package mx.edu.cbta.sistemaescolar.horario.service.impl;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.ClaseGrupoParaescolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.DocenteNoDisponibleException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.EliminarClasesParaescolaresException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.RegistrarClasesParaescolarException;
import mx.edu.cbta.sistemaescolar.horario.repository.ClaseParaescolarRepository;
import mx.edu.cbta.sistemaescolar.horario.service.ClaseParaescolarService;
import mx.edu.cbta.sistemaescolar.horario.mapper.ClaseParaescolarMapper;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseParaescolarDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.ClaseParaescolar;
import mx.edu.cbta.sistemaescolar.horario.mapper.HorarioMapper;
import mx.edu.cbta.sistemaescolar.horario.dto.HorarioDTO;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;
import mx.edu.cbta.sistemaescolar.horario.service.CoordinadorClasesService;

import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ClaseParaescolarServiceImpl implements ClaseParaescolarService {

    private CoordinadorClasesService coordinadorClasesService;

    private ClaseParaescolarRepository claseParaescolarRepository;

    @Autowired
    private ClaseParaescolarMapper claseParaescolarMapper;

    @Autowired
    private HorarioMapper horarioMapper;

    /**
     * Constructor para facilitacion de pruebas.
     * @param claseRepository Repositorio de acceso a datos de Clases.
     */
    public ClaseParaescolarServiceImpl(ClaseParaescolarRepository claseRepository, CoordinadorClasesService coordinadorClasesService) {
        this.claseParaescolarRepository = claseRepository;
        this.coordinadorClasesService = coordinadorClasesService;
    }

    @Override
    public List<ClaseParaescolarDTO> obtenerClasesPorGrupo(Long idGrupo) throws ClaseGrupoParaescolarNoEncontradoException {
        List<ClaseParaescolar> clases = this.claseParaescolarRepository.findByGrupoParaescolarId(idGrupo);

        if (clases.isEmpty()) {
            if (!this.claseParaescolarRepository.existsByGrupoParaescolarId(idGrupo)) {
                throw new ClaseGrupoParaescolarNoEncontradoException("No se encontraron clases para el grupo: " + idGrupo);
            }
        }

        return clases.stream()
                .map(this.claseParaescolarMapper::toDTO)
                .toList();
    }

    @Override
    public List<ClaseParaescolarDTO> registrarClases(Long grupoId, List<ClaseParaescolarDTO> listaClasesDTO)
            throws RegistrarClasesParaescolarException
    {
        if (grupoId == null) {
            throw new RegistrarClasesParaescolarException("El ID del grupo no puede ser nulo.");
        }

        List<ClaseParaescolar> clasesNuevas = listaClasesDTO.stream()
                .map(dto -> {
                    ClaseParaescolar entidad = claseParaescolarMapper.toEntity(dto);
                    entidad.setGrupoParaescolarId(grupoId);
                    return entidad;
                })
                .toList();

        if (clasesNuevas.isEmpty()) {
            throw new RegistrarClasesParaescolarException("No se adjuntó ninguna clase.");
        }

        List<HorarioDTO> horarios = clasesNuevas.stream()
                .map(ClaseParaescolar::getHorario)
                .map(this.horarioMapper::toDTO)
                .toList();

        try {
            this.coordinadorClasesService
                    .verificarDisponibilidadDocente(clasesNuevas.getFirst().getDocenteId(), horarios);
        }
        catch (DocenteNoDisponibleException | DocenteNoEncontradoException | CicloEscolarNoEncontradoException | ParaescolarNoEncontradaException e) {
            log.error(e.getMessage());
            throw new RegistrarClasesParaescolarException(e.getMessage());
        }

        List<ClaseParaescolar> registradas = this.claseParaescolarRepository.saveAll(clasesNuevas);

        return registradas.stream()
                .map(claseParaescolarMapper::toDTO)
                .toList();
    }

    @Override
    public List<ClaseParaescolarDTO> obtenerClasesVigentesDocente(Long docenteId) {
        return this.claseParaescolarRepository.obtenerClasesVigentesDocente(docenteId, LocalDate.now()).stream()
                .map(claseParaescolarMapper::toDTO)
                .toList();
    }

    @Override
    public void eliminarClasesParaescolaresPorGrupo(Long grupoId) throws EliminarClasesParaescolaresException {
        try {
            this.claseParaescolarRepository.deleteByGrupoParaescolarId(grupoId);
        } catch (Exception e) {
            throw new EliminarClasesParaescolaresException("No se pudo eliminar las clases del grupo con ID: %s".formatted(grupoId));
        }
    }
}
