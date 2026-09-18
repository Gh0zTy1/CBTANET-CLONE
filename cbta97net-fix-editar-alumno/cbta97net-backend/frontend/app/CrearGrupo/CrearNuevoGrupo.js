let materiaActualLi = null;
async function actualizarCicloEscolarEnHeader() {
  try {
    const response = await fetch('http://localhost:8080/ciclos-escolares/activo');
    const cicloData = await response.json();

    const cicloHeader = document.querySelector('.subtitle');
    const interactiveElements = document.querySelectorAll('input, select, button:not(.modal-content button)');

    if (cicloData && cicloData.id) {
      const fechaInicio = new Date(cicloData.fechaInicio).getFullYear();
      const fechaFin = new Date(cicloData.fechaFin).getFullYear();
      cicloHeader.textContent = `Ciclo Escolar Activo: [${fechaInicio}-${fechaFin}]`;
      interactiveElements.forEach(el => el.disabled = false);
    } else {
      cicloHeader.textContent = 'No hay ciclo escolar activo';
      interactiveElements.forEach(el => el.disabled = true);
      alert('No hay ciclo escolar activo. No se pueden crear grupos.');
    }
  } catch (error) {
    console.error('Error al obtener ciclo escolar:', error);
    const cicloHeader = document.querySelector('.subtitle');
    cicloHeader.textContent = 'Error al cargar ciclo escolar';
    const interactiveElements = document.querySelectorAll('input, select, button:not(.modal-content button)');
    interactiveElements.forEach(el => el.disabled = true);
  }
}

function abrirModalConMateria(materiaId, materiaNombre) {
  console.log("Abriendo modal para materia:", { materiaId, materiaNombre });

  const assignedList = document.getElementById("assigned-materials-list");
  const existingMateria = assignedList.querySelector(`li[data-materia-id="${materiaId}"]`);

  if (existingMateria) {
    abrirModalParaLi(existingMateria);
    return;
  }

  const li = document.createElement("li");
  li.dataset.materiaId = materiaId;
  li.dataset.materiaNombre = materiaNombre;
  li.innerHTML = `
    <span class="material-text">${materiaNombre}</span>
    <div class="material-actions">
        <span class="material-icons add-btn" title="Agregar horario">add_circle</span>
        <span class="material-icons delete-btn" title="Eliminar">delete</span>
    </div>
  `;
  assignedList.appendChild(li);

  const materiasList = document.getElementById("materias-list");
  const materiaItemToRemove = materiasList.querySelector(`.materia-item[data-materia-id="${materiaId}"]`);
  if (materiaItemToRemove) {
    materiaItemToRemove.remove();
  }

  const materiasDisponibles = materiasList.querySelectorAll('.materia-item');
  if (materiasDisponibles.length === 0) {
    materiasList.innerHTML = '<div class="materia-item">No hay más materias disponibles</div>';
  }

  abrirModalParaLi(li);
}





async function validarCreacionGrupo() {

  const cicloActivo = validarCiclo();

  const semestre = parseInt(document.getElementById("semestre").value);
  const carrera = document.getElementById("carrera").value;
  const area = document.getElementById("area-propedeutica").value;
  const nombreGrupo = document.getElementById("group-name").value;
  const letraGrupo = document
    .getElementById("group-letter")
    .value.toUpperCase();

  const turno = document.getElementById("turno").value;
  const materias = document.querySelectorAll("#assigned-materials-list li");
  const horarioCeldas = document.querySelectorAll(".class-scheduled");

  if (!cicloActivo) {
    alert("No existe un ciclo escolar activo.");
    return;
  }

  // const regexValido = /^[A-Za-z0-9\sÁÉÍÓÚÑáéíóúñ-]+$/;
  // if (!regexValido.test(nombreGrupo)) {
  //   alert("El nombre del grupo contiene caracteres inválidos.");
  //   return;
  // }

  if (semestre >= 2 && carrera === "tronco-comun") {
    alert("Debe seleccionar una carrera técnica para este semestre.");
    return;
  }

  if (semestre === 6 && area === "ninguna") {
    alert("Debe seleccionar un área propedéutica para este semestre.");
    return;
  }

  const totalMaterias = materias.length;
  const materiasAsignadas = Array.from(materias).filter((li) =>
    li.classList.contains("materia-asignada")
  ).length;

  if (materiasAsignadas < totalMaterias) {
    alert(
      "Debe asignar clases para todas las materias antes de crear el grupo."
    );
    return;
  }










  // const horasRequeridas = 4;
  // const horasAsignadas = horarioCeldas.length / totalMaterias;
  // if (horasAsignadas < horasRequeridas) {
  //   alert("Faltan horas semanales por completar en alguna materia.");
  //   return;
  // }



















  try {
    const clases = obtenerClasesDelHorario();

    const clasesValidas = clases.filter((clase) => {
      const esValida =
        clase.materiaId && !isNaN(clase.materiaId) && clase.horarios.length > 0;
      return esValida;
    });

    if (clasesValidas.length === 0) {
      // alert(
      //   "No hay clases válidas para enviar. Verifica que todas las materias tengan ID correcto."
      // );
      mostrarModal("No hay clases válidas para enviar. Verifica que todas las materias tengan ID correcto.");

      return;
    }

    if (
      !letraGrupo ||
      letraGrupo.length !== 1 ||
      !/^[A-Za-z]$/.test(letraGrupo)
    ) {
      // alert("Debe ingresar una letra válida para el grupo.");
      mostrarModal("Debe ingresar una letra válida para el grupo.");

      return;
    }

    if (
      !nombreGrupo ||
      nombreGrupo.length > 100 ||
      /\.com|http|www/i.test(nombreGrupo)
    ) {
      mostrarModal("El nombre del grupo no puede tener más de 100 caracteres ni contener enlaces.");
      return;
    }

    const grupoData = {
      nota: nombreGrupo,
      letra: letraGrupo,
      activo: true,
      semestre: semestre - 1,
      turno: turno.toUpperCase(),
      cicloEscolarId: 1,
      carreraTecnicaId: carrera !== "tronco-comun" ? parseInt(carrera) : null,
      areaPropedeuticaId: area !== "ninguna" ? parseInt(area) : null,
      clases: clasesValidas,
    };

    if (!validarDatosClases(clasesValidas)) {
      alert(
        "Error: Hay datos inválidos en las clases. Verifica que todos los docentes y aulas estén correctamente asignados."
      );
      return;
    }

    try {
      const materiasInfo = await Promise.all(
        clasesValidas.map(async (clase) => {
          const response = await fetch(`http://localhost:8080/materias/${clase.materiaId}`);
          if (!response.ok) throw new Error('Error al obtener materia');
          return await response.json();
        })
      );

      for (let i = 0; i < clasesValidas.length; i++) {
        const clase = clasesValidas[i];
        const materiaInfo = materiasInfo[i];
        const horasAsignadas = clase.horarios.length;
        console.log(materiaInfo.horas_por_semana);
        if (horasAsignadas < materiaInfo.horasPorSemana) {
          console.log("entre");
          alert(`Las horas asignadas (${horasAsignadas}) son menores al mínimo requerido (${materiaInfo.horasPorSemana}) para la materia ${materiaInfo.nombre}`);
          return;
        }

        if (horasAsignadas > materiaInfo.horasPorSemana) {
          alert(`Las horas asignadas (${horasAsignadas}) exceden el máximo permitido (${materiaInfo.horasPorSemana}) para la materia ${materiaInfo.nombre}`);
          return;
        }
      }
    } catch (error) {
      console.error('Error al validar horas:', error);
    }

    const response = await fetch("http://localhost:8080/grupos", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(grupoData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || `Error HTTP: ${response.status}`);
    }

    const grupoCreado = await response.json();
    // alert("Grupo creado correctamente.");
    mostrarModal("Grupo Creado Correctamente");

    location.reload();
  } catch (error) {
    console.error("Error al crear el grupo:", error);
    alert(`Error al crear el grupo: ${error.message}`);
  }
}

async function validarCiclo() {
  try {
    const cicloResponse = await fetch('http://localhost:8080/ciclos-escolares/activo');
    if (!cicloResponse.ok) {
      throw new Error('Error al verificar ciclo escolar');
    }

    const cicloData = await cicloResponse.json();

    if (!cicloData || !cicloData.id) {
      alert("No existe un ciclo escolar activo.");
      return;
    }





  } catch (error) {
    console.error('Error al validar ciclo escolar:', error);
    alert("Error al verificar el ciclo escolar activo.");
    return;
  }
}


function initializeModalLogic(scheduleBody, modal, materialsList) {
  if (!materialsList || !modal) {
    console.error(
      "initializeModalLogic: materialsList o modal no encontrados."
    );
    return;
  }

  const modalMateriaName = modal.querySelector("#modal-materia-name");
  const addHorarioBtn = modal.querySelector("#add-horario-btn");
  const horariosListBody = modal.querySelector("#horarios-list-body");
  const confirmBtn = modal.querySelector("#confirm-btn");
  const cancelBtn = modal.querySelector("#cancel-btn");
  const modalDocente = modal.querySelector("#modal-docente");
  const modalAula = modal.querySelector("#modal-aula");
  const horarioDiaSelect = modal.querySelector("#horario-dia");

  const diasSemana = [
    { value: "0", text: "Lunes" },
    { value: "1", text: "Martes" },
    { value: "2", text: "Miércoles" },
    { value: "3", text: "Jueves" },
    { value: "4", text: "Viernes" },
    { value: "5", text: "Sábado" },
    // { value: "6", text: "Domingo" },
  ];

  if (horarioDiaSelect) {
    horarioDiaSelect.innerHTML = diasSemana
      .map((dia) => `<option value="${dia.value}">${dia.text}</option>`)
      .join("");
  }

  const crearFilaHorarioDesdeObj = (h) => {
    const row = document.createElement("tr");
    const diaText =
      diasSemana.find((dia) => dia.value === h.day.toString())?.text ||
      `Día ${h.day}`;

    row.innerHTML = `
      <td data-day-value="${h.day}">${diaText}</td>
      <td>${h.start}:00</td>
      <td>${h.end}:00</td>
      <td><button type="button" class="delete-horario-btn">Eliminar</button></td>
    `;
    return row;
  };










  materialsList.addEventListener("click", async (event) => {
    const target = event.target;
    const li = target.closest("li");

    console.log("=== DEBUG CLICK ===");
    console.log("Target:", target);
    console.log("Target classList:", target.classList);
    console.log("LI encontrado:", li);
    console.log("=== FIN DEBUG ===");

    if (!li) return;

    // Verificar si es botón de agregar horario
    if (target.classList.contains("add-btn") || target.closest(".add-btn")) {
      console.log("Clic en add-btn");
      abrirModalParaLi(li);
      return;
    }

    if (target.classList.contains("delete-btn") || target.closest(".delete-btn")) {
      console.log("Clic en delete-btn");
      // if (!confirm("¿Eliminar esta clase del horario?")) return;
      const proceed = await mostrarModalConfirmacionAsync("¿Eliminar esta clase del horario?");
      if (!proceed) return;
      const materiaId = li.dataset.materiaId;
      const materiaNombre = li.dataset.materiaNombre;

      if (materiaNombre) {
        const celdas = scheduleBody.querySelectorAll(`td[data-materia="${materiaNombre}"]`);
        celdas.forEach((c) => {
          c.classList.remove("class-scheduled");
          c.removeAttribute("data-materia");
          c.innerHTML = "";
        });
      }

      const materiasList = document.getElementById("materias-list");
      if (materiasList && materiaId && materiaNombre) {
        const existingMateria = materiasList.querySelector(`.materia-item[data-materia-id="${materiaId}"]`);
        if (!existingMateria) {
          const materiaItem = document.createElement("div");
          materiaItem.className = "materia-item";
          materiaItem.dataset.materiaId = materiaId;
          materiaItem.dataset.materiaNombre = materiaNombre;
          materiaItem.textContent = materiaNombre;

          materiaItem.addEventListener("click", () => {
            abrirModalConMateria(materiaId, materiaNombre);
          });

          if (materiasList.innerHTML.includes('No hay más materias disponibles')) {
            materiasList.innerHTML = '';
          }

          materiasList.appendChild(materiaItem);
        }
      }

      li.remove();
      return;
    }

    if (target.classList.contains("edit-btn") || target.closest(".edit-btn")) {
      console.log("Clic en edit-btn");
      abrirModalParaLi(li);
      return;
    }

    if (li.classList.contains("materia-asignada")) {
      abrirModalParaLi(li);
    }
  });

















  const closeModal = () => {
    const modal = document.getElementById("addClassModal");
    if (modal) {
      modal.classList.remove("visible");
    }
  };

  if (cancelBtn) {
    cancelBtn.addEventListener("click", closeModal);
  }

  modal.addEventListener("click", (e) => {
    if (e.target === modal) {
      closeModal();
    }
  });

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && modal.classList.contains("visible")) {
      closeModal();
    }
  });

  if (addHorarioBtn) {
    addHorarioBtn.addEventListener("click", async () => {
      const diaSelect = modal.querySelector("#horario-dia");
      const inicio = modal.querySelector("#horario-inicio");
      const fin = modal.querySelector("#horario-fin");
      const docenteSelect = modal.querySelector("#modal-docente");
      const aulaSelect = modal.querySelector("#modal-aula");

      if (!inicio.value || !fin.value) {
        alert("Selecciona inicio y fin de horario");
        return;
      }

      const inicioHora = parseInt(inicio.value.split(':')[0]);
      const finHora = parseInt(fin.value.split(':')[0]);

      if (inicioHora >= finHora) {
        alert("Rango de horas inválido");
        return;
      }

      if (!docenteSelect.value || docenteSelect.value === "") {
        alert("Debe seleccionar un docente antes de agregar horarios");
        return;
      }

      if (!aulaSelect.value || aulaSelect.value === "") {
        // alert("Debe seleccionar un aula antes de agregar horarios");
       mostrarModal("Debe seleccionar un aula antes de agregar horarios");
        return;
      }

      const materiaId = materiaActualLi ? materiaActualLi.dataset.materiaId : null;
      if (!materiaId) {
        alert("Error: No se pudo identificar la materia");
        return;
      }

      const nuevoHorario = {
        dia: parseInt(diaSelect.value),
        horaInicio: inicioHora,
        horaFin: finHora,
        aulaId: parseInt(aulaSelect.value),
        docenteId: parseInt(docenteSelect.value)
      };

      const horariosExistentes = obtenerHorariosExistentesDeTabla(horariosListBody, nuevoHorario.aulaId, nuevoHorario.docenteId);
      const conflictosLocales = verificarConflictosLocales(horariosExistentes, nuevoHorario);

      if (conflictosLocales.length > 0) {
        const mensajes = conflictosLocales.map(c => c.mensaje).join('\n');
        // alert(`CONFLICTOS EN EL GRUPO ACTUAL:\n${mensajes}\n\nPor favor, seleccione otro horario, aula o docente.`);
        mostrarModal(`CONFLICTOS EN EL GRUPO ACTUAL:\n${mensajes}\n\nPor favor, seleccione otro horario, aula o docente.`);

        return;
      }


      const row = document.createElement("tr");
      const dayText = diaSelect.options[diaSelect.selectedIndex].text;
      row.innerHTML = `
      <td data-day-value="${diaSelect.value}">${dayText}</td>
      <td>${inicioHora}:00</td>
      <td>${finHora}:00</td>
      <td><button type="button" class="delete-horario-btn">borrar</button></td>
    `;
      horariosListBody.appendChild(row);

      // alert(" Horario agregado correctamente");

      inicio.value = "07:00";
      fin.value = "08:00";
      diaSelect.selectedIndex = 0;
    });
  }








  if (horariosListBody) {
    horariosListBody.addEventListener("click", (e) => {
      if (e.target.classList.contains("delete-horario-btn")) {
        e.target.closest("tr").remove();
      }
    });
  }

  if (confirmBtn) {
    confirmBtn.addEventListener("click", async () => {
      if (!materiaActualLi) {
        alert("Error interno: materia no seleccionada.");
        return;
      }

      const materia = modalMateriaName.value.trim();
      const docente = modalDocente.value.trim();
      const aula = modalAula.value.trim();

      if (!docente || docente === "" || docente === "undefined") {
        // alert("Debe seleccionar un docente válido");
        mostrarModal("Debe seleccionar un docente válido");
        return;
      }

      if (!aula || aula === "" || aula === "undefined") {
        alert("Debe seleccionar un aula válida");
        return;
      }

      const horariosRows = horariosListBody.querySelectorAll("tr");

      if (horariosRows.length === 0) {
        // alert("Agrega al menos un horario");
        mostrarModal("Agrega al menos un horario");

        return;
      }

      let hayErroresBloqueantes = false;

      try {
        const materiaId = materiaActualLi.dataset.materiaId;

        const claseDTO = {
          materiaId: parseInt(materiaId),
          docenteId: parseInt(docente),
          aulaId: parseInt(aula),
          horarios: Array.from(horariosRows).map(row => ({
            dia: parseInt(row.cells[0].dataset.dayValue),
            horaInicio: `${parseInt(row.cells[1].textContent.split(":")[0]).toString().padStart(2, '0')}:00:00`,
            horaFin: `${parseInt(row.cells[2].textContent.split(":")[0]).toString().padStart(2, '0')}:00:00`
          }))
        };

        console.log("Validando clase completa antes de confirmar:", claseDTO);

        const response = await fetch('http://localhost:8080/grupos/validar-clase', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(claseDTO)
        });

        if (!response.ok) {
          const errorData = await response.json();
          const errorMessage = errorData.error || errorData.message || 'Error en la validación';

          if (errorMessage.includes("horas asignadas") ||
            errorMessage.includes("horas por semana") ||
            errorMessage.includes("horas requeridas")) {

            const continuar = await mostrarModalConfirmacionAsync(`ADVERTENCIA DE HORAS:\n\n${errorMessage}\n\n¿Desea continuar con la creación de la clase de todas formas?`);
            if (!continuar) return;

          }
          else if (errorMessage.includes("docente no está disponible") ||
            errorMessage.includes("Docente no está disponible")) {
            alert(` CONFLICTO DE DOCENTE:\n${errorMessage}\n\nPor favor, seleccione otro docente u horario.`);
            hayErroresBloqueantes = true;
            return;
          } else if (errorMessage.includes("aula no se encuentra disponible") ||
            errorMessage.includes("Aula no se encuentra disponible")) {
            alert(` CONFLICTO DE AULA:\n${errorMessage}\n\nPor favor, seleccione otra aula u horario.`);
            hayErroresBloqueantes = true;
            return;
          } else {
            alert(` ERROR:\n${errorMessage}\n\nPor favor, verifique los datos e intente nuevamente.`);
            hayErroresBloqueantes = true;
            return;
          }
        }

        console.log(" Validación del backend exitosa");

      } catch (error) {
        console.error('Error en validación final:', error);
        alert(' Error al validar la clase con el servidor. Intente nuevamente.');
        return;
      }

      if (hayErroresBloqueantes) {
        return;
      }

      const docenteSelect = document.getElementById("modal-docente");
      const aulaSelect = document.getElementById("modal-aula");
      const docenteNombre = docenteSelect?.options[docenteSelect.selectedIndex]?.text || "Docente";
      const aulaNombre = aulaSelect?.options[aulaSelect.selectedIndex]?.text || "Aula";

      let conflicto = false;
      const horariosObjs = [];

      horariosRows.forEach((row) => {
        const dayValue = parseInt(row.cells[0].dataset.dayValue);
        const dayText = row.cells[0].textContent.trim();
        const start = parseInt(row.cells[1].textContent.split(":")[0]);
        const end = parseInt(row.cells[2].textContent.split(":")[0]);

        horariosObjs.push({ day: dayValue, dayText, start, end });

        for (let h = start; h < end; h++) {
          const cell = scheduleBody.querySelector(
            `td[data-day-index="${dayValue}"][data-hour="${h}"]`
          );
          if (cell && cell.classList.contains("class-scheduled")) {
            const existingMateria = cell.dataset.materia;
            if (existingMateria && existingMateria !== materia) {
              // alert(
              //   ` CONFLICTO EN HORARIO:\nYa existe la clase "${existingMateria}" programada el ${dayText} a las ${h}:00.\n\nPor favor, ajuste los horarios.`
              // );
              mostrarModal(`CONFLICTO EN HORARIO:\nYa existe la clase "${existingMateria}" programada el ${dayText} a las ${h}:00.\n\nPor favor, ajuste los horarios`);


              conflicto = true;
              break;
            }
          }
        }
        if (conflicto) return;
      });

      if (conflicto) return;

      const materiaAnterior = materiaActualLi.dataset.materiaNombre;
      if (materiaAnterior) {
        const prevCells = scheduleBody.querySelectorAll(
          `td[data-materia="${materiaAnterior}"]`
        );
        prevCells.forEach((cell) => {
          cell.classList.remove("class-scheduled");
          cell.removeAttribute("data-materia");
          cell.innerHTML = "";
        });
      }

      horariosObjs.forEach((h) => {
        for (let hh = h.start; hh < h.end; hh++) {
          const cell = scheduleBody.querySelector(
            `td[data-day-index="${h.day}"][data-hour="${hh}"]`
          );
          if (cell) {
            cell.innerHTML = "";
            cell.className = "";
            cell.classList.add("class-scheduled");
            cell.dataset.materia = materia;

            const contenido = `
            <div class="class-content">
              <strong>${materia}</strong><br>
              ${docenteNombre}<br>
              Aula: ${aulaNombre}
            </div>
          `;
            cell.innerHTML = contenido;
          }
        }
      });

      materiaActualLi.dataset.materiaNombre = materia;
      materiaActualLi.dataset.docente = docente;
      materiaActualLi.dataset.aula = aula;
      materiaActualLi.dataset.horarios = JSON.stringify(horariosObjs);

      materiaActualLi.classList.add("materia-asignada");
      materiaActualLi.innerHTML = `
      <span class="material-text">${materia}</span>
      <div class="material-actions">
          <span class="material-icons edit-btn" title="Editar">edit</span>
          <span class="material-icons delete-btn" title="Eliminar">delete</span>
      </div>
    `;

      closeModal();
      materiaActualLi = null;
      mostrarModal("Clase asignada correctamente al horario");
      // alert(" Clase asignada correctamente al horario");
    });
  }
}






async function cargarDocentes(materiaId) {
  try {
    const response = await fetch(
      `http://localhost:8080/docentes/materia/${materiaId}`
    );

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const data = await response.json();

    const select = document.getElementById("modal-docente");
    if (!select) {
      console.error("No se encontró el select de docentes");
      return;
    }

    select.innerHTML = '<option value="">Selecciona un docente</option>';
    data.forEach((d) => {
      const option = document.createElement("option");
      option.value = d.id;
      option.textContent = `${d.nombre} ${d.apellidoPaterno} ${d.apellidoMaterno}`;
      select.appendChild(option);
    });
  } catch (error) {
    console.error("Error al cargar docentes:", error);
    const select = document.getElementById("modal-docente");
    if (select) {
      select.innerHTML = '<option value="">Error al cargar docentes</option>';
    }
  }
}





function verificarConflictosLocales(horariosExistentes, nuevoHorario) {
  const conflictos = [];

  horariosExistentes.forEach(horarioExistente => {
    if (horarioExistente.dia === nuevoHorario.dia) {
      const existeSuperposicion =
        (nuevoHorario.horaInicio >= horarioExistente.horaInicio && nuevoHorario.horaInicio < horarioExistente.horaFin) ||
        (nuevoHorario.horaFin > horarioExistente.horaInicio && nuevoHorario.horaFin <= horarioExistente.horaFin) ||
        (nuevoHorario.horaInicio <= horarioExistente.horaInicio && nuevoHorario.horaFin >= horarioExistente.horaFin);

      if (existeSuperposicion) {
        if (horarioExistente.aulaId === nuevoHorario.aulaId) {
          conflictos.push({
            tipo: 'AULA OCUPADA',
            mensaje: `El aula ya está ocupada en este grupo el ${obtenerNombreDia(nuevoHorario.dia)} de ${formatearHora(horarioExistente.horaInicio)} a ${formatearHora(horarioExistente.horaFin)}`,
            horarioExistente
          });
        }

        if (horarioExistente.docenteId === nuevoHorario.docenteId) {
          conflictos.push({
            tipo: 'DOCENTE OCUPADO',
            mensaje: `El docente ya está asignado en este grupo el ${obtenerNombreDia(nuevoHorario.dia)} de ${formatearHora(horarioExistente.horaInicio)} a ${formatearHora(horarioExistente.horaFin)}`,
            horarioExistente
          });
        }
      }
    }
  });

  return conflictos;
}

async function verificarConflictosEnTiempoReal(nuevoHorario, materiaId) {
  try {
    const { dia, horaInicio, horaFin, aulaId, docenteId } = nuevoHorario;

    console.log("Verificando conflictos con backend:", {
      materiaId, dia, horaInicio, horaFin, aulaId, docenteId
    });

    const claseDTO = {
      materiaId: materiaId,
      docenteId: docenteId,
      aulaId: aulaId,
      horarios: [
        {
          dia: dia,
          horaInicio: `${horaInicio.toString().padStart(2, '0')}:00:00`,
          horaFin: `${horaFin.toString().padStart(2, '0')}:00:00`,
        }
      ]
    };

    const response = await fetch('http://localhost:8080/grupos/validar-clase', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(claseDTO)
    });

    if (response.ok) {
      // ✅ No hay conflictos - la validación pasó
      console.log("✅ Validación exitosa - sin conflictos");
      return { conflictos: [], advertencias: [] };
    } else {
      const errorData = await response.json();
      const errorMessage = errorData.error || errorData.message || '';

      console.log("Respuesta del backend:", errorData);

      if (errorMessage.includes("horas asignadas") ||
        errorMessage.includes("horas por semana") ||
        errorMessage.includes("horas requeridas")) {
        return {
          conflictos: [],
          advertencias: [{
            tipo: 'ADVERTENCIA_HORAS',
            mensaje: errorMessage,
            esAdvertencia: true
          }]
        };
      } else {
        return {
          conflictos: [{
            tipo: 'CONFLICTO',
            mensaje: errorMessage,
            esAdvertencia: false
          }],
          advertencias: []
        };
      }
    }

  } catch (error) {
    console.warn('Error verificando conflictos externos:', error);

    return {
      conflictos: [{
        tipo: 'ERROR_VERIFICACION',
        mensaje: 'No se pudo verificar la disponibilidad. Por favor, intente con otro horario.',
        esAdvertencia: false
      }],
      advertencias: []
    };
  }
}

function obtenerNombreDia(dia) {
  const dias = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
  return dias[dia] || `Día ${dia + 1}`;
}

function formatearHora(hora) {
  return `${hora}:00`;
}

function obtenerHorariosExistentesDeTabla(horariosListBody, aulaId, docenteId) {
  const horarios = [];
  const rows = horariosListBody.querySelectorAll("tr");

  rows.forEach(row => {
    const dia = parseInt(row.cells[0].dataset.dayValue);
    const horaInicio = parseInt(row.cells[1].textContent.split(":")[0]);
    const horaFin = parseInt(row.cells[2].textContent.split(":")[0]);

    horarios.push({
      dia: dia,
      horaInicio: horaInicio,
      horaFin: horaFin,
      aulaId: aulaId,
      docenteId: docenteId
    });
  });

  return horarios;
}

function _createOverlay(mensaje, tipo = "info") {
  const overlay = document.createElement("div");
  overlay.className = "custom-modal-overlay";
  overlay.innerHTML = `
    <div class="custom-modal-box ${tipo}">
      <p>${mensaje}</p>
      <div class="custom-modal-actions">
        <button class="custom-modal-ok" id="custom-modal-ok">Aceptar</button>
      </div>
    </div>
  `;
  return overlay;
}

function mostrarModal(mensaje, tipo = "info") {
  const overlay = _createOverlay(mensaje, tipo);
  document.body.appendChild(overlay);

  const okBtn = overlay.querySelector(".custom-modal-ok");
  const remove = () => overlay.remove();

  okBtn.addEventListener("click", remove);
  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) remove();
  });
}




function mostrarModalConfirmacionAsync(mensaje) {
  return new Promise((resolve) => {
    const overlay = document.createElement("div");
    overlay.className = "custom-modal-overlay";
    overlay.innerHTML = `
      <div class="custom-modal-box confirm" id="custom-modal-box-confirm">
        <p>${mensaje}</p>
        <div class="custom-modal-actions">
          <button class="custom-modal-yes">Aceptar</button>
          <button class="custom-modal-no">Cancelar</button>
        </div>
      </div>
    `;
    document.body.appendChild(overlay);

    const yes = overlay.querySelector(".custom-modal-yes");
    const no = overlay.querySelector(".custom-modal-no");

    const cleanup = (value) => {
      overlay.remove();
      resolve(value);
    };

    yes.addEventListener("click", () => cleanup(true));
    no.addEventListener("click", () => cleanup(false));
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay) cleanup(false);
    });
  });
}

window.alert = function (mensaje) {
  mostrarModal(mensaje, "info");
};

window.confirm = function (mensaje) {
  return new Promise(resolve => {
    mostrarModalConfirmacion(mensaje, () => resolve(true), () => resolve(false));
  });
};



// function abrirModalConMateria(materiaId, materiaNombre) {
//   console.log("Abriendo modal para materia:", { materiaId, materiaNombre });

//   const assignedList = document.getElementById("assigned-materials-list");
//   const existingMateria = assignedList.querySelector(`li[data-materia-id="${materiaId}"]`);

//   if (existingMateria) {
//     abrirModalParaLi(existingMateria);
//     return;
//   }

//   const li = document.createElement("li");
//   li.dataset.materiaId = materiaId;
//   li.dataset.materiaNombre = materiaNombre;
//   li.innerHTML = `
//     <span class="material-text">${materiaNombre}</span>
//     <div class="material-actions">
//         <span class="material-icons add-btn" title="Agregar horario">add_circle</span>
//     </div>
//   `;
//   assignedList.appendChild(li);

//   const materiasList = document.getElementById("materias-list");
//   const materiaItemToRemove = materiasList.querySelector(`.materia-item[data-materia-id="${materiaId}"]`);
//   if (materiaItemToRemove) {
//     materiaItemToRemove.remove();
//   }

//   const materiasDisponibles = materiasList.querySelectorAll('.materia-item');
//   if (materiasDisponibles.length === 0) {
//     materiasList.innerHTML = '<div class="materia-item">No hay más materias disponibles</div>';
//   }

//   abrirModalParaLi(li);
// }
function abrirModalParaLi(li) {
  //  ESTABLECER materiaActualLi PRIMERO
  materiaActualLi = li;

  const modal = document.getElementById("addClassModal");
  if (!modal) {
    console.error("Modal no encontrado");
    return;
  }

  const modalMateriaName = modal.querySelector("#modal-materia-name");
  const horariosListBody = modal.querySelector("#horarios-list-body");
  const modalDocente = modal.querySelector("#modal-docente");
  const modalAula = modal.querySelector("#modal-aula");

  // Limpiar horarios anteriores
  horariosListBody.innerHTML = "";

  // Establecer valores del modal
  modalMateriaName.value = li.dataset.materiaNombre || "";

  // Cargar docentes para esta materia
  const materiaId = li.dataset.materiaId;
  if (materiaId) {
    cargarDocentes(materiaId).then(() => {
      // Si ya hay un docente guardado, seleccionarlo
      if (li.dataset.docente && li.dataset.docente !== "undefined") {
        modalDocente.value = li.dataset.docente;
      } else {
        modalDocente.value = ""; // Limpiar si no hay docente
      }
    }).catch(error => {
      console.error("Error cargando docentes:", error);
      modalDocente.value = "";
    });
  }

  // Establecer aula si ya existe
  if (li.dataset.aula && li.dataset.aula !== "undefined") {
    modalAula.value = li.dataset.aula;
  } else {
    modalAula.value = ""; // Limpiar si no hay aula
  }

  // Cargar horarios existentes si los hay
  if (li.dataset.horarios) {
    try {
      const horarios = JSON.parse(li.dataset.horarios);
      horarios.forEach((h) => {
        const row = document.createElement("tr");
        const diaText = obtenerNombreDia(parseInt(h.day)) || `Día ${h.day}`;
        row.innerHTML = `
          <td data-day-value="${h.day}">${diaText}</td>
          <td>${h.start}:00</td>
          <td>${h.end}:00</td>
          <td><button type="button" class="delete-horario-btn">Eliminar</button></td>
        `;
        horariosListBody.appendChild(row);
      });
    } catch (e) {
      console.warn("Error parseando horarios guardados:", e);
    }
  }

  // Abrir el modal
  modal.classList.add("visible");

  console.log("Modal abierto para materia:", li.dataset.materiaNombre);
  console.log("materiaActualLi establecido:", materiaActualLi !== null);
}





function obtenerClasesDelHorario() {
  const clases = [];
  const celdasOcupadas = document.querySelectorAll(".class-scheduled");

  const materiasMap = new Map();

  celdasOcupadas.forEach((cell) => {
    const materiaNombre = cell.dataset.materia;

    if (!materiaNombre) {
      return;
    }

    const materiaId = obtenerMateriaIdPorNombre(materiaNombre);
    if (!materiaId) {
      return;
    }

    if (!materiasMap.has(materiaNombre)) {
      materiasMap.set(materiaNombre, {
        materiaId: materiaId,
        horarios: [],
      });
    }

    const dia = parseInt(cell.dataset.day);
    const hora = parseInt(cell.dataset.hour);

    const aulaId = obtenerAulaIdDeCelda(cell);
    const docenteId = obtenerDocenteIdDeCelda(cell);

    const horarioObj = {
      dia: dia,
      horaInicio: formatearHoraLocalTime(hora),
      horaFin: formatearHoraLocalTime(hora + 1),
      aulaId: aulaId,
      docenteId: docenteId,
    };

    materiasMap.get(materiaNombre).horarios.push(horarioObj);
  });

  materiasMap.forEach((claseData, materiaNombre) => {
    if (claseData.materiaId && claseData.horarios.length > 0) {
      const primerHorario = claseData.horarios[0];

      const claseObj = {
        materiaId: claseData.materiaId,
        docenteId: primerHorario.docenteId,
        aulaId: primerHorario.aulaId,
        horarios: claseData.horarios,
      };

      clases.push(claseObj);
    }
  });

  return clases;
}

function obtenerMateriaIdPorNombre(materiaNombre) {
  const materiasAsignadas = document.querySelectorAll(
    "#assigned-materials-list li"
  );

  for (const li of materiasAsignadas) {
    if (li.dataset.materiaNombre === materiaNombre) {
      const materiaId = li.dataset.materiaId;

      if (
        !materiaId ||
        materiaId === "undefined" ||
        materiaId === "null" ||
        materiaId === ""
      ) {
        return null;
      }

      const idNumero = parseInt(materiaId);
      if (isNaN(idNumero)) {
        return null;
      }

      return idNumero;
    }
  }

  return null;
}

function formatearHoraLocalTime(hora) {
  return `${hora.toString().padStart(2, "0")}:00:00`;
}

function obtenerAulaIdDeCelda(cell) {
  const contenido = cell.innerHTML;

  const aulaMatch = contenido.match(/Aula:\s*([^<]+)/);
  if (aulaMatch) {
    const aulaNombre = aulaMatch[1].trim();

    const aulaSelect = document.getElementById("modal-aula");
    if (aulaSelect) {
      for (const option of aulaSelect.options) {
        if (option.text === aulaNombre) {
          return parseInt(option.value);
        }
      }
    }
  }

  const materiaNombre = cell.dataset.materia;
  if (materiaNombre) {
    const materiaLi = document.querySelector(
      `li[data-materia-nombre="${materiaNombre}"]`
    );
    if (
      materiaLi &&
      materiaLi.dataset.aula &&
      materiaLi.dataset.aula !== "undefined"
    ) {
      return parseInt(materiaLi.dataset.aula);
    }
  }

  return null;
}

function obtenerDocenteIdDeCelda(cell) {
  const contenido = cell.innerHTML;

  const lineas = contenido.split("<br>");

  if (
    lineas.length > 1 &&
    lineas[1].trim() &&
    !lineas[1].includes("undefined")
  ) {
    const docenteNombre = lineas[1].trim();

    const docenteSelect = document.getElementById("modal-docente");
    if (docenteSelect) {
      for (const option of docenteSelect.options) {
        if (option.text === docenteNombre) {
          return parseInt(option.value);
        }
      }
    }
  }

  const materiaNombre = cell.dataset.materia;
  if (materiaNombre) {
    const materiaLi = document.querySelector(
      `li[data-materia-nombre="${materiaNombre}"]`
    );
    if (
      materiaLi &&
      materiaLi.dataset.docente &&
      materiaLi.dataset.docente !== "undefined"
    ) {
      return parseInt(materiaLi.dataset.docente);
    }
  }

  return null;
}

function validarDatosClases(clases) {
  for (const clase of clases) {
    for (const horario of clase.horarios) {
      if (
        !horario.docenteId ||
        horario.docenteId === "undefined" ||
        isNaN(horario.docenteId)
      ) {
        return false;
      }

      if (
        !horario.aulaId ||
        horario.aulaId === "undefined" ||
        isNaN(horario.aulaId)
      ) {
        return false;
      }

      if (!horario.horaInicio || !horario.horaFin) {
        return false;
      }
    }
  }

  return true;
}

function verificarBotonesMaterias() {
  const materiasAsignadas = document.querySelectorAll('#assigned-materials-list li');

  materiasAsignadas.forEach(li => {
    const materialActions = li.querySelector('.material-actions');
    const deleteBtn = li.querySelector('.delete-btn');

    if (materialActions && !deleteBtn) {
      const deleteButton = document.createElement('span');
      deleteButton.className = 'material-icons delete-btn';
      deleteButton.title = 'Eliminar';
      deleteButton.textContent = 'delete';
      materialActions.appendChild(deleteButton);

      console.log('Botón de eliminar agregado a:', li.dataset.materiaNombre);
    }
  });
}


// document.addEventListener('DOMContentLoaded', verificarBotonesMaterias);



document.addEventListener("DOMContentLoaded", async function () {
  await actualizarCicloEscolarEnHeader();
  const scheduleBody = document.querySelector(".schedule-table tbody");
  const startTime = 7;
  const endTime = 14;

  scheduleBody.innerHTML = "";
  for (let hour = startTime; hour <= endTime; hour++) {
    const row = document.createElement("tr");
    const timeHeaderCell = document.createElement("th");
    timeHeaderCell.textContent = `${hour}:00`;
    row.appendChild(timeHeaderCell);

    // for (let day = 0; day < 7; day++) {
    for (let day = 0; day < 6; day++) {
      const cell = document.createElement("td");
      cell.dataset.hour = hour;

      cell.dataset.day = day;
      cell.setAttribute("data-day-index", day);
      cell.innerHTML = "";
      //  cell.className = '';
      row.appendChild(cell);
    }
    scheduleBody.appendChild(row);
  }

  const semestreSelect = document.getElementById("semestre");
  const carreraSelect = document.getElementById("carrera");
  const areaSelect = document.getElementById("area-propedeutica");

  const actualizarCampos = () => {
    const semestre = parseInt(semestreSelect.value);
    if (semestre === 1) {
      carreraSelect.disabled = true;
      areaSelect.disabled = true;
    } else if (semestre >= 2 && semestre < 6) {
      carreraSelect.disabled = false;
      areaSelect.disabled = true;
    } else if (semestre === 6) {
      carreraSelect.disabled = false;
      areaSelect.disabled = false;
    }
  };

  semestreSelect.addEventListener("change", actualizarCampos);
  actualizarCampos();

  async function cargarAreasPropedeuticas() {
    const select = document.getElementById("area-propedeutica");
    try {
      const response = await fetch("http://localhost:8080/areas-propedeuticas");
      const areas = await response.json();
      select.innerHTML = '<option value="ninguna">Ninguna</option>';
      areas.forEach((area) => {
        const option = document.createElement("option");
        option.value = area.id;
        option.textContent = area.nombre;
        select.appendChild(option);
      });
    } catch (error) {
      console.error("Error al cargar las áreas propedéuticas:", error);
    }
  }

  async function cargarCarreras() {
    try {
      const response = await fetch("http://localhost:8080/carreras-tecnicas");
      const data = await response.json();
      const select = document.getElementById("carrera");
      select.innerHTML = '<option value="tronco-comun">Tronco Común</option>';
      data.forEach((c) => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = c.nombre;
        select.appendChild(option);
      });
    } catch (error) {
      console.error("Error al cargar carreras:", error);
    }
  }

  async function cargarAulas() {
    try {
      const response = await fetch("http://localhost:8080/aulas");
      const data = await response.json();
      const select = document.getElementById("modal-aula");
      select.innerHTML = data
        .map((c) => `<option value="${c.id}">${c.clave}</option>`)
        .join("");
    } catch (error) {
      console.error("Error al cargar aulas:", error);
    }
  }

  async function cargarMateriasFiltradas() {
    try {
      const semestre = document.getElementById("semestre").value;
      const carrera = document.getElementById("carrera").value;
      const area = document.getElementById("area-propedeutica").value;

      const materiasList = document.getElementById("materias-list");

      if (!materiasList) {
        console.error("No se encontró el contenedor de materias");
        return;
      }

      if (!semestre) {
        materiasList.innerHTML =
          '<div class="materia-item">Selecciona un semestre</div>';
        return;
      }

      let url = `http://localhost:8080/materias/grado/${semestre}`;

      if (carrera && carrera !== "tronco-comun" && semestre >= 2) {
        url += `/carrera/${carrera}`;
      }

      if (area && area !== "ninguna" && semestre === "6") {
        url += `/area/${area}`;
      }

      const res = await fetch(url);

      if (!res.ok) {
        throw new Error(`Error HTTP: ${res.status}`);
      }

      const materias = await res.json();

      if (materias.length === 0) {
        materiasList.innerHTML =
          '<div class="materia-item">No hay materias disponibles para estos filtros</div>';
      } else {
        materiasList.innerHTML = "";

        materias.forEach((materia) => {
          const materiaItem = document.createElement("div");
          materiaItem.className = "materia-item";
          materiaItem.dataset.materiaId = materia.id;
          materiaItem.dataset.materiaNombre = materia.nombre;
          materiaItem.textContent = materia.nombre;

          materiaItem.addEventListener("click", () => {
            abrirModalConMateria(materia.id, materia.nombre);
          });

          materiasList.appendChild(materiaItem);
        });
      }
    } catch (err) {
      console.error("Error al cargar materias filtradas:", err);
      const materiasList = document.getElementById("materias-list");
      if (materiasList) {
        materiasList.innerHTML =
          '<div class="materia-item">Error al cargar materias</div>';
      }
    }
  }










  const modalContainer = document.getElementById("modal-container");
  try {
    const response = await fetch("../AgregarClase/AgregarClase.html");
    if (!response.ok) {
      console.error("No se encontró AgregarClase.html");
      return;
    }
    const modalHTML = await response.text();
    modalContainer.innerHTML = modalHTML;

    const modal = modalContainer.querySelector("#addClassModal");
    const materialsList = document.getElementById("assigned-materials-list");

    await cargarAreasPropedeuticas();
    await cargarCarreras();
    await cargarAulas();
    await cargarMateriasFiltradas();

    semestreSelect.addEventListener("change", function () {
      actualizarCampos();
      cargarMateriasFiltradas();
    });

    carreraSelect.addEventListener("change", cargarMateriasFiltradas);
    areaSelect.addEventListener("change", cargarMateriasFiltradas);

    if (modal && materialsList) {
      initializeModalLogic(scheduleBody, modal, materialsList);
    } else {
      console.error(
        "No se pudo inicializar el modal: elementos no encontrados"
      );
    }

    const modalStyle = document.createElement("link");
    modalStyle.rel = "stylesheet";
    modalStyle.href = "../AgregarClase/AgregarClase.css";
    document.head.appendChild(modalStyle);
  } catch (err) {
    console.error("Error cargando la modal:", err);
  }

  const btnCrear = document.getElementById("create-group-btn");
  if (btnCrear) {
    btnCrear.addEventListener("click", (e) => {
      e.preventDefault();
      validarCreacionGrupo();
    });
  }
});
document.addEventListener("click", (e) => {
  if (e.target.id === "btn-cancelar-grupo") {
    location.reload();
  }
});