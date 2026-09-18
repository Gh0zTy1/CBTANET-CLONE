const driver = require('../../setup/driver');
const { By, until } = require('selenium-webdriver');

const path = require('path');

const { RegistrarAlumnoPage } = require('../../pages/alumnos/registrar-alumno.page');

require('dotenv').config();

jest.setTimeout(60000)

describe('CP #1 - Foto de Alumno con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();



    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que la foto del alumno seleccionada detecta cuando una imagen no tiene el formato correcto (solo se acepta JPEG, PNG, WEBP).', async () => {
        await registrarAlumnoPage.open();

        const rutaRaiz = process.cwd();

        const rutaAbsolutaArchivoInvalido = path.join(rutaRaiz, 'assets', 'archivo_invalido.txt');
        const rutaAbsolutaArchivoValido = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        // archivo NO valido
        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoInvalido);

        let archivoCargado = await registrarAlumnoPage.driver
            .findElement(registrarAlumnoPage.fotoAlumnoField)
            .getAttribute("value");

        expect(archivoCargado).not.toBeNull();
        expect(normalizarTexto(archivoCargado)).toContain('');

        await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.fotoAlumnoField).clear();

        // archivo valido
        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValido);

        archivoCargado = await registrarAlumnoPage.driver
            .findElement(registrarAlumnoPage.fotoAlumnoField)
            .getAttribute("value");

        expect(archivoCargado).not.toBeNull();
        expect(normalizarTexto(archivoCargado)).toContain('jpeg');
    });
});

describe('CP #2 - Matrícula con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente las matrículas de alumnos. Las matrículas deben contener solamente 8 dígitos fijos.\n', async () => {
        const matriculaIncorrecta = "6502mos";
        const matriculaValida = "19040042";

        await registrarAlumnoPage.open();

        // llenado de matricula incorrecto
        await registrarAlumnoPage.asignarMatriculaAlumno(matriculaIncorrecta); // TODO: separar todos los campos en su propio metodo (dentro de RegistrarAlumnoPage)
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.matriculaAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('matricula'); // algo como 'formato de matricula incorrecto', incluye 'matricula'

        // llenao de matricula correcto
        await registrarAlumnoPage.asignarMatriculaAlumno(matriculaValida);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.matriculaAlumnoField);

        expect(normalizarTexto(mensajeValidacion)).toContain(''); // sin mensaje de error
    });
});

describe('CP #3 - CURP con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente la CURP a registrar. El sistema debe mostrar un mensaje indicando que la CURP ingresada no cumple con el formato correcto.', async () => {
        let curpIncorrecta = "111???xxx";
        const curpValida = "EOLE049815HERRELA1";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarCURPAlumno(curpIncorrecta);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.curpAlumnoField);

        mensajeValidacion = normalizarTexto(mensajeValidacion);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');
        expect(normalizarTexto(mensajeValidacion)).toContain('curp');
        expect(normalizarTexto(mensajeValidacion)).toContain('incorrecto');

        await registrarAlumnoPage.asignarCURPAlumno(curpValida);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.curpAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});



describe('CP #4 - Nombre del Alumno con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente el nombre del alumno a registrar. El sistema debe mostrar un mensaje indicando que el nombre ingresado no cumple con el formato requerido.\n', async () => {
        let nombreNoValido = "C?rl?s";
        const nombreValido = "Carlos";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarNombreAlumno(nombreNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nombreAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarNombreAlumno(nombreValido)
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nombreAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});



describe('CP #5 - Apellido Paterno del Alumno con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente el apellido paterno del alumno a registrar. El sistema debe mostrar un mensaje indicando que el apellido ingresado no cumple con el formato requerido. El formato no permite números ni símbolos.', async () => {
        let apellidoNoValido = "P3r3z?";
        const apellidoValido = "Perez";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarApellidoPaternoAlumno(apellidoNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoPaternoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarApellidoPaternoAlumno(apellidoValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoPaternoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});




describe('CP #6 - Apellido Materno del Alumno con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente el apellido materno del alumno a registrar. El sistema debe mostrar un mensaje indicando que el apellido ingresado no cumple con el formato requerido. El formato no permite números ni símbolos.', async () => {
        let apellidoNoValido = "M0reN0?";
        const apellidoValido = "Moreno";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarApellidoMaternoAlumno(apellidoNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoMaternoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarApellidoMaternoAlumno(apellidoValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoMaternoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});



describe('CP #7 - Fecha de Nacimiento del Alumno no válida', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema detecta correctamente cuando se selecciona una fecha de nacimiento igual o posterior a la actual.', async () => {
        const hoy = new Date();

        const fechaActual = hoy.toISOString().split("T")[0];

        const fechaMas5 = new Date(hoy);
        fechaMas5.setDate(hoy.getDate() + 5);
        const fechaPosterior = fechaMas5.toISOString().split("T")[0];

        const fechaMenos15 = new Date(hoy);
        fechaMenos15.setFullYear(hoy.getFullYear() - 15);
        const fechaAnterior = fechaMenos15.toISOString().split("T")[0];

        await registrarAlumnoPage.open();

        // fecha actual
        await registrarAlumnoPage.asignarFechaNacimientoAlumno(fechaActual);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.fechaNacimientoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('anterior');

        // fecha posterior (5 días después de hoy)
        await registrarAlumnoPage.asignarFechaNacimientoAlumno(fechaPosterior);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.fechaNacimientoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('anterior');

        // fecha anterior (hace 15 años)
        await registrarAlumnoPage.asignarFechaNacimientoAlumno(fechaAnterior);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.fechaNacimientoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
    });
});




describe('CP #8 - Número de Seguro Social (NSS) del Alumno no válido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema detecta cuando el NSS del alumno tiene un formato incorrecto. El formato es de 11 dígitos.\n', async () => {

        let nssNoValido = "N555???";
        const nssValido = "01234567899";

        await registrarAlumnoPage.open();

        // NSS No valido #1
        await registrarAlumnoPage.asignarNSSAlumno(nssNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nssAlumnoField);
        expect(normalizarTexto(mensajeValidacion)).toContain('nss');
        expect(normalizarTexto(mensajeValidacion)).toContain('11');
        expect(normalizarTexto(mensajeValidacion)).toContain('digitos');
        expect(normalizarTexto(mensajeValidacion)).toContain('exactamente');


        // NSS No valido #2
        nssNoValido = "1234567890########";
        await registrarAlumnoPage.asignarNSSAlumno(nssNoValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nssAlumnoField);
        expect(normalizarTexto(mensajeValidacion)).toContain('nss');
        expect(normalizarTexto(mensajeValidacion)).toContain('11');
        expect(normalizarTexto(mensajeValidacion)).toContain('digitos');
        expect(normalizarTexto(mensajeValidacion)).toContain('exactamente');


        // NSS valido (correcto)
        await registrarAlumnoPage.asignarNSSAlumno(nssValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nssAlumnoField);
        expect(normalizarTexto(mensajeValidacion)).toContain('');


        expect(mensajeValidacion).not.toBeNull();
    });
});



describe('CP #9 - Póliza de Seguro del Alumno no válida', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema detecta cuando la póliza de seguro del alumno tiene un formato incorrecto. El formato aceptado son solo números. (sin rango especificado).', async () => {

        let polizaNoValida = "N555???";
        const polizaValida = "2474982004";

        await registrarAlumnoPage.open();

        // Poliza No Valida
        await registrarAlumnoPage.asignarPolizaSeguroAlumno(polizaNoValida);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.polizaAlumnoField);
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');
        expect(normalizarTexto(mensajeValidacion)).toContain('solo acepta');
        expect(normalizarTexto(mensajeValidacion)).toContain('numeros');


        // Poliza valida (correcta)
        await registrarAlumnoPage.asignarPolizaSeguroAlumno(polizaNoValida);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nssAlumnoField);
        expect(normalizarTexto(mensajeValidacion)).toBe('');


        expect(mensajeValidacion).not.toBeNull();
    });
});



describe('CP #10 - Nombre del Tutor con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente el nombre del tutor legal del alumno. El sistema debe mostrar un mensaje indicando que el nombre ingresado no cumple con el formato requerido. El formato no permite números ni símbolos.', async () => {
        let nombreNoValido = "D4vid?";
        const nombreValido = "David";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarNombreTutor(nombreNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nombreTutorField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarNombreTutor(nombreValido)
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nombreTutorField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});




describe('CP #11 - Apellido Paterno del tutor con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente el apellido paterno del tutor legal del alumno. El sistema debe mostrar un mensaje indicando que el apellido paterno ingresado no cumple con el formato requerido. El formato no permite números ni símbolos.', async () => {
        let apellidoNoValido = "P3r3z?";
        const apellidoValido = "Perez";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarApellidoPaternoAlumno(apellidoNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoPaternoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarApellidoPaternoAlumno(apellidoValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoPaternoAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});



describe('CP #12 - Apellido Materno del tutor con Formato Inválido', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente el apellido materno del tutor legal del alumno. El sistema debe mostrar un mensaje indicando que el apellido materno ingresado no cumple con el formato requerido. El formato no permite números ni símbolos.', async () => {
        let apellidoNoValido = "P3r3z???";
        const apellidoValido = "Perez";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarApellidoMaternoTutor(apellidoNoValido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoMaternoTutorField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarApellidoMaternoTutor(apellidoValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.apellidoMaternoTutorField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});



describe('CP #13 - Fecha de Nacimiento del Tutor no válida (Debe tener 18 años o más)', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida correctamente que el tutor legal tiene 18 años o más.', async () => {

        const hoy = new Date();

        const fechaInvalidaMenor = "2010-01-01"; // menor de 18 años

        const fechaValidaMayor = "1990-01-01";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarFechaNacimientoTutor(fechaInvalidaMenor);

        // =================================================================
        // SOLUCIÓN FINAL: Usamos un Explicit Wait para esperar al mensaje
        // =================================================================
        let mensajeValidacion = await registrarAlumnoPage.driver.wait(async () => {
            // Re-ejecuta la lectura del error, incluyendo el reportValidity() en el Page Object
            const msg = await registrarAlumnoPage.getNativeValidationError(
                registrarAlumnoPage.fechaNacimientoTutorField
            );

            // Si el mensaje NO está vacío, retornamos el mensaje (la espera termina)
            if (msg && msg.length > 0) {
                return msg;
            }
            // Si está vacío, retornamos false y la espera continúa
            return false;
        }, 5_000, 'Timeout: El mensaje de error de validación de edad del tutor no apareció después de 5s.');

        // =================================================================

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('edad');

        await registrarAlumnoPage.asignarFechaNacimientoTutor(fechaValidaMayor);

        // Aquí no necesitamos espera porque esperamos que sea nulo/vacío inmediatamente.
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.fechaNacimientoTutorField
        );

        expect(mensajeValidacion).not.toBeNull();
    });
});




describe('CP #14 - Validar el número de teléfono del tutor', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema valida que el número del teléfono del tutor no sea mayor a 10 dígitos o menor, y que no tenga caracteres especiales.', async () => {

        const telefonoInvalido = "644L01311";

        const telefonoCorrecto = "6444000000";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarNumeroTelefonoTutor(telefonoInvalido)
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.telefonoTutorField
        );

        mensajeValidacion = normalizarTexto(mensajeValidacion);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');
        expect(normalizarTexto(mensajeValidacion)).toContain('telefono');
        expect(normalizarTexto(mensajeValidacion)).toContain('debe ser');
        expect(normalizarTexto(mensajeValidacion)).toContain('10');
        expect(normalizarTexto(mensajeValidacion)).toContain('digitos');


        await registrarAlumnoPage.asignarNumeroTelefonoTutor(telefonoCorrecto)
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.telefonoTutorField
        );

        mensajeValidacion = normalizarTexto(mensajeValidacion);

        expect(mensajeValidacion).not.toBeNull();
        expect(mensajeValidacion).toBe('');
    });
});



describe('CP #15 - Documento Acta de Nacimiento - Formato y Tamaño', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar la validación de formato (solo PDF) y tamaño (max 10MB) del Acta de Nacimiento.', async () => {
        await registrarAlumnoPage.open();
        const rutaRaiz = process.cwd();

        // prueba documento formato no valido
        const rutaAbsolutaArchivoInvalido = path.join(rutaRaiz, 'assets', 'documentos', 'pesados', 'DocumentoTestPesado_10mb.txt');
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoInvalido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docActaNacimientoAlumnoField);

        mensajeValidacion = normalizarTexto(mensajeValidacion);

        expect(mensajeValidacion).not.toBeNull();
        expect(mensajeValidacion).toContain('pdf');
        expect(mensajeValidacion).toContain('documento');
        expect(mensajeValidacion).toContain('formato');
        expect(mensajeValidacion).toContain('acta de nacimiento');


        // prueba documento pesado PDF
        const rutaAbsolutaArchivoPesado = path.join(rutaRaiz, 'assets', 'documentos', 'pesados', 'DocumentoTestPesado_10mb.pdf');
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoPesado);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docActaNacimientoAlumnoField);

        mensajeValidacion = normalizarTexto(mensajeValidacion);

        expect(mensajeValidacion).not.toBeNull();
        expect(mensajeValidacion).toContain('el documento acta de nacimiento excede el tamano maximo de 10mb.');

        // prueba documento valido
        const rutaAbsolutaArchivoValido = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValido);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docActaNacimientoAlumnoField);

        mensajeValidacion = normalizarTexto(mensajeValidacion);

        let archivoCargado = await registrarAlumnoPage.driver
            .findElement(registrarAlumnoPage.docActaNacimientoAlumnoField)
            .getAttribute("value");

        expect(archivoCargado).not.toBeNull();
        expect(mensajeValidacion).toBe('');
    });
});



describe('CP #16 - Documento Certificado de Secundaria - Formato y Tamaño', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar la validación de formato (solo PDF) y tamaño (max 10MB) del Certificado de Secundaria.', async () => {
        await registrarAlumnoPage.open();
        const rutaRaiz = process.cwd();

        const rutaAbsolutaArchivoInvalido = path.join(rutaRaiz, 'assets', 'documentos', 'pesados', 'DocumentoTestPesado_10mb.txt');
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoInvalido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docCertificadoSecundariaAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('pdf');
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        const rutaAbsolutaArchivoPesado = path.join(rutaRaiz, 'assets', 'documentos', 'pesados', 'DocumentoTestPesado_10mb.pdf');
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoPesado);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docCertificadoSecundariaAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('el documento certificado de secundaria excede el tamano maximo de 10mb.');

        const rutaAbsolutaArchivoValido = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValido);

        let archivoCargado = await registrarAlumnoPage.driver
            .findElement(registrarAlumnoPage.docCertificadoSecundariaAlumnoField)
            .getAttribute("value");

        expect(archivoCargado).not.toBeNull();
        expect(normalizarTexto(archivoCargado)).toContain('pdf');

        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docCertificadoSecundariaAlumnoField);
        expect(mensajeValidacion).toBe("");
    });
});



describe('CP #17 - Documento CURP del Alumno - Formato y Tamaño', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar la validación de formato (solo PDF) y tamaño (max 10MB) del Documento CURP.', async () => {
        await registrarAlumnoPage.open();
        const rutaRaiz = process.cwd();

        const rutaAbsolutaArchivoInvalido = path.join(rutaRaiz, 'assets', 'documentos', 'pesados', 'DocumentoTestPesado_10mb.txt');
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoInvalido);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docCURPAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('pdf');
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        const rutaAbsolutaArchivoPesado = path.join(rutaRaiz, 'assets', 'documentos', 'pesados', 'DocumentoTestPesado_10mb.pdf');
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoPesado);
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docCURPAlumnoField);
        expect(mensajeValidacion).not.toBeNull();

        const rutaAbsolutaArchivoValido = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValido);

        let archivoCargado = await registrarAlumnoPage.driver
            .findElement(registrarAlumnoPage.docCURPAlumnoField)
            .getAttribute("value");

        expect(archivoCargado).not.toBeNull();
        expect(normalizarTexto(archivoCargado)).toContain('pdf');

        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.docCURPAlumnoField);
        expect(mensajeValidacion).toBe("");
    });
});


describe('CP #18 - Especificación de Condición Especial del Alumno', () => {
    let registrarAlumnoPage;

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que al seleccionar la opción de Condición Especial se muestra/oculta el campo de descripción.', async () => {
        await registrarAlumnoPage.open();

        await registrarAlumnoPage.toggleCondicionEspecialAlumno();

        let isVisible = await registrarAlumnoPage.isCondicionEspecialDescripcionVisible();
        console.log("#visible: " + isVisible);
        expect(isVisible).toBe(true);

        await registrarAlumnoPage.toggleCondicionEspecialAlumno();

        isVisible = await registrarAlumnoPage.isCondicionEspecialDescripcionVisible();
        console.log("#visible: " + isVisible);
        expect(isVisible).toBe(false);
    });
});



describe('CP #19 - No permitir el registro sin completar todos los campos', () => {
    let registrarAlumnoPage;

    const campoRequerido = 'Nombre del Alumno';

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    test(`Verificar que el sistema valida y detiene el registro si faltan campos obligatorios. (Probando con ${campoRequerido})`, async () => {
        await registrarAlumnoPage.open();

        await registrarAlumnoPage.clickRegistrarAlumno();

        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.nombreAlumnoField
        );

        expect(mensajeValidacion).not.toBeNull();
        expect(mensajeValidacion.length).toBeGreaterThan(0);
    });
});



describe('CP #20 - Intento de registro de alumno sin asignar documentos', () => {
    let registrarAlumnoPage;

    const documentoFaltante = 'Acta de Nacimiento';

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test(`Verificar que el sistema exige los documentos al intentar registrar. (Probando el error en el campo: ${documentoFaltante})`, async () => {
        await registrarAlumnoPage.open();

        await registrarAlumnoPage.llenarCamposExceptoDocumentos(registrarAlumnoPage);

        let mensajeValidacionPrevia = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.matriculaAlumnoField
        );
        expect(mensajeValidacionPrevia).toBe("");

        await registrarAlumnoPage.clickRegistrarAlumno();

        let mensajeValidacionDocumento = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.docActaNacimientoAlumnoField
        );

        expect(mensajeValidacionDocumento).not.toBeNull();
        expect(mensajeValidacionDocumento.length).toBeGreaterThan(0);
    });
});



describe('CP #21 - Problema de conexión al intentar Registrar Alumno', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que un fallo de conexión al intentar registrar genera un mensaje de error.', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.asignarMatriculaAlumno("19040042");
        await registrarAlumnoPage.asignarCURPAlumno("EOLE049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        //await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        //await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        let mensajeValidacionPrevia = await registrarAlumnoPage.getNativeValidationError(
            registrarAlumnoPage.matriculaAlumnoField
        );
        expect(mensajeValidacionPrevia).toBe("");

        await registrarAlumnoPage.setupConnectionRefusedMock();

        await registrarAlumnoPage.clickRegistrarAlumno();

        const mensajeErrorConexion = await registrarAlumnoPage.getOnScreenConnectionErrorMessage();

        expect(mensajeErrorConexion).not.toBeNull();
        expect(mensajeErrorConexion.length).toBeGreaterThan(0);

        const normalizado = mensajeErrorConexion
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase();

        expect(normalizado).toMatch(/(servidor|red|conexion|fallo|error)/);
    });
});



describe('CP #22 - Registro de Alumno exitoso', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema registra correctamente al alumno cuando todos sus datos cumplen con el formato correcto al igual que sus documentos.', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarMatriculaAlumno("19040042");
        await registrarAlumnoPage.asignarCURPAlumno("EOLE049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.clickRegistrarAlumno();

        const FINAL_SUCCESS_PHRASE_NORMALIZED = "registro completo";

        const statusElement = await registrarAlumnoPage.driver.wait(
            until.elementLocated(registrarAlumnoPage.connectionErrorAlert),
            10_000,
            'Timeout: El elemento de mensaje de estado no apareció en el DOM.'
        );

        await registrarAlumnoPage.driver.wait(async () => {
            try {
                const currentText = await statusElement.getText();

                const normalizado = currentText
                    .normalize("NFD")
                    .replace(/[\u0300-\u036f]/g, "")
                    .toLowerCase();

                return normalizado.includes(FINAL_SUCCESS_PHRASE_NORMALIZED);
            } catch (error) {
                // Si el elemento se vuelve Stale (se reemplaza), volvemos a intentar
                return false;
            }
        }, 10_000, 'Timeout: El mensaje final de éxito no apareció en el elemento de estado en el tiempo límite.');


        const mensajeExito = await statusElement.getText();

        expect(mensajeExito).not.toBeNull();
        expect(mensajeExito.length).toBeGreaterThan(0);

        const normalizado = mensajeExito
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase();

        expect(normalizado).toMatch(/(registro).*?(completo)/);
    });
});



describe('CP #23 - Validación campos opcionales NSS y Póliza de Seguro', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema pueda registrar a un alumno aún cuando se omitan los campos de Número de Seguro Social (NSS) y la Póliza de Seguro.\n', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarMatriculaAlumno("10305090");
        await registrarAlumnoPage.asignarCURPAlumno("SANE049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        // await registrarAlumnoPage.asignarNSSAlumno("01234567899"); // NOTE: se omite el NSS
        // await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004"); // NOTE: se omite la Poliza de Seguro

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.clickRegistrarAlumno();

        const FINAL_SUCCESS_PHRASE_NORMALIZED = "registro completo";

        const statusElement = await registrarAlumnoPage.driver.wait(
            until.elementLocated(registrarAlumnoPage.connectionErrorAlert),
            10_000,
            'Timeout: El elemento de mensaje de estado no apareció en el DOM.'
        );

        await registrarAlumnoPage.driver.wait(async () => {
            try {
                const currentText = await statusElement.getText();

                const normalizado = currentText
                    .normalize("NFD")
                    .replace(/[\u0300-\u036f]/g, "")
                    .toLowerCase();

                return normalizado.includes(FINAL_SUCCESS_PHRASE_NORMALIZED);
            } catch (error) {
                // Si el elemento se vuelve Stale (se reemplaza), volvemos a intentar
                return false;
            }
        }, 10_000, 'Timeout: El mensaje final de éxito no apareció en el elemento de estado en el tiempo límite.');


        const mensajeExito = await statusElement.getText();

        expect(mensajeExito).not.toBeNull();
        expect(mensajeExito.length).toBeGreaterThan(0);

        const normalizado = mensajeExito
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .toLowerCase();

        expect(normalizado).toMatch(/(registro).*?(completo)/);
    });
});


describe('CP #24 - Validación de campos contra inyecciones SQL', () => {
    let registrarAlumnoPage;
    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema sea resiliente ante inyecciones SQL.', async () => {
        let injeccionSQL = "; DROP TABLE alumnos; -- ";
        const nombreValido = "Carlos";

        await registrarAlumnoPage.open();

        await registrarAlumnoPage.asignarNombreAlumno(injeccionSQL);
        let mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nombreAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('formato');

        await registrarAlumnoPage.asignarNombreAlumno(nombreValido)
        mensajeValidacion = await registrarAlumnoPage.getNativeValidationError(registrarAlumnoPage.nombreAlumnoField);

        expect(mensajeValidacion).not.toBeNull();
        expect(normalizarTexto(mensajeValidacion)).toContain('');
    });
});


describe('CP #25 - Recuperación de datos ante reinicio', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el proceso se recupere después de que la página se haya reiniciado.\n', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarMatriculaAlumno("98007600");
        await registrarAlumnoPage.asignarCURPAlumno("CLKO049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        //await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        //await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.open();

        // NOTE: Esperar el reinicio...
        await registrarAlumnoPage.driver.sleep(3_000);

        const inputNombre = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.nombreAlumnoField);
        const inputMatricula = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.matriculaAlumnoField);
        const inputApellidoPaternoAlumno = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.apellidoPaternoAlumnoField);
        const inputApellidoMaternoAlumno = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.apellidoMaternoAlumnoField);
        const inputCURPAlumno = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.curpAlumnoField)


        const valorNombreAlumno = await inputNombre.getAttribute("value");
        const valorMatriculaAlumno = await inputMatricula.getAttribute("value");
        const valorApellidoPaternoAlumno = await inputApellidoPaternoAlumno.getAttribute("value");
        const valorApellidoMaternoAlumno = await inputApellidoMaternoAlumno.getAttribute("value");
        const valorCURPAlumno = await inputCURPAlumno.getAttribute("value");

        expect(valorMatriculaAlumno).toBe("98007600");
        expect(valorNombreAlumno).toBe('Carlos');
        expect(valorApellidoPaternoAlumno).toBe('Perez');
        expect(valorApellidoMaternoAlumno).toBe('Moreno');
        expect(valorCURPAlumno).toBe('CLKO049815HERRELA1');
    });
});



describe('CP #27 - Validación de CURP repetida', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema pueda detectar cuando se ingrese una CURP que ya está en el sistema.', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarMatriculaAlumno("82292202");
        await registrarAlumnoPage.asignarCURPAlumno("EOLE049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        //await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        //await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.clickRegistrarAlumno();

        const FINAL_SUCCESS_PHRASE_NORMALIZED = "el CURP 'EOLE049815HERRELA1' ya esta registrado";

        const statusElement = await registrarAlumnoPage.driver.wait(
            until.elementLocated(registrarAlumnoPage.connectionErrorAlert),
            20_000,
            'Timeout: El elemento de mensaje de estado no apareció en el DOM.'
        );

        await registrarAlumnoPage.driver.wait(async () => {
            try {
                const currentText = await statusElement.getText();

                const normalizado = normalizarTexto(currentText);

                return normalizado.trim().length > 0;
                //return normalizado.includes(FINAL_SUCCESS_PHRASE_NORMALIZED);
            } catch (error) {
                // Si el elemento se vuelve Stale (se reemplaza), volvemos a intentar
                return false;
            }
        }, 20_000, 'Timeout: El mensaje final de éxito no apareció en el elemento de estado en el tiempo límite.');


        const mensajeExito = await statusElement.getText();

        expect(mensajeExito).not.toBeNull();
        expect(mensajeExito.length).toBeGreaterThan(0);

        const normalizado = normalizarTexto(mensajeExito);

        expect(normalizado).not.toMatch(/(registro).*?(completo)/);
        expect(mensajeExito).toContain('EOLE049815HERRELA1');
        expect(mensajeExito).toContain('CURP');
    });
});



describe('CP #28 - Prevención de Envío Múltiple (Doble Clic) del Formulario', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar el comportamiento de la interfaz para prevenir envíos múltiples del formulario (double submission) que podrían ocurrir si el administrador hace doble clic rápidamente en el botón "Registrar Alumno". La interfaz debe deshabilitar el botón o mostrar un indicador de carga al primer clic.', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarMatriculaAlumno("98007600");
        await registrarAlumnoPage.asignarCURPAlumno("CLKO049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        //await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        //await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.clickRegistrarAlumno();

        const FINAL_SUCCESS_PHRASE_NORMALIZED = "registro completo";

        const statusElement = await registrarAlumnoPage.driver.wait(
            until.elementLocated(registrarAlumnoPage.connectionErrorAlert),
            10_000,
            'Timeout: El elemento de mensaje de estado no apareció en el DOM.'
        );

        await registrarAlumnoPage.driver.wait(async () => {
            try {
                const currentText = await statusElement.getText();

                const normalizado = normalizarTexto(currentText);

                return normalizado.includes(FINAL_SUCCESS_PHRASE_NORMALIZED);
            } catch (error) {
                // Si el elemento se vuelve Stale (se reemplaza), volvemos a intentar
                return false;
            }
        }, 10_000, 'Timeout: El mensaje final de éxito no apareció en el elemento de estado en el tiempo límite.');


        const mensajeExito = await statusElement.getText();

        expect(mensajeExito).not.toBeNull();
        expect(mensajeExito.length).toBeGreaterThan(0);

        const normalizado = normalizarTexto(mensajeExito);

        expect(normalizado).toMatch(/(registro).*?(completo)/);

        // NOTE: Esperar el reinicio...
        await registrarAlumnoPage.driver.sleep(3_000);

        const inputNombre = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.nombreAlumnoField);
        const inputMatricula = await registrarAlumnoPage.driver.findElement(registrarAlumnoPage.matriculaAlumnoField);

        const valorNombre = await inputNombre.getAttribute("value");
        const valorMatricula = await inputMatricula.getAttribute("value");

        console.log(`Valor Nombre tras registro: '${valorNombre}'`);
        console.log(`Valor Matrícula tras registro: '${valorMatricula}'`);

        expect(valorNombre).toBe("");
        expect(valorMatricula).toBe("");
    });
});



describe('CP #30 - Validación de Matricula repetida', () => {
    let registrarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        registrarAlumnoPage = new RegistrarAlumnoPage(driver.driver);
    });

    afterAll(async () => {
        await registrarAlumnoPage.clearLocalStorageBackup();
    })

    test('Verificar que el sistema detecta y evita el registro de un alumno si la Matrícula ingresada ya se encuentra registrada en la base de datos. Se debe mostrar un mensaje de error claro al usuario administrador. \n', async () => {
        await registrarAlumnoPage.open();

        const rutaAbsolutaArchivoValidoPdf = path.join(rutaRaiz, 'assets', 'documentos', 'validos', 'DocumentoTest_No_10mb.pdf');
        const rutaAbsolutaArchivoValidoJpeg = path.join(rutaRaiz, 'assets', 'archivo_valido.jpeg');

        await registrarAlumnoPage.asignarMatriculaAlumno("19040042");
        await registrarAlumnoPage.asignarCURPAlumno("PERE049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        //await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        //await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

        await registrarAlumnoPage.asignarFotoAlumno(rutaAbsolutaArchivoValidoJpeg);
        await registrarAlumnoPage.introducirDocumentoActaNacimientoAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCertificadoSecundariaAlumno(rutaAbsolutaArchivoValidoPdf);
        await registrarAlumnoPage.introducirDocumentoCURPAlumno(rutaAbsolutaArchivoValidoPdf);

        await registrarAlumnoPage.clickRegistrarAlumno();

        const FINAL_SUCCESS_PHRASE_NORMALIZED = "la matricula '19040042' ya existe";

        const statusElement = await registrarAlumnoPage.driver.wait(
            until.elementLocated(registrarAlumnoPage.connectionErrorAlert),
            10_000,
            'Timeout: El elemento de mensaje de estado no apareció en el DOM.'
        );

        await registrarAlumnoPage.driver.wait(async () => {
            try {
                const currentText = await statusElement.getText();

                const normalizado = normalizarTexto(currentText);

                return normalizado.includes(FINAL_SUCCESS_PHRASE_NORMALIZED);
            } catch (error) {
                // Si el elemento se vuelve Stale (se reemplaza), volvemos a intentar
                return false;
            }
        }, 10_000, 'Timeout: El mensaje final de éxito no apareció en el elemento de estado en el tiempo límite.');


        const mensajeExito = await statusElement.getText();

        expect(mensajeExito).not.toBeNull();
        expect(mensajeExito.length).toBeGreaterThan(0);

        const normalizado = normalizarTexto(mensajeExito);

        expect(normalizado).toContain('ya existe');
        expect(normalizado).toContain('19040042');
    });
});