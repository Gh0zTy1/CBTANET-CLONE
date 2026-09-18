package mx.edu.cbta.sistemaescolar.alumnado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.cbta.sistemaescolar.alumnado.dto.AlumnoDTO;
import mx.edu.cbta.sistemaescolar.alumnado.mapper.AlumnoMapper;
import mx.edu.cbta.sistemaescolar.alumnado.service.AlumnoService;
import mx.edu.cbta.sistemaescolar.alumnado.service.DocumentoAlumnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlumnoController.class)
class AlumnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlumnoService alumnoService;

    @MockitoBean
    private DocumentoAlumnoService documentoService;

    @MockitoBean
    private AlumnoMapper alumnoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private AlumnoDTO alumnoDTO;

    @BeforeEach
    void setUp() {
        alumnoDTO = new AlumnoDTO();
        alumnoDTO.setId(1L);
        alumnoDTO.setMatricula("12345");
        alumnoDTO.setCurp("CURP123456789");
        alumnoDTO.setNombre("Juan");
        alumnoDTO.setApellidoPaterno("Pérez");
        alumnoDTO.setApellidoMaterno("García");
        alumnoDTO.setFechaNacimiento(LocalDate.of(2000, 1, 1));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_READ")
    void listarGruposPaginados_ReturnsPage() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<AlumnoDTO> page = new PageImpl<>(List.of(alumnoDTO));
        when(alumnoService.obtenerAlumnosTodos(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/alumnos")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].matricula").value("12345"));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_DELETE")
    void eliminarAlumnoPorMatricula_Success() throws Exception {
        // Arrange
        doNothing().when(alumnoService).eliminarAlumno("12345");

        // Act & Assert
        mockMvc.perform(delete("/alumnos/12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Se ha eliminado al alumno correctamente."));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_READ")
    void obtenerAlumnoPorMatricula_ReturnsAlumno() throws Exception {
        // Arrange
        when(alumnoService.obtenerAlumnoPorMatricula("12345")).thenReturn(alumnoDTO);

        // Act & Assert
        mockMvc.perform(get("/alumnos/12345"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.matricula").value("12345"));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_CREATE")
    void registrarAlumno_Success() throws Exception {
        // Arrange
        when(alumnoService.registrarAlumno(any(AlumnoDTO.class))).thenReturn(alumnoDTO);

        // Act & Assert
        mockMvc.perform(post("/alumnos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("12345"));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_UPDATE")
    void guardarActaNacimiento_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("documento", "test.pdf", "application/pdf", "content".getBytes());
        doNothing().when(documentoService).guardarActaNacimiento(eq("12345"), any());

        // Act & Assert
        mockMvc.perform(multipart("/alumnos/12345/documentos/acta-nacimiento")
                .file(file)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("El acta de nacimiento se guardó correctamente."));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_UPDATE")
    void guardarCurp_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("documento", "test.pdf", "application/pdf", "content".getBytes());
        doNothing().when(documentoService).guardarCurp(eq("12345"), any());

        // Act & Assert
        mockMvc.perform(multipart("/alumnos/12345/documentos/curp")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("El documento del CURP se guardó correctamente."));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_UPDATE")
    void guardarCertificado_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("documento", "test.pdf", "application/pdf", "content".getBytes());
        doNothing().when(documentoService).guardarCertificadoSecundaria(eq("12345"), any());

        // Act & Assert
        mockMvc.perform(multipart("/alumnos/12345/documentos/certificado-secundaria")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("El certificado de secundaria se guardó correctamente."));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_UPDATE")
    void guardarFoto_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("documento", "test.jpg", "image/jpeg", "content".getBytes());
        doNothing().when(documentoService).guardarFotoEscolar(eq("12345"), any());

        // Act & Assert
        mockMvc.perform(multipart("/alumnos/12345/documentos/foto")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("La foto escolar del alumno se guardó correctamente."));
    }
//
    @Test
    @WithMockUser(roles = "ALUMNOS_READ")
    void obtenerActaNacimiento_ReturnsFile() throws Exception {
        // Arrange
        byte[] content = "test content".getBytes();
        when(documentoService.obtenerActaNacimiento("12345")).thenReturn(content);

        // Act & Assert
        mockMvc.perform(get("/alumnos/12345/documentos/acta-nacimiento"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"acta_nacimiento.pdf\""));
    }
//
    @Test
    @WithMockUser(roles = "ALUMNOS_READ")
    void obtenerCurp_ReturnsFile() throws Exception {
        // Arrange
        byte[] content = "test content".getBytes();
        when(documentoService.obtenerCurp("12345")).thenReturn(content);

        // Act & Assert
        mockMvc.perform(get("/alumnos/12345/documentos/curp"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"curp.pdf\""));
    }
//
    @Test
    @WithMockUser(roles = "ALUMNOS_READ")
    void obtenerCertificado_ReturnsFile() throws Exception {
        // Arrange
        byte[] content = "test content".getBytes();
        when(documentoService.obtenerCertificadoSecundaria("12345")).thenReturn(content);

        // Act & Assert
        mockMvc.perform(get("/alumnos/12345/documentos/certificado-secundaria"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"certificado_secundaria.pdf\""));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_READ")
    void obtenerFoto_ReturnsFile() throws Exception {
        // Arrange
        byte[] content = "test content".getBytes();
        when(documentoService.obtenerFotoEscolar("12345")).thenReturn(content);

        // Act & Assert
        mockMvc.perform(get("/alumnos/12345/documentos/foto"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"foto_escolar.pdf\""));
    }

    @Test
    @WithMockUser(roles = "ALUMNOS_CREATE")
    void importar_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("documento", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "content".getBytes());
        doNothing().when(alumnoService).importarAlumnos(any());

        // Act & Assert
        mockMvc.perform(multipart("/alumnos/importar")
                .file(file)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Los alumnos se han importado correctamente."));
    }
}