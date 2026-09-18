import requests
import json

URL_BASE = "http://localhost:8080"

# --- Listas Globales ---
SEMESTRES = [
        "Primer_Semestre", "Segundo_Semestre", "Tercer_Semestre",
        "Cuarto_Semestre", "Quinto_Semestre", "Sexto_Semestre"
]
AREAS_PROPEDEUTICAS = []
CARRERAS_TECNICAS   = []
MATERIAS            = []
DOCENTES            = []
AULAS               = [] # <-- NUEVA LISTA
CICLO_ESCOLAR_ACTIVO_ID = None 

# --- URLs ---
CICLOS_ESCOLARES_URL    = f"{URL_BASE}/ciclos-escolares"
MATERIAS_URL            = f"{URL_BASE}/materias"
AREAS_PROPEDEUTICAS_URL = f"{URL_BASE}/areas-propedeuticas"
CARRERAS_URL            = f"{URL_BASE}/carreras-tecnicas"
DOCENTES_URL            = f"{URL_BASE}/docentes"
AULAS_URL               = f"{URL_BASE}/aulas" # <-- NUEVA URL
GRUPOS_URL              = f"{URL_BASE}/grupos" # <-- NUEVA URL

# --- Funciones para Mostrar ---

def mostrar_semestres():
    print("\n--- SEMESTRES ---")
    for i, sem in enumerate(SEMESTRES):
        print(f"{i}. {sem}")

def mostrar_carreras():
    print("\n--- CARRERAS TÉCNICAS ---")
    for i, carrera in enumerate(CARRERAS_TECNICAS):
        print(f"{i}. {carrera['nombre']}")

def mostrar_areas():
    print("\n--- ÁREAS PROPEDÉUTICAS ---")
    for i, area in enumerate(AREAS_PROPEDEUTICAS):
        print(f"{i}. {area['nombre']}")

def mostrar_materias():
    print("\n--- MATERIAS DISPONIBLES ---")
    if not MATERIAS:
        print("No se encontraron materias.")
        return
    for i, mat in enumerate(MATERIAS):
        print(f"{i}. {mat['nombre']}")

def mostrar_docentes():
    print("\n--- DOCENTES DISPONIBLES ---")
    if not DOCENTES:
        print("No se encontraron docentes.")
        return
    for i, docente in enumerate(DOCENTES):
        nombre_completo = f"{docente.get('nombre', '')} {docente.get('apellidoPaterno', '')}"
        print(f"{i}. {nombre_completo.strip()}")

def mostrar_aulas(): # <-- NUEVA FUNCIÓN
    """Muestra la lista de aulas disponibles."""
    print("\n--- AULAS DISPONIBLES ---")
    if not AULAS:
        print("No se encontraron aulas.")
        return
    for i, aula in enumerate(AULAS):
        print(f"{i}. {aula['clave']}")


# --- Funciones para Obtener Datos (GET) ---

def obtener_datos(url, lista_global, nombre_entidad):
    """Función genérica para obtener datos de un endpoint."""
    global AREAS_PROPEDEUTICAS, CARRERAS_TECNICAS, AULAS, DOCENTES
    try:
        response = requests.get(url)
        response.raise_for_status()
        if nombre_entidad == "AREAS_PROPEDEUTICAS": AREAS_PROPEDEUTICAS = response.json()
        elif nombre_entidad == "CARRERAS_TECNICAS": CARRERAS_TECNICAS = response.json()
        elif nombre_entidad == "AULAS": AULAS = response.json()
        elif nombre_entidad == "DOCENTES": DOCENTES = response.json()
    except requests.exceptions.RequestException as e:
        print(f"[=!=] No se pudieron obtener las {nombre_entidad.lower()}: {e}")
        exit(1)

def obtener_materias(grado_idx=None, carrera_id=None, area_id=None):
    global MATERIAS
    # ... (Se mantiene la misma función de la pregunta anterior)
    url = MATERIAS_URL
    if grado_idx is not None and 1 <= grado_idx < len(SEMESTRES):
        url += f"/grado/{grado_idx}"
        if carrera_id is not None and carrera_id > 0: url += f"/carrera/{carrera_id}"
        if area_id is not None and area_id > 0: url += f"/area/{area_id}"
    elif area_id is not None and area_id > 0: url += f"/area/{area_id}"
    response = requests.get(url)
    MATERIAS = response.json() if response.status_code == 200 else []


def obtener_ciclo_escolar_activo():
    """Obtiene el ciclo escolar activo y almacena su ID globalmente."""
    global CICLO_ESCOLAR_ACTIVO_ID
    url = f"{CICLOS_ESCOLARES_URL}/activo"
    print(f"Verificando ciclo escolar activo en: {url}")
    try:
        response = requests.get(url)
        response.raise_for_status()
        ciclo = response.json()
        CICLO_ESCOLAR_ACTIVO_ID = ciclo.get('id')
        if CICLO_ESCOLAR_ACTIVO_ID:
            print(f"--> Ciclo Escolar Activo encontrado. ID: {CICLO_ESCOLAR_ACTIVO_ID}")
        else:
            print("[=!=] No se encontró un ID en la respuesta del ciclo activo.")
            exit(1)
    except requests.exceptions.RequestException as e:
        print(f"[=!=] ERROR: No se pudo obtener el ciclo escolar activo. {e}")
        print("Asegúrate de que haya un ciclo activo en la base de datos y que el endpoint /ciclos-escolares/activo exista.")
        exit(1)


# --- NUEVA FUNCIÓN PARA CREAR Y ENVIAR GRUPO (POST) ---

def crear_y_enviar_grupo():
    """Función principal para construir el objeto Grupo y enviarlo al servidor."""
    print("\n=====================================")
    print("    CREACIÓN DE UN NUEVO GRUPO")
    print("=====================================")

    global CICLO_ESCOLAR_ACTIVO_ID
    if CICLO_ESCOLAR_ACTIVO_ID == None:
        print("[!] No hay ciclo escolar activo...")

    # 1. Recopilar datos básicos del grupo
    mostrar_semestres()
    grado_idx = int(input("Selecciona el índice del semestre (1 - 6): "))
    letra = input("Introduce la letra del grupo (ej. A, B): ").upper()
    turno = input("Introduce el turno (MATUTINO o VESPERTINO): ").upper()
    #ciclo_id = int(input("Introduce el ID del ciclo escolar: "))
    nota = input("Añade una nota para el grupo: ")
    
    carrera_id = None
    if grado_idx >= 2: # A partir de tercer semestre se elige carrera
        mostrar_carreras()
        carrera_idx = int(input("Selecciona el índice de la carrera técnica: "))
        carrera_id = CARRERAS_TECNICAS[carrera_idx]['id']

    area_id = None
    if grado_idx >= 4: # A partir de quinto semestre se elige área
        mostrar_areas()
        area_idx = int(input("Selecciona el índice del área propedéutica: "))
        area_id = AREAS_PROPEDEUTICAS[area_idx]['id']

    # 2. Construir la lista de clases interactivamente
    clases_para_grupo = []
    while True:
        print("\n--- Añadir una nueva clase al grupo ---")
        
        # Obtener y mostrar materias para el semestre seleccionado
        obtener_materias(grado_idx, carrera_id, area_id)
        mostrar_materias()
        if not MATERIAS:
            print("No hay materias para este grado/carrera. No se pueden añadir más clases.")
            break
        
        materia_idx = int(input("Selecciona el índice de la materia: "))
        materia_seleccionada = MATERIAS[materia_idx]
        
        # Obtener y mostrar docentes para esa materia
        obtener_datos(f"{DOCENTES_URL}/materia/{materia_seleccionada['id']}", DOCENTES, "DOCENTES")
        mostrar_docentes()
        docente_idx = int(input("Selecciona el índice del docente: "))
        docente_seleccionado = DOCENTES[docente_idx]
        
        # Mostrar aulas (asumimos que todas están disponibles por simplicidad)
        mostrar_aulas()
        aula_idx = int(input("Selecciona el índice del aula: "))
        aula_seleccionada = AULAS[aula_idx]

        # Crear horario (ejemplo simple)
        dia = input("Día de la semana (LUNES, MARTES...): ").upper()
        hora_inicio = input("Hora de inicio (HH:MM:SS): ")
        hora_fin = input("Hora de fin (HH:MM:SS): ")
        
        # Construir el objeto Clase
        nueva_clase = {
            "materiaId": materia_seleccionada['id'],
            "docenteId": docente_seleccionado['id'],
            "aulaId": aula_seleccionada['id'],
            "horarios": [{
                "dia": dia,
                "horaInicio": hora_inicio,
                "horaFin": hora_fin
            }]
        }

        try:
            response = requests.post(f"{URL_BASE}/grupos/validar-clase", json=nueva_clase)
            response.raise_for_status() # Lanza excepción para errores 4xx/5xx
            #print("\n¡ÉXITO! Grupo creado correctamente.")
            print("Respuesta del servidor:")
            print(response.json())
            clases_para_grupo.append(nueva_clase)
        except requests.exceptions.RequestException as e:
            print("\n[=!=] ❌ ERROR al crear la clase")
            # Si hay respuesta del servidor (por ejemplo, un 400 o 500)
            if hasattr(e, "response") and e.response is not None:
                print(f"→ Código HTTP: {e.response.status_code}")
                try:
                    error_json = e.response.json()
                    print("→ Detalles del error (JSON):", json.dumps(error_json, indent=4, ensure_ascii=False))
                except ValueError:
                    print("→ Detalles del error (texto):", e.response.text)
            else:
                # Si fue un error de conexión o timeout
                print("→ No se recibió respuesta del servidor.")
                print(f"→ Detalles: {e}")

            continue # volver al inicio del bucle
        
        if input("¿Añadir otra clase? (s/n): ").lower() != 's':
            break

    # 3. Construir el payload final del Grupo
    grupo_payload = {
        "nota": nota,
        "letra": letra,
        "activo": True,
        "semestre": grado_idx,
        "turno": turno,
        "cicloEscolarId": CICLO_ESCOLAR_ACTIVO_ID,
        "carreraTecnicaId": carrera_id,
        "areaPropedeuticaId": area_id,
        "clases": clases_para_grupo
    }

    # 4. Enviar la petición POST
    print("\nEnviando datos del grupo al servidor...")
    print(json.dumps(grupo_payload, indent=2)) # Muestra el JSON que se va a enviar
    
    try:
        response = requests.post(GRUPOS_URL, json=grupo_payload)
        response.raise_for_status() # Lanza excepción para errores 4xx/5xx
        
        print("\n¡ÉXITO! Grupo creado correctamente.")
        print("Respuesta del servidor:")
        print(response.json())
        
    except requests.exceptions.RequestException as e:
        print("\n[=!=] ❌ ERROR al crear el grupo:")
        # Si hay respuesta del servidor (por ejemplo, un 400 o 500)
        if hasattr(e, "response") and e.response is not None:
            print(f"→ Código HTTP: {e.response.status_code}")
            try:
                error_json = e.response.json()
                print("→ Detalles del error (JSON):", json.dumps(error_json, indent=4, ensure_ascii=False))
            except ValueError:
                print("→ Detalles del error (texto):", e.response.text)
        else:
            # Si fue un error de conexión o timeout
            print("→ No se recibió respuesta del servidor.")
            print(f"→ Detalles: {e}")

# TODO: se debe de agregar la validacion de si se puede crear la clase.

# --- Flujo Principal ---
print("Cargando datos iniciales...")
obtener_ciclo_escolar_activo()
obtener_datos(AREAS_PROPEDEUTICAS_URL, AREAS_PROPEDEUTICAS, "AREAS_PROPEDEUTICAS")
obtener_datos(CARRERAS_URL, CARRERAS_TECNICAS, "CARRERAS_TECNICAS")
obtener_datos(AULAS_URL, AULAS, "AULAS")
print("¡Datos cargados!")

# Iniciar el proceso de creación de grupo
crear_y_enviar_grupo()
