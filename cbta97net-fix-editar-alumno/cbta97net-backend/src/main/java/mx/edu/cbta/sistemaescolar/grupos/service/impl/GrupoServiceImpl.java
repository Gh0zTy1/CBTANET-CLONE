package mx.edu.cbta.sistemaescolar.grupos.service.impl;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.HorasPorSemanaException;
import mx.edu.cbta.sistemaescolar.estructura.service.*;
import mx.edu.cbta.sistemaescolar.estructura.dto.*;

import mx.edu.cbta.sistemaescolar.grupos.domain.model.Grupo;
import mx.edu.cbta.sistemaescolar.grupos.domain.exception.*;

import mx.edu.cbta.sistemaescolar.horario.service.ClaseService;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;

import mx.edu.cbta.sistemaescolar.grupos.repository.GrupoRepository;
import mx.edu.cbta.sistemaescolar.grupos.service.GrupoService;
import mx.edu.cbta.sistemaescolar.grupos.mapper.GrupoMapper;
import mx.edu.cbta.sistemaescolar.grupos.dto.GrupoDTO;

import mx.edu.cbta.sistemaescolar.alumnado.service.AlumnoService;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.EliminarClasesGrupoException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class GrupoServiceImpl implements GrupoService {

    private GrupoRepository grupoRepository;

    private AreaPropedeuticaService areaPropedeuticaService;
    private CarreraTecnicaService carreraTecnicaService;
    private CicloEscolarService cicloEscolarService;
    private MateriaService materiaService;
    private AlumnoService alumnoService;
    private ClaseService claseService;
    private AulaService aulaService;

    @Autowired
    private GrupoMapper grupoMapper;

    public GrupoServiceImpl(
            GrupoRepository grupoRepository,
            CicloEscolarService cicloEscolarService,
            AlumnoService alumnoService,
            AulaService aulaService,
            ClaseService claseService,
            MateriaService materiaService,
            AreaPropedeuticaService areaPropedeuticaService,
            CarreraTecnicaService carreraTecnicaService
    ) {
        this.grupoRepository = grupoRepository;
        this.cicloEscolarService = cicloEscolarService;
        this.alumnoService = alumnoService;
        this.aulaService = aulaService;
        this.claseService = claseService;
        this.materiaService = materiaService;
        this.carreraTecnicaService = carreraTecnicaService;
        this.areaPropedeuticaService = areaPropedeuticaService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GrupoDTO registrarGrupo(GrupoDTO grupoDTO)
            throws GrupoException, GrupoYaExistenteException, RegistrarGrupoException
    {

        try {
            CicloEscolarDTO cicloActivo = this.cicloEscolarService.obtenerCicloEscolarActivo();

            // 1. Validaciones previas (Fail-fast)
            if (grupoDTO.getClases() == null || grupoDTO.getClases().isEmpty()) {
                throw new RegistrarGrupoException("No se adjuntaron clases para este grupo.");
            }

            if (grupoRepository.existsBySemestreAndLetraAndCicloEscolarId(
                    grupoDTO.getSemestre(), grupoDTO.getLetra(), cicloActivo.getId())) {
                throw new GrupoException("Ya existe un grupo de este semestre con la misma letra.");
            }

            // 2. Carga de dependencias opcionales
            AreaPropedeuticaDTO areaDTO = (grupoDTO.getAreaPropedeuticaId() != null && grupoDTO.getSemestre() > 5)
                    ? this.areaPropedeuticaService.obtenerAreaPropedeuticaPorId(grupoDTO.getAreaPropedeuticaId()) : null;

            CarreraTecnicaDTO carreraDTO = (grupoDTO.getCarreraTecnicaId() != null && grupoDTO.getSemestre() >= 2)
                    ? this.carreraTecnicaService.obtenerCarreraTecnicaPorId(grupoDTO.getCarreraTecnicaId()) : null;

            // 3. Preparar Entidad y Nota
            Grupo grupoRegistrar = this.grupoMapper.toEntity(grupoDTO);
            grupoRegistrar.setCicloEscolarId(cicloActivo.getId());

            if (grupoRegistrar.getNota() == null || grupoRegistrar.getNota().isEmpty()) {
                String nombreSemestre = Grupo.obtenerNombreSemestre(grupoDTO.getSemestre());

                StringBuilder sb = new StringBuilder(String.format("Grupo %d%c %s (%s)",
                        grupoDTO.getSemestre(), grupoDTO.getLetra(), grupoDTO.getTurno(), nombreSemestre));

                if (carreraDTO != null) sb.append(", de la carrera ").append(carreraDTO.getNombre());
                if (areaDTO != null) sb.append(", con especialidad de ").append(areaDTO.getNombre());
                grupoRegistrar.setNota(sb.toString());
            }

            // 4. Guardar grupo para obtener ID
            Grupo registrado = this.grupoRepository.save(grupoRegistrar);

            // 5. Preparar clases y VALIDAR HORAS
            for (ClaseDTO clase : grupoDTO.getClases()) {
                clase.setCicloEscolarId(cicloActivo.getId());
                clase.setGrupoId(registrado.getId());
                // Cargar materia para la validación de horas
                clase.setMateriaDTO(this.materiaService.obtenerMateriaPorId(clase.getMateriaId()));
            }

            this.validarHorasTotalesPorMateria(grupoDTO.getClases());

            // 6. Registrar en servicio de clases
            this.claseService.registrarClases(registrado.getId(), grupoDTO.getClases());

            // 7. Retornar DTO limpio
            GrupoDTO respuesta = this.grupoMapper.toDTO(registrado);
            if (carreraDTO != null) {
                carreraDTO.setDescripcion(null);
                respuesta.setCarreraTecnicaDTO(carreraDTO);
            }

            if (areaDTO != null) {
                areaDTO.setDescripcion(null);
                respuesta.setAreaPropedeuticaDTO(areaDTO);
            }

            return respuesta;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // Captura cualquier excepción (incluyendo las de horas o validación) y relanza
            throw new RegistrarGrupoException(e.getMessage());
        }
    }

    private void validarHorasTotalesPorMateria(List<ClaseDTO> clases) throws HorasPorSemanaException {
        Map<Long, List<ClaseDTO>> clasesPorMateria = clases.stream()
                .collect(Collectors.groupingBy(ClaseDTO::getMateriaId));

        for (Map.Entry<Long, List<ClaseDTO>> entry : clasesPorMateria.entrySet()) {
            List<ClaseDTO> clasesDeMateria = entry.getValue();

            MateriaDTO materia = clasesDeMateria.get(0).getMateriaDTO();

            if (materia == null) {
                materia = this.materiaService.obtenerMateriaPorId(entry.getKey());
            }

            // Calculamos el total de minutos sumando la duración de cada horario
            long totalMinutosAsignados = clasesDeMateria.stream()
                    .map(ClaseDTO::getHorario)
                    .mapToLong(h -> java.time.Duration.between(
                            h.getHoraInicio(),
                            h.getHoraFin()).toMinutes()
                    )
                    .sum();

            materia.cumpleConHorasPorSemana(totalMinutosAsignados);
        }
    }

    @Override
    public void actualizarGrupo(GrupoDTO grupoDTO) throws GrupoException, GrupoNoEncontradoException {
        // TODO
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void eliminarGrupo(Long idGrupo) throws EliminarGrupoSemestralException, GrupoNoEncontradoException {

        if (!this.grupoRepository.existsById(idGrupo)) {
            throw new GrupoNoEncontradoException("No se encontró el grupo con ID: %s.".formatted(idGrupo));
        }

        try {
            this.claseService.eliminarClasesPorGrupo(idGrupo);
        } catch (EliminarClasesGrupoException e) {
            throw new EliminarGrupoSemestralException(e.getMessage());
        } catch (mx.edu.cbta.sistemaescolar.horario.domain.exception.GrupoNoEncontradoException e) {
            // TODO: cambiar esta referencia
            throw new GrupoNoEncontradoException(e.getMessage());
        }

        try {
            this.grupoRepository.deleteById(idGrupo);
        } catch (Exception e) {
            throw new EliminarGrupoSemestralException(e.getMessage());
        }
    }

    @Override
    public Page<GrupoDTO> obtenerGrupos(Pageable pageable) {
        return this.grupoRepository.findAll(pageable).map(this.grupoMapper::toDTO);
    }

    @Override
    public GrupoDTO obtenerGrupoPorId(Long idGrupo) throws GrupoException, GrupoNoEncontradoException {
        return this.grupoRepository.findById(idGrupo)
                .map(this.grupoMapper::toDTO)
                .orElseThrow(() -> new GrupoNoEncontradoException(
                        "No se encontró el grupo con ID: '%s'."
                        .formatted(idGrupo)
                ));
    }

    @Override
    public List<GrupoDTO> obtenerGruposPorCicloEscolar(Long idCicloEscolar) {
        return this.grupoRepository.findByCicloEscolarId(idCicloEscolar)
                .stream()
                .map(this.grupoMapper::toDTO)
                .toList();
    }

    @Override
    public List<GrupoDTO> obtenerGruposActivos() throws GrupoException {
        try {
            CicloEscolarDTO cicloActivo = this.cicloEscolarService.obtenerCicloEscolarActivo();

            return this.grupoRepository.findByCicloEscolarId(cicloActivo.getId())
                    .stream()
                    .map(this.grupoMapper::toDTO)
                    .toList();

        } catch (CicloEscolarNoEncontradoException e) {
            throw new GrupoException(e.getMessage());
        }
    }
}