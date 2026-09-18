package mx.edu.cbta.sistemaescolar.gruposparaescolares.service.impl;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.ClaseGrupoParaescolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.EliminarClasesParaescolaresException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.RegistrarClasesParaescolarException;
import mx.edu.cbta.sistemaescolar.horario.domain.exception.DocenteNoDisponibleException;
import mx.edu.cbta.sistemaescolar.horario.service.CoordinadorClasesService;
import mx.edu.cbta.sistemaescolar.horario.service.ClaseParaescolarService;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseParaescolarDTO;

import mx.edu.cbta.sistemaescolar.gruposparaescolares.repository.AlumnoInscritoParaescolarRepository;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.mapper.AlumnoInscritoParaescolarMapper;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model.AlumnoInscritoParaescolar;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.repository.GrupoParaescolarRepository;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.AlumnoInscritoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.InscripcionMasivaAlumnosDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.service.GrupoParaescolarService;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.mapper.GrupoParaescolarMapper;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.model.GrupoParaescolar;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.BajaMasivaAlumnosDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.dto.GrupoParaescolarDTO;
import mx.edu.cbta.sistemaescolar.gruposparaescolares.domain.exception.*;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.service.ActividadParaescolarService;
import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.service.CicloEscolarService;
import mx.edu.cbta.sistemaescolar.estructura.service.AulaService;
import mx.edu.cbta.sistemaescolar.estructura.dto.CicloEscolarDTO;

import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.AlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.alumnado.service.AlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.dto.AlumnoDTO;

import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Slf4j
@Service
public class GrupoParaescolarServiceImpl implements GrupoParaescolarService {

    private CoordinadorClasesService coordinadorClasesService;
    private ActividadParaescolarService actividadParaescolarService;
    private ClaseParaescolarService claseParaescolarService;
    private CicloEscolarService cicloEscolarService;
    private AlumnoService alumnoService;
    private AulaService aulaService;

    private AlumnoInscritoParaescolarRepository alumnoInscritoParaescolarRepository;
    private GrupoParaescolarRepository grupoParaescolarRepository;

    @Autowired
    private GrupoParaescolarMapper grupoParaescolarMapper;

    @Autowired
    private AlumnoInscritoParaescolarMapper alumnoInscritoParaescolarMapper;

    public GrupoParaescolarServiceImpl(
            GrupoParaescolarRepository grupoParaescolarRepository,
            ActividadParaescolarService actividadParaescolarService,
            CicloEscolarService cicloEscolarService,
            AlumnoService alumnoService,
            AlumnoInscritoParaescolarRepository alumnoInscritoParaescolarRepository,
            CoordinadorClasesService coordinadorClasesService,
            AulaService aulaService,
            ClaseParaescolarService claseParaescolarService

    ) {
        this.grupoParaescolarRepository = grupoParaescolarRepository;
        this.cicloEscolarService = cicloEscolarService;
        this.actividadParaescolarService = actividadParaescolarService;
        this.alumnoService = alumnoService;
        this.alumnoInscritoParaescolarRepository = alumnoInscritoParaescolarRepository;
        this.coordinadorClasesService = coordinadorClasesService;
        this.aulaService = aulaService;
        this.claseParaescolarService = claseParaescolarService;
    }

    @Override
    public Page<GrupoParaescolarDTO> obtenerGruposParaescolares(Pageable pageable) {
        return this.grupoParaescolarRepository.findAll(pageable).map(grupoParaescolarMapper::toDTO);
    }

    @Override
    public List<GrupoParaescolarDTO> obtenerGruposParaescolaresActivos() throws GrupoParaescolarException {
        Long idCicloActivo = null;
        try {
            idCicloActivo = this.cicloEscolarService.obtenerCicloEscolarActivo().getId();
            List<GrupoParaescolar> gruposActivos = this.grupoParaescolarRepository.findGrupoParaescolarByCicloEscolarId(idCicloActivo);

            List<GrupoParaescolarDTO> gruposActivosDTO = gruposActivos.stream().map(grupoParaescolarMapper::toDTO).toList();

            for (GrupoParaescolarDTO grupo: gruposActivosDTO) {
                List<ClaseParaescolarDTO> clasesDTO = this.claseParaescolarService.obtenerClasesPorGrupo(grupo.getId());
                int cuposDisponibles = this.grupoParaescolarRepository.consultarEspaciosDisponibles(grupo.getId());
                grupo.setCuposDisponibles(cuposDisponibles);
                grupo.setClases(clasesDTO);
                clasesDTO.forEach(c -> ___eliminarRedundanciaDeClase(c));
            }

            return gruposActivosDTO;
        } catch (CicloEscolarNoEncontradoException | ClaseGrupoParaescolarNoEncontradoException e) {
            throw new GrupoParaescolarException(e.getMessage());
        }
    }

    /**
     * Oculta estos campos en la respuesta al cliente para evitar redundancia de datos. Ya que
     * el grupo ya cuenta con estos datos.
     * @param clase Clase a limpiar.
     */
    private void ___eliminarRedundanciaDeClase(ClaseParaescolarDTO clase) {
        clase.setDocenteId(null);
        clase.setCicloEscolarId(null);
        clase.setActividadParaescolarId(null);
        clase.setGrupoParaescolarId(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GrupoParaescolarDTO crearGrupo(GrupoParaescolarDTO grupoParaescolar) throws CrearGrupoParaescolarException {

        try {
            CicloEscolarDTO cicloActivo = this.cicloEscolarService.obtenerCicloEscolarActivo();
            this.actividadParaescolarService.obtenerParaescolarPorId(grupoParaescolar.getActividadParaescolarId());

            this.coordinadorClasesService.verificarDisponibilidadDocente(
                    grupoParaescolar.getDocenteId(),
                    grupoParaescolar.getClases().stream()
                            .map(ClaseParaescolarDTO::getHorario)
                            .collect(Collectors.toList())
            );

            GrupoParaescolar grupoRegistrar = this.grupoParaescolarMapper.toEntity(grupoParaescolar);
            grupoRegistrar.setCicloEscolarId(cicloActivo.getId());

            GrupoParaescolar registrado = this.grupoParaescolarRepository.save(grupoRegistrar);

            List<ClaseParaescolarDTO> clasesParaescolares = grupoParaescolar.getClases();
            for (ClaseParaescolarDTO clase : clasesParaescolares) {
                clase.setGrupoParaescolarId(registrado.getId());
                clase.setActividadParaescolarId(registrado.getActividadParaescolarId());
                clase.setCicloEscolarId(cicloActivo.getId());
                clase.setDocenteId(registrado.getDocenteId());
            }

            this.claseParaescolarService.registrarClases(registrado.getId(), clasesParaescolares);

            GrupoParaescolarDTO respuesta = this.grupoParaescolarMapper.toDTO(registrado);
            respuesta.setClases(clasesParaescolares);

            return respuesta;

        } catch (CicloEscolarNoEncontradoException | ParaescolarNoEncontradaException
                 | RegistrarClasesParaescolarException | DocenteNoDisponibleException
                 | DocenteNoEncontradoException e) {
            log.error(e.getMessage(), e);
            throw new CrearGrupoParaescolarException(e.getMessage());
        }
    }

    @Override
    public boolean tieneAlumnosInscritosEnParaescolar(Long idParaescolar) throws ParaescolarNoEncontradaException {
        ActividadParaescolarDTO encontrado = this.actividadParaescolarService.obtenerParaescolarPorId(idParaescolar);
        long totalInscritos = grupoParaescolarRepository.countAlumnosInscritosPorActividadParaescolar(encontrado.getId());
        return totalInscritos > 0;
    }

    @Override
    public List<GrupoParaescolarDTO> obtenerGruposParaescolaresPorDocenteYCicloEscolar(Long idDocente, Long idCicloEscolar) {
        return this.grupoParaescolarRepository.findGrupoParaescolarByDocenteIdAndCicloEscolarId(idDocente, idCicloEscolar)
                .stream()
                .map(grupoParaescolarMapper::toDTO)
                .toList();
    }

    @Override
    public List<GrupoParaescolarDTO> obtenerGruposParaescolaresActualesPorDocente(Long idDocente) {
        CicloEscolarDTO cicloActivo = null;
        try {
            cicloActivo = this.cicloEscolarService.obtenerCicloEscolarActivo();
        } catch (CicloEscolarNoEncontradoException e) {
            return List.of();
        }
        return this.obtenerGruposParaescolaresPorDocenteYCicloEscolar(idDocente, cicloActivo.getId());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public AlumnoInscritoParaescolarDTO inscribirAlumnoAGrupoParaescolar(String matricula, Long idGrupo)
            throws GrupoParaescolarNoEncontradoException, InscribirAlumnoParaescolarException {

        GrupoParaescolar grupo = grupoParaescolarRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoParaescolarNoEncontradoException("No se encontró el grupo con ID: " + idGrupo));

        AlumnoDTO alumno = null;
        try {
            alumno = this.alumnoService.obtenerAlumnoPorMatricula(matricula);
        } catch (AlumnoNoEncontradoException e) {
            throw new InscribirAlumnoParaescolarException("El alumno con matrícula " + matricula + " no existe.");
        }

        Optional<AlumnoInscritoParaescolar> inscripcionActiva =
                this.alumnoInscritoParaescolarRepository.findByAlumnoIdAndGrupoParaescolarIdAndFechaBajaIsNull(alumno.getId(), idGrupo);

        if (inscripcionActiva.isPresent()) {
            throw new InscribirAlumnoParaescolarException("El alumno ya tiene una inscripción ACTIVA en este grupo.");
        }

        long inscritosActivos = this.alumnoInscritoParaescolarRepository.findAllByGrupoParaescolarId(idGrupo)
                .stream()
                .filter(i -> i.getFechaBaja() == null)
                .count();

        if (grupo.getMaximoEspacios() != null && inscritosActivos >= grupo.getMaximoEspacios()) {
            throw new InscribirAlumnoParaescolarException("El grupo ya no cuenta con espacios disponibles.");
        }

        AlumnoInscritoParaescolar nuevaInscripcion = new AlumnoInscritoParaescolar();
        nuevaInscripcion.setAlumnoId(alumno.getId());
        nuevaInscripcion.setGrupoParaescolarId(idGrupo);
        nuevaInscripcion.setFechaInscripcion(LocalDate.now());
        nuevaInscripcion.setFechaBaja(null);

        AlumnoInscritoParaescolar guardado = this.alumnoInscritoParaescolarRepository.save(nuevaInscripcion);

        log.info("Alumno {} inscrito exitosamente en el grupo {}", matricula, idGrupo);

        return this.alumnoInscritoParaescolarMapper.toDTO(guardado);
    }


    @Override
    public GrupoParaescolarDTO obtenerGrupoPorId(Long id) throws GrupoParaescolarNoEncontradoException {
        return this.grupoParaescolarRepository.findById(id)
                .map(grupoParaescolarMapper::toDTO)
                .orElseThrow(() -> new GrupoParaescolarNoEncontradoException(
                        String.format("No se encontró el grupo paraescolar con el ID: %d", id)
                ));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void eliminarGrupoParaescolar(Long grupoId) throws GrupoParaescolarNoEncontradoException {

        if (!this.grupoParaescolarRepository.existsById(grupoId)) {
            throw new GrupoParaescolarNoEncontradoException("No se encontró el grupo con ID: %s.".formatted(grupoId));
        }

        try {
            this.claseParaescolarService.eliminarClasesParaescolaresPorGrupo(grupoId);
        } catch (EliminarClasesParaescolaresException e) {
            throw new GrupoParaescolarNoEncontradoException(e.getMessage());
        }

        this.grupoParaescolarRepository.deleteById(grupoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void darDeBajaAlumnoDeGrupoParaescolar(String matricula, Long idGrupo)
            throws BajaAlumnoParaescolarException, GrupoParaescolarNoEncontradoException {

        GrupoParaescolar grupo = grupoParaescolarRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoParaescolarNoEncontradoException("No se encontró el grupo con ID: " + idGrupo));

        AlumnoDTO alumno = null;
        try {
            alumno = this.alumnoService.obtenerAlumnoPorMatricula(matricula);
        } catch (AlumnoNoEncontradoException e) {
            throw new BajaAlumnoParaescolarException("El alumno con matrícula " + matricula + " no existe.");
        }

        AlumnoInscritoParaescolar inscripcion = alumnoInscritoParaescolarRepository
                .findByAlumnoIdAndGrupoParaescolarIdAndFechaBajaIsNull(alumno.getId(), idGrupo)
                .orElseThrow(() -> new BajaAlumnoParaescolarException(
                        "No existe una inscripción activa para este alumno en el grupo especificado."));

        inscripcion.setFechaBaja(LocalDate.now());

        this.alumnoInscritoParaescolarRepository.save(inscripcion);
        log.info("Baja procesada: Alumno {} del grupo {}", matricula, idGrupo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inscribirMasivamenteAlumnosAGrupoParaescolar(InscripcionMasivaAlumnosDTO dto)
            throws GrupoParaescolarNoEncontradoException, InscribirAlumnoParaescolarException {

        log.info("Iniciando inscripción masiva para el grupo ID: {}", dto.getGrupoId());

        if (!grupoParaescolarRepository.existsById(dto.getGrupoId())) {
            throw new GrupoParaescolarNoEncontradoException("No se encontró el grupo con ID: " + dto.getGrupoId());
        }

        for (String matricula : dto.getMatriculas()) {
            try {
                this.inscribirAlumnoAGrupoParaescolar(matricula, dto.getGrupoId());
            } catch (InscribirAlumnoParaescolarException e) {
                log.error("Fallo en inscripción masiva: Matrícula {} no pudo ser inscrita. Motivo: {}", matricula, e.getMessage());
                throw new InscribirAlumnoParaescolarException("No se pudo inscribir al alumno con matrícula " + matricula + ": " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void darBajaMasivamenteAlumnosDeGrupoParaescolar(BajaMasivaAlumnosDTO dto)
            throws BajaAlumnoParaescolarException, GrupoParaescolarNoEncontradoException {

        log.info("Iniciando baja masiva para el grupo ID: {}", dto.getGrupoId());

        if (!grupoParaescolarRepository.existsById(dto.getGrupoId())) {
            throw new GrupoParaescolarNoEncontradoException("No se encontró el grupo con ID: " + dto.getGrupoId());
        }

        for (String matricula : dto.getMatriculas()) {
            try {
                this.darDeBajaAlumnoDeGrupoParaescolar(matricula, dto.getGrupoId());
            } catch (BajaAlumnoParaescolarException e) {
                log.error("Fallo en baja masiva: Matrícula {} no pudo ser procesada. Motivo: {}", matricula, e.getMessage());
                throw new BajaAlumnoParaescolarException("No se pudo dar de baja al alumno con matrícula  " + matricula + ": " + e.getMessage());
            }
        }
    }
}