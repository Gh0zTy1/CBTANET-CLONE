const { By, until } = require('selenium-webdriver');
const { BASE_URL } = require('../../utils/config');
const path = require('path');

class InscribirAlumnoParaescolarPage {

    constructor(driver) {
        this.driver = driver;
        this.url = `${BASE_URL}/InscribirAlumnoParaescolar/InscribirAlumnoParaescolar.html`;

        // informacion basica alumno
        this.nombreAlumnoText = By.id('lblNombreCompleto');
        //this.matriculaAlumnoText = By.id('');

        // busqueda
        this.searchMatriculaField = By.id('txtMatricula');
        this.searchAlumnoBtn = By.id('btnBuscar');

        this.tbodyGrupos = By.id('tbodyGrupos');
        this.filasGrupos = By.css('#tbodyGrupos tr');
        this.btnInscribir = By.css('.btn-inscribir');
    }

    async open() {
        await this.driver.get(this.url);
    }

    async waitForAlert(timeout = 5000) {
        // ... (rest of the code is fine)
        try {
            // Espera hasta que aparezca un alert
            await this.driver.wait(until.alertIsPresent(), timeout);

            // Cambia el foco al alert
            const alert = await this.driver.switchTo().alert();

            // Lee el texto del alert (puede variar)
            const text = await alert.getText();

            // Cierra el alert (aceptar)
            await alert.accept();

            // Devuelve el texto por si lo necesitas en el test
            return text;
        } catch (err) {

            return null;
        }
    }

    /**
     * Espera a que aparezca una alerta/confirmación en el navegador y la acepta (hace clic en 'Aceptar').
     * @param {number} timeoutMs El tiempo máximo de espera en milisegundos (ej: 5000).
     */
    async acceptConfirmationAlert(timeoutMs = 5000) {// Tu instancia de WebDriver

        try {
            await this.driver.wait(until.alertIsPresent(), timeoutMs);
            const alert = await this.driver.switchTo().alert();
            const alertText = await alert.getText();
            console.log(`Texto de la confirmación: ${alertText}`);
            await alert.accept();

        } catch (error) {
            if (error.name === 'TimeoutError') {
                throw new Error(`Timeout: La alerta de confirmación no apareció después de ${timeoutMs}ms.`);
            }
            throw error;
        }
    }

    async getAllGruposInfo() {
        const filas = await this.driver.findElements(this.filasGrupos);

        const gruposData = [];

        for (const fila of filas) {
            const celdas = await fila.findElements(By.css('td'));

            if (celdas.length >= 5) {
                // Extraer información de las celdas
                const nombreGrupo = await celdas[0].findElement(By.css('strong')).getText();
                const descripcion = await celdas[0].findElement(By.css('small')).getText();
                const profesor = await celdas[1].getText();
                const horario = await celdas[2].getText();
                const cupo = await celdas[3].getText();

                // Encontrar el botón de inscripción dentro de la última celda
                const botonInscribir = await celdas[4].findElement(this.btnInscribir);

                gruposData.push({
                    nombre: nombreGrupo,
                    descripcion: descripcion,
                    profesor: profesor,
                    horario: horario,
                    cupo: cupo.trim(),
                    botonElement: botonInscribir,
                    habilitado: !(await botonInscribir.getAttribute('disabled'))
                });
            }
        }

        return gruposData;
    }

    /**
     * Hace clic en el botón de 'Inscribir' para un grupo específico (por índice).
     * NOTA: Este método asume que el índice 0 es el primer grupo visible.
     * @param {number} index El índice del grupo que se quiere inscribir (0-based).
     */
    async inscribirAlumnoPorIndice(index) {
        const grupos = await this.getAllGruposInfo();

        if (index >= grupos.length) {
            throw new Error(`Índice ${index} fuera del rango de grupos (${grupos.length}).`);
        }

        const grupoAInscribir = grupos[index];

        if (grupoAInscribir.habilitado) {
            await grupoAInscribir.botonElement.click();
        } else {
            throw new Error(`El grupo en el índice ${index} está lleno o deshabilitado para inscripción.`);
        }
    }

    /**
     * Función interna para encontrar un elemento y obtener su texto.
     * @param {By} locator El localizador del elemento (e.g., this.nombreAlumnoText).
     * @returns {Promise<string>} El texto del elemento.
     */
    async #getTextFromElement(locator) {
        // Podrías añadir una espera implícita o explícita aquí si es necesario
        const element = await this.driver.findElement(locator);
        return element.getText();
    }

    /**
     * Ingresa la matrícula de un alumno en el campo de búsqueda.
     * @param {string} matricula La matrícula a buscar.
     */
    async setMatriculaSearchText(matricula) {
        const searchField = await this.driver.findElement(this.searchMatriculaField);
        await searchField.clear();
        await searchField.sendKeys(matricula);
    }

    /**
     * Hace clic en el botón de búsqueda.
     */
    async clickSearchButton() {
        const searchButton = await this.driver.findElement(this.searchAlumnoBtn);
        await searchButton.click();
    }

    /**
     * Obtiene el texto del nombre completo del alumno.
     * @returns {Promise<string>} El nombre del alumno.
     */
    async getNombreAlumnoText() {
        return this.#getTextFromElement(this.nombreAlumnoText);
    }

    /**
     * Obtiene el texto de la matrícula del alumno.
     * @returns {Promise<string>} La matrícula del alumno.
     */
    async getMatriculaAlumnoText() {
        return this.#getTextFromElement(this.matriculaAlumnoText);
    }

    /**
     * Intercepta la funcion nativa de XMLHttpRequest del navegador para hacer que las peticiones
     * se pierdan y cause un error con el servidor (500). Simulando un fallo de conexion.
     */
    async setupConnectionRefusedMock() {
        await this.driver.executeScript(`
        if (window.XMLHttpRequest) {
            const originalXHRSend = XMLHttpRequest.prototype.send;
            XMLHttpRequest.prototype.send = function() {
                // Simular un estado de conexión fallida: status 0
                if (this.onerror) {
                    const fakeErrorEvent = new Event('error');
                    this.onerror(fakeErrorEvent);
                }
                Object.defineProperty(this, 'readyState', { value: 4 });
                Object.defineProperty(this, 'status', { value: 0 });
                if (this.onloadend) {
                    this.onloadend(); 
                }
                console.log('Interceptado XHR. Forzando fallo de conexión.');
                // No llamar a originalXHRSend.apply para evitar la petición real.
            };
        }

        if (window.fetch) {
            const originalFetch = window.fetch;
            window.fetch = function() {
                console.log('Interceptado fetch(). Forzando fallo de conexión (Network Error).');
                // Devolvemos una Promise que rechaza para simular un fallo de red real (e.g., TypeError)
                return new Promise((resolve, reject) => {
                    // El TypeError es lo que la aplicación espera en un fallo de red puro.
                    reject(new TypeError('Failed to fetch. No se pudo conectar a la red.')); 
                });
            };
        }
        
        console.log('Mocking de XMLHttpRequest.send y fetch() ejecutado.');
    `);
    }
}

module.exports = { InscribirAlumnoParaescolarPage };