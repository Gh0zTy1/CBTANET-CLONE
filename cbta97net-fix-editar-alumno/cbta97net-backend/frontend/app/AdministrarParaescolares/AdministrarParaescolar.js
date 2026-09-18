const API_BASE = "http://localhost:8080/paraescolares";
let paraescolares = [];
let editId = null;
let deleteId = null;

function showError(message){
    alert(message || "Ocurrió un error. Intenta nuevamente.");
}

function renderTable() {
    const tbody = document.querySelector("#tablaParaescolares tbody");
    tbody.innerHTML = "";
    paraescolares.forEach((p) => {
        const nombre = p.nombre ?? p.actividad; // compat fallback
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><strong>${nombre}</strong></td>
            <td>${p.descripcion ?? ""}</td>
            <td>
                <div class="actions">
                    <button class="icon-btn" title="Editar" onclick="editParaescolar(${p.id})"><i class="fas fa-pencil-alt"></i></button>
                    <button class="icon-btn delete" title="Eliminar" onclick="deleteParaescolar(${p.id})"><i class="fas fa-trash"></i></button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openModal() {
    editId = null;
    document.getElementById("modalTitle").textContent = "Nueva Paraescolar";
    document.getElementById("actividadNombre").value = "";
    document.getElementById("actividadDescripcion").value = "";
    document.getElementById("modalParaescolar").style.display = "flex";
}

function closeModal() {
    document.getElementById("modalParaescolar").style.display = "none";
}

function editParaescolar(id) {
    editId = id;
    const p = paraescolares.find(x => x.id === id);
    document.getElementById("modalTitle").textContent = "Editar Paraescolar";
    document.getElementById("actividadNombre").value = p.nombre ?? p.actividad;
    document.getElementById("actividadDescripcion").value = p.descripcion;
    document.getElementById("modalParaescolar").style.display = "flex";
}

document.getElementById("guardarBtn").addEventListener("click", () => {
    const nombre = document.getElementById("actividadNombre").value.trim();
    const descripcion = document.getElementById("actividadDescripcion").value.trim();

    if(nombre === "" || descripcion === ""){
        alert("Por favor llena todos los campos.");
        return;
    }

    if(editId !== null){
        // PATCH (include id in DTO in case mapper requires it)
        fetch(`${API_BASE}/${editId}`, {
            method: "PATCH",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ id: editId, nombre, descripcion })
        })
        .then(r => {
            if(r.status !== 200) throw new Error("No se pudo actualizar");
            return r.json();
        })
        .then(() => {
            closeModal();
            cargarParaescolares();
        })
        .catch(() => showError("Error al actualizar la actividad."));
        } else {
        // POST
        fetch(API_BASE, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ nombre, descripcion })
        })
        .then(r => {
            if(r.status !== 201) throw new Error("No se pudo crear");
            return r.json();
        })
        .then(() => {
            closeModal();
            cargarParaescolares();
        })
        .catch(() => showError("Error al crear la actividad."));
    }
});

function searchTable(value){
    value = value.toLowerCase();
    const rows = document.querySelectorAll("#tablaParaescolares tbody tr");
    rows.forEach(row => {
        const actividad = row.cells[0].textContent.toLowerCase();
        const descripcion = row.cells[1].textContent.toLowerCase();
        if(actividad.includes(value) || descripcion.includes(value)){
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
}

// Eliminar
function deleteParaescolar(id){
    deleteId = id;
    document.getElementById("modalEliminar").style.display = "flex";
}

document.getElementById("confirmDeleteBtn").addEventListener("click", () => {
    if (deleteId !== null) {
        fetch(`${API_BASE}/${deleteId}`, { method: "DELETE" })
            .then(async r => {
                const data = await r.json().catch(() => ({}));

                if (!r.ok) {
                    const msg = data.mensaje || "Ocurrió un error en el servidor.";
                    throw new Error(msg);
                }

                return data; // por si necesitas algo
            })
            .then(() => {
                deleteId = null;
                closeDeleteModal();
                cargarParaescolares();
            })
            .catch(error => showError(error.message));
    }
});

function closeDeleteModal(){
    document.getElementById("modalEliminar").style.display = "none";
}

function cargarParaescolares(){
    fetch(API_BASE)
        .then(r => {
            if(!r.ok) throw new Error("No se pudo listar");
            return r.json();
        })
        .then(data => {
            // data: List<ActividadParaescolarDTO> { id, nombre, descripcion }
            paraescolares = Array.isArray(data) ? data : [];
            renderTable();
        })
        .catch(() => showError("Error al cargar las actividades."));
}

// Inicializar con datos del backend
document.addEventListener("DOMContentLoaded", cargarParaescolares);
