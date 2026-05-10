// ── API Base URL ──────────────────────────────────────────────
const API = 'http://localhost:8080/api';

// ── App State ─────────────────────────────────────────────────
let currentUser = null;
let budgets     = [];
let transactions = [];

// ─────────────────────────────────────────────────────────────
//  AUTH
// ─────────────────────────────────────────────────────────────

function switchTab(tab) {
    document.getElementById('loginForm').style.display    = tab === 'login'    ? 'block' : 'none';
    document.getElementById('registerForm').style.display = tab === 'register' ? 'block' : 'none';
    document.querySelectorAll('.tab').forEach((t, i) => {
        t.classList.toggle('active', (tab === 'login' && i === 0) || (tab === 'register' && i === 1));
    });
}

async function login() {
    const email    = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    document.getElementById('loginError').textContent = '';

    try {
        const res = await fetch(`${API}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Login failed');
        setCurrentUser(data);
    } catch (err) {
        document.getElementById('loginError').textContent = err.message;
    }
}

async function register() {
    const name     = document.getElementById('regName').value.trim();
    const email    = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    document.getElementById('registerError').textContent = '';

    try {
        const res = await fetch(`${API}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Registration failed');
        setCurrentUser(data);
    } catch (err) {
        document.getElementById('registerError').textContent = err.message;
    }
}

function setCurrentUser(user) {
    currentUser = user;
    document.getElementById('authSection').style.display      = 'none';
    document.getElementById('dashboardSection').style.display = 'block';
    document.getElementById('logoutBtn').style.display        = 'inline-block';
    document.getElementById('navUsername').textContent        = `👤 ${user.name}`;
    loadDashboard();
}

function logout() {
    currentUser  = null;
    budgets      = [];
    transactions = [];
    document.getElementById('authSection').style.display      = 'block';
    document.getElementById('dashboardSection').style.display = 'none';
    document.getElementById('logoutBtn').style.display        = 'none';
    document.getElementById('navUsername').textContent        = '';
}

// ─────────────────────────────────────────────────────────────
//  DASHBOARD
// ─────────────────────────────────────────────────────────────

async function loadDashboard() {
    await loadBudgets();
    await loadTransactions();
    updateSummaryCards();
}

// ─────────────────────────────────────────────────────────────
//  BUDGETS
// ─────────────────────────────────────────────────────────────

async function loadBudgets() {
    const res = await fetch(`${API}/budgets/user/${currentUser.id}`);
    budgets   = await res.json();
    renderBudgets();
    populateBudgetDropdown();
}

function renderBudgets() {
    const container = document.getElementById('budgetsList');
    if (budgets.length === 0) {
        container.innerHTML = '<p class="empty-state">No budgets yet. Create one above!</p>';
        return;
    }

    container.innerHTML = budgets.map(b => {
        const spent   = (b.transactions || [])
            .filter(t => t.type === 'EXPENSE')
            .reduce((sum, t) => sum + t.amount, 0);
        const percent = b.totalAmount > 0 ? Math.min((spent / b.totalAmount) * 100, 100) : 0;

        return `
        <div class="budget-card">
            <h3>${b.name}</h3>
            <div class="progress-bar">
                <div class="progress-fill" style="width:${percent}%"></div>
            </div>
            <div class="budget-meta">
                <span>Spent: $${spent.toFixed(2)}</span>
                <span>Total: $${b.totalAmount.toFixed(2)}</span>
            </div>
            <button class="btn btn-danger" style="margin-top:0.8rem;width:100%"
                onclick="deleteBudget(${b.id})">Delete</button>
        </div>`;
    }).join('');
}

function openBudgetModal() {
    document.getElementById('budgetModal').style.display = 'flex';
}

async function createBudget() {
    const name   = document.getElementById('budgetName').value.trim();
    const amount = parseFloat(document.getElementById('budgetAmount').value);
    document.getElementById('budgetError').textContent = '';

    if (!name || isNaN(amount) || amount <= 0) {
        document.getElementById('budgetError').textContent = 'Please fill all fields correctly.';
        return;
    }

    try {
        const res = await fetch(`${API}/budgets/user/${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, totalAmount: amount })
        });
        if (!res.ok) throw new Error('Failed to create budget');
        closeModal('budgetModal');
        document.getElementById('budgetName').value   = '';
        document.getElementById('budgetAmount').value = '';
        loadDashboard();
    } catch (err) {
        document.getElementById('budgetError').textContent = err.message;
    }
}

async function deleteBudget(id) {
    if (!confirm('Delete this budget and all its transactions?')) return;
    await fetch(`${API}/budgets/${id}`, { method: 'DELETE' });
    loadDashboard();
}

// ─────────────────────────────────────────────────────────────
//  TRANSACTIONS
// ─────────────────────────────────────────────────────────────

async function loadTransactions() {
    const res    = await fetch(`${API}/transactions/user/${currentUser.id}`);
    transactions = await res.json();
    renderTransactions();
}

function renderTransactions() {
    const container = document.getElementById('transactionsList');
    if (transactions.length === 0) {
        container.innerHTML = '<p class="empty-state">No transactions yet.</p>';
        return;
    }

    // Show most recent first
    const sorted = [...transactions].sort((a, b) =>
        new Date(b.createdAt) - new Date(a.createdAt));

    container.innerHTML = sorted.map(t => `
        <div class="transaction-item">
            <div class="tx-info">
                <span class="tx-desc">${t.description}</span>
                <span class="tx-date">${t.createdAt ? new Date(t.createdAt).toLocaleDateString() : 'N/A'}</span>
            </div>
            <span class="tx-amount ${t.type === 'INCOME' ? 'income' : 'expense'}">
                ${t.type === 'INCOME' ? '+' : '-'}$${t.amount.toFixed(2)}
            </span>
        </div>
    `).join('');
}

function openTransactionModal() {
    if (budgets.length === 0) {
        alert('Please create a budget first!');
        return;
    }
    document.getElementById('transactionModal').style.display = 'flex';
}

function populateBudgetDropdown() {
    const select = document.getElementById('txBudgetId');
    select.innerHTML = budgets.map(b =>
        `<option value="${b.id}">${b.name}</option>`
    ).join('');
}

async function addTransaction() {
    const budgetId    = document.getElementById('txBudgetId').value;
    const description = document.getElementById('txDescription').value.trim();
    const amount      = parseFloat(document.getElementById('txAmount').value);
    const type        = document.getElementById('txType').value;
    document.getElementById('transactionError').textContent = '';

    if (!description || isNaN(amount) || amount <= 0) {
        document.getElementById('transactionError').textContent = 'Please fill all fields correctly.';
        return;
    }

    try {
        const res = await fetch(`${API}/transactions/budget/${budgetId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ description, amount, type })
        });
        if (!res.ok) throw new Error('Failed to add transaction');
        closeModal('transactionModal');
        document.getElementById('txDescription').value = '';
        document.getElementById('txAmount').value      = '';
        loadDashboard();
    } catch (err) {
        document.getElementById('transactionError').textContent = err.message;
    }
}

// ─────────────────────────────────────────────────────────────
//  SUMMARY CARDS
// ─────────────────────────────────────────────────────────────

function updateSummaryCards() {
    const income   = transactions.filter(t => t.type === 'INCOME').reduce((s, t) => s + t.amount, 0);
    const expenses = transactions.filter(t => t.type === 'EXPENSE').reduce((s, t) => s + t.amount, 0);
    const net      = income - expenses;

    document.getElementById('totalIncome').textContent   = `$${income.toFixed(2)}`;
    document.getElementById('totalExpenses').textContent = `$${expenses.toFixed(2)}`;
    document.getElementById('netBalance').textContent    = `$${net.toFixed(2)}`;
}

// ─────────────────────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────────────────────

function closeModal(id) {
    document.getElementById(id).style.display = 'none';
}

// Close modal when clicking the dark overlay
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal(modal.id);
    });
});
