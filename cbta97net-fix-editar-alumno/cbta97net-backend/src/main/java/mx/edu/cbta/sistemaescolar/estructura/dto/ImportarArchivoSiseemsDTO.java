package mx.edu.cbta.sistemaescolar.estructura.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportarArchivoSiseemsDTO {

    @NotNull(message = "El documento de importación es obligatorio.")
    private MultipartFile documento;
}
