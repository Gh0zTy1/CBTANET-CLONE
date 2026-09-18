package mx.edu.cbta.sistemaescolar.usuarios.service;

import mx.edu.cbta.sistemaescolar.usuarios.dto.KeycloakUserDTO;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface KeycloakService {
    Map<String, Object> login(String username, String password) throws Exception;
    List<UserRepresentation> findAllUsers();
    List<UserRepresentation> searchUserByUsername(String username);
    String createUser(KeycloakUserDTO userDTO);
    void deleteUser(String userId);
    void updateUser(String userId, KeycloakUserDTO userDTO);

    List<RoleRepresentation> getRealmRoles() throws RuntimeException;
    List<RoleRepresentation> getClientRoles() throws RuntimeException;

    List<RoleRepresentation> getUserRealmRoles(String userId);
    List<RoleRepresentation> getUserClientRoles(String userId);

    void assignRoles(String userId, List<String> roles) throws RuntimeException;
    void assignPermissions(String userId, List<String> permissions) throws RuntimeException;

    void unassignRoles(String userId, List<String> roles) throws RuntimeException;
    void unassignPermissions(String userId, List<String> roles) throws RuntimeException;

    void logout(String refreshToken) throws Exception;

    boolean isTokenActive(String accessToken);

    Map<String, Object> refreshToken(String refreshToken) throws Exception;
}
