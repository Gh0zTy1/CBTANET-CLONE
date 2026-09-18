package mx.edu.cbta.sistemaescolar.arquitectura;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "mx.edu.cbta.sistemaescolar")
public class ReglasDeCapasTest {

    /**
     * Verifica y garantiza la integridad de la arquitectura de capas del sistema.
     * <p>
     * Esta regla impone una comunicación unidireccional y jerárquica para evitar acoplamientos indeseados:
     * <ul>
     * <li><b>Controladores:</b> Actúan como punto de entrada y no pueden ser consumidos por otras capas internas.</li>
     * <li><b>Servicios:</b> Contienen la lógica de negocio y solo son accesibles desde los Controladores o Mappers.</li>
     * <li><b>Persistencia:</b> Gestiona el acceso a datos y su uso queda restringido exclusivamente a la capa de Servicios y Mappers
     * (para facilitar la resolución de entidades durante la conversión de datos).</li>
     * </ul>
     * * @see <a href="https://www.archunit.org/userguide/html/000_Index.html#_layered_architecture">ArchUnit Layered Architecture</a>
     */
    @ArchTest
    static final ArchRule integridad_de_capas = layeredArchitecture()
            .consideringAllDependencies()
            // definicion de capas
            .layer("Controladores").definedBy("..controller..")
            .layer("Mappers").definedBy("..mapper..")
            .layer("Servicios").definedBy("..service..")
            .layer("Persistencia").definedBy("..repository..")
            // definicion de accesos
            .whereLayer("Controladores").mayNotBeAccessedByAnyLayer()
            .whereLayer("Mappers").mayOnlyBeAccessedByLayers("Servicios", "Controladores")
            .whereLayer("Servicios").mayOnlyBeAccessedByLayers("Controladores", "Mappers")
            .whereLayer("Persistencia").mayOnlyBeAccessedByLayers("Servicios", "Mappers");

}