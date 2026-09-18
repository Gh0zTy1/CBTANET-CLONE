// URL de tu backend
const API_URL = 'http://localhost:8080/usuarios/login';

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    disableButton(true);
    
    const idUsuario = document.getElementById('userId').value;
    const password = document.getElementById('password').value;

    // Construimos el JSON exacto que espera IniciarSesionDTO
    const payload = {
        id: parseInt(idUsuario), // Importante: Convertir a número (Long en Java)
        contrasena: password
    };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            // Login exitoso: El backend devuelve UsuarioDTO
            const usuarioData = await response.json();
            
            // GUARDAMOS LA SESIÓN EN EL NAVEGADOR (LocalStorage)
            // Ya que el backend no gestiona sesión por cookie en esta configuración
            localStorage.setItem('usuario_sesion', JSON.stringify(usuarioData));
            
            // Redirigir al Dashboard
            window.location.href = '../Dashboard/dashboard.html';
        } else {
            // Error (Credenciales inválidas)
            showError('ID o contraseña incorrectos.');
            disableButton(false);
        }
    } catch (error) {
        console.error('Error:', error);
        showError('No se pudo conectar con el servidor.');
        disableButton(false);
    }
});

function showError(msg) {
    const feedback = document.getElementById('feedbackMessage');
    feedback.textContent = msg;
    feedback.style.display = 'block';
}

function disableButton(disabled) {
    const btn = document.querySelector('.btn-primary');
    btn.disabled = disabled;
    btn.textContent = disabled ? 'VERIFICANDO...' : 'INGRESAR';
}