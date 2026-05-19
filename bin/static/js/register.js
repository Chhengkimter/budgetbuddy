const API = 'http://localhost:8080/api';

async function handleRegister(event) {
    event.preventDefault();

    const firstName = document.getElementById('firstName').value.trim();
    const lastName  = document.getElementById('lastName').value.trim();
    const name      = `${firstName} ${lastName}`;
    const email     = document.getElementById('regEmail').value.trim();
    const password  = document.getElementById('regPassword').value;

    const errorDiv   = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');

    errorDiv.style.display   = 'none';
    successDiv.style.display = 'none';
    errorDiv.textContent     = '';
    successDiv.textContent   = '';

    try {
        if (!firstName || !lastName || !email || !password) {
            throw new Error('Please fill in all fields');
        }
        if (password.length < 6) {
            throw new Error('Password must be at least 6 characters');
        }

        const response = await fetch(`${API}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Registration failed');
        }

        successDiv.textContent = 'Account created successfully! Redirecting to login...';
        successDiv.style.display = 'block';

        setTimeout(() => { window.location.href = 'login.html'; }, 2000);

    } catch (error) {
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    }
}

document.getElementById('registerForm')
    .addEventListener('submit', handleRegister);