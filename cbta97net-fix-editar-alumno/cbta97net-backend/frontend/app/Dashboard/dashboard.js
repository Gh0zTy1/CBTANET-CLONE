document.addEventListener('DOMContentLoaded', () => {
    // 1. Seguridad: Verificar Sesión
    //verificarSesionLocal();

    // 2. UI: Mostrar fecha actual
    mostrarFecha();

    // 3. Listener para Logout
    document.getElementById('btnLogout').addEventListener('click', cerrarSesion);

    // 4. Lógica para Submenús (Acordeón)
    const toggles = document.querySelectorAll('.submenu-toggle');

    toggles.forEach(toggle => {
        toggle.addEventListener('click', (e) => {
            e.preventDefault(); // Evita navegar a #

            // Alternar clase 'active' para girar la flecha
            toggle.classList.toggle('active');

            // Buscar el <ul> hermano (submenu) y mostrarlo/ocultarlo
            const submenu = toggle.nextElementSibling;
            if (submenu) {
                submenu.classList.toggle('open');
            }
        });
    });
});

function verificarSesionLocal() {
    const sesion = localStorage.getItem('usuario_sesion');

    if (!sesion) {
        // Si no hay sesión, mandar al login
        window.location.href = '../Auth/login.html';
        return;
    }

    try {
        const usuario = JSON.parse(sesion);

        // Rellenar datos en la Topbar
        const nombreCompleto = `${usuario.nombre || ''} ${usuario.apellidoPaterno || ''}`;
        document.getElementById('userNameDisplay').textContent = nombreCompleto || 'Usuario';
        document.getElementById('userIdDisplay').textContent = `ID: ${usuario.id}`;

        // Rol
        const rolNombre = usuario.rol ? usuario.rol.nombre : 'USUARIO';
        document.getElementById('userRoleBadge').textContent = rolNombre;

    } catch (e) {
        console.error("Error al leer sesión", e);
        cerrarSesion();
    }
}

/**
 * Carga un módulo dentro del Iframe.
 * @param {string} ruta - Ruta relativa del archivo HTML a cargar.
 * @param {string} titulo - Título a mostrar en la barra superior.
 */
function cargarModulo(ruta, titulo) {
    // Actualizar título
    document.getElementById('moduleTitle').textContent = titulo || 'CBTANet';

    // Elementos DOM
    const welcomeContainer = document.getElementById('welcomeContainer');
    const iframe = document.getElementById('mainFrame');

    // Cambiar visibilidad
    welcomeContainer.style.display = 'none';
    iframe.style.display = 'block';

    // Cargar URL en el iframe
    iframe.src = ruta;
}

/**
 * Vuelve a la pantalla de bienvenida.
 */
function cargarInicio() {
    document.getElementById('moduleTitle').textContent = 'Panel Principal';

    const welcomeContainer = document.getElementById('welcomeContainer');
    const iframe = document.getElementById('mainFrame');

    welcomeContainer.style.display = 'flex'; // Usamos flex para centrar contenido
    iframe.style.display = 'none';
    iframe.src = ''; // Limpiar para liberar memoria
}

function cerrarSesion() {
    localStorage.removeItem('usuario_sesion');
    window.location.href = '../Auth/login.html';
}

function mostrarFecha() {
    const opciones = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const fecha = new Date().toLocaleDateString('es-MX', opciones);
    const el = document.getElementById('fechaHoy');
    if(el) {
        // Capitalizar primera letra
        el.textContent = fecha.charAt(0).toUpperCase() + fecha.slice(1);
    }
}