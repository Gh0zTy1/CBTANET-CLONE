package mx.edu.cbta.sistemaescolar.config.provider;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Slf4j
public class KeycloakProvider {

    @PostConstruct
    public void logConfig() {
        log.info("=== KeycloakProvider Config ===");
        log.info("Server URL: {}", serverUrl);
        log.info("Realm Name: {}", realmName);
        log.info("Client ID: {}", clientId);
        log.info("Realm Master: {}", realmMaster);
        log.info("Admin CLI Client ID: {}", adminCli);
        log.info("User Console: {}", userConsole);
        log.info("Password Console: {}", passwordConsole);
        log.info("Client Secret Admin: {}", clientSecretAdmin);
        //log.info("Password Console: {}", passwordConsole != null ? "[CONFIGURED]" : "[NOT CONFIGURED]");
        //log.info("Client Secret Admin: {}", clientSecretAdmin != null ? "[CONFIGURED]" : "[NOT CONFIGURED]");
        log.info("===============================");
    }

    @Value("${keycloak.server-url:http://localhost:9090}")
    private String serverUrl;

    @Value("${keycloak.realm:cbta97-realm-prod}")
    private String realmName;

    @Value("${keycloak.client-id:cbta97-client-api-rest}")
    private String clientId;

    @Value("${keycloak.client-secret:semilla_secreta}")
    private String clientSecret;

    @Value("${keycloak.realm-master:master}")
    private String realmMaster;

    @Value("${keycloak.admin-cli}")
    private String adminCli;

    @Value("${keycloak.user-console}")
    private String userConsole;

    @Value("${keycloak.password-console}")
    private String passwordConsole;

    @Value("${keycloak.client-secret-admin}")
    private String clientSecretAdmin;


    public RealmResource getRealmResource() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(this.serverUrl)
                .realm(this.realmMaster)
                .clientId(this.adminCli)
                .username(this.userConsole)
                .password(this.passwordConsole)
                .clientSecret(this.clientSecretAdmin)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();

        return keycloak.realm(this.realmName);
    }

    public UsersResource getUserResource() {
        RealmResource realmResource = this.getRealmResource();
        return realmResource.users();
    }

}