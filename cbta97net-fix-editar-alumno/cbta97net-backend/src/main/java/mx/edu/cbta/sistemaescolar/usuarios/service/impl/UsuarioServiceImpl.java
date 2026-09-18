package mx.edu.cbta.sistemaescolar.usuarios.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import mx.edu.cbta.sistemaescolar.usuarios.dto.*;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.*;

import mx.edu.cbta.sistemaescolar.usuarios.mapper.UsuarioMapper;
import mx.edu.cbta.sistemaescolar.usuarios.repository.UsuarioRepository;
import mx.edu.cbta.sistemaescolar.usuarios.service.KeycloakService;
import mx.edu.cbta.sistemaescolar.usuarios.service.UsuarioService;
import mx.edu.cbta.sistemaescolar.usuarios.domain.model.Usuario;

import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private KeycloakService keycloakService;

    private static final List<String> ROLES_RESERVADOS = List.of(
            "offline_access",
            "uma_authorization",
            "default-roles-cbta97-realm-prod"
    );

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, KeycloakService keycloakService) {
        this.usuarioRepository = usuarioRepository;
        this.keycloakService = keycloakService;
    }

    @Override
    public Map<String, Object> iniciarSesion(Long idUsuario, String contrasena) throws UsuarioException, CredencialesInvalidasException {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado."));

        if (!usuario.isActivo()) {
            throw new UsuarioException("La cuenta de usuario está inactiva.");
        }

        try {
            Map<String, Object> tokenDataInmutable = this.keycloakService.login(idUsuario.toString(), contrasena);

            Map<String, Object> tokenData = new HashMap<>(tokenDataInmutable);

            List<RolDTO> roles = this.obtenerRolesUsuario(idUsuario);
            List<String> nombreRoles = roles.stream().map(RolDTO::getNombre).toList();

            tokenData.put("roles", nombreRoles);
            tokenData.put("usuario_id", usuario.getId());
            tokenData.put("usuario_nombre", usuario.getNombreCompleto());

            return tokenData;
        } catch (jakarta.ws.rs.NotAuthorizedException e) {
            log.warn("Credenciales inválidas para usuario {}: {}", idUsuario, e.getMessage());
            throw new CredencialesInvalidasException("ID de Usuario o contraseña incorrectos.");
        } catch (RuntimeException e) {
            log.error("Error inesperado al autenticar en Keycloak: {}", e.getMessage(), e);
            throw new UsuarioException("Error al conectarse con el servidor de autenticación.");
        } catch (Exception e) {
            throw new UsuarioException(e.getMessage());
        }
    }

    @Override
    public void cerrarSesion(String refreshToken) throws UsuarioException {
        try {
            this.keycloakService.logout(refreshToken);
        } catch (Exception e) {
            throw new UsuarioException(e.getMessage());
        }
    }

    @Override
    public boolean tieneSesionActiva(String token) throws UsuarioException {

        if (token == null) {
            throw new UsuarioException("El token del usuario es null.");
        }

        return this.keycloakService.isTokenActive(token);
    }

    @Override
    public Page<UsuarioDTO> obtenerUsuariosTodos(Pageable pageable) {
        Page<Usuario> usuarios = this.usuarioRepository.findAll(pageable);
        return usuarios.map(this.usuarioMapper::toDTO);
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long usuarioId) throws UsuarioNoEncontradoException {
        UsuarioDTO usuarioDTO = this.usuarioRepository.findById(usuarioId)
                .map(this.usuarioMapper::toDTO)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró al usuario con ID: %s".formatted(usuarioId.toString())));

        List<RolDTO> roles;
        List<PermisoDTO> permisos;

        try {
            roles = this.obtenerRolesUsuario(usuarioDTO.getId());
            usuarioDTO.setRoles(new HashSet<>(roles));
        } catch (RolesException e) {
            log.error(e.getMessage(), e);
        }

        try {
            permisos = this.obtenerPermisosUsuario(usuarioDTO.getId());
            usuarioDTO.setPermisos(new HashSet<>(permisos));
        } catch (PermisosException e) {
            log.error(e.getMessage(), e);
        }

        return usuarioDTO;
    }

    @Override
    public boolean usuarioExiste(Long usuarioId) {
        return this.usuarioRepository.existsById(usuarioId);
    }

    @Override
    public String obtenerNombre(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getNombre)
                .orElse("Nombre no encontrado");
    }

    @Override
    public String obtenerApellidoPaterno(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getApellidoPaterno)
                .orElse("Apellido paterno no encontrado");
    }

    @Override
    public String obtenerApellidoMaterno(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getApellidoMaterno)
                .orElse("Apellido materno no encontrado");
    }

    @Override
    public String obtenerCorreoElectronico(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(Usuario::getEmail)
                .orElse("Correo electrónico no encontrado");
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


    @Transactional(rollbackFor = Exception.class)
    @Override
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO, String contrasena) throws UsuarioException, RegistrarUsuarioException {

        // 1. validaciones previas
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RegistrarUsuarioException("El correo electrónico ya está registrado.");
        }

        if (usuarioDTO.getTelefono() != null && usuarioRepository.existsByTelefono(usuarioDTO.getTelefono())) {
            throw new RegistrarUsuarioException("El número de teléfono ya está registrado por otro usuario.");
        }

        if (usuarioDTO.getCurp() != null && usuarioRepository.existsByCurp(usuarioDTO.getCurp())) {
            throw new RegistrarUsuarioException("La CURP dada ya está ligada a otro usuario en el sistema.");
        }

        String usernameKeycloak = null;

        try {
            usuarioDTO.setNombre(normalizarTexto(usuarioDTO.getNombre()));
            usuarioDTO.setApellidoPaterno(normalizarTexto(usuarioDTO.getApellidoPaterno()));
            usuarioDTO.setApellidoMaterno(normalizarTexto(usuarioDTO.getApellidoMaterno()));

            Usuario entidadUsuario = this.usuarioMapper.toEntity(usuarioDTO);
            Usuario usuarioGuardado = usuarioRepository.saveAndFlush(entidadUsuario);

            usernameKeycloak = usuarioGuardado.getId().toString();

            KeycloakUserDTO keycloakUser = KeycloakUserDTO.builder()
                    .username(usernameKeycloak)
                    .email(usuarioGuardado.getEmail())
                    .firstName(usuarioGuardado.getNombre())
                    .lastName(usuarioGuardado.getApellidoPaterno() + " " + usuarioGuardado.getApellidoMaterno())
                    .password(contrasena)
                    .roles(usuarioGuardado.getRoles())
                    .build();

            String resultadoKeycloak = keycloakService.createUser(keycloakUser);

            if (!"User created successfully!!".equals(resultadoKeycloak)) {
                throw new RegistrarUsuarioException(resultadoKeycloak);
            }

            try {
                this.asignarRolesUsuario(usuarioGuardado.getId(), List.of("USUARIO_BASE"));
                this.asignarRolesUsuario(usuarioGuardado.getId(), List.of("ADMIN"));
            } catch (Exception e) {
                log.error("Fallo en asignación de roles post-registro. Ejecutando acción compensatoria en Keycloak...");
                ___eliminarUsuarioDeKeycloakSeguro(usernameKeycloak);
                throw e;
            }

            return this.usuarioMapper.toDTO(usuarioGuardado);

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Error de integridad de datos duplicados (Captura de última instancia): {}", e.getMessage());
            throw new RegistrarUsuarioException("No se pudo registrar: Ya existen registros con estos datos de contacto.");
        } catch (Exception e) {
            log.error("Fallo crítico en el flujo de registro: {}", e.getMessage());
            if (usernameKeycloak != null) {
                log.error("Ejecutando acción compensatoria global en Keycloak...");
                ___eliminarUsuarioDeKeycloakSeguro(usernameKeycloak);
            }

            throw new RegistrarUsuarioException(e.getMessage());
        }
    }

    /**
     * Método auxiliar para ejecutar la acción compensatoria de forma segura sin romper el flujo del Rollback
     */
    private void ___eliminarUsuarioDeKeycloakSeguro(String username) {
        try {
            keycloakService.deleteUser(username);
            log.info("Acción compensatoria exitosa: Usuario {} eliminado de Keycloak.", username);
        } catch (Exception ex) {
            log.error("¡ERROR CRÍTICO EN SAGA! No se pudo eliminar al usuario {} de Keycloak durante la compensación: {}",
                    username, ex.getMessage());
        }
    }

    /*
    @Override
    public void modificar(Long idUsuario, UsuarioDTO usuarioDetails) throws UsuarioException {
        Usuario usuarioExistente = usuarioRepository.findById(usuarioDetails.getId())
                .orElseThrow(() -> new UsuarioException("No se encontró el usuario con ID: " + usuarioDetails.getId()));

        // Actualizar campos
        usuarioExistente.setNombre(usuarioDetails.getNombre());
        usuarioExistente.setApellidoPaterno(usuarioDetails.getApellidoPaterno());
        usuarioExistente.setApellidoMaterno(usuarioDetails.getApellidoMaterno());
        usuarioExistente.setCurp(usuarioDetails.getCurp());
        usuarioExistente.setTelefono(usuarioDetails.getTelefono());
        //usuarioExistente.setRoles(usuarioDetails.getRoles());
        usuarioExistente.setActivo(usuarioDetails.isActivo());

        usuarioRepository.save(usuarioExistente);
    }*/


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modificar(Long idUsuario, UsuarioDTO usuarioDetails) throws UsuarioException {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioException("No se encontró el usuario con ID: " + idUsuario));

        if (!usuarioExistente.getEmail().equalsIgnoreCase(usuarioDetails.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDetails.getEmail())) {
                throw new UsuarioException("El nuevo correo electrónico ya está registrado por otro usuario.");
            }
        }

        try {
            String nombreNorm = normalizarTexto(usuarioDetails.getNombre());
            String apellidoPNorm = normalizarTexto(usuarioDetails.getApellidoPaterno());
            String apellidoMNorm = normalizarTexto(usuarioDetails.getApellidoMaterno());

            usuarioExistente.setNombre(nombreNorm);
            usuarioExistente.setApellidoPaterno(apellidoPNorm);
            usuarioExistente.setApellidoMaterno(apellidoMNorm);
            usuarioExistente.setEmail(usuarioDetails.getEmail());
            usuarioExistente.setCurp(usuarioDetails.getCurp());
            usuarioExistente.setTelefono(usuarioDetails.getTelefono());
            usuarioExistente.setActivo(usuarioDetails.isActivo());

            usuarioRepository.save(usuarioExistente);

            KeycloakUserDTO keycloakUpdate = KeycloakUserDTO.builder()
                    .username(usuarioExistente.getId().toString())
                    .email(usuarioExistente.getEmail())
                    .firstName(nombreNorm)
                    .lastName(apellidoPNorm + " " + apellidoMNorm)
                    .build();

            if (usuarioDetails.getContrasena() != null && !usuarioDetails.getContrasena().trim().isEmpty()) {
                System.out.println("contrasena cargada: " + usuarioDetails.getContrasena().trim());
                keycloakUpdate.setPassword(usuarioDetails.getContrasena().trim());
            }

            var usersFound = keycloakService.searchUserByUsername(usuarioExistente.getId().toString());
            if (usersFound.isEmpty()) {
                throw new UsuarioException("El usuario existe en BD pero no se encontró en el servidor de identidad.");
            }

            String keycloakUuid = usersFound.get(0).getId();
            keycloakService.updateUser(keycloakUuid, keycloakUpdate);

            log.info("Usuario ID: {} actualizado exitosamente en BD y Keycloak", idUsuario);

        } catch (Exception e) {
            log.error("Error al actualizar usuario {}: {}", idUsuario, e.getMessage());
            throw new UsuarioException("Error al sincronizar la actualización: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void eliminar(Long idUsuario) throws UsuarioException {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioException("No se encontró el usuario con ID: " + idUsuario);
        }

        try {
            this.keycloakService.deleteUser(idUsuario.toString());
        } catch (RuntimeException ex) {
            throw new UsuarioException("No se pudo eliminar el usuario debido a un error, intente de nuevo más tarde.");
        }

        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    public List<RolDTO> obtenerRolesDisponibles() throws RolesException {

        try {
            return this.keycloakService.getRealmRoles()
                    .stream()
                    .filter(r -> !ROLES_RESERVADOS.contains(r.getName()))
                    .filter(r -> r.getName().equals(r.getName().toUpperCase()))
                    .map(r -> new RolDTO(r.getId(), r.getName(), r.getDescription()))
                    .sorted(Comparator.comparing(RolDTO::getNombre))
                    .toList();
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex.getSuppressed());
            throw new RolesException("No se pudo obtener los roles disponibles del sistema. Intente de nuevo más tarde.");
        }
    }

    @Override
    public List<PermisoDTO> obtenerPermisosDisponibles() throws PermisosException {
        try {
            return this.keycloakService.getClientRoles()
                    .stream()
                    .map(r -> new PermisoDTO(r.getId(), r.getName(), r.getDescription()))
                    .sorted(Comparator.comparing(PermisoDTO::getNombre))
                    .toList();
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex.getSuppressed());
            throw new PermisosException("No se pudo obtener los permisos disponibles del sistema. Intente de nuevo más tarde.");
        }
    }

    @Transactional
    @Override
    public List<RolDTO> obtenerRolesUsuario(Long usuarioId) throws RolesException, UsuarioNoEncontradoException {

        if (!this.usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException("No se encontró al usuario con ID: " + usuarioId);
        }

        try {
            List<RoleRepresentation> rolesAsignados = this.keycloakService.getUserRealmRoles(usuarioId.toString());

            return rolesAsignados.stream()
                    .filter(r -> !ROLES_RESERVADOS.contains(r.getName()))
                    .filter(r -> r.getName().equals(r.getName().toUpperCase()))
                    .map(r -> new RolDTO(r.getId(), r.getName(), r.getDescription()))
                    .sorted(Comparator.comparing(RolDTO::getNombre))
                    .toList();

        } catch (RuntimeException ex) {
            log.error("Error al obtener roles del usuario {}: {}", usuarioId, ex.getMessage());
            throw new RolesException("No se pudo obtener los roles de usuario.");
        }
        catch (Exception ex) {
            log.error("Error al obtener roles del usuario {}: {}", usuarioId, ex.getMessage());
            throw new RolesException("Error al consultar los roles del usuario.");
        }
    }

    @Override
    public List<PermisoDTO> obtenerPermisosUsuario(Long usuarioId) throws PermisosException, UsuarioNoEncontradoException {
        if (!this.usuarioRepository.existsById(usuarioId)) {
            throw new UsuarioNoEncontradoException("No se encontró al usuario con ID: " + usuarioId);
        }

        try {
            List<RoleRepresentation> rolesAsignados = this.keycloakService.getUserClientRoles(usuarioId.toString());

            return rolesAsignados.stream()
                    .filter(r -> !ROLES_RESERVADOS.contains(r.getName()))
                    .filter(r -> r.getName().equals(r.getName().toUpperCase()))
                    .map(r -> new PermisoDTO(r.getId(), r.getName(), r.getDescription()))
                    .sorted(Comparator.comparing(PermisoDTO::getNombre))
                    .toList();

        } catch (RuntimeException ex) {
            log.error("Error al obtener roles del usuario {}: {}", usuarioId, ex.getMessage());
            throw new PermisosException("No se pudo obtener los permisos del usuario.");
        }
        catch (Exception ex) {
            log.error("Error al obtener roles del usuario {}: {}", usuarioId, ex.getMessage());
            throw new PermisosException("Error al consultar los permisos del usuario.");
        }
    }

    @Override
    public void asignarRolesUsuario(Long usuarioId, List<String> listaRoles) throws RolesException, UsuarioNoEncontradoException {
        try {

            if (usuarioId == null) {
                throw new RolesException("El ID de usuario no puede ser nulo.");
            }

            if (usuarioId <= 0) {
                throw new RolesException("El ID de usuario no puede ser negativo.");
            }

            boolean existe = this.usuarioRepository.existsById(usuarioId);
            if (!existe) {
                throw new UsuarioNoEncontradoException("No se encontró al usuario con ID: %s".formatted(usuarioId.toString()));
            }

            this.keycloakService.assignRoles(usuarioId.toString(), listaRoles);
        }
        catch (NotFoundException ex) {
            log.error(ex.getMessage(), ex);
            throw new RolesException("Ocurrió un error al asignar los permisos debido a que uno no existe. Intenta de nuevo.");
        }
        catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            throw new RolesException("No se pudo asignar los roles de usuario debido a un error. Intente de nuevo más tarde.");
        }
    }

    @Override
    public void asignarPermisosUsuario(Long usuarioId, List<String> listaPermisos) throws PermisosException, UsuarioNoEncontradoException {
        try {

            if (usuarioId == null) {
                throw new PermisosException("El ID de usuario no puede ser nulo.");
            }

            if (usuarioId <= 0) {
                throw new PermisosException("El ID de usuario no puede ser negativo.");
            }

            boolean existe = this.usuarioRepository.existsById(usuarioId);
            if (!existe) {
                throw new UsuarioNoEncontradoException("No se encontró al usuario con ID: %s".formatted(usuarioId.toString()));
            }

            this.keycloakService.assignPermissions(usuarioId.toString(), listaPermisos);
        }
        catch (NotFoundException ex) {
            log.error(ex.getMessage(), ex);
            throw new PermisosException("Ocurrió un error al asignar los permisos debido a que uno no existe. Intenta de nuevo.");
        }
        catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            throw new PermisosException("No se pudo asignar los roles de usuario debido a un error. Intente de nuevo más tarde.");
        }
    }

    @Override
    public void removerRolesUsuario(Long usuarioId, List<String> listaRoles) throws RolesException, UsuarioNoEncontradoException {
        try {
            if (usuarioId == null || usuarioId <= 0) {
                throw new RolesException("El ID de usuario no es válido.");
            }

            if (!this.usuarioRepository.existsById(usuarioId)) {
                throw new UsuarioNoEncontradoException("No se encontró al usuario con ID: %s".formatted(usuarioId));
            }

            this.keycloakService.unassignRoles(usuarioId.toString(), listaRoles);

            log.info("Roles eliminados correctamente para el usuario ID: {}", usuarioId);

        } catch (NotFoundException ex) {
            log.error("Error: Uno de los roles proporcionados no existe en Keycloak: {}", ex.getMessage());
            throw new RolesException("Ocurrió un error al remover los roles porque uno de ellos no existe.");
        } catch (RuntimeException ex) {
            log.error("Error al remover roles del usuario {}: {}", usuarioId, ex.getMessage());
            throw new RolesException("No se pudieron quitar los roles de usuario. Intente de nuevo más tarde.");
        }
    }

    @Override
    public void removerPermisosUsuario(Long usuarioId, List<String> listaPermisos) throws PermisosException, UsuarioNoEncontradoException {
        try {
            if (usuarioId == null || usuarioId <= 0) {
                throw new PermisosException("El ID de usuario no es válido.");
            }

            if (!this.usuarioRepository.existsById(usuarioId)) {
                throw new UsuarioNoEncontradoException("No se encontró al usuario con ID: %s".formatted(usuarioId));
            }

            this.keycloakService.unassignPermissions(usuarioId.toString(), listaPermisos);

            log.info("Permisos eliminados correctamente para el usuario ID: {}", usuarioId);

        } catch (NotFoundException ex) {
            log.error("Error: Uno de los permisos proporcionados no existe en el cliente: {}", ex.getMessage());
            throw new PermisosException("Ocurrió un error al remover los permisos porque uno de ellos no existe.");
        } catch (RuntimeException ex) {
            log.error("Error al remover permisos del usuario {}: {}", usuarioId, ex.getMessage());
            throw new PermisosException("No se pudieron quitar los permisos de usuario. Intente de nuevo más tarde.");
        }
    }

    @Override
    public Page<InformacionBasicaUsuarioDTO> obtenerUsuariosPorCredenciales(String credenciales, Pageable pageable) {
        Long idLong = -1L;

        try {
            idLong = Long.parseLong(credenciales);
        } catch (NumberFormatException e) {
        }

        return this.usuarioRepository.obtenerPorCredenciales(credenciales, idLong, pageable);
    }

    @Override
    public Map<String, Object> refrescarToken(String refreshToken) throws UsuarioException {
        try {
            return this.keycloakService.refreshToken(refreshToken);
        } catch (Exception e) {
            throw new UsuarioException(e.getMessage());
        }
    }
}
