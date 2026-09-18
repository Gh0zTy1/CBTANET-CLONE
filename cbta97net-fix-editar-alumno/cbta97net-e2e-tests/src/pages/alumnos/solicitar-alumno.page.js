const { By, until } = require('selenium-webdriver');
const { BASE_URL } = require('../../utils/config');
const path = require('path');

class SolicitarAlumnoPage {

    constructor(driver) {
        this.driver = driver;
        this.url = `${BASE_URL}/SolicitarAlumno/SolicitarAlumno.html`;

        // informacion basica alumno
        this.nombreAlumnoText = By.id('detalle-nombre');
        this.matriculaAlumnoText = By.id('detalle-matricula');
        this.fechaNacimientoAlumnoDateField = By.id('detalle-nacimiento');
        this.nssAlumnoText = By.id('detalle-nss');
        this.documentosLista = By.id('documentos-lista');
        this.documentosEnlaces = By.css('#documentos-lista li a');
        //this.matriculaAlumno = By.id('detalle-poliza');
        this.curpLink = By.css('a[href*="tipo=CURP"]');
        this.actaLink = By.css('a[href*="tipo=ACTA_NACIMIENTO"]');
        this.certificadoLink = By.css('a[href*="tipo=CERTIFICADO_SECUNDARIA"]');

        // informacion del tutor
        this.nombreTutorText = By.id('detalle-tutor-legal');
        this.telefonoTutorText= By.id('detalle-tutor-tel');

        // busqueda
        this.searchMatriculaField = By.id('matricula-search');
        this.searchAlumnoBtn = By.id('btn-buscar');
    }

    async open() {
        await this.driver.get(this.url);
    }

    async clickCurpLink() {
        await this.driver.findElement(this.curpLink).click();
    }

    async clickActaLink() {
        await this.driver.findElement(this.actaLink).click();
    }

    async clickCertificadoLink() {
        await this.driver.findElement(this.certificadoLink).click();
    }

    async getDocumentosUrls() {
        const enlaces = await this.driver.findElements(this.documentosEnlaces);

        // Mapea el array de elementos para obtener el atributo 'href' de cada uno
        const urls = await Promise.all(
            enlaces.map(enlace => enlace.getAttribute('href'))
        );
        return urls;
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
     * Obtiene el texto de la fecha de nacimiento del alumno.
     * @returns {Promise<string>} La fecha de nacimiento.
     */
    async getFechaNacimientoAlumnoText() {
        return this.#getTextFromElement(this.fechaNacimientoAlumnoDateField);
    }

    /**
     * Obtiene el texto del Número de Seguridad Social (NSS) del alumno.
     * @returns {Promise<string>} El NSS.
     */
    async getNSSAlumnoText() {
        return this.#getTextFromElement(this.nssAlumnoText);
    }


    /**
     * Obtiene el texto del nombre del tutor legal.
     * @returns {Promise<string>} El nombre del tutor.
     */
    async getNombreTutorText() {
        return this.#getTextFromElement(this.nombreTutorText);
    }

    /**
     * Obtiene el texto del teléfono del tutor.
     * @returns {Promise<string>} El teléfono del tutor.
     */
    async getTelefonoTutorText() {
        return this.#getTextFromElement(this.telefonoTutorText);
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

module.exports = { SolicitarAlumnoPage };