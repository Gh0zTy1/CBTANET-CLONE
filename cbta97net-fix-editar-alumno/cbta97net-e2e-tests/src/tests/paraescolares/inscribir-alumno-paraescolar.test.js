const driver = require('../../setup/driver');
const { By, until } = require('selenium-webdriver');

const path = require('path');

const { InscribirAlumnoParaescolarPage } = require('../../pages/paraescolares/inscribir-alumno-paraescolar.page');

require('dotenv').config();

jest.setTimeout(60000)

describe('CP #1 - Inscribir un alumno a un grupo paraescolar exitosamente', () => {
    let inscribirAlumnoPage;

    const rutaRaiz = process.cwd();

    const normalizarTexto = (str) => str
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();

    beforeAll(() => {
        inscribirAlumnoPage = new InscribirAlumnoParaescolarPage(driver.driver);
    });

    test('Verificar que si se guarda un alumno en el grupo paraescolar.\n.\n', async () => {
        await inscribirAlumnoPage.open();

        let matriculaEsperada = "11223344";
        let nombreAlumnoEsperado = "Miguel Perez Elo Oleo";
        let fechaNacimientoAlumnoEsperada = "2004-06-07";

        await inscribirAlumnoPage.setMatriculaSearchText(matriculaEsperada);

        await inscribirAlumnoPage.clickSearchButton();

        // espera unos 4 segundos...
        await driver.driver.sleep(4000);

        const nombreAlumno = await inscribirAlumnoPage.getNombreAlumnoText();
        //const matriculaAlumno = await inscribirAlumnoPage.getMatriculaAlumnoText();
        //const fechaNacimiento = await inscribirAlumnoPage.getFechaNacimientoAlumnoText();

        expect(nombreAlumno).toBe(nombreAlumnoEsperado);
        //expect(matriculaAlumno).toBe(matriculaEsperada);
        //expect(fechaNacimiento).toBe(fechaNacimientoAlumnoEsperada);

        await inscribirAlumnoPage.inscribirAlumnoPorIndice(0);

        await inscribirAlumnoPage.acceptConfirmationAlert(3000);

        const message = await inscribirAlumnoPage.waitForAlert(3000);
        console.log(message);
        expect(normalizarTexto(message)).toContain('se ha inscrito correctamente al alumno en el grupo');
        //expect(normalizarTexto(message)).toContain('exito');
        //expect(normalizarTexto(message)).toContain('alumno');
    });
});

