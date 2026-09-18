package mx.edu.cbta.sistemaescolar.paraescolar.controller;

import mx.edu.cbta.sistemaescolar.estructura.controller.ActividadParaescolarController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ActividadParaescolarController.class)
class ActividadParaescolarControllerTest {

    /*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActividadParaescolarService actividadParaescolarService;

    @MockitoBean
    private ActividadParaescolarMapper actividadParaescolarMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ActividadParaescolarDTO actividadDTO;

    @BeforeEach
    void setUp() {
        actividadDTO = new ActividadParaescolarDTO();
        actividadDTO.setId(1L);
        actividadDTO.setNombre("Deportes");
        actividadDTO.setDescripcion("Actividades deportivas");
    }

    @Test
    @WithMockUser(roles = "ACTIVIDADES_PARAESCOLARES_READ")
    void listar_ReturnsList() throws Exception {
        // Arrange
        when(actividadParaescolarService.obtenerParaescolares()).thenReturn(List.of(actividadDTO));

        // Act & Assert
        mockMvc.perform(get("/paraescolares"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Deportes"));
    }

    @Test
    @WithMockUser(roles = "ACTIVIDADES_PARAESCOLARES_CREATE")
    void crear_Success() throws Exception {
        // Arrange
        when(actividadParaescolarService.crearActividadParaescolar(any(ActividadParaescolarDTO.class))).thenReturn(actividadDTO);

        // Act & Assert
        mockMvc.perform(post("/paraescolares")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actividadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Deportes"));
    }

    @Test
    @WithMockUser(roles = "ACTIVIDADES_PARAESCOLARES_UPDATE")
    void modificar_Success() throws Exception {
        // Arrange
        when(actividadParaescolarService.modificarParaescolar(1L, actividadDTO)).thenReturn(actividadDTO);

        // Act & Assert
        mockMvc.perform(patch("/paraescolares/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actividadDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Deportes"));
    }

    @Test
    @WithMockUser(roles = "ACTIVIDADES_PARAESCOLARES_DELETE")
    void eliminar_Success() throws Exception {
        // Arrange
        doNothing().when(actividadParaescolarService).eliminarParaescolar(1L);

        // Act & Assert
        mockMvc.perform(delete("/paraescolares/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("La actividad paraescolar ha sido eliminada con éxito."));
    }*/
}