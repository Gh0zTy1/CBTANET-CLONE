package mx.edu.cbta.sistemaescolar.estructura.domain.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@Entity
@Table(name = "ciclos_escolares")
public class CicloEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-yyyy", new Locale("es", "MX"));
        String inicio = fechaInicio.format(formatter);
        String fin = fechaFin.format(formatter);

        return String.format("Ciclo %s, %s", inicio, fin);
    }
}