document.addEventListener("DOMContentLoaded", () => {
    const btnBuscar = document.getElementById("btn-buscar");
    const inputMatricula = document.getElementById("matricula-search");
    const detalleContainer = document.getElementById("detalle-alumno-container");
    const statusMessage = document.getElementById("status-message");
    const httpBase = "http://localhost:8080"; // Base de tu API

    btnBuscar.addEventListener("click", buscarAlumno);
    inputMatricula.addEventListener("keyup", (e) => {
        if (e.key === "Enter") {
            buscarAlumno();
        }
    });

    async function buscarAlumno() {
        const matricula = inputMatricula.value.trim();
        if (!matricula) {
            showStatus("Por favor, ingrese una matrícula.", "error");
            return;
        }

        clearStatus();
        detalleContainer.style.display = "none";
        btnBuscar.disabled = true;
        btnBuscar.textContent = "Buscando...";

        try {
            // Llamamos al nuevo endpoint en el nuevo controlador
            const response = await fetch(`${httpBase}/alumnos/${matricula}`);

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.error || "Error al conectar con el servidor.");
            }

            const data = await response.json();
            renderizarDatos(data);
            detalleContainer.style.display = "block";

        } catch (error) {
            console.error("Error al buscar:", error);
            showStatus(error.message, "error");
        } finally {
            btnBuscar.disabled = false;
            btnBuscar.textContent = "Buscar";
        }
    }

    function renderizarDatos(data) {
        // ---- Foto y Nombre ----
        const fotoDTO = data.documentos?.find(d => d.tipoDocumento === "FOTO_ALUMNO");
        // Usamos la URL base y la URL relativa del DTO
        const fotoUrl = fotoDTO ? `${httpBase}${fotoDTO.urlDescarga}` : "../resources/placeholder-user.webp";
        
        document.getElementById("detalle-foto").src = fotoUrl;
        document.getElementById("detalle-nombre").textContent = `${data.nombre || ''} ${data.apellidoPaterno || ''} ${data.apellidoMaterno || ''}`;
        
        // ---- Datos Personales ----
        document.getElementById("detalle-matricula").textContent = data.matricula || "---";
        document.getElementById("detalle-curp").textContent = data.curp || "---";
        document.getElementById("detalle-nacimiento").textContent = data.fechaNacimiento || "---";
        document.getElementById("detalle-nss").textContent = data.numeroSeguroSocial || "No proporcionado";
        document.getElementById("detalle-poliza").textContent = data.numeroPolizaSeguro || "No proporcionado";

        // ---- Tutoría ----
        if (data.tutorLegal) {
            document.getElementById("detalle-tutor-legal").textContent = 
                `${data.tutorLegal.nombre || ''} ${data.tutorLegal.apellidoPaterno || ''} ${data.tutorLegal.apellidoMaterno || ''}`;
            document.getElementById("detalle-tutor-tel").textContent = data.tutorLegal.telefono || "---";
        } else {
             document.getElementById("detalle-tutor-legal").textContent = "No proporcionado";
             document.getElementById("detalle-tutor-tel").textContent = "---";
        }

        if (data.tutorAcademico) {
            document.getElementById("detalle-tutor-academico").textContent = 
                `${data.tutorAcademico.nombre || ''} ${data.tutorAcademico.apellidoPaterno || ''} ${data.tutorAcademico.apellidoMaterno || ''}`;
        } else {
            document.getElementById("detalle-tutor-academico").textContent = "Aún no asignado";
        }

        // ---- Área Educativa (Sin Paraescolares) ----
        const descContainer = document.getElementById("condicion-desc-container");
        if (data.condicionEspecial) {
            document.getElementById("detalle-condicion").textContent = "Sí";
            document.getElementById("detalle-condicion-desc").textContent = data.condicionEspecialDescripcion || "No hay descripción.";
            descContainer.style.display = "block";
        } else {
            document.getElementById("detalle-condicion").textContent = "No";
            document.getElementById("detalle-condicion-desc").textContent = "";
            descContainer.style.display = "none";
        }
        
        // ---- Documentos ----
        const ulDocumentos = document.getElementById("documentos-lista");
        if (data.documentos && data.documentos.length > 0) {
            ulDocumentos.innerHTML = "";
            const documentosFiltrados = data.documentos.filter(doc => doc.tipoDocumento !== "FOTO_ALUMNO");

            if (documentosFiltrados.length > 0) {
                documentosFiltrados.forEach(doc => {
                    const li = document.createElement("li");
                    const a = document.createElement("a");
                    a.href = `${httpBase}${doc.urlDescarga}`; // Usamos la URL completa
                    a.textContent = doc.nombreOriginal || doc.tipoDocumento;
                    a.target = "_blank"; // Abrir en nueva pestaña
                    li.appendChild(a);
                    ulDocumentos.appendChild(li);
                });
            } else {
                 ulDocumentos.innerHTML = "<li>No hay documentos adjuntos</li>";
            }
        } else {
            ulDocumentos.innerHTML = "<li>No hay documentos adjuntos</li>";
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
});