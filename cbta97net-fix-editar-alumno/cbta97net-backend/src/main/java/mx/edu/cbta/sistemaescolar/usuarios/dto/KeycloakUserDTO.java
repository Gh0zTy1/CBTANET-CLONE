package mx.edu.cbta.sistemaescolar.usuarios.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "KeycloakUserBuilderDTO")
public class KeycloakUserDTO implements Serializable {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Set<String> roles;
}
