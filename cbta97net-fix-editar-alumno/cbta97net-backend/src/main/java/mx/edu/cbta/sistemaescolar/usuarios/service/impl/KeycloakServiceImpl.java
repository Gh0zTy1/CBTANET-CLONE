package mx.edu.cbta.sistemaescolar.usuarios.service.impl;

import jakarta.ws.rs.NotAuthorizedException;
import mx.edu.cbta.sistemaescolar.config.provider.KeycloakProvider;
import mx.edu.cbta.sistemaescolar.usuarios.dto.KeycloakUserDTO;

import mx.edu.cbta.sistemaescolar.usuarios.service.KeycloakService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.UserResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import org.keycloak.OAuth2Constants;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private KeycloakProvider keycloakProvider;

    /**
     * Método para autenticar un usuario y obtener sus tokens
     * @param username El ID del usuario (o username)
     * @param password La contraseña
     * @return Map con el access_token y otros datos
     */
    @Override
    public Map<String, Object> login(String username, String password) throws Exception {

        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(this.keycloakProvider.getServerUrl())
                    .realm(this.keycloakProvider.getRealmName())
                    .clientId(this.keycloakProvider.getClientId())
                    .clientSecret(this.keycloakProvider.getClientSecret())
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(username)
                    .password(password)
                    .build();

            return Map.of(
                    "access_token", keycloak.tokenManager().getAccessToken().getToken(),
                    "refresh_token", keycloak.tokenManager().getAccessToken().getRefreshToken(),
                    "expires_in", keycloak.tokenManager().getAccessToken().getExpiresIn(),
                    "refresh_expires_in", keycloak.tokenManager().getAccessToken().getRefreshExpiresIn()
                    //"token_type", keycloak.tokenManager().getAccessToken().getTokenType()
            );
        } catch (NotAuthorizedException ex) {
            throw new Exception("ID de Usuario o contraseña incorrectos.");
        }
    }



    @Autowired
    public KeycloakServiceImpl(KeycloakProvider keycloakProvider){
        this.keycloakProvider = keycloakProvider;
    }

    /**
     * Metodo para listar todos los usuarios de Keycloak
     * @return List<UserRepresentation>
     */
    public List<UserRepresentation> findAllUsers(){
        return this.keycloakProvider.getRealmResource()
                .users()
                .list();
    }


    /**
     * Metodo para buscar un usuario por su username
     * @return List<UserRepresentation>
     */
    public List<UserRepresentation> searchUserByUsername(String username) {
        return this.keycloakProvider.getRealmResource()
                .users()
                .searchByUsername(username, true);
    }


    /**
     * Metodo para crear un usuario en keycloak
     * @return String
     */
    public String createUser(@NonNull KeycloakUserDTO userDTO) {
        int status = 0;
        UsersResource usersResource = this.keycloakProvider.getUserResource();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        Response response = usersResource.create(userRepresentation);
        status = response.getStatus();

        if (status == 201) {
            // ... (Tu lógica de éxito se mantiene igual)
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource = this.keycloakProvider.getRealmResource();
            List<RoleRepresentation> rolesRepresentation;

            if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
                rolesRepresentation = List.of(realmResource.roles().get("USUARIO_BASE").toRepresentation());
            } else {
                rolesRepresentation = realmResource.roles()
                        .list()
                        .stream()
                        .filter(role -> userDTO.getRoles()
                                .stream()
                                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                        .toList();
            }

            realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);
            return "User created successfully!!";

        } else {
            // AQUÍ ESTÁ EL CAMBIO CLAVE:
            // Keycloak suele devolver un JSON como {"errorMessage":"User already exists"}
            String errorMessage = "Error desconocido";

            if (response.hasEntity()) {
                try {
                    // Intentamos obtener el mensaje de error del cuerpo de la respuesta
                    Map<String, String> errorMap = response.readEntity(Map.class);
                    errorMessage = errorMap.getOrDefault("errorMessage", "Sin detalle de error");
                } catch (Exception e) {
                    // Si no es un Map, intentamos leerlo como String
                    errorMessage = response.readEntity(String.class);
                }
            }

            log.error("Error al crear usuario en Keycloak. Status: {}, Mensaje: {}", status, errorMessage);

            if (status == 409) {
                return "Conflicto: " + errorMessage; // Ejemplo: "User already exists"
            }

            return "Error de Keycloak (Status " + status + "): " + errorMessage;
        }
    }


    /**
     * Metodo para borrar un usuario en keycloak
     * @return void
     */
    public void deleteUser(String userId){
        List<UserRepresentation> usuariosEncontrados = this.keycloakProvider.getRealmResource()
                .users()
                .search(userId, true);

        if (usuariosEncontrados.isEmpty()) {
            throw new RuntimeException("Error: Usuario no existe en Keycloak con username: " + userId);
        }

        String keycloakUuid = usuariosEncontrados.get(0).getId();

        this.keycloakProvider.getRealmResource()
                .users()
                .get(keycloakUuid)
                .remove();
    }


    private UserResource getUserResourceByUsername(String username) {
        List<UserRepresentation> users = keycloakProvider.getRealmResource()
                .users()
                .searchByUsername(username, true);

        if (users.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado en Keycloak: " + username);
        }

        return keycloakProvider.getRealmResource()
                .users()
                .get(users.get(0).getId());
    }

    /*
    public void updateUser(String username, @NonNull KeycloakUserDTO userDTO) {

        UserResource userResource = this.getUserResourceByUsername(username);

        UserRepresentation user = userResource.toRepresentation();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(true);

        userResource.update(user);

        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userDTO.getPassword().trim());
            credential.setTemporary(false);

            userResource.resetPassword(credential);
            log.info("Contraseña actualizada para el usuario {}", username);
        }
    }*/

    @Override
    public void updateUser(String username, @NonNull KeycloakUserDTO userDTO) {
        try {
            UserResource userResource;
            UserRepresentation userRep;

            // 1. Localizar al usuario y obtener su UUID real
            if (username.contains("-")) {
                // Si tiene guiones, es el UUID directo de Keycloak
                userResource = this.keycloakProvider.getRealmResource().users().get(username);
                userRep = userResource.toRepresentation();
            } else {
                // Si es el ID de tu BD (ej: 100006), lo buscamos como username
                List<UserRepresentation> found = this.keycloakProvider.getRealmResource().users()
                        .searchByUsername(username, true);

                if (found.isEmpty()) {
                    log.error("No se encontró el usuario en Keycloak con username: {}", username);
                    throw new RuntimeException("Usuario no encontrado en el servidor de identidad.");
                }

                userRep = found.get(0);
                userResource = this.keycloakProvider.getRealmResource().users().get(userRep.getId());
            }

            // 2. Actualizar datos de perfil (Nombre y Email)
            userRep.setFirstName(userDTO.getFirstName());
            userRep.setLastName(userDTO.getLastName());
            userRep.setEmail(userDTO.getEmail());
            userResource.update(userRep);

            // 3. ACTUALIZAR CONTRASEÑA (Este es el paso que te faltaba)
            if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(userDTO.getPassword().trim());
                credential.setTemporary(false);

                userResource.resetPassword(credential);
                log.info("Contraseña actualizada exitosamente para: {}", username);
            }

        } catch (Exception e) {
            log.error("Error al actualizar usuario en Keycloak: {}", e.getMessage());
            throw new RuntimeException("Error de sincronización con Keycloak: " + e.getMessage());
        }
    }

    public List<RoleRepresentation> getRealmRoles() throws RuntimeException {
        return this.keycloakProvider.getRealmResource()
                .roles()
                .list();
    }

    @Override
    public List<RoleRepresentation> getClientRoles() throws RuntimeException {
        String clientIdName = this.keycloakProvider.getClientId();

        String clientUuid = this.keycloakProvider.getRealmResource()
                .clients()
                .findByClientId(clientIdName)
                .get(0)
                .getId();

        return this.keycloakProvider.getRealmResource()
                .clients()
                .get(clientUuid)
                .roles()
                .list();
    }

    @Override
    public void assignRoles(String userId, List<String> roles) throws RuntimeException {
        try {

            String username = userId.toString();

            RealmResource realmResource = this.keycloakProvider.getRealmResource();

            List<UserRepresentation> users = realmResource.users().searchByUsername(username, true);
            if (users.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado en Keycloak: " + username);
            }
            String keycloakUuid = users.get(0).getId();

            UserResource userResource = realmResource.users().get(keycloakUuid);

            List<RoleRepresentation> rolesToAdd = roles.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .toList();

            List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();
            userResource.roles().realmLevel().remove(currentRoles);

            userResource.roles().realmLevel().add(rolesToAdd);

            log.info("Roles de Realm asignados correctamente al usuario: {}", username);
        } catch (Exception e) {
            log.error("Error al asignar roles de Realm: {}", e.getMessage());
            throw new RuntimeException("No se pudieron asignar los roles del sistema.");
        }
    }

    @Override
    public void assignPermissions(String userId, List<String> permissions) throws RuntimeException {
        try {
            RealmResource realmResource = this.keycloakProvider.getRealmResource();

            String username = userId.toString();

            List<UserRepresentation> users = realmResource.users().searchByUsername(username, true);
            if (users.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado en Keycloak: " + username);
            }
            String userUuid = users.get(0).getId();

            String clientIdName = this.keycloakProvider.getClientId();
            String clientUuid = realmResource.clients()
                    .findByClientId(clientIdName)
                    .get(0)
                    .getId();

            UserResource userResource = realmResource.users().get(userUuid);

            List<RoleRepresentation> rolesToAdd = permissions.stream()
                    .map(roleName -> realmResource.clients().get(clientUuid).roles().get(roleName).toRepresentation())
                    .toList();

            List<RoleRepresentation> currentPermissions = userResource.roles()
                    .clientLevel(clientUuid)
                    .listAll();

            if (!currentPermissions.isEmpty()) {
                userResource.roles().clientLevel(clientUuid).remove(currentPermissions);
            }

            userResource.roles().clientLevel(clientUuid).add(rolesToAdd);

            log.info("Permisos de Cliente asignados correctamente al usuario: {}", username);
        } catch (Exception e) {
            log.error("Error al asignar permisos de cliente: {}", e.getMessage());
            throw new RuntimeException("No se pudieron asignar los permisos específicos de la aplicación.");
        }
    }

    @Override
    public void unassignRoles(String userId, List<String> roles) throws RuntimeException {
        try {
            RealmResource realmResource = this.keycloakProvider.getRealmResource();

            List<UserRepresentation> users = realmResource.users().searchByUsername(userId, true);
            if (users.isEmpty()) {
                throw new RuntimeException("No se encontró el usuario con el ID: %s".formatted(userId));
            }
            String userUuid = users.get(0).getId();
            UserResource userResource = realmResource.users().get(userUuid);

            List<RoleRepresentation> rolesToRemove = roles.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .toList();

            userResource.roles().realmLevel().remove(rolesToRemove);

            log.info("Roles de Realm desasignados correctamente del usuario: {}", userId);
        } catch (Exception e) {
            log.error("Error al desasignar roles de Realm: {}", e.getMessage());
            throw new RuntimeException("No se pudieron quitar los roles del sistema.");
        }
    }

    @Override
    public void unassignPermissions(String userId, List<String> permissions) throws RuntimeException {
        try {
            RealmResource realmResource = this.keycloakProvider.getRealmResource();

            List<UserRepresentation> users = realmResource.users().searchByUsername(userId, true);
            if (users.isEmpty()) {
                throw new RuntimeException("No se encontró el usuario con el ID: %s".formatted(userId));
            }
            String userUuid = users.get(0).getId();

            String clientIdName = this.keycloakProvider.getClientId();
            String clientUuid = realmResource.clients()
                    .findByClientId(clientIdName)
                    .get(0)
                    .getId();

            UserResource userResource = realmResource.users().get(userUuid);

            List<RoleRepresentation> rolesToRemove = permissions.stream()
                    .map(roleName -> realmResource.clients().get(clientUuid).roles().get(roleName).toRepresentation())
                    .toList();

            userResource.roles().clientLevel(clientUuid).remove(rolesToRemove);

            log.info("Permisos de Cliente desasignados correctamente del usuario: {}", userId);
        } catch (Exception e) {
            log.error("Error al desasignar permisos de cliente: {}", e.getMessage());
            throw new RuntimeException("No se pudieron quitar los permisos específicos de la aplicación.");
        }
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) throws Exception {
        try {
            String url = this.keycloakProvider.getServerUrl() + "/realms/" +
                    this.keycloakProvider.getRealmName() + "/protocol/openid-connect/token";

            // Preparamos los parámetros requeridos por OAuth2 para refresh_token
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", this.keycloakProvider.getClientId());
            params.add("client_secret", this.keycloakProvider.getClientSecret());
            params.add("grant_type", "refresh_token");
            params.add("refresh_token", refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            Map<String, Object> token = new HashMap<>(response.getBody());

            token.remove("token_type");
            token.remove("not-before-policy");
            token.remove("session_state");
            token.remove("scope");

            return token;
        } catch (Exception e) {
            log.error("Error al refrescar el token: {}", e.getMessage());
            throw new Exception("La sesión ha expirado por completo. Por favor, inicie sesión de nuevo.");
        }
    }

    @Override
    public List<RoleRepresentation> getUserRealmRoles(String userId) {
        List<UserRepresentation> users = this.keycloakProvider.getRealmResource()
                .users()
                .searchByUsername(userId, true);

        if (users.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado en Keycloak: " + userId);
        }

        String keycloakUuid = users.get(0).getId();

        return this.keycloakProvider.getRealmResource()
                .users()
                .get(keycloakUuid)
                .roles()
                .realmLevel()
                .listAll();
    }

    @Override
    public List<RoleRepresentation> getUserClientRoles(String userId) {
        List<UserRepresentation> users = this.keycloakProvider.getRealmResource()
                .users()
                .searchByUsername(userId, true);

        if (users.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado en Keycloak: " + userId);
        }

        String keycloakUuid = users.get(0).getId();

        String clientIdName = this.keycloakProvider.getClientId();
        String clientUuid = this.keycloakProvider.getRealmResource()
                .clients()
                .findByClientId(clientIdName)
                .get(0)
                .getId();

        return this.keycloakProvider.getRealmResource()
                .users()
                .get(keycloakUuid)
                .roles()
                .clientLevel(clientUuid)
                .listAll();
    }

    @Override
    public boolean isTokenActive(String accessToken) {
        try {
            String url = this.keycloakProvider.getServerUrl() + "/realms/" +
                    this.keycloakProvider.getRealmName() + "/protocol/openid-connect/token/introspect";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", this.keycloakProvider.getClientId());
            params.add("client_secret", this.keycloakProvider.getClientSecret());
            params.add("token", accessToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody() != null && (boolean) response.getBody().get("active");
        } catch (Exception e) {
            log.error("Error verificando validez del token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void logout(String refreshToken) throws Exception {
        try {
            String url = this.keycloakProvider.getServerUrl() + "/realms/" +
                    this.keycloakProvider.getRealmName() + "/protocol/openid-connect/logout";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", this.keycloakProvider.getClientId());
            params.add("client_secret", this.keycloakProvider.getClientSecret());
            params.add("refresh_token", refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();

            // Keycloak devuelve 204 No Content si el logout es exitoso
            restTemplate.postForEntity(url, request, String.class);

            log.info("Sesión cerrada correctamente en Keycloak para el token proporcionado.");
        } catch (Exception e) {
            log.error("Error al cerrar sesión en Keycloak: {}", e.getMessage());
            throw new Exception("No se pudo cerrar la sesión correctamente.");
        }
    }
}
