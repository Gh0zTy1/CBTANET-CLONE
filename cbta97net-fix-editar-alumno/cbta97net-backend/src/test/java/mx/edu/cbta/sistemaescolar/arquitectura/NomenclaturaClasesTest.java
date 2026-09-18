package mx.edu.cbta.sistemaescolar.arquitectura;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Table;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Pruebas arquitectónicas orientadas a validar la nomenclatura y
 * organización de clases dentro del sistema.
 * <p>
 * Esta clase utiliza ArchUnit para asegurar el cumplimiento de
 * convenciones arquitectónicas acordadas, promoviendo:
 * <ul>
 *   <li>Consistencia en la estructura del proyecto</li>
 *   <li>Claridad semántica en los roles de las clases</li>
 *   <li>Mantenibilidad y facilidad de comprensión del código</li>
 * </ul>
 * <p>
 * Las reglas aquí definidas forman parte de las decisiones
 * arquitectónicas del sistema y su incumplimiento indica una
 * desviación del diseño establecido.
 */
@AnalyzeClasses(packages = "mx.edu.cbta.sistemaescolar")
public class NomenclaturaClasesTest {

    /**
     * Regla arquitectónica que valida la correcta ubicación y nomenclatura
     * de las implementaciones de servicios del sistema.
     *
     * <p>Esta regla asegura que todas las clases anotadas con {@link Service}:</p>
     * <ul>
     *   <li>Residan exclusivamente en el subpaquete <code>service.impl</code></li>
     *   <li>Tengan un nombre de clase que termine con el sufijo <b>"ServiceImpl"</b></li>
     * </ul>
     *
     * <p>
     * La intención de esta convención es separar explícitamente los contratos
     * de servicio (interfaces) de sus implementaciones concretas, facilitando:
     * </p>
     * <ul>
     *   <li>La lectura y comprensión de la arquitectura</li>
     *   <li>El intercambio de implementaciones sin afectar a los consumidores</li>
     *   <li>El cumplimiento de principios como DIP y separación de responsabilidades</li>
     * </ul>
     *
     * <p>
     * Además, restringir el uso de {@link Service} a implementaciones evita
     * anotaciones incorrectas en interfaces o capas no destinadas a contener
     * lógica de negocio.
     * </p>
     */
    @ArchTest
    static final ArchRule nomenclaturaServicesImplementaciones = classes()
            .that().areAnnotatedWith(Service.class)
            .should().resideInAnyPackage("..service.impl..")
            .andShould().haveSimpleNameEndingWith("ServiceImpl")
            .as(
                    "Las clases de implementación de interfaces de tipo '@Service' " +
                    "deben alojarse en el paquete 'service.impl' del modulo."
            );

    /**
     * Regla arquitectónica que valida la correcta definición de los contratos
     * de servicio del sistema.
     *
     * <p>Esta regla asegura que todas las interfaces que representan servicios:</p>
     * <ul>
     *   <li>Sean declaradas explícitamente como interfaces</li>
     *   <li>Tengan un nombre que termine con el sufijo <b>"Service"</b></li>
     *   <li>Residan en el paquete dedicado <code>service</code></li>
     * </ul>
     *
     * <p>
     * Estas interfaces representan los contratos de la capa de negocio y no deben
     * contener detalles de implementación ni dependencias técnicas.
     * </p>
     *
     * <p>
     * Mantener esta convención permite:
     * </p>
     * <ul>
     *   <li>Identificar claramente los puntos de entrada a la lógica de negocio</li>
     *   <li>Reducir el acoplamiento entre capas</li>
     *   <li>Facilitar pruebas, sustitución de implementaciones y evolución del sistema</li>
     * </ul>
     */
    @ArchTest
    static final ArchRule nomenclaturaServicesInterfaces = classes()
            .that().areInterfaces()
            .and().haveSimpleNameEndingWith("Service")
            .should().resideInAnyPackage("..service..")
            .as(
                    "Las interfaces para definición de clases tipo '@Service' " +
                    "deben alojarse en la raíz del paquete 'service' del módulo."
            );


    /**
     * Regla arquitectónica que asegura que todas las clases anotadas con
     * {@link Repository}:
     * <ul>
     *   <li>Se encuentren dentro del paquete de repositorios</li>
     *   <li>Finalicen su nombre con el sufijo "Repository"</li>
     * </ul>
     * <p>
     * Esto refuerza la separación de responsabilidades entre la capa
     * de persistencia y el resto del sistema.
     */
    @ArchTest
    static final ArchRule nomenclaturaRepositories = classes()
            .that().areInterfaces().and().areAnnotatedWith(Repository.class)
            .should().resideInAnyPackage("..repository..")
            .andShould().haveSimpleNameEndingWith("Repository")
            .as("Las interfaces de tipo '@Repository' deben alojarse en el paquete 'repository'.");

    /**
     * Regla arquitectónica para los controladores REST.
     * <p>
     * Toda clase anotada con {@link RestController} debe:
     * <ul>
     *   <li>Residir en un paquete "controller"</li>
     *   <li>Estar anotada explícitamente con {@link RequestMapping}</li>
     *   <li>Tener un nombre que termine con el sufijo "Controller"</li>
     * </ul>
     * <p>
     * Esto garantiza uniformidad en la exposición de endpoints y
     * facilita la navegación y comprensión de la capa de presentación.
     */
    @ArchTest
    static final ArchRule nomenclaturaControladores = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAnyPackage("..controller..")
            .andShould().beAnnotatedWith(RequestMapping.class)
            .andShould().haveSimpleNameEndingWith("Controller")
            .as("Las clases de tipo '@RestController' deben alojarse en el paquete 'controller' del módulo.");

    /**
     * Regla arquitectónica para los mappers definidos con MapStruct.
     * <p>
     * Toda clase anotada con {@link Mapper} debe:
     * <ul>
     *   <li>Residir en un paquete dedicado llamado "mapper"</li>
     *   <li>Finalizar su nombre con el sufijo "Mapper"</li>
     * </ul>
     * <p>
     * Esto permite identificar claramente los componentes responsables
     * de la transformación entre entidades y DTOs.
     */
    @ArchTest
    static final ArchRule nomenclaturaMappers = classes()
            .that().areAnnotatedWith(Mapper.class)
            .should().resideInAPackage("..mapper..")
            .andShould().haveSimpleNameEndingWith("Mapper")
            .as("Las clases de tipo '@Mapper' deben alojarse en el paquete 'mapper' del módulo.");

    /**
     * Regla arquitectónica que valida que todas las entidades de
     * persistencia anotadas con {@link Table}:
     * <ul>
     *   <li>Residan exclusivamente en el paquete "domain"</li>
     * </ul>
     * <p>
     * Esta restricción ayuda a centralizar el modelo de dominio
     * persistente y evita su dispersión en otras capas.
     */
    @ArchTest
    static final ArchRule nomenclaturaEntidades = classes()
            .that().areAnnotatedWith(Table.class)
            .should().resideInAnyPackage("..domain..")
            .as(
                    "Las clases que tengan anotación '@Table' deben " +
                    "alojarse en la raíz del paquete 'domain' del módulo."
            );

    /**
     * Regla arquitectónica para los Data Transfer Objects (DTO).
     * <p>
     * Toda clase que resida en el paquete "dto" debe:
     * <ul>
     *   <li>No ser una clase interna o anónima</li>
     *   <li>Finalizar su nombre con el sufijo "DTO"</li>
     * </ul>
     * <p>
     * Esto refuerza la claridad en la transferencia de datos entre capas
     * y evita confusiones con entidades o modelos de dominio.
     */
    @ArchTest
    static final ArchRule nomenclaturaDTOs = classes()
            .that().resideInAPackage("..dto..")
            .and().haveSimpleNameNotContaining("$")
            .should().haveSimpleNameEndingWith("DTO")
            .as(
                    "Las clases que tengan al final de su nombre 'DTO'" +
                            " deben alojarse en el paquete 'dto' del módulo."
            );
}
