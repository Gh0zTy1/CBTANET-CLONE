package mx.edu.cbta.sistemaescolar.alumnado.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GuardarDocumentoAlumnoDTO {

    @NotNull(message = "El documento de acta de nacimiento es obligatorio.")
    private MultipartFile documento;

}
