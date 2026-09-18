package mx.edu.cbta.sistemaescolar.personal.service.impl;


import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteDuplicadoException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.DocenteNoEncontradoException;
import mx.edu.cbta.sistemaescolar.personal.domain.exception.RegistrarDocenteException;
import mx.edu.cbta.sistemaescolar.personal.repository.DocenteRepository;
import mx.edu.cbta.sistemaescolar.personal.service.DocenteService;
import mx.edu.cbta.sistemaescolar.personal.mapper.DocenteMapper;
import mx.edu.cbta.sistemaescolar.personal.dto.DocenteDTO;
import mx.edu.cbta.sistemaescolar.personal.domain.model.Docente;

import mx.edu.cbta.sistemaescolar.usuarios.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocenteServiceImpl implements DocenteService {

    private DocenteRepository docenteRepository;
    private UsuarioService usuarioService;

    @Autowired
    private DocenteMapper docenteMapper;

    public DocenteServiceImpl(
            DocenteRepository docenteRepository,
            UsuarioService usuarioService
    ) {
        this.docenteRepository = docenteRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public DocenteDTO registrarDocente(DocenteDTO nuevoDocenteDTO) throws RegistrarDocenteException, DocenteDuplicadoException {


        if (!usuarioService.usuarioExiste(nuevoDocenteDTO.getUsuarioId())) {
            throw new RegistrarDocenteException("No se encontró al usuario con ID: %s.".formatted(nuevoDocenteDTO.getUsuarioId()));
        }

        if (docenteRepository.existsByCedulaProfesional(nuevoDocenteDTO.getCedulaProfesional())) {
            throw new RegistrarDocenteException("La cédula del docente ya existe.");
        }

        if (docenteRepository.existsByUsuarioId(nuevoDocenteDTO.getUsuarioId())) {
            throw new RegistrarDocenteException("Ya existe un docente el ID de usuario dado.");
        }

        Docente entidad = this.docenteMapper.toEntity(nuevoDocenteDTO);

        Docente registrado = this.docenteRepository.save(entidad);

        return this.docenteMapper.toDto(registrado);
    }

    @Override
    public Page<DocenteDTO> obtenerTodos(Pageable pageable) {
        return this.docenteRepository.findAll(pageable).map(d -> docenteMapper.toDto(d));
    }

    @Override
    public DocenteDTO obtenerDocentePorId(Long docenteId) throws DocenteNoEncontradoException {
        Docente docenteEncontrado = this.docenteRepository.findById(docenteId)
                .orElseThrow(() -> new DocenteNoEncontradoException("No se encontró al docente con el ID: %s".formatted(docenteId)));

        return this.docenteMapper.toDto(docenteEncontrado);
    }

    @Override
    public String obtenerNombre(Long idDocente) {
        return docenteRepository.findById(idDocente)
                .map(docente -> this.usuarioService.obtenerNombre(docente.getUsuarioId()))
                .orElse("Nombre no encontrado");
    }

    @Override
    public String obtenerApellidoPaterno(Long idDocente) {
        return docenteRepository.findById(idDocente)
                .map(docente -> this.usuarioService.obtenerApellidoPaterno(docente.getUsuarioId()))
                .orElse("Apellido paterno no encontrado");
    }

    @Override
    public String obtenerApellidoMaterno(Long idDocente) {
        return docenteRepository.findById(idDocente)
                .map(docente -> this.usuarioService.obtenerApellidoMaterno(docente.getUsuarioId()))
                .orElse("Apellido materno no encontrado");
    }

    @Override
    public boolean existeDocentePorId(Long idDocente) {
        return this.docenteRepository.existsById(idDocente);
    }

    @Override
    public boolean existeDocentePorCedula(String cedulaProfesional) {
        return this.docenteRepository.existsByCedulaProfesional(cedulaProfesional);
    }

    /*
    @Override
    public boolean docenteDisponibleEnHorario(Long idDocente, Horario horarioNuevo) throws DocenteNoDisponibleException, DocenteNoEncontradoException {

        List<ClaseDTO> clasesDocente = this.claseService.obtenerClasesVigentesDocente(idDocente);

        Optional<ClaseDTO> claseConConflicto = clasesDocente.stream()
                .filter(clase -> clase.getHorarios().stream()
                        .anyMatch(horarioClase -> horarioClase.seEmpalmaCon(horarioNuevo)))
                .findFirst();

        if (claseConConflicto.isPresent()) {
            ClaseDTO conflicto = claseConConflicto.get();
            DocenteDTO docenteDTO = obtenerDocentePorId(idDocente);

            MateriaDTO materiaDTO = this.materiaService.obtenerMateriaPorId(conflicto.getMateriaId());

            Horario horarioChocante = conflicto.getHorarios().stream()
                    .filter(h -> h.seEmpalmaCon(horarioNuevo))
                    .findFirst()
                    .orElse(horarioNuevo);

            throw new DocenteNoDisponibleException(
                    "El docente %s %s %s no está disponible. Tiene un conflicto con la materia '%s' en el horario: %s (%s - %s)."
                            .formatted(
                                    docenteDTO.getNombre(),
                                    docenteDTO.getApellidoPaterno(),
                                    docenteDTO.getApellidoMaterno(),
                                    materiaDTO.getNombre(),
                                    horarioChocante.getDia(),
                                    horarioChocante.getHoraInicio(),
                                    horarioChocante.getHoraFin()
                            )
            );
        }

        return true;
    }*/

    @Override
    public Page<DocenteDTO> obtenerDocentePorMateria(Long materiaId, Pageable pageable) {
        return this.docenteRepository.findByMateriasId(materiaId, pageable).map(d -> docenteMapper.toDto(d));
    }
}