package mx.edu.cbta.sistemaescolar.alumnado.service.impl;

import mx.edu.cbta.sistemaescolar.alumnado.dto.AlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.dto.TutorDTO;
import mx.edu.cbta.sistemaescolar.alumnado.mapper.AlumnoMapper;
import mx.edu.cbta.sistemaescolar.alumnado.mapper.TutorMapper;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Tutor;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.AlumnoNoEncontradoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.EliminarAlumnoException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.ImportarAlumnosException;
import mx.edu.cbta.sistemaescolar.alumnado.domain.exception.RegistrarAlumnoException;
import mx.edu.cbta.sistemaescolar.alumnado.repository.AlumnoRepository;
import mx.edu.cbta.sistemaescolar.alumnado.repository.TutorRepository;
import mx.edu.cbta.sistemaescolar.alumnado.util.ExcelUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceImplTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private AlumnoMapper alumnoMapper;

    @Mock
    private TutorMapper tutorMapper;

    @Mock
    private ExcelUtility excelUtility;

    @InjectMocks
    private AlumnoServiceImpl alumnoService;

    private AlumnoDTO alumnoDTO;
    private Alumno alumno;
    private TutorDTO tutorDTO;
    private Tutor tutor;

    @BeforeEach
    void setUp() {
        // Initialize test data
        alumnoDTO = new AlumnoDTO();
        alumnoDTO.setId(1L);
        alumnoDTO.setMatricula("12345");
        alumnoDTO.setCurp("CURP123456789");
        alumnoDTO.setNombre("Juan");
        alumnoDTO.setApellidoPaterno("Pérez");
        alumnoDTO.setApellidoMaterno("García");
        alumnoDTO.setFechaNacimiento(LocalDate.of(2000, 1, 1));

        tutorDTO = new TutorDTO();
        tutorDTO.setId(1L);
        tutorDTO.setNombre("María");
        tutorDTO.setApellidoPaterno("López");
        tutorDTO.setApellidoMaterno("Hernández");
        tutorDTO.setTelefono("5551234567");

        alumnoDTO.setTutorLegal(tutorDTO);

        alumno = new Alumno();
        alumno.setId(1L);
        alumno.setMatricula("12345");
        alumno.setCurp("CURP123456789");
        alumno.setNombre("JUAN");
        alumno.setApellidoPaterno("PEREZ");
        alumno.setApellidoMaterno("GARCIA");
        alumno.setFechaNacimiento(LocalDate.of(2000, 1, 1));

        tutor = new Tutor();
        tutor.setId(1L);
        tutor.setNombre("MARIA");
        tutor.setApellidoPaterno("LOPEZ");
        tutor.setApellidoMaterno("HERNANDEZ");
        tutor.setTelefono("5551234567");

        alumno.setTutorLegal(tutor);

        // Set the mocked excelUtility using reflection
        try {
            java.lang.reflect.Field field = AlumnoServiceImpl.class.getDeclaredField("excelUtility");
            field.setAccessible(true);
            field.set(alumnoService, excelUtility);
        } catch (Exception e) {
            // Handle exception
        }
    }

    @Test
    void registrarAlumno_MatriculaExistente_ThrowsException() {
        // Arrange
        when(alumnoRepository.findByMatricula("12345")).thenReturn(Optional.of(alumno));

        // Act & Assert
        RegistrarAlumnoException exception = assertThrows(RegistrarAlumnoException.class,
            () -> alumnoService.registrarAlumno(alumnoDTO));
        assertEquals("La matrícula '12345' ya existe.", exception.getMessage());
    }

    @Test
    void registrarAlumno_CurpExistente_ThrowsException() {
        // Arrange
        when(alumnoRepository.findByMatricula("12345")).thenReturn(Optional.empty());
        when(alumnoRepository.findByCurp("CURP123456789")).thenReturn(Optional.of(alumno));

        // Act & Assert
        RegistrarAlumnoException exception = assertThrows(RegistrarAlumnoException.class,
            () -> alumnoService.registrarAlumno(alumnoDTO));
        assertEquals("El CURP 'CURP123456789' ya está registrado.", exception.getMessage());
    }

    @Test
    void obtenerMatriculaPorId_AlumnoEncontrado_ReturnsMatricula() {
        // Arrange
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno));

        // Act
        String result = alumnoService.obtenerMatriculaPorId(1L);

        // Assert
        assertEquals("12345", result);
    }

    @Test
    void obtenerMatriculaPorId_AlumnoNoEncontrado_ReturnsMensaje() {
        // Arrange
        when(alumnoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        String result = alumnoService.obtenerMatriculaPorId(1L);

        // Assert
        assertEquals("Matricula no encontrada", result);
    }

    @Test
    void eliminarAlumno_AlumnoExiste_EliminaCorrectamente() throws AlumnoNoEncontradoException, EliminarAlumnoException {
        // Arrange
        when(alumnoRepository.existsByMatricula("12345")).thenReturn(true);

        // Act
        alumnoService.eliminarAlumno("12345");

        // Assert
        verify(alumnoRepository).deleteByMatricula("12345");
    }

    @Test
    void eliminarAlumno_AlumnoNoExiste_ThrowsException() {
        // Arrange
        when(alumnoRepository.existsByMatricula("99999")).thenReturn(false);

        // Act & Assert
        AlumnoNoEncontradoException exception = assertThrows(AlumnoNoEncontradoException.class,
            () -> alumnoService.eliminarAlumno("99999"));
        assertEquals("No se encontró alumno con matrícula: 99999", exception.getMessage());
    }

    @Test
    void importarAlumnos_ImportacionExitosa() throws ImportarAlumnosException, IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(excelUtility.readExcel(any())).thenReturn(List.of(alumno));
        when(alumnoRepository.existsByMatricula("12345")).thenReturn(false);

        // Act
        alumnoService.importarAlumnos(file);

        // Assert
        verify(excelUtility).readExcel(file.getInputStream());
        verify(alumnoRepository).saveAll(List.of(alumno));
    }

    @Test
    void importarAlumnos_IOException_ThrowsException() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(excelUtility.readExcel(any())).thenThrow(new IOException("Error"));

        // Act & Assert
        ImportarAlumnosException exception = assertThrows(ImportarAlumnosException.class,
            () -> alumnoService.importarAlumnos(file));
        assertTrue(exception.getMessage().contains("Excel data is failed to store"));
    }
}