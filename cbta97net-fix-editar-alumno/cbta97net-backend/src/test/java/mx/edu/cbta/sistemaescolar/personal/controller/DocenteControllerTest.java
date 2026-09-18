package mx.edu.cbta.sistemaescolar.personal.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(DocenteController.class)
class DocenteControllerTest {

    /*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocenteService docenteService;

    @MockitoBean
    private DocenteMapper docenteMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private DocenteDTO docenteDTO;
    private RegistrarDocenteDTO registrarDocenteDTO;

    @BeforeEach
    void setUp() {
        docenteDTO = new DocenteDTO();
        docenteDTO.setIdDocente(1L);
        docenteDTO.setUsuarioId(1L);
        docenteDTO.setNombre("Juan");
        docenteDTO.setApellidoPaterno("Pérez");
        docenteDTO.setApellidoMaterno("García");
        docenteDTO.setEmail("juan@example.com");
        docenteDTO.setTelefono("5551234567");
        docenteDTO.setCurp("CURP123456789");
        docenteDTO.setActivo(true);
        docenteDTO.setRoles(Set.of("ROLE_DOCENTE"));
        docenteDTO.setCedulaProfesional("12345678");

        registrarDocenteDTO = new RegistrarDocenteDTO();
        registrarDocenteDTO.setCedulaProfesional("12345678");
        registrarDocenteDTO.setUsuarioId(1L);
    }

    @Test
    @WithMockUser(roles = "DOCENTES_CREATE")
    void registrarDocente_Success() throws Exception {
        // Arrange
        when(docenteMapper.toDto(registrarDocenteDTO)).thenReturn(docenteDTO);
        when(docenteService.registrarDocente(any(DocenteDTO.class))).thenReturn(docenteDTO);

        // Act & Assert
        mockMvc.perform(post("/docentes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrarDocenteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_docente").value(1));
    }

    @Test
    @WithMockUser(roles = "DOCENTES_READ")
    void obtenerDocentesPorMateria_ReturnsPage() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocenteDTO> page = new PageImpl<>(List.of(docenteDTO));
        when(docenteService.obtenerDocentePorMateria(1L, pageable)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/docentes/materia/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCENTES_READ")
    void obtenerTodosLosDocentes_ReturnsPage() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocenteDTO> page = new PageImpl<>(List.of(docenteDTO));
        when(docenteService.obtenerTodos(pageable)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/docentes")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }*/
}