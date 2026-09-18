window.materiaActualLi = null;
window.clasesParaescolares = []; 

document.addEventListener('DOMContentLoaded', async () => {
  await cargarActividades();
  await cargarCicloEscolar();
  generarTablaHorario();
  await cargarModal();
  configurarListenersPrincipales();

  const selectActividades = document.getElementById("actividades");
  const listaActividadesAsignadas = document.getElementById("assigned-actividades-list");


  if (selectActividades && listaActividadesAsignadas) {
    selectActividades.addEventListener("change", function () {
      const actividadId = this.value;
      const actividadTexto = this.options[this.selectedIndex].text;

      if (!actividadId) return;

      // Verificar si ya hay una actividad agregada
      if (listaActividadesAsignadas.querySelector('li')) {
        mostrarModal('Ya hay una actividad seleccionada. Elimina la actual para agregar otra.');
        this.value = ''; // Resetear select
        return;
      }

      const li = document.createElement("li");
      li.dataset.actividadId = actividadId;
      li.dataset.actividadNombre = actividadTexto;
      li.innerHTML = `
        <span>${actividadTexto}</span>
        <button class="add-actividad-btn">+</button>
      `;

      listaActividadesAsignadas.appendChild(li);
      this.value = ''; // Resetear select después de agregar
      this.disabled = true;
    });
  }


  if (selectActividades) {
    selectActividades.addEventListener("change", function () {
      const actividadId = this.value;
      const actividadTexto = this.options[this.selectedIndex].text;

      if (!actividadId) return;

      const displayContainer = document.getElementById('selected-activity-display');
      if (displayContainer && displayContainer.querySelector('.activity-item')) {
        mostrarModal('Ya hay una actividad seleccionada.');
        this.value = '';
        return;
      }

      let container = document.getElementById('selected-activity-display');
      if (!container) {
        container = document.createElement('div');
        container.id = 'selected-activity-display';
        container.style.cssText = 'margin: 20px 0; padding: 15px; background: #f8f9fa; border-radius: 8px;';
        container.innerHTML = '<h3 style="margin-bottom: 10px;">Actividad Seleccionada</h3>';
        selectActividades.parentNode.insertBefore(container, selectActividades.nextSibling);
      }

      const activityItem = document.createElement('div');
      activityItem.className = 'activity-item';
      activityItem.dataset.actividadId = actividadId;
      activityItem.dataset.actividadNombre = actividadTexto;
      activityItem.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px; background: white; border-radius: 6px; border: 1px solid #ddd;">
          <div>
            <strong>${actividadTexto}</strong>
            <div style="font-size: 12px; color: #666;">
              Haz clic en + para agregar horarios
            </div>
          </div>
          <button class="add-class-btn" style="background: #4CAF50; color: white; border: none; border-radius: 50%; width: 40px; height: 40px; font-size: 20px; cursor: pointer;">
            +
          </button>
        </div>
      `;

      container.appendChild(activityItem);

      this.disabled = true;
      this.style.opacity = '0.6';
      this.style.cursor = 'not-allowed';

      this.value = '';
    });
  }

  const btnCancelar = document.getElementById("btn-cancelar-grupo");
  if (btnCancelar) {
    btnCancelar.addEventListener("click", function () {
      if (confirm('¿Seguro que quieres cancelar? Se perderán todos los datos.')) {
        location.reload();
      }
    });
  }
});


async function cargarActividades() {
  try {
    const res = await fetch('http://localhost:8080/paraescolares');
    const data = await res.json();

    const select = document.getElementById('actividades');
    select.innerHTML = '<option value="">Selecciona una actividad</option>';

    data.forEach(act => {
      const opt = document.createElement('option');
      opt.value = act.id;
      opt.textContent = act.nombre;
      select.appendChild(opt);
    });

  } catch (err) {
    console.error('Error cargando actividades:', err);
    mostrarModal('Error al cargar actividades. Recarga la página.');
  }
}

async function cargarCicloEscolar() {
  try {
    const res = await fetch('http://localhost:8080/ciclos-escolares/activo');
    const data = await res.json();
    document.querySelector('.subtitle').textContent = data.nombre;
  } catch (err) {
    console.warn('No se pudo cargar el ciclo escolar');
  }
}


function generarTablaHorario() {
  const tbody = document.querySelector('.schedule-table tbody');
  tbody.innerHTML = '';

  for (let h = 7; h <= 13; h++) {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${h}:00</td>`;

    for (let d = 0; d < 6; d++) {
      const td = document.createElement('td');
      td.dataset.dayIndex = d;
      td.dataset.hour = h;
      tr.appendChild(td);
    }

    tbody.appendChild(tr);
  }
}


let abrirModalParaActividad = null;

async function cargarModal() {
  const container = document.getElementById('modal-container');

  try {
    const res = await fetch('AgregarClaseParaescolar/AgregarClaseParaescolar.html');
    const html = await res.text();
    container.innerHTML = html;

    const modal = document.getElementById('addClassModal');
    const scheduleBody = document.querySelector('.schedule-table tbody');
    const assignedList = document.getElementById('assigned-actividades-list');

    if (!modal) {
      console.error(' Modal no encontrado después de cargar HTML');
      mostrarModal('Error al cargar el modal. Recarga la página.');
      return;
    }

    console.log(' Modal cargado correctamente:', modal.id);

    const api = initializeModalParaescolarLogicCompleta(
      scheduleBody,
      modal,
      assignedList,
      cargarInstructores
    );

    if (api && api.abrirParaActividad) {
      abrirModalParaActividad = api.abrirParaActividad;
      console.log(' Función abrirParaActividad asignada');
    } else {
      console.error(' No se pudo obtener abrirParaActividad');
    }
  } catch (error) {
    console.error(' Error al cargar modal:', error);
    mostrarModal('Error al cargar el formulario de clases.');
  }
}


function configurarListenersPrincipales() {
  const actividadesSelect = document.getElementById('actividades');
  const assignedList = document.getElementById('assigned-actividades-list');

  
  if (assignedList) {
    assignedList.style.display = 'none';
  }

  actividadesSelect.addEventListener('change', function () {
    const id = this.value;
    const nombre = this.options[this.selectedIndex].text;

    if (!id) return;

    const selectedActivityDisplay = document.getElementById('selected-activity-display');
    if (!selectedActivityDisplay) {
      const container = document.createElement('div');
      container.id = 'selected-activity-display';
      container.style.cssText = 'margin: 20px 0; padding: 15px; background: #f8f9fa; border-radius: 8px;';
      container.innerHTML = '<h3 style="margin-bottom: 10px;">Actividad Seleccionada</h3>';
      actividadesSelect.parentNode.insertBefore(container, actividadesSelect.nextSibling);
    }

    const displayContainer = document.getElementById('selected-activity-display');
    if (displayContainer.querySelector('.activity-item')) {
    
      displayContainer.innerHTML = '<h3 style="margin-bottom: 10px;">Actividad Seleccionada</h3>';
    }
    this.value = '';
    this.disabled = true;
    this.style.cssText = `
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
  opacity: 0.7;
`;
    const activityItem = document.createElement('div');
    activityItem.className = 'activity-item';
    activityItem.dataset.actividadId = id;
    activityItem.dataset.actividadNombre = nombre;
    activityItem.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px; background: white; border-radius: 6px; border: 1px solid #ddd;">
        <div>
          <strong>${nombre}</strong>
          <div style="font-size: 12px; color: #666;">
            Haz clic en + para agregar horarios
          </div>
        </div>
        <button class="add-class-btn" style="background: #4CAF50; color: white; border: none; border-radius: 50%; width: 40px; height: 40px; font-size: 20px; cursor: pointer;">
          +
        </button>
      </div>
    `;

    displayContainer.appendChild(activityItem);
    this.value = ''; 
  });

  document.addEventListener('click', e => {
    if (e.target.classList.contains('add-class-btn') || e.target.closest('.add-class-btn')) {
      const activityItem = e.target.closest('.activity-item');

      if (!activityItem) return;

      console.log(' Click en botón agregar para:', activityItem.dataset.actividadNombre);

      if (typeof abrirModalParaActividad !== 'function') {
        console.error(' abrirModalParaActividad no es una función');
        mostrarModal('Error: El modal no está listo. Recarga la página.');
        return;
      }

      window.materiaActualLi = activityItem;

      try {
        abrirModalParaActividad(activityItem);
      } catch (error) {
        console.error(' Error al abrir modal:', error);
        mostrarModal('Error al abrir el modal. Verifica la consola.');
      }
    }
  });

  document.getElementById('create-group-btn').addEventListener('click', validarGrupoFinal);
}


function limpiarHorarioDeActividad(nombre) {
  document.querySelectorAll('.class-scheduled').forEach(cell => {
    if (cell.dataset.actividad === nombre) {
      cell.innerHTML = '';
      cell.classList.remove('class-scheduled');
      delete cell.dataset.actividad;
      delete cell.dataset.claseId;
    }
  });
}


async function validarGrupoFinal() {
  console.log(' Iniciando validación y creación de grupo...');

  const nombreGrupo = document.getElementById('group-letter').value.trim();
  const cupo = document.getElementById('cupo-maximo').value.trim();

  const actividadItem = document.querySelector('.activity-item');

  const celdasConClases = document.querySelectorAll('td.class-scheduled');

  if (!nombreGrupo) {
    mostrarModal('Ingresa un nombre de grupo (nota identificatoria)');
    return;
  }

  if (nombreGrupo.length > 300) {
    mostrarModal('El nombre del grupo no puede exceder 300 caracteres');
    return;
  }

  if (!cupo || isNaN(cupo) || parseInt(cupo, 10) <= 0) {
    mostrarModal('Cupo inválido. Ingresa un número mayor a 0.');
    return;
  }

  if (!actividadItem) {
    mostrarModal('Debes seleccionar una actividad paraescolar');
    return;
  }

  if (celdasConClases.length === 0) {
    mostrarModal('Debes agregar al menos una clase con horarios');
    return;
  }

  const actividadId = actividadItem.dataset.actividadId;
  const actividadNombre = actividadItem.dataset.actividadNombre;

  let instructorId = null;
  let instructorNombre = null;
  const horariosSet = new Set();
  const horariosArray = [];

  const clasesUnicas = new Set();
  celdasConClases.forEach(cell => {
    clasesUnicas.add(cell.dataset.claseId);
  });

  for (const claseId of clasesUnicas) {
    if (!claseId) continue;

    const primeraCelda = document.querySelector(`td[data-clase-id="${claseId}"]`);
    if (!primeraCelda) continue;

    const claseInfo = window.clasesParaescolares?.find(c => c.id === claseId);
    if (!claseInfo) {
      console.warn(`No se encontró información para la clase ${claseId}`);
      continue;
    }

    if (claseInfo.actividad.id !== actividadId) {
      mostrarModal(`Error: Todas las clases deben ser de la misma actividad.\nActividad seleccionada: ${actividadNombre}`);
      return;
    }

       instructorId = claseInfo.instructor.id;
       instructorNombre = claseInfo.instructor.nombre;
  
    claseInfo.horarios.forEach(horario => {
      const horarioKey = `${horario.day}-${horario.start}-${horario.end}`;

      if (horariosSet.has(horarioKey)) {
        mostrarModal(`Error: Hay horarios duplicados.\nDía: ${horario.day}, Hora: ${horario.start}:00-${horario.end}:00`);
        throw new Error('Horario duplicado');
      }

      horariosSet.add(horarioKey);

      const horaInicioFormateada = String(horario.start).padStart(2, '0') + ':00:00';
      const horaFinFormateada = String(horario.end).padStart(2, '0') + ':00:00';

      horariosArray.push({
        dia: horario.day,
        horaInicio: horaInicioFormateada,
        horaFin: horaFinFormateada
      });
    });
  }

  if (horariosArray.length === 0) {
    mostrarModal('No hay horarios válidos para enviar');
    return;
  }

  const grupoParaescolar = {
    nota: nombreGrupo,
    actividadParaescolarId: parseInt(actividadId, 10),
    docenteId: parseInt(instructorId, 10),
    maximoEspaciosAlumnos: parseInt(cupo, 10),
    horarios: horariosArray
  };

  console.log(' Datos a enviar al backend:', grupoParaescolar);
  console.log(` Resumen: ${actividadNombre} con ${horariosArray.length} horarios, instructor: ${instructorNombre}`);

  const horariosTexto = horariosArray.map(h => {
    const dias = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
    return `${dias[h.dia] || 'Día'} ${h.horaInicio.split(':')[0]}:00-${h.horaFin.split(':')[0]}:00`;
  }).join('\n');

  const confirmacion = `¿Crear grupo paraescolar?\n\n` +
    `Nombre: ${nombreGrupo}\n` +
    `Actividad: ${actividadNombre}\n` +
    `Instructor: ${instructorNombre}\n` +
    `Cupo: ${cupo} alumnos\n` +
    `Horarios:\n${horariosTexto}`;

  if (!confirm(confirmacion)) {
    return;
  }

  const createBtn = document.getElementById('create-group-btn');
  const originalText = createBtn.textContent;
  createBtn.textContent = 'Creando...';
  createBtn.disabled = true;

  try {
    const response = await fetch('http://localhost:8080/paraescolares/grupos', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(grupoParaescolar)
    });

    console.log(' Respuesta del servidor:', response.status, response.statusText);

    if (response.ok) {
      const data = await response.json();
      console.log(' Grupo creado exitosamente:', data);

      mostrarModal(
        ` Grupo creado exitosamente\n\n` +
        `ID: ${data.grupo_id || 'N/A'}\n` +
        `Mensaje: ${data.mensaje || 'Grupo paraescolar creado'}`
      );

    setTimeout(() => {
  location.reload(); // Recarga la página completa
}, 1500);

   } else {
  let errorMsg = 'Error al crear el grupo';
  
  try {
    const errorData = await response.json();
    console.error(' Error del servidor:', errorData);
    
    // EL MENSAJE ESTÁ EN "mensaje" NO EN "message"
    if (errorData.mensaje) {
      errorMsg = errorData.mensaje; // ← "El docente no está disponible..."
    } else if (errorData.message) {
      errorMsg = errorData.message;
    } else if (errorData.error) {
      errorMsg = errorData.error;
    }
    
    // ... resto del código
  } catch (parseError) {
    errorMsg += ` (Código: ${response.status})`;
  }
  
  mostrarModal(` ${errorMsg}`);
  createBtn.textContent = originalText;
  createBtn.disabled = false;
}
  } catch (error) {
    console.error(' Error de conexión:', error);
    mostrarModal(` Error de conexión: ${error.message}\n\nVerifica que el servidor esté funcionando.`);
    createBtn.textContent = originalText;
    createBtn.disabled = false;
  }
}

// Función para limpiar el formulario después de crear un grupo
function limpiarFormulario() {
  document.getElementById('group-letter').value = '';
  document.getElementById('cupo-maximo').value = '';
  document.getElementById('actividades').value = '';

  // Limpiar actividad seleccionada
  const selectedDisplay = document.getElementById('selected-activity-display');
  if (selectedDisplay) {
    selectedDisplay.innerHTML = '';
  }

  // Limpiar lista lateral (oculta)
  const assignedList = document.getElementById('assigned-actividades-list');
  if (assignedList) {
    assignedList.innerHTML = '';
  }

  // Limpiar horario visual
  document.querySelectorAll('.class-scheduled').forEach(cell => {
    cell.innerHTML = '';
    cell.classList.remove('class-scheduled');
    delete cell.dataset.actividad;
    delete cell.dataset.claseId;
  });

  window.clasesParaescolares = [];
}


function mostrarModal(msg, tipo = 'info') {
  console.log(` Mostrar mensaje [${tipo}]:`, msg.substring(0, 50) + '...');

  document.querySelectorAll('.message-modal-overlay').forEach(m => m.remove());

  const modal = document.createElement('div');
  modal.className = `message-modal-overlay visible ${tipo}`;

  modal.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 10001; /* MAYOR que el modal principal (10000) */
    display: flex;
    justify-content: center;
    align-items: center;
  `;

  const color = tipo === 'error' ? '#f44336' : tipo === 'success' ? '#4CAF50' : '#2196F3';

  modal.innerHTML = `
    <div class="modal-content" style="
      background: white;
      padding: 25px;
      border-radius: 10px;
      min-width: 300px;
      max-width: 500px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.2);
      border-top: 5px solid ${color};
    ">
      <p style="white-space: pre-line; margin: 0 0 20px 0; font-size: 16px; line-height: 1.5;">${msg}</p>
      <div style="text-align: right;">
        <button class="btn-aceptar" style="
          padding: 10px 25px;
          background: ${color};
          color: white;
          border: none;
          border-radius: 5px;
          cursor: pointer;
          font-size: 16px;
        ">Aceptar</button>
      </div>
    </div>
  `;

  document.body.appendChild(modal);

  const btnAceptar = modal.querySelector('.btn-aceptar');
  btnAceptar.addEventListener('click', () => {
    modal.style.opacity = '0';
    modal.style.transition = 'opacity 0.3s';
    setTimeout(() => modal.remove(), 300);
  });

  modal.addEventListener('click', (e) => {
    if (e.target === modal) {
      modal.style.opacity = '0';
      setTimeout(() => modal.remove(), 300);
    }
  });

  if (tipo === 'info') {
    setTimeout(() => {
      if (modal.parentNode) {
        modal.style.opacity = '0';
        setTimeout(() => modal.remove(), 300);
      }
    }, 5000);
  }
}

async function cargarInstructores() {
  try {
    const res = await fetch('http://localhost:8080/docentes');
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    console.log(` ${data.length} instructores cargados`);
    return data;
  } catch (error) {
    console.error('Error cargando instructores:', error);
    mostrarModal('Error al cargar la lista de instructores');
    return [];
  }
}


window.initializeModalParaescolarLogicCompleta = function (scheduleBody, modal, assignedList, cargarInstructoresFn) {
  console.log(' Inicializando lógica del modal...');

  if (!modal) {
    console.error(' Modal no encontrado');
    return null;
  }

  const modalActividadName = modal.querySelector('#modal-actividad-name');
  const instructorSelect = modal.querySelector('#modal-instructor');
  const addHorarioBtn = modal.querySelector('#add-horario-btn');
  const horariosListBody = modal.querySelector('#horarios-list-body');
  const confirmBtn = modal.querySelector('#confirm-btn');
  const cancelBtn = modal.querySelector('#cancel-btn');

  let actividadActualLi = null;
  let contadorClases = window.contadorClases || 0;

  if (typeof cargarInstructoresFn === 'function') {
    console.log(' Cargando instructores...');
    cargarInstructoresFn().then(list => {
      console.log(` ${list.length} instructores cargados`);
      instructorSelect.innerHTML = '<option value="">Selecciona un instructor</option>';
      list.forEach(i => {
        const opt = document.createElement('option');
        opt.value = i.id;
        opt.textContent = `${i.nombre} ${i.apellidoPaterno || ''} ${i.apellidoMaterno || ''}`.trim();
        instructorSelect.appendChild(opt);
      });
    }).catch(err => {
      console.error(' Error al cargar instructores:', err);
      instructorSelect.innerHTML = '<option value="">Error al cargar instructores</option>';
    });
  }

  function abrirParaActividad(li) {
    console.log(' abrirParaActividad - INICIO');

    if (!li) {
      console.error(' No se proporcionó elemento li');
      return;
    }

    actividadActualLi = li;
    const actividadNombre = li.dataset.actividadNombre || li.textContent.trim();
    console.log(' Actividad a asignar:', actividadNombre);

    if (modalActividadName) {
      modalActividadName.value = actividadNombre;
    } else {
      console.error(' modalActividadName no encontrado');
    }

    if (horariosListBody) {
      horariosListBody.innerHTML = '';
    }

    let modalReal = null;

    modalReal = document.querySelector('.modal-overlay');
    console.log(' Buscando .modal-overlay:', modalReal ? 'ENCONTRADO' : 'NO');

    if (!modalReal) {
      modalReal = document.getElementById('addClassModal');
      console.log(' Buscando #addClassModal:', modalReal ? 'ENCONTRADO' : 'NO');
    }

    if (!modalReal) {
      modalReal = document.querySelector('[class*="modal"]');
      console.log(' Buscando [class*="modal"]:', modalReal ? 'ENCONTRADO' : 'NO');
    }

    if (!modalReal) {
      console.error(' NO SE ENCONTRÓ NINGÚN MODAL EN EL DOM');
      mostrarModal('Error crítico: No se pudo abrir el formulario. Recarga la página.');
      return;
    }

    console.log('Modal encontrado:', {
      id: modalReal.id,
      className: modalReal.className,
      tagName: modalReal.tagName
    });

  
    modalReal.style.cssText = `
    display: flex !important;
    visibility: visible !important;
    opacity: 1 !important;
    position: fixed !important;
    top: 0 !important;
    left: 0 !important;
    width: 100% !important;
    height: 100% !important;
    background-color: rgba(0, 0, 0, 0.7) !important;
    z-index: 99999 !important;
    justify-content: center !important;
    align-items: center !important;
  `;

    modalReal.classList.add('visible');

    const modalContent = modalReal.querySelector('.modal-content');
    if (modalContent) {
      modalContent.style.cssText = `
      display: block !important;
      visibility: visible !important;
      opacity: 1 !important;
      z-index: 100000 !important;
      position: relative !important;
    `;
    }

    modalReal.offsetHeight;

    console.log(' Modal ABIERTA con fuerza:', {
      display: window.getComputedStyle(modalReal).display,
      visibility: window.getComputedStyle(modalReal).visibility,
      opacity: window.getComputedStyle(modalReal).opacity,
      zIndex: window.getComputedStyle(modalReal).zIndex
    });

    if (instructorSelect) {
      instructorSelect.selectedIndex = 0;
    }

    const diaSelect = modalReal.querySelector('#horario-dia');
    if (diaSelect) diaSelect.selectedIndex = 0;

    const inicio = modalReal.querySelector('#horario-inicio');
    if (inicio) inicio.value = '';

    const fin = modalReal.querySelector('#horario-fin');
    if (fin) fin.value = '';

    setTimeout(() => {
      modalReal.style.outline = '5px solid red';
      setTimeout(() => {
        modalReal.style.outline = '';
      }, 1000);
    }, 100);
  }

  cancelBtn.addEventListener('click', () => {
    console.log(' Modal cancelado');
    modal.style.display = 'none';
    modal.classList.remove('visible');
    actividadActualLi = null;
  });

  modal.addEventListener('click', (e) => {
    if (e.target === modal) {
      console.log(' Click fuera del modal - cerrando');
      modal.style.display = 'none';
      modal.classList.remove('visible');
      actividadActualLi = null;
    }
  });

  if (addHorarioBtn) {
    addHorarioBtn.addEventListener('click', () => {
      const diaSelect = modal.querySelector('#horario-dia');
      const inicio = modal.querySelector('#horario-inicio');
      const fin = modal.querySelector('#horario-fin');

      if (!diaSelect || !inicio || !fin) {
        console.error(' Campos de horario no encontrados');
        return;
      }

      if (!diaSelect.value || !inicio.value || !fin.value) {
        alert('Completa todos los campos del horario');
        return;
      }

      const startHour = parseInt(inicio.value.split(':')[0], 10);
      const endHour = parseInt(fin.value.split(':')[0], 10);

      if (startHour >= endHour) {
        alert('La hora de fin debe ser mayor a la hora de inicio');
        return;
      }

      const tr = document.createElement('tr');
      const diaText = diaSelect.options[diaSelect.selectedIndex].text;
      tr.innerHTML = `<td data-day-value="${diaSelect.value}">${diaText}</td>
        <td>${inicio.value}</td><td>${fin.value}</td>
        <td><button type="button" class="delete-horario-btn" title="Eliminar horario">✕</button></td>`;

      if (horariosListBody) {
        horariosListBody.appendChild(tr);
      }

      inicio.value = '';
      fin.value = '';
    });
  }

  if (horariosListBody) {
    horariosListBody.addEventListener('click', (e) => {
      if (e.target.classList.contains('delete-horario-btn')) {
        e.target.closest('tr').remove();
      }
    });
  }

  if (confirmBtn) {
    confirmBtn.addEventListener('click', async () => {
      console.log(' Botón confirmar clickeado');

      const esModoEdicion = confirmBtn.dataset.modo === 'edicion' && window.claseEditandoId;

      if (!instructorSelect) {
        alert('Error: No se puede acceder al selector de instructores');
        return;
      }

      const instructorId = instructorSelect.value;
      const horarioRows = horariosListBody ? horariosListBody.querySelectorAll('tr') : [];

      if (!instructorId) {
        alert('Selecciona un instructor');
        return;
      }

      if (horarioRows.length === 0) {
        alert('Agrega al menos un horario');
        return;
      }

      const instructorNombre = instructorSelect.options[instructorSelect.selectedIndex].text;

      const horariosObjs = [];
      for (const row of horarioRows) {
        const day = parseInt(row.cells[0].dataset.dayValue, 10);
        const start = parseInt(row.cells[1].textContent.split(':')[0], 10);
        const end = parseInt(row.cells[2].textContent.split(':')[0], 10);
        horariosObjs.push({ day, start, end });
      }

      if (esModoEdicion && window.claseEditandoId) {
        console.log('🔄 Modo edición: eliminando clase vieja', window.claseEditandoId);

        document.querySelectorAll(`td[data-clase-id="${window.claseEditandoId}"]`).forEach(cell => {
          cell.classList.remove('class-scheduled');
          cell.innerHTML = '';
          delete cell.dataset.claseId;
          delete cell.dataset.actividad;
        });

        if (window.clasesParaescolares) {
          window.clasesParaescolares = window.clasesParaescolares.filter(c => c.id !== window.claseEditandoId);
        }

        console.log(' Clase vieja eliminada');
      }

      const conflictos = validarConflictosHorarios(horariosObjs, scheduleBody, esModoEdicion ? window.claseEditandoId : null);
      if (conflictos.length > 0) {
        alert(`Conflicto de horario: ${conflictos.join(', ')}. Ajusta los horarios.`);
        return;
      }

      contadorClases++;
      window.contadorClases = contadorClases;
      const claseId = esModoEdicion ? window.claseEditandoId : `clase-${Date.now()}-${contadorClases}`;
      const nombreActividad = modalActividadName ? modalActividadName.value :
        (actividadActualLi ? actividadActualLi.dataset.actividadNombre : 'Actividad');

      const claseInfo = {
        id: claseId,
        actividad: {
          id: actividadActualLi ? actividadActualLi.dataset.actividadId : '',
          nombre: nombreActividad
        },
        instructor: {
          id: instructorId,
          nombre: instructorNombre
        },
        horarios: horariosObjs
      };

      if (!window.clasesParaescolares) window.clasesParaescolares = [];
      if (esModoEdicion) {
        const index = window.clasesParaescolares.findIndex(c => c.id === claseId);
        if (index !== -1) {
          window.clasesParaescolares[index] = claseInfo;
        } else {
          window.clasesParaescolares.push(claseInfo);
        }
      } else {
        window.clasesParaescolares.push(claseInfo);
      }

      console.log(' Clase guardada en memoria:', claseInfo);

      horariosObjs.forEach(h => {
        for (let hh = h.start; hh < h.end; hh++) {
          const cell = scheduleBody.querySelector(`td[data-day-index="${h.day}"][data-hour="${hh}"]`);
          if (cell) {
            if (cell.classList.contains('class-scheduled')) {
              cell.innerHTML = '';
            }

            cell.classList.add('class-scheduled');
            cell.dataset.claseId = claseId;
            cell.dataset.actividad = nombreActividad;
            cell.dataset.actividadId = actividadActualLi ? actividadActualLi.dataset.actividadId : '';

            cell.innerHTML = `
              <div class="class-content">
                <div class="class-header">
                  <strong>${nombreActividad}</strong>
                  <div class="class-actions">
                
                    <button class="delete-class-btn" data-clase-id="${claseId}" title="Eliminar">
                      <span class="material-icons" style="font-size: 14px;">delete</span>
                    </button>
                  </div>
                </div>
                <div class="class-details">
                  <small>${instructorNombre}</small><br>
                  <small>${String(h.start).padStart(2, '0')}:00-${String(h.end).padStart(2, '0')}:00</small>
                </div>
              </div>
            `;

            setTimeout(() => {
              const editBtn = cell.querySelector('.edit-class-btn');
              const deleteBtn = cell.querySelector('.delete-class-btn');

              if (editBtn) {
                editBtn.addEventListener('click', (e) => {
                  e.stopPropagation();
                  editarClaseDesdeTabla(claseId);
                });
              }

              if (deleteBtn) {
                deleteBtn.addEventListener('click', (e) => {
                  e.stopPropagation();
                  eliminarClaseDesdeTabla(claseId);
                });
              }
            }, 50);
          }
        }
      });
      modal.style.display = 'none';
      modal.classList.remove('visible');
      modal.classList.remove('visible');
      console.log(' Modal cerrado');

      mostrarModal(esModoEdicion ? ' Clase actualizada correctamente' : ' Clase agregada correctamente');

      actividadActualLi = null;
      if (horariosListBody) horariosListBody.innerHTML = '';

      if (esModoEdicion) {
        delete window.claseEditandoId;
        confirmBtn.textContent = 'Confirmar';
        delete confirmBtn.dataset.modo;
      }
    });
  }

  function validarConflictosHorarios(nuevosHorarios, scheduleBody, ignorarClaseId = null) {
    const conflictos = [];
    nuevosHorarios.forEach(h => {
      for (let hh = h.start; hh < h.end; hh++) {
        const cell = scheduleBody.querySelector(`td[data-day-index="${h.day}"][data-hour="${hh}"]`);
        if (cell && cell.classList.contains('class-scheduled')) {
          if (ignorarClaseId && cell.dataset.claseId === ignorarClaseId) {
            continue;
          }
          conflictos.push(`Conflicto en día ${h.day}, hora ${hh}`);
        }
      }
    });
    return conflictos;
  }

  return {
    abrirParaActividad,
    debugInfo: () => ({ modalId: modal.id })
  };
};


function editarClaseDesdeTabla(claseId) {
  console.log('✏️ Editando clase desde tabla:', claseId);

  const claseInfo = window.clasesParaescolares?.find(c => c.id === claseId);
  if (!claseInfo) {
    mostrarModal('Error: No se encontró la clase para editar');
    return;
  }

  const activityItem = document.querySelector('.activity-item');
  if (!activityItem) {
    mostrarModal('Error: No se encontró la actividad');
    return;
  }

  window.claseEditandoId = claseId;

  if (typeof abrirModalParaActividad !== 'function') {
    mostrarModal('Error: El modal no está disponible');
    return;
  }

  window.materiaActualLi = activityItem;
  abrirModalParaActividad(activityItem);

  setTimeout(() => {
    const modal = document.getElementById('addClassModal');
    if (modal) {
      const instructorSelect = modal.querySelector('#modal-instructor');
      if (instructorSelect && claseInfo.instructor.id) {
        instructorSelect.value = claseInfo.instructor.id;
      }

      const horariosListBody = modal.querySelector('#horarios-list-body');
      if (horariosListBody && claseInfo.horarios.length > 0) {
        horariosListBody.innerHTML = '';

        claseInfo.horarios.forEach(h => {
          const tr = document.createElement('tr');
          const dias = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
          tr.innerHTML = `
            <td data-day-value="${h.day}">${dias[h.day] || 'Día'}</td>
            <td>${String(h.start).padStart(2, '0')}:00</td>
            <td>${String(h.end).padStart(2, '0')}:00</td>
            <td><button class="delete-horario-btn" type="button">✕</button></td>
          `;
          horariosListBody.appendChild(tr);
        });
      }

      const confirmBtn = modal.querySelector('#confirm-btn');





      if (confirmBtn) {
        confirmBtn.textContent = 'Actualizar Clase';
        confirmBtn.dataset.modo = 'edicion';
      }
    }
  }, 100);
}

function eliminarClaseDesdeTabla(claseId) {
  if (!confirm('¿Eliminar esta clase del horario?')) return;

  console.log(' Eliminando clase desde tabla:', claseId);

  document.querySelectorAll(`td[data-clase-id="${claseId}"]`).forEach(cell => {
    cell.classList.remove('class-scheduled');
    cell.innerHTML = '';
    delete cell.dataset.claseId;
    delete cell.dataset.actividad;
  });

  if (window.clasesParaescolares) {
    window.clasesParaescolares = window.clasesParaescolares.filter(c => c.id !== claseId);
  }

  mostrarModal(' Clase eliminada del horario');
}