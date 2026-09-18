document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-registrar-alumno");
    const statusMessage = document.getElementById("status-message");
    const checkCondicion = document.getElementById("condicionEspecial");
    const campoDescCondicion = document.getElementById("campo-descripcion-condicion");

    const STORAGE_KEY = 'registroAlumnoData';

    const formFieldIds = [
        'matricula', 'curp', 'nombre', 'apellidoPaterno', 'apellidoMaterno',
        'fechaNacimiento', 'nss', 'poliza', 'condicionEspecial', 'condicionDescripcion',
        'tutor_telefono', 'tutor_fechaNacimiento', 'tutor_nombre', 'tutor_apellidoPaterno', 'tutor_apellidoMaterno'
    ];

    function saveFormData() {
        const data = {};
        formFieldIds.forEach(id => {
            const field = document.getElementById(id);
            if (field) {
                if (field.type === 'checkbox') {
                    data[id] = field.checked;
                } else if (field.type !== 'file') {
                    data[id] = field.value;
                }
            }
        });

        // Guardar en localStorage
        if (Object.keys(data).length > 0) {
            try {
                localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
            } catch (e) {
                console.error("Error al guardar en localStorage:", e);
            }
        }
    }

    function loadFormData() {
        try {
            const savedData = localStorage.getItem(STORAGE_KEY);
            if (!savedData) return;

            const data = JSON.parse(savedData);

            formFieldIds.forEach(id => {
                const field = document.getElementById(id);
                if (field && data[id] !== undefined) {
                    if (field.type === 'checkbox') {
                        field.checked = data[id];
                        // Disparar evento 'change' para ejecutar la lógica de visibilidad/estado
                        field.dispatchEvent(new Event('change'));
                    } else if (field.type !== 'file') {
                        field.value = data[id];
                    }
                }
            });
            console.log("Datos de formulario cargados desde el respaldo (localStorage).");
        } catch (e) {
            console.error("Error al cargar datos desde localStorage:", e);
            // Limpiar datos corruptos
            localStorage.removeItem(STORAGE_KEY);
        }
    }

    loadFormData();

    formFieldIds.forEach(id => {
        const field = document.getElementById(id);
        if (field && field.type !== 'file') {
            const eventType = (field.type === 'text' || field.tagName === 'TEXTAREA' || field.type === 'number') ? 'input' : 'change';
            field.addEventListener(eventType, saveFormData);
        }
    });

    const fotoInput = document.getElementById("fotoAlumno");
    const fotoPreview = document.getElementById("fotoPreview");
    const placeholderImage = "../resources/placeholder-user.webp";

    fotoInput.addEventListener("change", () => {
        const file = fotoInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                fotoPreview.src = e.target.result;
            };
            reader.readAsDataURL(file);
        } else {
            fotoPreview.src = placeholderImage;
        }
    });


    const setValidation = (id, conditionFn, errorMessage) => {
        const input = document.getElementById(id);
        if (!input) return;

        const validate = () => {
            const value = input.value;
            if (value && !conditionFn(value)) {
                input.setCustomValidity(errorMessage);
            } else {
                input.setCustomValidity("");
            }
        };

        input.addEventListener("input", validate);
        input.addEventListener("change", validate);
        input.addEventListener("blur", validate);

        input.manualValidate = validate;

        // Ejecutar validación inicial
        validate();
    };

    const noNumerosSimbolos = val => /^[a-zA-Z\u00C0-\u017F\s]+$/.test(val);
    const curpValido = val => /^[A-Z0-9]{18}$/.test(val);


    setValidation("matricula", (val) => /^\d{8}$/.test(val), "La matrícula debe contener exactamente 8 dígitos.");
    setValidation("nombre", noNumerosSimbolos, "El formato del nombre no permite números ni símbolos.")
    setValidation("apellidoPaterno", noNumerosSimbolos, "El formato del apellido paterno no permite números ni símbolos.");
    setValidation("apellidoMaterno", noNumerosSimbolos, "El formato del apellido materno no permite números ni símbolos.");
    setValidation("curp", curpValido, "El formato de la CURP es incorrecto.")

    setValidation("fechaNacimiento", (val) => {
        if (!val) return true;
        const fecha = new Date(val);
        const hoy = new Date();
        return fecha < hoy;
    }, "La fecha de nacimiento debe ser anterior a la fecha actual.");

    setValidation("nss", (val) => {
        if (!val) return true;
        return /^\d{11}$/.test(val);
    }, "El NSS debe tener exactamente 11 dígitos.");

    setValidation("poliza", (val) => {
        if (!val) return true;
        return /^\d+$/.test(val);
    }, "El formato de la póliza solo acepta números.");

    setValidation("tutor_nombre", (val) => /^[a-zA-Z\u00C0-\u017F\s]+$/.test(val), "El formato del nombre no permite números ni símbolos.");
    setValidation("tutor_apellidoPaterno", (val) => /^[a-zA-Z\u00C0-\u017F\s]+$/.test(val), "El formato del apellido paterno no permite números ni símbolos.");
    setValidation("tutor_apellidoMaterno", (val) => /^[a-zA-Z\u00C0-\u017F\s]+$/.test(val), "El formato del apellido materno no permite números ni símbolos.");


    setValidation("tutor_fechaNacimiento", (val) => {
        if (!val) return true;
        const fechaLimite = new Date();
        fechaLimite.setFullYear(fechaLimite.getFullYear() - 18);
        fechaLimite.setHours(23, 59, 59, 999);
        const fechaNac = new Date(val + 'T00:00:00');
        return fechaNac.getTime() <= fechaLimite.getTime();

    }, "La edad del tutor debe ser de 18 años o más.");

    // CP #14: Teléfono del Tutor (10 dígitos)
    setValidation("tutor_telefono", (val) => /^\d{10}$/.test(val), "El formato del teléfono debe ser de 10 dígitos.");

    // Validar Archivos (PDF y Peso < 10MB) - Función Reutilizable
    const validarArchivo = (id, nombreArchivo) => {
        const input = document.getElementById(id);
        if (!input) return;

        const validateFile = () => {
            const file = input.files[0];
            if (file) {
                if (file.type !== "application/pdf") {
                    input.setCustomValidity(`El documento ${nombreArchivo} debe estar en formato PDF.`);
                } else if (file.size > 10 * 1024 * 1024) { // 10MB en bytes
                    input.setCustomValidity(`El documento ${nombreArchivo} excede el tamaño máximo de 10MB.`);
                } else {
                    input.setCustomValidity("");
                }
            } else {
                input.setCustomValidity("");
            }
        };
        input.addEventListener("change", validateFile);
    };

    validarArchivo("actaNacimiento", "Acta de Nacimiento");
    validarArchivo("certificadoSecundaria", "Certificado de Secundaria");
    validarArchivo("documentoCurp", "Documento CURP");

    // Lógica para mostrar/ocultar descripción de condición
    checkCondicion.addEventListener("change", () => {
        if (checkCondicion.checked) {
            campoDescCondicion.style.display = "block";
        } else {
            campoDescCondicion.style.display = "none";
            document.getElementById("condicionDescripcion").value = "";
        }
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const btn = document.getElementById("btn-registrar");
        btn.disabled = true;
        btn.textContent = "Registrando...";

        clearStatus();

        // --- RECOPILACIÓN DE DATOS DEL TUTOR LEGAL ---
        const tutorLegal = {
            nombre: document.getElementById("tutor_nombre").value,
            apellidoPaterno: document.getElementById("tutor_apellidoPaterno").value,
            apellidoMaterno: document.getElementById("tutor_apellidoMaterno").value,
            telefono: document.getElementById("tutor_telefono").value,
            fechaNacimiento: document.getElementById("tutor_fechaNacimiento").value || null
        };

        // 1. Recopilar datos del alumno (DTO)
        const alumno = {
            matricula: document.getElementById("matricula").value,
            curp: document.getElementById("curp").value.toUpperCase(),
            nombre: document.getElementById("nombre").value,
            apellidoPaterno: document.getElementById("apellidoPaterno").value,
            apellidoMaterno: document.getElementById("apellidoMaterno").value,
            fechaNacimiento: document.getElementById("fechaNacimiento").value,

            numeroSeguroSocial: document.getElementById("nss").value || null,
            numeroPolizaSeguro: document.getElementById("poliza").value || null,

            condicionEspecial: document.getElementById("condicionEspecial").checked,
            condicionEspecialDescripcion: document.getElementById("condicionDescripcion").value || null,

            tutorLegal: tutorLegal,
        };

        // --- RECOPILAR TODOS LOS ARCHIVOS ---
        const fotoFile = document.getElementById("fotoAlumno").files[0];
        const actaFile = document.getElementById("actaNacimiento").files[0];
        const certFile = document.getElementById("certificadoSecundaria").files[0];
        const curpFile = document.getElementById("documentoCurp").files[0];

        if (!fotoFile || !actaFile || !certFile || !curpFile) {
            showStatus("Debe adjuntar la foto y los 3 documentos requeridos.", "error");
            btn.disabled = false;
            btn.textContent = "Registrar Alumno";
            return;
        }

        try {
            // 2. Registrar los datos del alumno (esto también creará al tutor)
            const alumnoResponse = await fetch("http://localhost:8080/alumnos", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(alumno)
            });

            if (!alumnoResponse.ok) {
                const err = await alumnoResponse.json();
                throw new Error(err.error || "Error al registrar los datos del alumno.");
            }

            const alumnoCreado = await alumnoResponse.json();
            const matricula = alumnoCreado.matricula;
            showStatus(`Alumno ${matricula} registrado. Subiendo archivos...`, "success");

            // 3. Subir todos los documentos (4 archivos)
            await uploadDocument(matricula, fotoFile, "FOTO_ALUMNO"); // <--- TIPO CAMBIADO
            showStatus(`Foto de perfil subida...`, "success");

            await uploadDocument(matricula, actaFile, "ACTA_NACIMIENTO");
            showStatus(`Acta de nacimiento subida...`, "success");

            await uploadDocument(matricula, certFile, "CERTIFICADO_SECUNDARIA");
            showStatus(`Certificado de secundaria subido...`, "success");

            await uploadDocument(matricula, curpFile, "CURP");
            showStatus(`Documento CURP subido. ¡Registro completo!`, "success");

            form.reset();
            localStorage.removeItem(STORAGE_KEY);

            campoDescCondicion.style.display = "none";
            fotoPreview.src = placeholderImage;
            btn.disabled = false;
            btn.textContent = "Registrar Alumno";

        } catch (error) {
            console.error("Error en el registro:", error);
            showStatus(error.message, "error");
            btn.disabled = false;
            btn.textContent = "Registrar Alumno";
        }
    });

    async function uploadDocument(matricula, file, tipo) {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("tipo", tipo.toUpperCase());

        const response = await fetch(`http://localhost:8080/alumnos/${matricula}/documentos`, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.error || `Error al subir ${tipo}`);
        }
    }

    function showStatus(message, type) {
        statusMessage.textContent = message;
        statusMessage.className = `status-message status-${type}`;
        statusMessage.style.display = "block";
    }

    function clearStatus() {
        statusMessage.textContent = "";
        statusMessage.style.display = "none";
    }

    document.getElementById("btn-cancelar").addEventListener("click", (e) => {
        e.preventDefault();
        form.reset();
        clearStatus();

        localStorage.removeItem(STORAGE_KEY);

        campoDescCondicion.style.display = "none";
        fotoPreview.src = placeholderImage;
    });
});