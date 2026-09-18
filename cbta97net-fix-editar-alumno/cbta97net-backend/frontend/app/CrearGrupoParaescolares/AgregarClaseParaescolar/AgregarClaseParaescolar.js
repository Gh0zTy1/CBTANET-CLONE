window.initializeModalParaescolarLogic = function (scheduleBody, modal, assignedList, cargarInstructoresFn) {
  console.log(' initializeModalParaescolarLogic ejecutándose');
  
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
  let contadorClases = 0;

  if (typeof cargarInstructoresFn === 'function') {
    cargarInstructoresFn().then(list => {
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
    if (!li) return;
    
    actividadActualLi = li;
    const actividadNombre = li.dataset.actividadNombre || li.dataset.materiaNombre || li.textContent.trim();
    
    if (modalActividadName) {
      modalActividadName.value = actividadNombre;
    }
    
    if (horariosListBody) {
      horariosListBody.innerHTML = '';
    }
    
    modal.classList.add('visible');
    
    if (instructorSelect) instructorSelect.selectedIndex = 0;
    const diaSelect = modal.querySelector('#horario-dia');
    if (diaSelect) diaSelect.selectedIndex = 0;
    const inicio = modal.querySelector('#horario-inicio');
    if (inicio) inicio.value = '';
    const fin = modal.querySelector('#horario-fin');
    if (fin) fin.value = '';
  }

  if (cancelBtn) {
    cancelBtn.addEventListener('click', () => {
      modal.classList.remove('visible');
      actividadActualLi = null;
    });
  }
  
  modal.addEventListener('click', (e) => { 
    if (e.target === modal) {
      modal.classList.remove('visible');
      actividadActualLi = null;
    }
  });

  if (addHorarioBtn) {
    addHorarioBtn.addEventListener('click', () => {
      const diaSelect = modal.querySelector('#horario-dia');
      const inicio = modal.querySelector('#horario-inicio');
      const fin = modal.querySelector('#horario-fin');

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

      const diaValor = diaSelect.value;
      const diaTexto = diaSelect.options[diaSelect.selectedIndex].text;
      const inicioValor = inicio.value;
      const finValor = fin.value;
      
      const filasExistentes = horariosListBody.querySelectorAll('tr');
      let esDuplicado = false;
      
      for (let fila of filasExistentes) {
        const celdas = fila.querySelectorAll('td');
        if (celdas.length >= 3) {
          const diaExistente = celdas[0].textContent.trim();
          const inicioExistente = celdas[1].textContent.trim();
          const finExistente = celdas[2].textContent.trim();
          
          if (diaExistente === diaTexto && 
              inicioExistente === inicioValor && 
              finExistente === finValor) {
            esDuplicado = true;
            break;
          }
        }
      }
      
      if (esDuplicado) {
        alert(' Este horario ya está agregado:\n' + diaTexto + ' ' + inicioValor + ' - ' + finValor);
        return;
      }

      const tr = document.createElement('tr');
      tr.innerHTML = `<td data-day-value="${diaValor}">${diaTexto}</td>
        <td>${inicioValor}</td><td>${finValor}</td>
        <td><button type="button" class="delete-horario-btn" title="Eliminar horario">✕</button></td>`;
      
      horariosListBody.appendChild(tr);
      
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

      const conflictos = validarConflictosHorarios(horariosObjs, scheduleBody);
      if (conflictos.length > 0) {
        alert(`Conflicto de horario: ${conflictos.join(', ')}. Ajusta los horarios.`);
        return;
      }

      contadorClases++;
      const claseId = `clase-${Date.now()}-${contadorClases}`;
      const nombreActividad = modalActividadName ? modalActividadName.value : 
                              (actividadActualLi ? actividadActualLi.dataset.actividadNombre : 'Actividad');

      const claseElement = document.createElement('div');
      claseElement.className = 'clase-asignada-item';
      claseElement.dataset.claseId = claseId;
      claseElement.dataset.instructorId = instructorId;
      claseElement.dataset.instructorNombre = instructorNombre;
      claseElement.dataset.horarios = JSON.stringify(horariosObjs);
      
      const horariosTexto = horariosObjs.map(h => {
        const dias = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
        return `${dias[h.day] || 'Día'}: ${h.start}:00-${h.end}:00`;
      }).join(', ');

      claseElement.innerHTML = `
        <div class="clase-info">
          <strong>${nombreActividad}</strong><br>
          <small>Instructor: ${instructorNombre}</small><br>
          <small>Horarios: ${horariosTexto}</small>
        </div>
        <div class="clase-actions">
          <button class="edit-clase-btn" title="Editar clase">✏️</button>
          <button class="delete-clase-btn" title="Eliminar clase">🗑️</button>
        </div>
      `;

      if (assignedList) {
        assignedList.appendChild(claseElement);
      }

      horariosObjs.forEach(h => {
        for (let hh = h.start; hh < h.end; hh++) {
          const cell = scheduleBody.querySelector(`td[data-day-index="${h.day}"][data-hour="${hh}"]`);
          if (cell && !cell.classList.contains('class-scheduled')) {
            cell.classList.add('class-scheduled');
            cell.dataset.claseId = claseId;
            cell.dataset.actividad = nombreActividad;
            cell.innerHTML = `<div class="class-content">
              <strong>${nombreActividad}</strong><br>
              <small>${instructorNombre}</small>
            </div>`;
          }
        }
      });

      modal.classList.remove('visible');
      actividadActualLi = null;
      if (horariosListBody) horariosListBody.innerHTML = '';
      
      alert(' Clase agregada correctamente');
    });
  }

  function validarConflictosHorarios(nuevosHorarios, scheduleBody) {
    const conflictos = [];
    nuevosHorarios.forEach(h => {
      for (let hh = h.start; hh < h.end; hh++) {
        const cell = scheduleBody.querySelector(`td[data-day-index="${h.day}"][data-hour="${hh}"]`);
        if (cell && cell.classList.contains('class-scheduled')) {
          conflictos.push(`Día ${h.day} hora ${hh}`);
        }
      }
    });
    return conflictos;
  }

  return { 
    abrirParaActividad
  };
};