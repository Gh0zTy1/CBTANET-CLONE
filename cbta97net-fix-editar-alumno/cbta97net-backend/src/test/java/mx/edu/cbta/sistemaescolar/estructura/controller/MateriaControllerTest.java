package mx.edu.cbta.sistemaescolar.estructura.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(MateriaController.class)
class MateriaControllerTest {

    /*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MateriaService materiaService;

    @MockitoBean
    private MateriaMapper materiaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MateriaDTO materiaDTO;

    @BeforeEach
    void setUp() {
        materiaDTO = new MateriaDTO();
        materiaDTO.setId(1L);
        materiaDTO.setNombre("Matemáticas");
        materiaDTO.setSemestre(1);
        materiaDTO.setHorasPorSemana(5);
        materiaDTO.setCarreraTecnicaId(1L);
        materiaDTO.setCarreraTecnicaNombre("Informática");
    }

    @Test
    void obtenerMateriaPorId_ReturnsMateria() throws Exception {
        // Arrange
        when(materiaService.obtenerMateriaPorId(1L)).thenReturn(materiaDTO);

        // Act & Assert
        mockMvc.perform(get("/materias/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Matemáticas"));
    }

    @Test
    void registrarMateria_Success() throws Exception {
        // Arrange
        when(materiaService.registrarMateria(any(MateriaDTO.class))).thenReturn(materiaDTO);

        // Act & Assert
        mockMvc.perform(post("/materias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Matemáticas"));
    }

    @Test
    void obtenerTodasLasMaterias_ReturnsList() throws Exception {
        // Arrange
        when(materiaService.obtenerTodasLasMaterias()).thenReturn(List.of(materiaDTO));

        // Act & Assert
        mockMvc.perform(get("/materias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Matemáticas"));
    }

    @Test
    void obtenerMateriasPorCarrera_ReturnsList() throws Exception {
        // Arrange
        when(materiaService.obtenerMateriasPorCarrera(1L)).thenReturn(List.of(materiaDTO));

        // Act & Assert
        mockMvc.perform(get("/materias/carrera/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Matemáticas"));
    }*/
}