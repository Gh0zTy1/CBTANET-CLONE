document.addEventListener('DOMContentLoaded', () => {
    // --- CONFIGURACIÓN ---
    const HTTP_BASE = 'http://localhost:8080'; // Puerto de tu Backend Spring Boot

    // --- REFERENCIAS AL DOM ---
    const txtMatricula = document.getElementById('txtMatricula');
    const btnBuscar = document.getElementById('btnBuscar');
    const infoAlumnoContainer = document.getElementById('infoAlumnoContainer');
    const gruposSection = document.getElementById('gruposSection');
    const tbodyGrupos = document.getElementById('tbodyGrupos');
    const btnRecargarGrupos = document.getElementById('btnRecargarGrupos');
    const loadingGrupos = document.getElementById('loadingGrupos');
    const mensajeSinGrupos = document.getElementById('mensajeSinGrupos');

    // --- ESTADO LOCAL ---
    let alumnoActual = null;
    let gruposDisponibles = []; // <--- NUEVO: Aquí guardaremos la lista completa de objetos grupo

    // --- EVENTOS ---
    btnBuscar.addEventListener('click', buscarAlumno);
    btnRecargarGrupos.addEventListener('click', cargarGruposActivos);

    // Permitir buscar con la tecla Enter
    txtMatricula.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') buscarAlumno();
    });

    // =========================================================
    // LÓGICA DE NEGOCIO
    // =========================================================

    /**
     * 1. BUSCAR ALUMNO
     * Consume: GET /alumnos/{matricula}
     */
    async function buscarAlumno() {
        const matricula = txtMatricula.value.trim();
        if (!matricula) {
            alert("Por favor ingresa una matrícula válida.");
            return;
        }

        // UI: Bloquear botón mientras busca
        btnBuscar.disabled = true;
        btnBuscar.textContent = "Buscando...";
        limpiarInterfaz(); // Limpia datos anteriores

        try {
            const response = await fetch(`${HTTP_BASE}/alumnos/${encodeURIComponent(matricula)}`);

            if (response.ok) {
                const alumno = await response.json();
                mostrarInfoAlumno(alumno);
                // Si encontramos al alumno, cargamos los grupos
                cargarGruposActivos();
            } else if (response.status === 404) {
                alert("Alumno no encontrado. Verifica la matrícula e intenta de nuevo.");
            } else {
                const errorData = await response.json().catch(() => ({}));
                console.error("Error servidor:", errorData);
                alert("Error al buscar alumno. El servidor respondió con estado: " + response.status);
            }
        } catch (error) {
            console.error("Error de conexión:", error);
            alert("No se pudo conectar con el servidor. Verifica que el backend esté corriendo en el puerto 8080.");
        } finally {
            btnBuscar.disabled = false;
            btnBuscar.textContent = "Buscar Alumno";
        }
    }

    /**
     * Muestra los datos del alumno en la tarjeta de información.
     */
    function mostrarInfoAlumno(alumno) {
        alumnoActual = alumno; // Guardar en memoria para usar al inscribir

        // Construir nombre completo
        const nombreCompleto = `${alumno.nombre || ''} ${alumno.apellidoPaterno || ''} ${alumno.apellidoMaterno || ''}`.trim();

        // Asignar valores al HTML
        document.getElementById('lblNombreCompleto').textContent = nombreCompleto;

        // Validar campos opcionales que vienen en tu DTO
        document.getElementById('lblCarrera').textContent = alumno.carreraTecnica ? alumno.carreraTecnica.nombre : "Tronco Común / No asignada";
        document.getElementById('lblSemestre').textContent = alumno.semestre ? `${alumno.semestre}° Semestre` : "N/A";

        // Mostrar secciones
        infoAlumnoContainer.classList.remove('hidden');
        gruposSection.classList.remove('hidden');
    }

    /**
     * 2. CARGAR GRUPOS
     * Consume: GET /paraescolares/grupos/activos?page=0&size=100
     */
    async function cargarGruposActivos() {
        tbodyGrupos.innerHTML = ''; // Limpiar tabla
        loadingGrupos.classList.remove('hidden');
        mensajeSinGrupos.classList.add('hidden');

        try {
            // Solicitamos size=100 para traer todos los grupos en una sola vista
            const response = await fetch(`${HTTP_BASE}/paraescolares/grupos/activos?page=0&size=100`);

            if (response.ok) {
                const data = await response.json();

                // Spring Boot devuelve un objeto Page, la lista real está en ".content"
                const listaGrupos = data.content || [];

                // --- CAMBIO IMPORTANTE ---
                gruposDisponibles = listaGrupos; // Guardamos los objetos completos en memoria global

                renderizarTablaGrupos(listaGrupos);
            } else {
                console.error("Error al cargar grupos:", response.status);
                alert("No se pudieron cargar los grupos disponibles.");
            }
        } catch (error) {
            console.error("Error de red cargando grupos:", error);
            alert("Error de conexión al cargar los grupos.");
        } finally {
            loadingGrupos.classList.add('hidden');
        }
    }

    /**
     * Renderiza las filas de la tabla de grupos.
     */
    function renderizarTablaGrupos(grupos) {
        if (!grupos || grupos.length === 0) {
            mensajeSinGrupos.classList.remove('hidden');
            return;
        }

        grupos.forEach(grupo => {
            // Cálculo de cupo basado en tu DTO
            const inscritos = Array.isArray(grupo.alumnosInscritos) ? grupo.alumnosInscritos.length : (grupo.totalInscritos || 0);
            const cupoMax = grupo.maximoEspacios || 0;
            const disponible = cupoMax - inscritos;
            const estaLleno = disponible <= 0;

            const tr = document.createElement('tr');

            tr.innerHTML = `
                <td>
                    <strong>${grupo.actividadParaescolar?.nombre || 'Sin Nombre'}</strong><br>
                    <small class="text-muted">${grupo.actividadParaescolar?.descripcion || ''}</small>
                </td>
                <td>${grupo.docente ? (grupo.docente.nombre + ' ' + grupo.docente.apellidoPaterno) : 'Por asignar'}</td>
                <td>${formatearHorarios(grupo.horarios)}</td>
                <td class="text-center">
                    <span class="cupo-badge ${estaLleno ? 'cupo-full' : 'cupo-ok'}">
                        ${inscritos} / ${cupoMax}
                    </span>
                </td>
                <td class="text-center">
                    <button class="btn btn-inscribir btn-sm" 
                        onclick="inscribirAlumno(${grupo.id})" 
                        ${estaLleno ? 'disabled' : ''}>
                        ${estaLleno ? 'Lleno' : 'Inscribir'}
                    </button>
                </td>
            `;
            tbodyGrupos.appendChild(tr);
        });
    }

    function formatearHorarios(horarios) {
        if (!horarios || horarios.length === 0) return 'Sin horario definido';
        return horarios.map(h => {
            const diaCorto = h.dia.substring(0, 3);
            return `${diaCorto} ${h.horaInicio}-${h.horaFin}`;
        }).join(', ');
    }

    /**
     * 3. INSCRIBIR ALUMNO
     * Flujo: Botón(ID) -> Fetch Info(ID) -> Confirmar -> Fetch POST(ID, Matricula)
     */
    window.inscribirAlumno = async function(idDelBoton) {

        // DEBUG: Verificar que el botón sí trajo un ID
        console.log("ID recibido del botón:", idDelBoton);
        if (!idDelBoton) {
            alert("Error interno: El ID del grupo es nulo. Recarga la página.");
            return;
        }

        // 1. Validaciones previas
        if (!alumnoActual) {
            alert("⚠️ Error: No has seleccionado ningún alumno.");
            return;
        }
        const matriculaAEnviar = txtMatricula.value.trim();

        try {
            // PASO A: Traer el objeto grupo fresco desde el backend
            // Esto confirma que el ID es válido en la base de datos
            const responseGrupo = await fetch(`${HTTP_BASE}/paraescolares/grupos/${idDelBoton}`);

            if (!responseGrupo.ok) {
                throw new Error("No se pudo obtener la info del grupo (404/500).");
            }

            const grupoCompleto = await responseGrupo.json();

            // VALIDACIÓN CRÍTICA: ¿El backend devolvió el ID?
            if (!grupoCompleto.id) {
                throw new Error("El Backend devolvió el grupo SIN ID. Revisa GrupoParaescolarDTO.java");
            }

            // PASO B: Confirmación Visual
            const nombreActividad = grupoCompleto.actividadParaescolar?.nombre || "Actividad";
            const confirma = confirm(`¿Inscribir a ${alumnoActual.nombre} en ${nombreActividad}?`);

            if (!confirma) return;

            // PASO C: Envío final (Payload)
            const payload = {
                matricula_alumno: matriculaAEnviar, // Coincide con @JsonProperty
                grupo_id: grupoCompleto.id          // Coincide con @JsonProperty
            };

            console.log("Enviando Payload:", payload); // DEBUG FINAL

            const responseInscripcion = await fetch(`${HTTP_BASE}/paraescolares/grupos/inscripciones`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await responseInscripcion.json();

            if (responseInscripcion.ok) {
                alert(`✅ Éxito: ${data.mensaje}`);
                cargarGruposActivos();
            } else {
                alert(`⚠️ Error: ${data.mensaje}`);
            }

        } catch (error) {
            console.error(error);
            alert("❌ Error técnico: " + error.message);
        }
    };
    /**
     * Limpia la interfaz para una nueva búsqueda
     */
    function limpiarInterfaz() {
        infoAlumnoContainer.classList.add('hidden');
        gruposSection.classList.add('hidden');
        document.getElementById('lblNombreCompleto').textContent = '--';
        document.getElementById('lblCarrera').textContent = '--';
        document.getElementById('lblSemestre').textContent = '--';
    }
});