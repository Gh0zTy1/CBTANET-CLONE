package mx.edu.cbta.sistemaescolar.horario.service.impl;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.CicloEscolarNoEncontradoException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.service.CicloEscolarService;
import mx.edu.cbta.sistemaescolar.estructura.service.MateriaService;
import mx.edu.cbta.sistemaescolar.estructura.service.AulaService;
import mx.edu.cbta.sistemaescolar.estructura.dto.MateriaDTO;

import mx.edu.cbta.sistemaescolar.horario.domain.exception.DocenteNoDisponibleException;
import mx.edu.cbta.sistemaescolar.horario.repository.ClaseParaescolarRepository;
import mx.edu.cbta.sistemaescolar.horario.service.CoordinadorClasesService;
import mx.edu.cbta.sistemaescolar.horario.repository.ClaseRepository;
import mx.edu.cbta.sistemaescolar.horario.domain.model.ClaseParaescolar;
import mx.edu.cbta.sistemaescolar.horario.mapper.HorarioMapper;
import mx.edu.cbta.sistemaescolar.horario.dto.HorarioDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.Horario;
import mx.edu.cbta.sistemaescolar.horario.dto.ClaseDTO;
import mx.edu.cbta.sistemaescolar.horario.domain.model.Clase;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ParaescolarNoEncontradaException;
import mx.edu.cbta.sistemaescolar.estructura.service.ActividadParaescolarService;
import mx.edu.cbta.sistemaescolar.estructura.dto.ActividadParaescolarDTO;

import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;
import mx.edu.cbta.sistemaescolar.personal.service.DocenteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordinadorClasesServiceImpl implements CoordinadorClasesService {

    private ActividadParaescolarService actividadParaescolarService;
    private CicloEscolarService cicloEscolarService;
    private MateriaService materiaService;
    private DocenteService docenteService;
    private AulaService aulaService;

    private ClaseParaescolarRepository claseParaescolarRepository;
    private ClaseRepository claseRepository;

    @Autowired
    private HorarioMapper horarioMapper;

    public CoordinadorClasesServiceImpl(
            ClaseRepository claseRepository,
            ClaseParaescolarRepository claseParaescolarRepository,
            MateriaService materiaService,
            ActividadParaescolarService actividadParaescolarService,
            DocenteService docenteService,
            CicloEscolarService cicloEscolarService,
            AulaService aulaService
    ) {
        this.claseParaescolarRepository = claseParaescolarRepository;
        this.claseRepository = claseRepository;
        this.materiaService = materiaService;
        this.actividadParaescolarService = actividadParaescolarService;
        this.docenteService = docenteService;
        this.cicloEscolarService = cicloEscolarService;
        this.aulaService = aulaService;
    }

    @Override
    public void verificarDisponibilidadDocente(Long docenteId, List<HorarioDTO> nuevosHorarios)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException
    {

        // solo para verificar si existe un ciclo escolar activo...
        Long cicloEscolarId = this.cicloEscolarService.obtenerCicloEscolarActivo().getId();

        if (!this.docenteService.existeDocentePorId(docenteId)) {
            throw new DocenteNoEncontradoException("No se encontro al docente con ID: %s".formatted(docenteId));
        }

        for (int i = 0; i < nuevosHorarios.size(); i++) {
            for (int j = i + 1; j < nuevosHorarios.size(); j++) {
                HorarioDTO h1 = nuevosHorarios.get(i);
                HorarioDTO h2 = nuevosHorarios.get(j);

                if (h1.seEmpalmaCon(h2)) {
                    throw new DocenteNoDisponibleException(
                            "El horario %s de %s a %s se empalma con el horario %s de %s a %s."
                                    .formatted(
                                            h1.getDia(),
                                            h1.getHoraInicio(),
                                            h1.getHoraFin(),
                                            h2.getDia(),
                                            h2.getHoraInicio(),
                                            h2.getHoraFin()
                                    )
                    );
                }
            }
        }

        List<Clase> clasesNormales = this.claseRepository.findByDocenteIdAndCicloEscolarId(docenteId, cicloEscolarId);
        List<ClaseParaescolar> clasesPara = this.claseParaescolarRepository
                .findByDocenteIdAndCicloEscolarId(docenteId, cicloEscolarId);

        for (HorarioDTO nuevo : nuevosHorarios) {

            Horario nuevoParsed = this.horarioMapper.toEntity(nuevo);

            // Verificar contra clases normales
            for (Clase c : clasesNormales) {
                Horario hExistente = c.getHorario();
                if (hExistente != null && hExistente.seEmpalmaCon(nuevoParsed)) {
                    MateriaDTO materia = this.materiaService.obtenerMateriaPorId(c.getMateriaId());
                    throw new DocenteNoDisponibleException(
                            "El docente ya tiene la materia '%s' el día %s de %s a %s."
                                    .formatted(
                                            materia.getNombre().toUpperCase(),
                                            hExistente.getDia(),
                                            hExistente.getHoraInicio(),
                                            hExistente.getHoraFin()
                                    )
                    );
                }
            }

            for (ClaseParaescolar cp : clasesPara) {
                Horario hExistente = cp.getHorario();
                if (hExistente != null && hExistente.seEmpalmaCon(nuevoParsed)) {
                    ActividadParaescolarDTO actividad = this.actividadParaescolarService
                            .obtenerParaescolarPorId(cp.getActividadParaescolarId());

                    throw new DocenteNoDisponibleException(
                            "El docente ya tiene la actividad '%s' el día %s de %s a %s."
                                    .formatted(
                                            actividad.getNombre().toUpperCase(),
                                            hExistente.getDia(),
                                            hExistente.getHoraInicio(),
                                            hExistente.getHoraFin()
                                    )
                    );
                }
            }
        }
    }
    @Override
    public void verificarDisponibilidadAula(Long aulaId, List<HorarioDTO> nuevosHorarios)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            CicloEscolarNoEncontradoException
    {
        Long cicloEscolarId = this.cicloEscolarService.obtenerCicloEscolarActivo().getId();

        List<Clase> clasesNormales = this.claseRepository.findByAulaIdAndCicloEscolarId(aulaId, cicloEscolarId);
        List<ClaseParaescolar> clasesPara = this.claseParaescolarRepository.findByCicloEscolarId(cicloEscolarId);

        for (HorarioDTO nuevo : nuevosHorarios) {

            Horario nuevoParsed = this.horarioMapper.toEntity(nuevo);

            for (Clase c : clasesNormales) {
                Horario hExistente = c.getHorario();
                if (hExistente != null && hExistente.seEmpalmaCon(nuevoParsed)) {
                    MateriaDTO materia = this.materiaService.obtenerMateriaPorId(c.getMateriaId());
                    throw new DocenteNoDisponibleException(
                            "El aula ya está ocupada el día %s de %s a %s por la materia: %s"
                                    .formatted(
                                            hExistente.getDia(),
                                            hExistente.getHoraInicio(),
                                            hExistente.getHoraFin(),
                                            materia.getNombre()
                                    )
                    );
                }
            }

            for (ClaseParaescolar cp : clasesPara) {
                Horario hExistente = cp.getHorario();
                if (hExistente != null && hExistente.seEmpalmaCon(nuevoParsed)) {
                    ActividadParaescolarDTO actividad = this.actividadParaescolarService
                            .obtenerParaescolarPorId(cp.getActividadParaescolarId());

                    throw new DocenteNoDisponibleException(
                            "El aula ya está ocupada el día %s de %s a %s por la actividad: %s"
                                    .formatted(
                                            hExistente.getDia(),
                                            hExistente.getHoraInicio(),
                                            hExistente.getHoraFin(),
                                            actividad.getNombre()
                                    )
                    );
                }
            }
        }
    }

    @Override
    public void validarCreacionClaseSemestral(ClaseDTO claseDTO)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException
    {
        List<HorarioDTO> horarioUnico = List.of(claseDTO.getHorario());

        this.verificarDisponibilidadDocente(claseDTO.getDocenteId(), horarioUnico);
        this.verificarDisponibilidadAula(claseDTO.getAulaId(), horarioUnico);
    }

    @Override
    public void validarCreacionClasesSemestrales(List<ClaseDTO> listaClasesDTO)
            throws DocenteNoDisponibleException,
            ParaescolarNoEncontradaException,
            MateriaNoEncontradaException,
            DocenteNoEncontradoException,
            CicloEscolarNoEncontradoException
    {
        if (listaClasesDTO == null) {
            throw new IllegalArgumentException("La lista de clases no debe ser nula.");
        }

        for (ClaseDTO clase: listaClasesDTO) {
            this.validarCreacionClaseSemestral(clase);
        }
    }
}