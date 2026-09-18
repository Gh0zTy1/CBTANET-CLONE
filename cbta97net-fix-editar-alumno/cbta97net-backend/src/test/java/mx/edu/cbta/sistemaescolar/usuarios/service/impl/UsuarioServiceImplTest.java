package mx.edu.cbta.sistemaescolar.usuarios.service.impl;

import mx.edu.cbta.sistemaescolar.usuarios.dto.UsuarioDTO;
import mx.edu.cbta.sistemaescolar.usuarios.mapper.UsuarioMapper;
import mx.edu.cbta.sistemaescolar.usuarios.domain.model.Usuario;
import mx.edu.cbta.sistemaescolar.usuarios.repository.UsuarioRepository;
import mx.edu.cbta.sistemaescolar.usuarios.service.KeycloakService;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.CredencialesInvalidasException;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.RegistrarUsuarioException;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.UsuarioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

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
        //usuarioDTO.setRoles(Set.of("ROLE_USER"));

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellidoPaterno("Pérez");
        usuario.setApellidoMaterno("García");
        usuario.setEmail("juan@example.com");
        usuario.setTelefono("5551234567");
        usuario.setCurp("CURP123456789");
        usuario.setActivo(true);
        usuario.setRoles(Set.of("ROLE_USER"));
    }

    @Test
    void iniciarSesion_UsuarioValido_ReturnsTokenData() throws Exception {
        // Arrange
        Map<String, Object> tokenData = Map.of("access_token", "token", "expires_in", 3600);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(keycloakService.login("1", "password")).thenReturn(tokenData);

        // Act
        Map<String, Object> result = usuarioService.iniciarSesion(1L, "password");

        // Assert
        assertNotNull(result);
        assertEquals("token", result.get("access_token"));
        verify(keycloakService).login("1", "password");
    }

    @Test
    void iniciarSesion_UsuarioNoEncontrado_ThrowsException() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        CredencialesInvalidasException exception = assertThrows(CredencialesInvalidasException.class,
            () -> usuarioService.iniciarSesion(1L, "password"));
        assertEquals("Usuario no encontrado.", exception.getMessage());
    }

    @Test
    void iniciarSesion_UsuarioInactivo_ThrowsException() {
        // Arrange
        usuario.setActivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act & Assert
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.iniciarSesion(1L, "password"));
        assertEquals("La cuenta de usuario está inactiva.", exception.getMessage());
    }

    @Test
    void usuarioExiste_ReturnsTrue() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = usuarioService.usuarioExiste(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void obtenerNombre_ReturnsNombre() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        String result = usuarioService.obtenerNombre(1L);

        // Assert
        assertEquals("Juan", result);
    }

    @Test
    void obtenerNombre_UsuarioNoEncontrado_ReturnsMensaje() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        String result = usuarioService.obtenerNombre(1L);

        // Assert
        assertEquals("Nombre no encontrado", result);
    }

    @Test
    void obtenerApellidoMaterno_ReturnsApellido() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        String result = usuarioService.obtenerApellidoMaterno(1L);

        // Assert
        assertEquals("García", result);
    }

    @Test
    void obtenerCorreoElectronico_ReturnsEmail() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        String result = usuarioService.obtenerCorreoElectronico(1L);

        // Assert
        assertEquals("juan@example.com", result);
    }

    @Test
    void registrar_EmailExistente_ThrowsException() {
        // Arrange
        when(usuarioRepository.existsByEmail("juan@example.com")).thenReturn(true);

        // Act & Assert
        RegistrarUsuarioException exception = assertThrows(RegistrarUsuarioException.class,
            () -> usuarioService.registrar(usuarioDTO, "password"));
        assertEquals("El correo electrónico ya está registrado.", exception.getMessage());
    }

    @Test
    void modificar_UsuarioExistente_Updates() throws UsuarioException {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.modificar(1L, usuarioDTO);

        // Assert
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void modificar_UsuarioNoEncontrado_ThrowsException() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.modificar(1L, usuarioDTO));
        assertEquals("No se encontró el usuario con ID: 1", exception.getMessage());
    }

    @Test
    void eliminar_UsuarioExistente_Deletes() throws UsuarioException {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.eliminar(1L);

        // Assert
        verify(keycloakService).deleteUser("1");
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void eliminar_UsuarioNoEncontrado_ThrowsException() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        UsuarioException exception = assertThrows(UsuarioException.class,
            () -> usuarioService.eliminar(1L));
        assertEquals("No se encontró el usuario con ID: 1", exception.getMessage());
    }
}