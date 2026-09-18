package mx.edu.cbta.sistemaescolar.arquitectura;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "mx.edu.cbta.sistemaescolar")
public class NomenclaturaMetodosTest {

    /**
     * Verifica que todos los métodos públicos del sistema
     * sigan la convención camelCase.
     *
    @ArchTest
    static final ArchRule nomenclaturaMetodosCamelCase = methods()
            .that().arePublic().and().getClass().isRecord()
            .should().haveNameMatching("^[a-z][a-zA-Z0-9]*$");*/
}
