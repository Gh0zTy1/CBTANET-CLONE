package mx.edu.cbta.sistemaescolar.alumnado.service.impl;

import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Direccion;
import mx.edu.cbta.sistemaescolar.alumnado.dto.DireccionDTO;
import mx.edu.cbta.sistemaescolar.alumnado.dto.InformacionBasicaAlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.*;
import mx.edu.cbta.sistemaescolar.alumnado.repository.AlumnoRepository;
import mx.edu.cbta.sistemaescolar.alumnado.repository.TutorRepository;
import mx.edu.cbta.sistemaescolar.alumnado.service.AlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.mapper.AlumnoMapper;
import mx.edu.cbta.sistemaescolar.alumnado.mapper.TutorMapper;
import mx.edu.cbta.sistemaescolar.alumnado.dto.AlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Tutor;

import mx.edu.cbta.sistemaescolar.alumnado.service.DocumentoAlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.util.ExcelUtility;
import mx.edu.cbta.sistemaescolar.alumnado.util.impl.ExcelUtilityImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;

    @Autowired
    private AlumnoMapper alumnoMapper;

    @Autowired
    private TutorMapper tutorMapper;

    private ExcelUtility excelUtility;

    public AlumnoServiceImpl(AlumnoRepository alumnoRepository, TutorRepository tutorRepository) {
        this.alumnoRepository = alumnoRepository;
        this.tutorRepository = tutorRepository;
        excelUtility = new ExcelUtilityImpl();
    }

    private static final String REGEX_CURP =
            "^[A-Z]{4}[0-9]{6}[H,M][A-Z]{5}[A-Z0-9]{2}$";

    private Predicate<String> curpNoVacio =
            curp -> curp != null && !curp.isBlank();

    private Predicate<String> curpFormatoValido =
            curp -> curp.matches(REGEX_CURP);

    private static final String REGEX_NSS = "^\\d{11}$";

    private Predicate<String> nssNoVacio =
            nss -> nss != null && !nss.isBlank();

    private Predicate<String> nssFormatoValido =
            nss -> this.nssNoVacio.test(nss) && nss.matches(REGEX_NSS);

    /**
     * Si la condicion es 'false', entonces arroja la excepcion definida como parametro.
     * @param condicion Bandera booleana de una expresion.
     * @param exceptionSupplier Excepcion a lanzar en caso de error.
     */
    private void validar(boolean condicion,
                         Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!condicion) {
            throw exceptionSupplier.get();
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
    public AlumnoDTO obtenerAlumnoPorMatricula(String matricula) throws AlumnoNoEncontradoException {
        return alumnoRepository.findByMatricula(matricula)
                .map(alumnoMapper::toDTO)
                .orElseThrow(
                        () -> new AlumnoNoEncontradoException("No se encontró al alumno con la matrícula específicada.")
                );
    }

    @Override
    public Page<AlumnoDTO> obtenerAlumnosTodos(Pageable pageable) {
        return alumnoRepository.findAll(pageable).map(alumnoMapper::toDTO);
    }

    @Override
    public AlumnoDTO obtenerAlumnoPorCurp(String curp) {
        return alumnoRepository.findByCurp(curp)
                .map(alumnoMapper::toDTO)
                .orElse(null);
    }

    @Override
    public Page<InformacionBasicaAlumnoDTO> obtenerAlumnosPorCredenciales(String credenciales, Pageable pageable) {
        return this.alumnoRepository.obtenerPorCredencialesOptimizado(credenciales, pageable);
    }

    @Override
    public String obtenerMatriculaPorId(Long id) {
        return this.alumnoRepository.findById(id).map(Alumno::getMatricula).orElse("Matricula no encontrada");
    }

    private void gestionarTutor(Alumno alumno, AlumnoDTO dto) {

        if (dto.getTutorLegal() == null) {
            return;
        }

        Tutor tutorRequest = tutorMapper.toEntity(dto.getTutorLegal());
        String nombreNorm = normalizarTexto(tutorRequest.getNombre());
        
        // Si el tutor no tiene nombre ni teléfono válido, no lo procesamos
        if ((nombreNorm == null || nombreNorm.isBlank()) && (tutorRequest.getTelefono() == null || tutorRequest.getTelefono().isBlank())) {
            return;
        }

        tutorRequest.setNombre(nombreNorm);
        tutorRequest.setApellidoPaterno(normalizarTexto(tutorRequest.getApellidoPaterno()));
        tutorRequest.setApellidoMaterno(normalizarTexto(tutorRequest.getApellidoMaterno()));

        Tutor tutorExistente = null;

        if (tutorRequest.getId() != null) {
            tutorExistente = tutorRepository.findById(tutorRequest.getId()).orElse(null);
        }

        if (tutorExistente == null && tutorRequest.getTelefono() != null && !tutorRequest.getTelefono().isBlank()) {
            tutorExistente = tutorRepository
                    .findByTelefono(tutorRequest.getTelefono())
                    .orElse(null);
        }

        alumno.setTutorLegal(
                tutorExistente != null
                        ? actualizarTutor(tutorExistente, tutorRequest)
                        : tutorRepository.save(tutorRequest)
        );
    }

    private Tutor actualizarTutor(Tutor existente, Tutor request) {
        existente.setNombre(request.getNombre());
        existente.setApellidoPaterno(request.getApellidoPaterno());
        existente.setApellidoMaterno(request.getApellidoMaterno());
        if (request.getTelefono() != null) existente.setTelefono(request.getTelefono());
        if (request.getFechaNacimiento() != null) existente.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getParentezco() != null) existente.setParentezco(request.getParentezco());
        return existente;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlumnoDTO registrarAlumno(AlumnoDTO alumnoDto) throws AlumnoException, RegistrarAlumnoException {

        validar(
                !alumnoRepository.findByMatricula(alumnoDto.getMatricula()).isPresent(),
                () -> new RegistrarAlumnoException("La matrícula '" + alumnoDto.getMatricula() + "' ya existe.")
        );

        validar(
                this.curpNoVacio.test(alumnoDto.getCurp()),
                () -> new RegistrarAlumnoException("El CURP es obligatorio.")
        );

        String curpNormalizado = alumnoDto.getCurp().trim().toUpperCase();

        validar(
                !this.alumnoRepository.findByCurp(curpNormalizado).isPresent(),
                () -> new RegistrarAlumnoException("El CURP '%s' ya está registrado.".formatted(curpNormalizado))
        );

        validar(
                this.curpFormatoValido.test(alumnoDto.getCurp()),
                () -> new RegistrarAlumnoException("El formato del CURP es inválido.")
        );

        if (this.nssNoVacio.test(alumnoDto.getNumeroSeguroSocial())) {

            validar(
                    this.nssFormatoValido.test(alumnoDto.getNumeroSeguroSocial()),
                    () -> new RegistrarAlumnoException(
                            "El Número de Seguro Social debe contener exactamente 11 dígitos."
                    )
            );

            validar(
                    !this.alumnoRepository.findByNumeroSeguroSocial(alumnoDto.getNumeroSeguroSocial()).isPresent(),
                    () -> new RegistrarAlumnoException(
                            "El Número de Seguro Social del alumno ya está registrado en el sistema."
                    )
            );
        } else {
            alumnoDto.setNumeroSeguroSocial(null); // note: No cambiar.
        }

        try {
            Alumno alumno = alumnoMapper.toEntity(alumnoDto);

            alumno.setNombre(normalizarTexto(alumno.getNombre()));
            alumno.setApellidoPaterno(normalizarTexto(alumno.getApellidoPaterno()));
            alumno.setApellidoMaterno(normalizarTexto(alumno.getApellidoMaterno()));

            Direccion direccion = alumno.getDireccion();

            if (direccion != null) {
                direccion.setCalle(normalizarTexto(direccion.getCalle()));
                direccion.setColonia(normalizarTexto(direccion.getColonia()));
                direccion.setLocalidad(normalizarTexto(direccion.getLocalidad()));
            }

            gestionarTutor(alumno, alumnoDto);

            Alumno guardado = alumnoRepository.save(alumno);
            return alumnoMapper.toDTO(guardado);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RegistrarAlumnoException("Ocurrió un error al intentar registrar el alumno. " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlumnoDTO actualizarAlumno(AlumnoDTO alumnoDTO) throws AlumnoNoEncontradoException, ActualizarAlumnoException {

        Alumno alumnoExistente = alumnoRepository.findByMatricula(alumnoDTO.getMatricula())
                .orElseThrow(
                        () -> new AlumnoNoEncontradoException(
                                "No se encontró al alumno con la matrícula: " + alumnoDTO.getMatricula()
                        )
                );

        validar(
                this.curpNoVacio.test(alumnoDTO.getCurp()),
                () -> new ActualizarAlumnoException("El CURP es obligatorio.")
        );

        String curpNormalizado = alumnoDTO.getCurp().trim().toUpperCase();

        // Validar si el CURP cambió y si el nuevo ya está en uso
        if (!curpNormalizado.equals(alumnoExistente.getCurp())) {
            validar(
                    alumnoRepository.findByCurp(curpNormalizado).isEmpty(),
                    () -> new ActualizarAlumnoException(
                            "El CURP '" + curpNormalizado + "' ya está registrado por otro alumno."
                    )
            );

            validar(
                    this.curpFormatoValido.test(curpNormalizado),
                    () -> new ActualizarAlumnoException("El formato del CURP es inválido.")
            );
        }

        if (this.nssNoVacio.test(alumnoDTO.getNumeroSeguroSocial())) {
            validar(
                    this.nssFormatoValido.test(alumnoDTO.getNumeroSeguroSocial()),
                    () -> new ActualizarAlumnoException(
                            "El Número de Seguro Social debe contener exactamente 11 dígitos."
                    )
            );

            if (!alumnoDTO.getNumeroSeguroSocial().equals(alumnoExistente.getNumeroSeguroSocial())) {
                validar(
                        alumnoRepository.findByNumeroSeguroSocial(alumnoDTO.getNumeroSeguroSocial()).isEmpty(),
                        () -> new ActualizarAlumnoException("El NSS ya está registrado en el sistema.")
                );
            }
        }

        try {
            alumnoExistente.setNombre(normalizarTexto(alumnoDTO.getNombre()));
            alumnoExistente.setApellidoPaterno(normalizarTexto(alumnoDTO.getApellidoPaterno()));
            alumnoExistente.setApellidoMaterno(normalizarTexto(alumnoDTO.getApellidoMaterno()));
            alumnoExistente.setCurp(curpNormalizado);
            alumnoExistente.setFechaNacimiento(alumnoDTO.getFechaNacimiento());
            alumnoExistente.setNumeroSeguroSocial(alumnoDTO.getNumeroSeguroSocial());
            alumnoExistente.setNumeroPolizaSeguro(alumnoDTO.getNumeroPolizaSeguro());
            alumnoExistente.setCondicionEspecialDescripcion(alumnoDTO.getCondicionEspecialDescripcion());

            if (alumnoDTO.getDireccion() != null) {
                Direccion dirExistente = alumnoExistente.getDireccion();
                if (dirExistente == null) {
                    dirExistente = new Direccion();
                }
                dirExistente.setCalle(normalizarTexto(alumnoDTO.getDireccion().getCalle()));
                dirExistente.setColonia(normalizarTexto(alumnoDTO.getDireccion().getColonia()));
                dirExistente.setLocalidad(normalizarTexto(alumnoDTO.getDireccion().getLocalidad()));
                dirExistente.setNumeroExterior(alumnoDTO.getDireccion().getNumeroExterior());
                dirExistente.setCodigoPostal(alumnoDTO.getDireccion().getCodigoPostal());
                alumnoExistente.setDireccion(dirExistente);
            }

            gestionarTutor(alumnoExistente, alumnoDTO);

            Alumno actualizado = alumnoRepository.save(alumnoExistente);
            return alumnoMapper.toDTO(actualizado);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ActualizarAlumnoException("Error al actualizar el alumno: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void eliminarAlumno(String matricula) throws AlumnoNoEncontradoException, EliminarAlumnoException {
        if (!alumnoRepository.existsByMatricula(matricula)) {
            throw new AlumnoNoEncontradoException("No se encontró alumno con matrícula: " + matricula);
        }

        try {
            alumnoRepository.deleteByMatricula(matricula);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EliminarAlumnoException("Ocurrió un error al intentar eliminar al alumno. Intente de nuevo más tarde.");
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importarAlumnos(MultipartFile file) throws ImportarAlumnosException {
        try {
            System.out.println("Importando alumnos (Modo Upsert Seguro)...");
            List<Alumno> alumnosExcel = excelUtility.readExcel(file.getInputStream());

            for (Alumno alumnoExcel : alumnosExcel) {
                String matricula = alumnoExcel.getMatricula();
                String curpNormalizado = alumnoExcel.getCurp() != null ? alumnoExcel.getCurp().trim().toUpperCase() : "";

                Alumno alumnoExistente = null;

                var porMatricula = alumnoRepository.findByMatricula(matricula);
                if (porMatricula.isPresent()) {
                    alumnoExistente = porMatricula.get();
                } else {
                    var porCurp = alumnoRepository.findByCurp(curpNormalizado);
                    if (porCurp.isPresent()) {
                        alumnoExistente = porCurp.get();
                    }
                }

                if (alumnoExistente != null) {
                    alumnoExistente.setMatricula(matricula);
                    alumnoExistente.setCurp(curpNormalizado);
                    alumnoExistente.setNombre(alumnoExcel.getNombre());
                    alumnoExistente.setApellidoPaterno(alumnoExcel.getApellidoPaterno());
                    alumnoExistente.setApellidoMaterno(alumnoExcel.getApellidoMaterno());
                    alumnoExistente.setFechaNacimiento(alumnoExcel.getFechaNacimiento());

                    alumnoRepository.save(alumnoExistente);
                } else {
                    alumnoExcel.setCurp(curpNormalizado);

                    alumnoRepository.save(alumnoExcel);
                }
            }
        } catch (IOException ex) {
            System.out.println(String.format("# AlumnoServiceImpl.importarAlumnos(m) -> ERROR: %s", ex.getMessage()));
            throw new ImportarAlumnosException("No se pudo realizar la importación de alumnos. Por favor, intente más tarde.");
        } catch (Exception ex) {
            System.out.println(String.format("# AlumnoServiceImpl.importarAlumnos(m) -> ERROR CRÍTICO: %s", ex.getMessage()));
            throw new ImportarAlumnosException("Error en la consistencia de datos: " + ex.getMessage());
        }
    }
}