const driver = require('../../setup/driver');
const { By, until } = require('selenium-webdriver');

const path = require('path');

const { SolicitarAlumnoPage } = require('../../pages/alumnos/solicitar-alumno.page');

require('dotenv').config();

jest.setTimeout(60000)

//http://localhost:8080/alumnos/11223344/documentos?tipo=ACTA_NACIMIENTO

describe('CP #1 - Consultar información de Alumno', () => {
    let solicitarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        solicitarAlumnoPage = new SolicitarAlumnoPage(driver.driver);
    });

    test('Consultar los documentos e información de un alumno.\n', async () => {
        await solicitarAlumnoPage.open();

        let matriculaEsperada = "11223344";
        let nombreAlumnoEsperado = "Miguel Perez Elo Oleo";
        let fechaNacimientoAlumnoEsperada = "2004-06-07";

        await solicitarAlumnoPage.setMatriculaSearchText(matriculaEsperada);

        await solicitarAlumnoPage.clickSearchButton();

        // espera unos 4 segundos...
        await driver.driver.sleep(4000);

        const nombreAlumno = await solicitarAlumnoPage.getNombreAlumnoText();
        const matriculaAlumno = await solicitarAlumnoPage.getMatriculaAlumnoText();
        const fechaNacimiento = await solicitarAlumnoPage.getFechaNacimientoAlumnoText();
        //const nssAlumno = await solicitarAlumnoPage.getNSSAlumnoText();

        expect(nombreAlumno).toBe(nombreAlumnoEsperado);
        expect(matriculaAlumno).toBe(matriculaEsperada);
        expect(fechaNacimiento).toBe(fechaNacimientoAlumnoEsperada);
    });
});



describe('CP #2 - Ver CURP del alumno seleccionado', () => {
    let solicitarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        solicitarAlumnoPage = new SolicitarAlumnoPage(driver.driver);
    });

    test('Verificar que el sistema puede ver el documento curp en una ventana aparte.\n', async () => {
        await solicitarAlumnoPage.open();

        const matriculaEsperada = "11223344";
        const tiposDocumentosEsperados = [
            'ACTA_NACIMIENTO',
            'CERTIFICADO_SECUNDARIA',
            'CURP'
        ];

        await solicitarAlumnoPage.setMatriculaSearchText(matriculaEsperada);
        await solicitarAlumnoPage.clickSearchButton();

        await driver.driver.sleep(4000);

        const urlsDocumentos = await solicitarAlumnoPage.getDocumentosUrls();

        expect(urlsDocumentos.length).toBe(tiposDocumentosEsperados.length);

        for (let i = 0; i < urlsDocumentos.length; i++) {
            const url = urlsDocumentos[i];
            const tipoEsperado = tiposDocumentosEsperados[i];

            expect(url).toContain(`/alumnos/${matriculaEsperada}/documentos`);
            expect(url).toContain(`?tipo=${tipoEsperado}`);

            //console.log(`URL [${i + 1}] verificado: ${url}`);
        }
    });
});



describe('CP #3 - Ver Acta de nacimiento del alumno seleccionado', () => {
    let solicitarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        solicitarAlumnoPage = new SolicitarAlumnoPage(driver.driver);
    });

    test('Verificar que el sistema puede ver el documento acta de nacimiento en una ventana aparte.\n', async () => {
        await solicitarAlumnoPage.open();

        const matriculaEsperada = "11223344";
        const tiposDocumentosEsperados = [
            'ACTA_NACIMIENTO',
            'CERTIFICADO_SECUNDARIA',
            'CURP'
        ];

        await solicitarAlumnoPage.setMatriculaSearchText(matriculaEsperada);
        await solicitarAlumnoPage.clickSearchButton();

        await driver.driver.sleep(4000);

        const urlsDocumentos = await solicitarAlumnoPage.getDocumentosUrls();

        expect(urlsDocumentos.length).toBe(tiposDocumentosEsperados.length);

        for (let i = 0; i < urlsDocumentos.length; i++) {
            const url = urlsDocumentos[i];
            const tipoEsperado = tiposDocumentosEsperados[i];

            expect(url).toContain(`/alumnos/${matriculaEsperada}/documentos`);
            expect(url).toContain(`?tipo=${tipoEsperado}`);

            //console.log(`URL [${i + 1}] verificado: ${url}`);
        }
    });
});

describe('CP #4 - Ver Certificado de secundaria del alumno seleccionado', () => {
    let solicitarAlumnoPage;
    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        solicitarAlumnoPage = new SolicitarAlumnoPage(driver.driver);
    });

    test('Verificar que el sistema puede ver el documento certificado de secundaria en una ventana aparte.\n\n', async () => {
        await solicitarAlumnoPage.open();

        const matriculaEsperada = "11223344";
        const tiposDocumentosEsperados = [
            'ACTA_NACIMIENTO',
            'CERTIFICADO_SECUNDARIA',
            'CURP'
        ];

        await solicitarAlumnoPage.setMatriculaSearchText(matriculaEsperada);
        await solicitarAlumnoPage.clickSearchButton();

        await driver.driver.sleep(4000);

        const urlsDocumentos = await solicitarAlumnoPage.getDocumentosUrls();

        expect(urlsDocumentos.length).toBe(tiposDocumentosEsperados.length);

        for (let i = 0; i < urlsDocumentos.length; i++) {
            const url = urlsDocumentos[i];
            const tipoEsperado = tiposDocumentosEsperados[i];

            expect(url).toContain(`/alumnos/${matriculaEsperada}/documentos`);
            expect(url).toContain(`?tipo=${tipoEsperado}`);

            //console.log(`URL [${i + 1}] verificado: ${url}`);
        }
    });
});