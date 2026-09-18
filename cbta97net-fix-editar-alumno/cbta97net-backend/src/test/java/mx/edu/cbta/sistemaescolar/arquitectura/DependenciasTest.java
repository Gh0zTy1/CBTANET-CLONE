package mx.edu.cbta.sistemaescolar.arquitectura;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Controla las dependencias entre módulos del sistema, donde cada uno tiene
 * una lista (White-List) a la que puede acceder. Además de detectar cualquier tipo de
 * dependencia cíclica en el proyecto.
 */
@AnalyzeClasses(packages = "mx.edu.cbta.sistemaescolar")
public class DependenciasTest {

    /**
     * Garantiza que no existan dependencias circulares entre los diferentes módulos del sistema.
     * Los ciclos aumentan el acoplamiento y dificultan el mantenimiento y la reutilización del código.
     */
    @ArchTest
    static final ArchRule evitar_dependencias_ciclicas = slices()
            .matching("mx.edu.cbta.sistemaescolar.(*)..")
            .should().beFreeOfCycles();

    /**
     * Define y valida las reglas de aislamiento de fronteras para la arquitectura de Modulith.
     * * <p>Esta regla asegura que los módulos (slices) del sistema mantengan una alta cohesión
     * interna y un bajo acoplamiento externo. Se basa en el principio de que los módulos
     * solo deben comunicarse a través de sus APIs públicas (Interfaces y DTOs).</p>
     * * <p><b>Restricciones aplicadas:</b></p>
     * <ul>
     * <li>Los módulos no pueden depender entre sí de forma directa (ciclos o acceso a lógica interna).</li>
     * <li>Se prohíbe el acceso a las implementaciones de servicios ({@code .service.impl..}).</li>
     * <li>Se prohíbe el acceso a los componentes de mapeo ({@code .mapper..}) entre módulos.</li>
     * </ul>
     * * <p><b>Excepciones permitidas (Canales Oficiales):</b></p>
     * <ul>
     * <li>Acceso al paquete raíz de configuración para infraestructura común.</li>
     * <li>Intercambio de datos mediante DTOs, Modelos de Dominio y Enums.</li>
     * <li>Propagación y manejo de Excepciones de dominio.</li>
     * <li>Uso de Interfaces de Servicio (API pública del módulo).</li>
     * </ul>
     * * @see <a href="https://www.archunit.org/userguide/html/000_Index.html#_slices">ArchUnit Slices</a>
     * @return ArchRule que valida la integridad de los límites de cada módulo.
     */
    @ArchTest
    static final ArchRule modulith_boundary_security =
            slices()
                    .matching("mx.edu.cbta.sistemaescolar.(*)..")
                    .should()
                    .notDependOnEachOther()
                    .ignoreDependency(resideInAPackage(".."), resideInAPackage("..sistemaescolar.config.."))
                    .ignoreDependency(
                            resideInAPackage(".."),
                            resideInAPackage("..dto..").or(resideInAPackage("..domain.model.."))
                    )
                    .ignoreDependency(
                            resideInAPackage(".."),
                            resideInAPackage("..domain.exception..")
                    )
                    .ignoreDependency(
                            resideInAPackage(".."),
                            resideInAPackage("..service..")
                                    .and(not(resideInAPackage("..service.impl..")))
                                    .and(not(resideInAPackage("..mapper..")))
                    )
                    .as("Los módulos (estructura, paraescolar, grupos, etc) solo comparten su API (Interfaces), DTOs y Excepciones.")
                    .because("Queremos evitar el acoplamiento directo entre las implementaciones de los módulos.");

    /**
     * Regla de Arquitectura: Control de dependencias del módulo Académico.
     * <p>
     * Esta regla asegura que la lógica académica (calificaciones, actas, etc.)
     * mantenga un acoplamiento controlado, permitiendo únicamente la interacción
     * con los módulos de {@code grupos} y {@code estructura}.
     * </p>
     * <b>Restricción:</b> Ninguna clase de 'academica' puede depender de módulos
     * de nivel superior como 'alumnado' o 'personal' para evitar ciclos.
     */
    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Academico = noClasses()
        .that().resideInAPackage("mx.edu.cbta.sistemaescolar.academica..")
        .should().dependOnClassesThat()
        .resideOutsideOfPackages(
            "mx.edu.cbta.sistemaescolar.academica..",
                    "mx.edu.cbta.sistemaescolar.grupos..",
                    "mx.edu.cbta.sistemaescolar.estructura..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "org.springframework.."
    )
        .as("El módulo 'Académica' solo puede depender: Grupos y Estructura")
        .because("se controla el acoplamiento entre módulos del sistema y se previenen dependencias ciclicas.");


    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Estructura = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.estructura..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.estructura..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.apache.poi..",
                    "org.springframework.."
            )
            .as("El módulo 'Estructura' debe ser altamente estable, ya que se utiliza como módulo compartido.")
            .because("se controla el acoplamiento entre módulos del sistema y se previenen dependencias ciclicas.");

    /**
     * Regla de Arquitectura: Aislamiento de Seguridad y Usuarios.
     * <p>
     * Protege el módulo de identidad para que sea independiente de la lógica escolar.
     * Solo se comunica con el paquete {@code config} para parámetros globales.
     * </p>
     * <b>Integración:</b> Se autoriza explícitamente el SDK de <b>Keycloak</b>
     * y las librerías de soporte para Pruebas Unitarias (JUnit/Mockito).
     */
    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Usuarios = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.usuarios..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.usuarios..",
                    "mx.edu.cbta.sistemaescolar.config..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.keycloak..",
                    "org.junit..",
                    "org.mockito..",
                    "org.assertj..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.springframework.."
            )
            .as("El módulo 'Usuarios' debe permanecer estable y no depender de ningún otro módulo más que 'sistemaescolar.config'.")
            .because("se controla el acoplamiento entre módulos del sistema y se previenen dependencias ciclicas.");

    /**
     * Regla de Arquitectura: Gestión de Alumnado y Expedientes.
     * <p>
     * El módulo {@code alumnado} solo debe conocer la organización de la institución
     * ({@code estructura}). No debe conocer horarios ni procesos de personal.
     * </p>
     * <b>Infraestructura:</b> Permite el uso de protocolos <b>SFTP</b> para el
     * almacenamiento de documentos legales del alumno.
     */
    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Alumnado = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.alumnado..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.alumnado..",
                    "mx.edu.cbta.sistemaescolar.estructura..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.apache.poi..",
                    "org.springframework..",
                    "org.junit..",
                    "org.mockito..",
                    "org.apache.sshd.sftp..",
                    "org.apache.poi.."
            )
            .as("El módulo 'Alumnado' debe depender solamente del módulo: Estructura.")
            .because("se controla el acoplamiento entre módulos del sistema y se previenen dependencias ciclicas.");



    /**
     * Regla de Arquitectura: Orquestación de Grupos Paraescolares (Estabilidad Controlada).
     * <p>
     * El módulo {@code gruposparaescolares} actúa como un integrador de alto nivel dentro del sistema.
     * Su función principal es vincular las actividades definidas en el módulo {@code paraescolar}
     * con la {@code estructura} institucional, la gestión de {@code horario} y el registro de {@code alumnado}.
     * </p>
     * * <b>Flujo de Dependencias Autorizado:</b>
     * <ul>
     * <li>{@code paraescolar}: Para obtener el catálogo de talleres y actividades disponibles.</li>
     * <li>{@code estructura}: Para validar los ciclos escolares y espacios físicos.</li>
     * <li>{@code horario}: Para gestionar las sesiones y tiempos de las actividades.</li>
     * <li>{@code alumnado}: Para realizar el proceso de inscripción y control de cupos.</li>
     * </ul>
     * * <b>Nota de Diseño:</b> Al ser un módulo que consume múltiples dominios, se clasifica como
     * de "Estabilidad Controlada". Los cambios en los módulos base mencionados pueden afectar
     * a este módulo, pero este módulo no debe afectar a las bases.
     */
    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_GruposParaescolares = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.gruposparaescolares..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.gruposparaescolares..",
                    "mx.edu.cbta.sistemaescolar.paraescolar..",
                    "mx.edu.cbta.sistemaescolar.estructura..",
                    "mx.edu.cbta.sistemaescolar.horario..",
                    "mx.edu.cbta.sistemaescolar.alumnado..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.apache.poi..",
                    "org.springframework..",
                    "org.junit..",
                    "org.mockito.."
            )
            .as("El módulo 'GruposParaescolares' debe depender solamente de los módulos: Estructura, Paraescolar, Horario y Alumnado.")
            .because("Es un módulo que depende mucho de las reglas y elementos del negocio, es estabilidad controlada.");


    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Grupos = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.grupos..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.grupos..",
                    "mx.edu.cbta.sistemaescolar.estructura..",
                    "mx.edu.cbta.sistemaescolar.horario..",
                    "mx.edu.cbta.sistemaescolar.alumnado..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.apache.poi..",
                    "org.springframework..",
                    "org.junit..",
                    "org.mockito.."
            )
            .as("El módulo 'Grupos' debe depender solamente de los módulos: Estructura, Horario y Alumnado.")
            .because("Es un módulo que depende mucho de las reglas y elementos del negocio, es estabilidad controlada.");

    /**
     * Regla de Arquitectura: Gestión de Tiempos y Desacoplamiento de Calendarios (Horario).
     * <p>
     * El módulo {@code horario} actúa como el motor de asignación temporal del sistema. Su diseño
     * permite el desacoplamiento entre las clases semestrales regulares y las actividades
     * paraescolares, gestionando la disponibilidad de recursos compartidos.
     * </p>
     * * <b>Dominios de Integración Autorizados:</b>
     * <ul>
     * <li>{@code estructura}: Para la gestión de espacios físicos (aulas, canchas) y ciclos escolares.</li>
     * <li>{@code paraescolar}: Para la asignación de horarios específicos a talleres y clubes.</li>
     * <li>{@code personal}: Para validar la carga horaria y disponibilidad de los docentes/instructores.</li>
     * </ul>
     * * <b>Estrategia de Desacoplamiento:</b>
     * Al restringir sus dependencias, se asegura que el motor de horarios sea una entidad técnica
     * pura que no se vea afectada por la lógica de calificaciones (Académica) o de expedientes (Alumnado),
     * permitiendo que las clases paraescolares y normales compartan infraestructura sin colisionar.
     */
    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Horario = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.horario..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.horario..",
                    "mx.edu.cbta.sistemaescolar.estructura..",
                    "mx.edu.cbta.sistemaescolar.paraescolar..",
                    "mx.edu.cbta.sistemaescolar.personal..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.apache.poi..",
                    "org.springframework..",
                    "org.junit..",
                    "org.mockito.."
            )
            .as("El módulo 'Horario' debe depender solamente de los módulos: Estructura, Paraescolar y Personal.")
            .because("Es un módulo que depende mucho de las reglas y elementos del negocio, es estabilidad controlada.");

    /**
     * Regla de Arquitectura: Gestión de Capital Humano e Integración de Identidades (Personal).
     * <p>
     * El módulo {@code personal} se encarga del registro y administración de los trabajadores de la institución.
     * Su diseño arquitectónico prioriza la independencia de los procesos administrativos frente a los
     * flujos académicos o escolares.
     * </p>
     * * <b>Relación de Dependencia Autorizada:</b>
     * <ul>
     * <li>{@code usuarios}: Única dependencia interna permitida. Es crucial para vincular la entidad
     * física (empleado) con su entidad digital (cuenta de acceso, roles y permisos).</li>
     * </ul>
     * * <b>Estrategia contra la Ciclicidad:</b>
     * Al restringir el conocimiento de {@code personal} únicamente hacia {@code usuarios}, se previene que
     * este módulo dependa de {@code horario} o {@code grupos}, evitando así dependencias circulares,
     * ya que son esos módulos los que deben consumir la información del personal (docentes/instructores)
     * y no al revés.
     * * <b>Capacidades Técnicas:</b>
     * Permite el uso de herramientas de oficina (Apache POI) para la carga masiva de plantillas de personal
     * y librerías de mapeo (MapStruct) para la transferencia de objetos de datos (DTOs).
     */
    @ArchTest
    static final ArchRule dependenciasPermitidasModulo_Personal = noClasses()
            .that().resideInAPackage("mx.edu.cbta.sistemaescolar.personal..")
            .should().dependOnClassesThat()
            .resideOutsideOfPackages(
                    "mx.edu.cbta.sistemaescolar.personal..",
                    "mx.edu.cbta.sistemaescolar.usuarios..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "com.fasterxml.jackson..",
                    "org.mapstruct..",
                    "org.slf4j..",
                    "lombok..",
                    "org.apache.poi..",
                    "org.springframework..",
                    "org.junit..",
                    "org.mockito.."
            )
            .as("El módulo 'Personal' debe depender solamente del módulo: Usuarios.")
            .because("Es un módulo que debe permanecer lo más estable posible para no permitir dependencias cíclicas.");
}