const API_BASE = 'http://localhost:8080/api';

async function handleLogin(e) {
    e.preventDefault();

    const errorBox = document.getElementById('loginError');
    const btn      = document.getElementById('loginBtn');

    // Reset state
    errorBox.style.display = 'none';
    errorBox.textContent   = '';

    const payload = {
        userEmail:    document.getElementById('loginEmail').value.trim(),
        userPassword: document.getElementById('loginPassword').value,
    };

    if (!payload.userEmail || !payload.userPassword) {
        errorBox.textContent   = 'Please enter your email and password.';
        errorBox.style.display = 'block';
        return;
    }

    // Loading state
    btn.textContent = 'Logging in...';
    btn.disabled    = true;

    try {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const user = await response.json();

            // Save user to sessionStorage so other pages can read it
            // sessionStorage clears when the browser tab is closed
            sessionStorage.setItem('currentUser', JSON.stringify(user));
            sessionStorage.setItem('userID', user.userID);

            // If "Remember me" is checked, also persist to localStorage
            const rememberMe = document.getElementById('remember').checked;
            if (rememberMe) {
                localStorage.setItem('currentUser', JSON.stringify(user));
                localStorage.setItem('userID', user.userID);
            }

            // Redirect to dashboard
            window.location.href = 'dashboard.html';

        } else {
            // 401 from backend — always same message, never reveal which field failed
            errorBox.textContent   = 'Invalid email or password.';
            errorBox.style.display = 'block';
        }

    } catch (err) {
        errorBox.textContent   = 'Cannot connect to server. Make sure the backend is running.';
        errorBox.style.display = 'block';

    } finally {
        btn.textContent = 'Log In';
        btn.disabled    = false;
    }
}