package mx.edu.cbta.sistemaescolar.usuarios.controller;

import jakarta.ws.rs.PathParam;
import mx.edu.cbta.sistemaescolar.usuarios.dto.*;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.*;

import mx.edu.cbta.sistemaescolar.usuarios.service.UsuarioService;
import mx.edu.cbta.sistemaescolar.usuarios.mapper.UsuarioMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USUARIOS_UPDATE')")
    public ResponseEntity<?> modificarUsuario(
            @PathVariable("id") Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO
    ) throws UsuarioException {

        usuarioDTO.setId(id);

        this.usuarioService.modificar(id, usuarioDTO);

        Map<String, String> body = new HashMap<>();
        body.put("message", "La información del usuario con ID " + id + " ha sido actualizada exitosamente.");

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USUARIOS_READ_ALL')")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable("id") Long id) throws UsuarioNoEncontradoException {

        UsuarioDTO encontradoDTO = this.usuarioService.obtenerUsuarioPorId(id);

        return ResponseEntity.ok(encontradoDTO);
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerInformacionPerfilUsuario(@AuthenticationPrincipal Jwt jwt) throws UsuarioNoEncontradoException, CredencialesInvalidasException {

        String usuarioId = jwt.getClaimAsString("preferred_username");

        if (usuarioId == null) {
            throw new CredencialesInvalidasException("No cuentas con las credenciales válidas para acceder a este recurso.");
        }

        UsuarioDTO usuarioDTO = this.usuarioService.obtenerUsuarioPorId(Long.valueOf(usuarioId));

        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('USUARIOS_READ_ALL')")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosTodos(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        Page<UsuarioDTO> usuariosPage = this.usuarioService.obtenerUsuariosTodos(pageable);
        return ResponseEntity.ok(usuariosPage);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody IniciarSesionDTO iniciarSesionDTO)
            throws UsuarioException, CredencialesInvalidasException {

        Map<String, Object> respuestaKeycloak = this.usuarioService.iniciarSesion(
                iniciarSesionDTO.getId(),
                iniciarSesionDTO.getContrasena()
        );

        String refreshToken = respuestaKeycloak.get("refresh_token").toString();

        Map<String, Object> bodyResponse = new HashMap<>(respuestaKeycloak);
        bodyResponse.remove("refresh_token");

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(2592000)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(bodyResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) throws UsuarioException {

        if (refreshToken != null) {
            this.usuarioService.cerrarSesion(refreshToken);
        }

        ResponseCookie cookieLimpiadora = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, String> body = new HashMap<>();
        body.put("message", "Sesión cerrada exitosamente");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieLimpiadora.toString())
                .body(body);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistrarUsuarioDTO registrarUsuarioDTO) throws UsuarioException, RegistrarUsuarioException {

        UsuarioDTO usuarioARegistrar = this.usuarioMapper.toDTO(registrarUsuarioDTO);

        UsuarioDTO registrado = this.usuarioService.registrar(usuarioARegistrar, registrarUsuarioDTO.getContrasena());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Te has registrado con éxito. Tu nuevo ID es '%s.'".formatted(registrado.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USUARIOS_DELETE')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) throws UsuarioException {
        this.usuarioService.eliminar(id);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Usuario con ID " + id + " eliminado correctamente del sistema.");

        return ResponseEntity.ok(body);
    }



    /////////////////////////// Manejo de Roles /////////////////////////////

    @GetMapping("/roles/disponibles")
    @PreAuthorize("hasRole('ROLES_READ')")
    public ResponseEntity<List<RolDTO>> obtenerRolesDisponibles() throws RolesException {
        List<RolDTO> rolesDisponiblesDTO = this.usuarioService.obtenerRolesDisponibles();
        return ResponseEntity.ok(rolesDisponiblesDTO);
    }

    @DeleteMapping("/{id}/roles")
    @PreAuthorize("hasRole('USUARIOS_UPDATE')")
    public ResponseEntity<?> removerRolesUsuario(
            @PathVariable("id") Long id, @Valid @RequestBody RemoverRolesDTO rolesDTO
    ) throws UsuarioNoEncontradoException, RolesException
    {
        this.usuarioService.removerRolesUsuario(id, rolesDTO.getRoles());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Se removieron los roles correctamente.");

        return ResponseEntity.ok(body);
    }


    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USUARIOS_READ_ALL')")
    public ResponseEntity<Page<InformacionBasicaUsuarioDTO>> obtenerUsuariosPaginadosPorBusqueda(
            @PageableDefault(page = 0, size = 10, sort = "nombre") Pageable pageable,
            @RequestParam(name = "credenciales", required = false, defaultValue = "") String credenciales
    ) {
        Page<InformacionBasicaUsuarioDTO> pagina = this.usuarioService.obtenerUsuariosPorCredenciales(credenciales, pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('USUARIOS_READ_ALL')")
    public ResponseEntity<List<RolDTO>> obtenerRolesUsuario(@PathVariable("id") Long usuarioId) throws RolesException, UsuarioNoEncontradoException {
        List<RolDTO> rolesUsuarioDTO = this.usuarioService.obtenerRolesUsuario(usuarioId);
        return ResponseEntity.ok(rolesUsuarioDTO);
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('USUARIOS_UPDATE')")
    public ResponseEntity<?> asignarRolesUsuario(@PathVariable("id") Long usuarioId, @Valid @RequestBody AsignarRolesDTO rolesDTO) throws UsuarioNoEncontradoException, RolesException {
        this.usuarioService.asignarRolesUsuario(usuarioId, rolesDTO.getRoles());

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Se asignaron los roles correctamente.");

        return ResponseEntity.ok(body);
    }

    /////////////////////////// Manejo de Permisos /////////////////////////////
    @GetMapping("/permisos/disponibles")
    @PreAuthorize("hasRole('PERMISOS_READ')")
    public ResponseEntity<List<PermisoDTO>> obtenerPermisosDisponibles() throws PermisosException {
        List<PermisoDTO> permisosDisponiblesDTO = this.usuarioService.obtenerPermisosDisponibles();
        return ResponseEntity.ok(permisosDisponiblesDTO);
    }

    @GetMapping("/{id}/permisos")
    @PreAuthorize("hasRole('USUARIOS_READ_ALL')")
    public ResponseEntity<List<PermisoDTO>> obtenerPermisosUsuario(@PathVariable("id") Long usuarioId)
            throws UsuarioNoEncontradoException, PermisosException
    {
        List<PermisoDTO> permisosUsuarioDTO = this.usuarioService.obtenerPermisosUsuario(usuarioId);
        return ResponseEntity.ok(permisosUsuarioDTO);
    }

    @DeleteMapping("/{id}/permisos")
    @PreAuthorize("hasRole('USUARIOS_UPDATE')")
    public ResponseEntity<?> removerPermisosUsuario(
            @PathVariable("id") Long id, @Valid @RequestBody RemoverPermisosDTO permisosDTO
    ) throws UsuarioNoEncontradoException, PermisosException {
        this.usuarioService.removerPermisosUsuario(id, permisosDTO.getPermisos());

        Map<String, String> body = new HashMap<>();
        body.put("message", "Se removieron los permisos correctamente.");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/permisos")
    @PreAuthorize("hasRole('USUARIOS_UPDATE')")
    public ResponseEntity<?> asignarPermisosUsuario(
            @PathVariable("id") Long usuarioId,
            @Valid @RequestBody AsignarPermisosDTO permisosDTO
    ) throws UsuarioNoEncontradoException, PermisosException
    {
        this.usuarioService.asignarPermisosUsuario(usuarioId, permisosDTO.getPermisos());

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Se asignaron los permisos correctamente.");

        return ResponseEntity.ok(body);
    }

    /////////////////////////// Verificar si la sesion sigue activa ///////////////////////////
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(@AuthenticationPrincipal Jwt jwt) throws UsuarioException {

        String token = jwt.getTokenValue();

        boolean sesionActiva = this.usuarioService.tieneSesionActiva(token);

        if (!sesionActiva) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "La sesión ha sido invalidada"));
        }

        Map<String, String> body = new HashMap<>();
        body.put("message", "Sesión activa.");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/refresh-session")
    public ResponseEntity<Map<String, Object>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) throws UsuarioException {

        if (refreshToken == null) {
            throw new UsuarioException("No hay sesión activa o el token de refresco ha expirado.");
        }

        Map<String, Object> nuevaSesion = this.usuarioService.refrescarToken(refreshToken);

        String nuevoRefreshToken = nuevaSesion.get("refresh_token").toString();

        Map<String, Object> bodyResponse = new HashMap<>(nuevaSesion);
        bodyResponse.remove("refresh_token");

        ResponseCookie cookie = ResponseCookie.from("refresh_token", nuevoRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(2592000)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(bodyResponse);
    }
}
