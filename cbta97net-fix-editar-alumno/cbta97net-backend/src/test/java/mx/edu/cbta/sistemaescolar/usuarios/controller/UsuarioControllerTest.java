package mx.edu.cbta.sistemaescolar.usuarios.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    /*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTO;
    private RegistrarUsuarioDTO registrarUsuarioDTO;
    private IniciarSesionDTO iniciarSesionDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombre("Juan");
        usuarioDTO.setApellidoPaterno("Pérez");
        usuarioDTO.setApellidoMaterno("García");
        usuarioDTO.setEmail("juan@example.com");
        usuarioDTO.setTelefono("5551234567");
        usuarioDTO.setCurp("CURP123456789");
        usuarioDTO.setActivo(true);
        //usuarioDTO.setRoles(Set.of(new RolDTO("ROLE_USER")));

        registrarUsuarioDTO = new RegistrarUsuarioDTO();
        registrarUsuarioDTO.setNombre("Juan");
        registrarUsuarioDTO.setApellidoPaterno("Pérez");
        registrarUsuarioDTO.setApellidoMaterno("García");
        registrarUsuarioDTO.setEmail("juan@example.com");
        registrarUsuarioDTO.setTelefono("5551234567");
        registrarUsuarioDTO.setCurp("CURP123456789");
        registrarUsuarioDTO.setContrasena("password");

        iniciarSesionDTO = new IniciarSesionDTO();
        iniciarSesionDTO.setId(1L);
        iniciarSesionDTO.setContrasena("password");
    }

    @Test
    @WithMockUser(roles = "USUARIOS_READ_ALL")
    void obtenerUsuariosTodos_ReturnsPage() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<UsuarioDTO> page = new PageImpl<>(List.of(usuarioDTO));
        when(usuarioService.obtenerUsuariosTodos(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/usuarios")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].email").value("juan@example.com"));
    }

    @Test
    void login_Success() throws Exception {
        // Arrange
        Map<String, Object> tokenData = Map.of("access_token", "token", "expires_in", 3600);
        when(usuarioService.iniciarSesion(1L, "password")).thenReturn(tokenData);

        // Act & Assert
        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(iniciarSesionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token"));
    }

//    @Test
//    void registrar_Success() throws Exception {
//        // Arrange
//        when(usuarioMapper.toDTO(registrarUsuarioDTO)).thenReturn(usuarioDTO);
//        when(usuarioService.registrar(any(UsuarioDTO.class), eq("password"))).thenReturn(usuarioDTO);
//
//        // Act & Assert
//        mockMvc.perform(post("/usuarios/registrar")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registrarUsuarioDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("Te has registrado con éxito. Tu nuevo ID es '1.'"));
//    }

    @Test
    @WithMockUser(roles = "USUARIOS_DELETE")
    void eliminarUsuario_Success() throws Exception {
        // Arrange
        doNothing().when(usuarioService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/usuarios/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario con ID 1 eliminado correctamente del sistema."));
    }

     */
}