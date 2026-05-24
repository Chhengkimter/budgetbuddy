// ── API Base URL ──────────────────────────────────────────────
const API = 'http://localhost:8080/api';

// ── Handle Registration ────────────────────────────────────────
async function handleRegister(event) {
    event.preventDefault();

    const name     = document.getElementById('regName').value.trim();
    const email    = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;

    const errorDiv   = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');

    errorDiv.style.display   = 'none';
    successDiv.style.display = 'none';
    errorDiv.textContent   = '';
    successDiv.textContent = '';

    try {
        // Validate input
        if (!name || !email || !password) {
            throw new Error('Please fill in all fields');
        }
        if (password.length < 6) {
            throw new Error('Password must be at least 6 characters');
        }

        // Call the backend registration endpoint
        const response = await fetch(`${API}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Registration failed');
        }

        // Success
        successDiv.textContent = 'Account created successfully! Redirecting to login...';
        successDiv.style.display = 'block';

        // Store user data in localStorage (for reference)
        localStorage.setItem('userId', data.id);
        localStorage.setItem('userName', data.name);
        localStorage.setItem('userEmail', data.email);

        // Redirect to login page after 2 seconds
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);

    } catch (error) {
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    }
}
