// ── API Base URL ──────────────────────────────────────────────
const API = 'http://localhost:8080/api';

// ── Handle Login ───────────────────────────────────────────────
async function handleLogin(event) {
    event.preventDefault();

    const email    = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    const errorDiv = document.getElementById('loginError');
    errorDiv.style.display = 'none';
    errorDiv.textContent = '';

    try {
        // Validate input
        if (!email || !password) {
            throw new Error('Please enter email and password');
        }

        // Call the backend login endpoint
        const response = await fetch(`${API}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Login failed');
        }

        // Success - store user data
        localStorage.setItem('userId', data.id);
        localStorage.setItem('userName', data.name);
        localStorage.setItem('userEmail', data.email);
        localStorage.setItem('isLoggedIn', 'true');

        // Redirect to dashboard
        window.location.href = 'dashboard.html';

    } catch (error) {
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    }
}
