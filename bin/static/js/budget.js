// ── API Base URL ──────────────────────────────────────────────
const API = 'http://localhost:8080/api';

// ── App State ─────────────────────────────────────────────────
let currentUser = null;
let budgets = [];

// ─────────────────────────────────────────────────────────────
//  PAGE INITIALIZATION
// ─────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const userId = localStorage.getItem('userId');

    if (!isLoggedIn || !userId) {
        // Redirect to login if not logged in
        window.location.href = 'login.html';
        return;
    }

    // Load budget data
    currentUser = { id: userId };
    loadBudgetData();
});

function redirectToLogin() {
    window.location.href = 'index.html';
}

// ─────────────────────────────────────────────────────────────
//  LOAD BUDGET DATA FROM API
// ─────────────────────────────────────────────────────────────

async function loadBudgetData() {
    if (!currentUser || !currentUser.id) {
        redirectToLogin();
        return;
    }

    try {
        const res = await fetch(`${API}/budgets/user/${currentUser.id}`);
        if (!res.ok) throw new Error('Failed to load budgets');
        
        budgets = await res.json();
        renderBudgetList();
        updateSummaryCards();
    } catch (err) {
        console.error('Error loading budgets:', err);
        showError('Failed to load budgets');
    }
}

// ─────────────────────────────────────────────────────────────
//  RENDER BUDGET LIST (dynamically from API)
// ─────────────────────────────────────────────────────────────

function renderBudgetList() {
    const listContainer = document.getElementById('budgetListContainer');
    
    if (!listContainer) return; // Container may not exist on this page
    
    if (budgets.length === 0) {
        listContainer.innerHTML = '<p class="empty-state">No budgets yet. Click "New Budget" to create one.</p>';
        return;
    }

    listContainer.innerHTML = budgets.map(budget => {
        const spent = budget.spentAmount || 0;
        const percent = budget.totalAmount > 0 ? Math.min((spent / budget.totalAmount) * 100, 100) : 0;
        const remaining = budget.remainingBalance || 0;

        return `
        <div class="budget-item-wrap">
            <div class="budget-meta-info">
                <span>${budget.name} (${budget.category})</span>
                <span class="bold-amt">$${spent.toFixed(2)} / $${budget.totalAmount.toFixed(2)}</span>
            </div>
            <div class="progress-container">
                <div class="progress-bar-fill" style="width: ${percent}%; background: var(--primary);"></div>
            </div>
            <div class="budget-description" style="font-size: 12px; color: #666; margin-top: 4px;">
                Remaining: $${remaining.toFixed(2)}
            </div>
        </div>`;
    }).join('');
}

// ─────────────────────────────────────────────────────────────
//  SUMMARY CARDS
// ─────────────────────────────────────────────────────────────

function updateSummaryCards() {
    const totalBudget = budgets.reduce((sum, b) => sum + (b.totalAmount || 0), 0);
    const totalSpent = budgets.reduce((sum, b) => sum + (b.spentAmount || 0), 0);
    const totalRemaining = budgets.reduce((sum, b) => sum + (b.remainingBalance || 0), 0);
    const percentUsed = totalBudget > 0 ? Math.min((totalSpent / totalBudget) * 100, 100) : 0;

    // Update summary cards if they exist
    const totalBudgetEl = document.querySelector('[data-budget-total]');
    const totalSpentEl = document.querySelector('[data-budget-spent]');
    const percentEl = document.querySelector('[data-budget-percent]');

    if (totalBudgetEl) totalBudgetEl.textContent = `$${totalBudget.toFixed(2)}`;
    if (totalSpentEl) totalSpentEl.textContent = `$${totalSpent.toFixed(2)}`;
    if (percentEl) percentEl.textContent = `${percentUsed.toFixed(0)}% used`;
}

// ─────────────────────────────────────────────────────────────
//  MODAL FUNCTIONS
// ─────────────────────────────────────────────────────────────

function openBudgetModal() {
    const modal = document.getElementById('budgetModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeBudgetModal() {
    const modal = document.getElementById('budgetModal');
    if (modal) {
        modal.style.display = 'none';
    }
    // Clear form
    clearBudgetForm();
}

function clearBudgetForm() {
    document.getElementById('budgetName').value = '';
    document.getElementById('budgetCategory').value = '';
    document.getElementById('budgetAmount').value = '';
    document.getElementById('budgetDescription').value = '';
    document.getElementById('budgetError').style.display = 'none';
    document.getElementById('budgetError').textContent = '';
}

// ─────────────────────────────────────────────────────────────
//  CREATE BUDGET
// ─────────────────────────────────────────────────────────────

async function handleCreateBudget() {
    const name = document.getElementById('budgetName').value.trim();
    const category = document.getElementById('budgetCategory').value.trim();
    const amount = parseFloat(document.getElementById('budgetAmount').value);
    const description = document.getElementById('budgetDescription').value.trim();
    const errorEl = document.getElementById('budgetError');

    errorEl.style.display = 'none';
    errorEl.textContent = '';

    // Validation
    if (!name) {
        showBudgetError('Budget name is required');
        return;
    }
    if (!category) {
        showBudgetError('Category is required');
        return;
    }
    if (isNaN(amount) || amount <= 0) {
        showBudgetError('Amount must be a positive number');
        return;
    }

    try {
        const res = await fetch(`${API}/budgets/user/${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: name,
                category: category,
                totalAmount: amount,
                description: description
            })
        });

        if (!res.ok) {
            const error = await res.json();
            throw new Error(error.error || 'Failed to create budget');
        }

        // Success - close modal and reload
        closeBudgetModal();
        await loadBudgetData();
        showSuccess('Budget created successfully!');
    } catch (err) {
        console.error('Error creating budget:', err);
        showBudgetError(err.message);
    }
}

function showBudgetError(message) {
    const errorEl = document.getElementById('budgetError');
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
    }
}

function showError(message) {
    console.error(message);
    // Could add a toast notification here
}

function showSuccess(message) {
    console.log(message);
    // Could add a toast notification here
}

// ─────────────────────────────────────────────────────────────
//  MODAL BACKDROP CLICK
// ─────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('budgetModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeBudgetModal();
            }
        });
    }
});
