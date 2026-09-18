const { By, until } = require('selenium-webdriver');
const { BASE_URL } = require('../../utils/config');
const path = require('path');

class RegistrarAlumnoPage {

    constructor(driver) {
        this.driver = driver;
        this.url = `${BASE_URL}/RegistrarAlumno/RegistrarAlumno.html`;

        // informacion basica alumno
        this.fotoAlumnoField = By.id('fotoAlumno');
        this.matriculaAlumnoField = By.id('matricula');
        this.nombreAlumnoField = By.id('matricula');
        this.curpAlumnoField = By.id('curp');
        this.nombreAlumnoField = By.id('nombre');
        this.apellidoPaternoAlumnoField = By.id('apellidoPaterno');
        this.apellidoMaternoAlumnoField = By.id('apellidoMaterno');
        this.fechaNacimientoAlumnoField = By.id('fechaNacimiento');
        this.nssAlumnoField = By.id('nss');
        this.polizaAlumnoField = By.id('poliza');
        this.condicionEspecialAlumnoCheckbox = By.id('condicionEspecial');
        this.condicionEspecialDescripcionAlumnoField = By.id('condicionDescripcion');

        // documentos del alumno
        this.docActaNacimientoAlumnoField = By.id('actaNacimiento');
        this.docCertificadoSecundariaAlumnoField = By.id('certificadoSecundaria');
        this.docCURPAlumnoField = By.id('documentoCurp');

        // tutor legal del alumno.
        this.nombreTutorField = By.id('tutor_nombre');
        this.apellidoPaternoTutorField = By.id('tutor_apellidoPaterno');
        this.apellidoMaternoTutorField = By.id('tutor_apellidoMaterno');
        this.telefonoTutorField = By.id('tutor_telefono');
        this.fechaNacimientoTutorField = By.id('tutor_fechaNacimiento');

        this.registrarAlumnoBtn = By.id('btn-registrar');
        this.connectionErrorAlert = By.id('status-message');
    }

    async open() {
        await this.driver.get(this.url);
    }

    /*
    async login(email, password) {
        // 1. Espera a que el campo de email aparezca y sea visible/interactuable
        await this.driver.wait(until.elementLocated(this.emailField), 5000, 'El campo de email no apareció en 5 segundos');
        // Nota: findElement(this.emailField) ya te da el elemento, no necesitas pasarlo como parámetro a findElement
        await this.driver.wait(until.elementIsVisible(this.driver.findElement(this.emailField)), 5000, 'El campo de email no es visible en 5 segundos');

        // 2. Interacción: usa los campos definidos
        await this.driver.findElement(this.emailField).sendKeys(email);
        await this.driver.findElement(this.passwordField).sendKeys(password);
        await this.driver.findElement(this.loginButton).click();
    }

    async llenarInformacionBasicaAlumno(matricula, curp, nombres, apellidoPaterno, apellidoMaterno) {
        await this.driver.findElement(this.matriculaAlumnoField).sendKeys(matricula);
        await this.driver.findElement(this.curpAlumnoField).sendKeys(curp);
        await this.driver.findElement(this.nombreAlumnoField).sendKeys(nombres);
        await this.driver.findElement(this.apellidoPaternoAlumnoField).sendKeys(apellidoPaterno);
        await this.driver.findElement(this.apellidoMaternoAlumnoField).sendKeys(apellidoMaterno);
    }*/

    async asignarMatriculaAlumno(matricula) {
        await this.driver.findElement(this.matriculaAlumnoField).sendKeys(matricula);
    }

    async asignarCURPAlumno(curp) {
        await this.driver.findElement(this.curpAlumnoField).sendKeys(curp);
    }

    async asignarNombreAlumno(nombre) {
        await this.driver.findElement(this.nombreAlumnoField).sendKeys(nombre);
    }

    async asignarApellidoPaternoAlumno(apellidoPaterno) {
        await this.driver.findElement(this.apellidoPaternoAlumnoField).sendKeys(apellidoPaterno);
    }

    async asignarApellidoMaternoAlumno(apellidoMaterno) {
        await this.driver.findElement(this.apellidoMaternoAlumnoField).sendKeys(apellidoMaterno);
    }

    async asignarFechaNacimientoAlumno(fechaNacimiento) {
        await this.driver.findElement(this.fechaNacimientoAlumnoField).sendKeys(fechaNacimiento);
    }

    async asignarNSSAlumno(nss) {
        await this.driver.findElement(this.nssAlumnoField).sendKeys(nss);
    }

    async asignarPolizaSeguroAlumno(poliza) {
        await this.driver.findElement(this.polizaAlumnoField).sendKeys(poliza);
    }

    async asignarFotoAlumno(rutaArchivo) {
        await this.driver.findElement(this.fotoAlumnoField).sendKeys(rutaArchivo);
    }

    async asignarNombreTutor(nombre) {
        await this.driver.findElement(this.nombreTutorField).sendKeys(nombre);
    }

    async asignarApellidoPaternoTutor(apellidoPaterno) {
        await this.driver.findElement(this.apellidoPaternoTutorField).sendKeys(apellidoPaterno);
    }

    async asignarApellidoMaternoTutor(apellidoMaterno) {
        await this.driver.findElement(this.apellidoMaternoTutorField).sendKeys(apellidoMaterno);
    }

    async asignarNumeroTelefonoTutor(telefono) {
        const element = this.driver.findElement(this.telefonoTutorField);
        element.clear();
        element.sendKeys(telefono);
    }

    async asignarFechaNacimientoTutor(fechaNacimiento) {
        const element = await this.driver.findElement(this.fechaNacimientoTutorField);
        await element.clear();

        await this.driver.executeScript("arguments[0].value = arguments[1];", element, fechaNacimiento);

        // 2. Disparar eventos MÚLTIPLES para despertar al navegador
        // Algunos frameworks escuchan 'input', otros 'change', otros 'blur'. Disparamos todos.
        await this.driver.executeScript(`
            arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
            arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
            arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));
        `, element);
    }

    async introducirDocumentoActaNacimientoAlumno(rutaActaNacimiento) {
        await this.driver.findElement(this.docActaNacimientoAlumnoField).sendKeys(rutaActaNacimiento);
    }

    async introducirDocumentoCertificadoSecundariaAlumno(rutaCertificado) {
        await this.driver.findElement(this.docCertificadoSecundariaAlumnoField).sendKeys(rutaCertificado);
    }

    async introducirDocumentoCURPAlumno(rutaCURP) {
        await this.driver.findElement(this.docCURPAlumnoField).sendKeys(rutaCURP);
    }

    async toggleCondicionEspecialAlumno() {
        // Localiza el checkbox por su ID y haz clic en él
        await this.driver.findElement(this.condicionEspecialAlumnoCheckbox).click();
    }

    async isCondicionEspecialDescripcionVisible() {
        // Busca el elemento de descripción de la condición especial
        const elements = await this.driver.findElements(this.condicionEspecialDescripcionAlumnoField);

        // Si no se encuentra en el DOM (length === 0), está oculto.
        if (elements.length === 0) {
            return false;
        }

        // Si se encuentra, verifica su visibilidad.
        try {
            const isVisible = await elements[0].isDisplayed();
            return isVisible;
        } catch (e) {
            // En caso de error de visibilidad o de elemento no adjunto, asumimos que no está visible
            return false;
        }
    }

    async clickRegistrarAlumno() {
        await this.driver.findElement(this.registrarAlumnoBtn).click();
    }


    async getOnScreenConnectionErrorMessage() {
        try {
            // Esperamos a que el elemento se encuentre en el DOM (máx. 5 segundos)
            const errorElement = await this.driver.wait(
                until.elementLocated(this.connectionErrorAlert),
                5000
            );

            // 1. Verificamos que esté visible
            if (await errorElement.isDisplayed()) {
                // 2. Si está visible, retornamos el texto
                return await errorElement.getText();
            }

            // 3. Si se encuentra pero NO está visible, retornamos null
            return null;

        } catch (error) {
            // Si hay un TimeoutError (el elemento nunca apareció) o cualquier otro error,
            // retornamos null. ESTO EVITA EL ERROR DE 'undefined.length'.
            return null;
        }
    }

    /**
     * Espera un tiempo determinado por una alerta "alert()" de JavaScript.
     * @param timeout
     * @returns {Promise<!Promise<string>|null>}
     */
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
            //console.error('[!] No apareció ningún alert dentro del tiempo esperado');
            return null;
        }
    }

    /**
     * Limpia explícitamente el respaldo de datos del LocalStorage.
     * La clave 'registroAlumnoData' fue la utilizada para guardar los datos del formulario.
     * @returns {Promise<void>}
     */
    async clearLocalStorageBackup() {
        await this.driver.executeScript(`window.localStorage.removeItem('registroAlumnoData');`);
    }

    async llenarCamposExceptoDocumentos(registrarAlumnoPage) {

        // Datos Válidos para Alumno (pasando CP #2 a #14)
        await registrarAlumnoPage.asignarMatriculaAlumno("19040042");
        await registrarAlumnoPage.asignarCURPAlumno("EOLE049815HERRELA1");
        await registrarAlumnoPage.asignarNombreAlumno("Carlos");
        await registrarAlumnoPage.asignarApellidoPaternoAlumno("Perez");
        await registrarAlumnoPage.asignarApellidoMaternoAlumno("Moreno");
        // Asumiendo que 2000-01-01 es una fecha válida para alumno (mayor de edad mínima)
        await registrarAlumnoPage.asignarFechaNacimientoAlumno("2000-01-01");
        await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");
        // No toggle Condicion Especial para mantener el formulario simple (y el campo de descripcion oculto)

        // Datos Válidos para Tutor (pasando CP #10 a #14)
        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        // Asumiendo que 1970-01-01 es una fecha válida para tutor (mayor de 18 años)
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");

    }

    async llenarCamposDatosValidos(registrarAlumnoPage, rutaRaiz) {
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
        await registrarAlumnoPage.asignarNSSAlumno("01234567899");
        await registrarAlumnoPage.asignarPolizaSeguroAlumno("2474982004");

        await registrarAlumnoPage.asignarNombreTutor("David");
        await registrarAlumnoPage.asignarApellidoPaternoTutor("Sanchez");
        await registrarAlumnoPage.asignarApellidoMaternoTutor("Lopez");
        await registrarAlumnoPage.asignarNumeroTelefonoTutor("6444000000");
        await registrarAlumnoPage.asignarFechaNacimientoTutor("1970-01-01");
    }

    /**
     * Obtiene el mensaje de validación HTML5 de un campo.
     * Esto funciona para inputs con required, type="email", etc.
     */
    async getNativeValidationError(locator) {


        const element = await this.driver.findElement(locator);

        // 1. Forzar la visualización de la burbuja nativa ANTES de leerla
        // La validación ya se ejecutó con el evento 'change', esto solo la muestra/actualiza.
        await this.driver.executeScript("arguments[0].reportValidity();", element);

        // 2. Leer el mensaje
        return element.getAttribute('validationMessage');
        /*
        const element = await this.driver.findElement(locator);
        const validationMessage = await this.driver.executeScript(
            // El script usa el API de validación nativa del elemento DOM
            'return arguments[0].validationMessage;',
            element // Pasa el elemento de Selenium como argumento[0]
        );
        return validationMessage;

         */
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

module.exports = { RegistrarAlumnoPage };