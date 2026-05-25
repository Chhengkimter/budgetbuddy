const API_BASE = 'http://localhost:8080/api';

document.getElementById('registerForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const errorBox   = document.getElementById('registerError');
    const successBox = document.getElementById('registerSuccess');
    const btn        = document.getElementById('registerBtn');

    // Reset state
    errorBox.style.display   = 'none';
    successBox.style.display = 'none';
    errorBox.textContent     = '';
    successBox.textContent   = '';

    // Read form values
    const payload = {
        userFirstName:   document.getElementById('firstName').value.trim(),
        userLastName:    document.getElementById('lastName').value.trim(),
        userEmail:       document.getElementById('regEmail').value.trim(),
        userPassword:    document.getElementById('regPassword').value,
    };

    // Basic front-end guard
    if (!payload.userFirstName || !payload.userLastName || !payload.userEmail || !payload.userPassword) {
        errorBox.textContent   = 'Please fill in all fields.';
        errorBox.style.display = 'block';
        return;
    }

    // Loading state
    btn.textContent  = 'Creating account...';
    btn.disabled     = true;

    try {
        const response = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            // Registration successful
            successBox.textContent   = 'Account created! Redirecting to login...';
            successBox.style.display = 'block';

            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);

        } else {
            // Backend returned an error message (e.g. "Email already in use")
            const errorText        = await response.text();
            errorBox.textContent   = errorText || 'Registration failed. Please try again.';
            errorBox.style.display = 'block';
        }

    } catch (err) {
        // Network error — backend probably not running
        errorBox.textContent   = 'Cannot connect to server. Make sure the backend is running.';
        errorBox.style.display = 'block';

    } finally {
        btn.textContent = 'Create Account';
        btn.disabled    = false;
    }
});