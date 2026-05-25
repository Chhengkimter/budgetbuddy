const API_BASE = 'http://localhost:8080/api';

// ── Auth guard — redirect to login if no user in session ─────────────────────
const currentUser = JSON.parse(sessionStorage.getItem('currentUser') || localStorage.getItem('currentUser') || 'null');
if (!currentUser) window.location.href = 'index.html';
const USER_ID = currentUser?.userID;

// ── Global state ──────────────────────────────────────────────────────────────
let currentActiveDate = new Date();
let selectedBudgetID  = null;   // tracks which budget row is selected for edit/delete

const monthNamesList = [
    "January","February","March","April","May","June",
    "July","August","September","October","November","December"
];

// ── Date label ────────────────────────────────────────────────────────────────
function updateDateUILabel() {
    const label = `${monthNamesList[currentActiveDate.getMonth()]} ${currentActiveDate.getFullYear()}`;
    document.getElementById('globalMonthYearLabel').innerText = label;
    document.getElementById('activeSubHeadingLabel').innerText = label;
}

async function changeMonth(direction) {
    currentActiveDate.setMonth(currentActiveDate.getMonth() + direction);
    updateDateUILabel();
    await loadBudgetDataFromDB();
}

// ── Load summary ──────────────────────────────────────────────────────────────
async function loadBudgetDataFromDB() {
    const month = currentActiveDate.getMonth() + 1;
    const year  = currentActiveDate.getFullYear();

    try {
        const res = await fetch(`${API_BASE}/budgets/summary?userID=${USER_ID}&month=${month}&year=${year}`);
        if (!res.ok) throw new Error();
        const data = await res.json();
        renderDynamicBudgetDashboard(data);
    } catch (err) {
        console.error('Failed to load budget summary:', err);
    }
}

// ── Render dashboard ──────────────────────────────────────────────────────────
function renderDynamicBudgetDashboard(data) {
    // Header totals
    document.getElementById('totalBudgetSumHeader').innerText =
        `$${data.totalBudget.toLocaleString('en-US', { minimumFractionDigits: 2 })}`;
    document.getElementById('totalSpendSumHeader').innerText =
        `$${data.totalSpend.toLocaleString('en-US', { minimumFractionDigits: 2 })}`;

    const pctUsed = data.totalBudget > 0
        ? Math.round((data.totalSpend / data.totalBudget) * 100) : 0;
    document.getElementById('totalSpendPctBadge').innerText = `${pctUsed}% used`;

    // Budget rows
    const listContainer = document.getElementById('dynamicBudgetItemsContainer');
    listContainer.innerHTML = '';

    data.budgetsList.forEach(item => {
        const pct = item.limit > 0
            ? Math.min(Math.round((item.spent / item.limit) * 100), 100) : 0;

        const row = document.createElement('div');
        row.className = 'budget-item-wrap';
        row.style.cursor = 'pointer';
        row.dataset.budgetId = item.budgetID;
        row.innerHTML = `
        <div class="budget-meta-info">
            <span>${item.categoryName}</span>
            <div style="display:flex;align-items:center;gap:10px;">
                <span class="bold-amt">$${Number(item.spent).toFixed(2)} / $${Number(item.limit).toFixed(2)}</span>
                <button onclick="event.stopPropagation(); openEditModal(${JSON.stringify(item).replace(/"/g,'&quot;')})"
                    style="background:none;border:none;cursor:pointer;color:#6B7280;padding:0;" title="Edit">
                    ✏️
                </button>
                <button onclick="event.stopPropagation(); deleteBudgetPlanFromDB(${item.budgetID})"
                    style="background:none;border:none;cursor:pointer;color:#EF4444;padding:0;" title="Delete">
                    🗑️
                </button>
            </div>
        </div>
        <div class="progress-container">
            <div class="progress-bar-fill" style="width:${pct}%; background:${item.colorVar || 'var(--primary)'};"></div>
        </div>`;

        listContainer.appendChild(row);
    });

    // Savings goal progress
    const goalWrapper = document.getElementById('overallSavingsProgressWrapper');
    const goalPct = data.savingsGoal.target > 0
        ? Math.min(Math.round((data.savingsGoal.spent / data.savingsGoal.target) * 100), 100) : 0;

    goalWrapper.innerHTML = `
        <div class="budget-meta-info">
            <span>Overall progress</span>
            <span class="bold-amt">$${Number(data.savingsGoal.spent).toFixed(2)} / $${Number(data.savingsGoal.target).toFixed(2)} target</span>
        </div>
        <div class="progress-container">
            <div class="progress-bar-fill" style="width:${goalPct}%; background:var(--primary);"></div>
        </div>`;
}

// ── Edit modal ────────────────────────────────────────────────────────────────
function openEditModal(item) {
    selectedBudgetID = item.budgetID;
    document.getElementById('budgetCategory').value = item.categoryName;
    document.getElementById('monthlyLimit').value   = Number(item.limit).toFixed(2);
    document.getElementById('editBudgetOverlay').classList.add('open');
}
function closeEditModal() {
    document.getElementById('editBudgetOverlay').classList.remove('open');
}
function handleEditOverlayClick(e) {
    if (e.target.id === 'editBudgetOverlay') closeEditModal();
}

// ── Create budget ─────────────────────────────────────────────────────────────
async function createNewBudgetPlanInDB() {
    const name  = document.getElementById('newBudgetCategorySelect').value.trim();
    const limit = parseFloat(document.getElementById('newBudgetAmountInput').value);

    if (!name || isNaN(limit) || limit <= 0) {
        alert('Please fill in all fields correctly.');
        return;
    }

    const payload = {
        budgetName:       name,
        budgetLimit:      limit,
        budgetMonth:      currentActiveDate.getMonth() + 1,
        budgetYear:       currentActiveDate.getFullYear(),
        budgetIsRecurring: false
    };

    try {
        const res = await fetch(`${API_BASE}/budgets?userID=${USER_ID}`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });
        if (!res.ok) throw new Error(await res.text());
        closeBudgetModal();
        await loadBudgetDataFromDB();
    } catch (err) {
        alert('Failed to create budget: ' + err.message);
    }
}

// ── Update budget limit ───────────────────────────────────────────────────────
async function updateBudgetPlanInDB() {
    if (!selectedBudgetID) {
        alert('Please click a budget row first to select it.');
        return;
    }

    const payload = {
        budgetName:  document.getElementById('budgetCategory').value.trim(),
        budgetLimit: parseFloat(document.getElementById('monthlyLimit').value),
        budgetMonth: currentActiveDate.getMonth() + 1,
        budgetYear:  currentActiveDate.getFullYear()
    };

    try {
        const res = await fetch(`${API_BASE}/budgets/${selectedBudgetID}`, {
            method:  'PUT',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });
        if (!res.ok) throw new Error(await res.text());
        closeEditModal();
        await loadBudgetDataFromDB();
    } catch (err) {
        alert('Failed to update budget: ' + err.message);
    }
}

async function deleteBudgetPlanFromDB(budgetID) {
    if (!confirm('Delete this budget? This cannot be undone.')) return;
    try {
        const res = await fetch(`${API_BASE}/budgets/${budgetID}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());
        await loadBudgetDataFromDB();
    } catch (err) {
        alert('Failed to delete budget: ' + err.message);
    }
}

// ── Modal controls ────────────────────────────────────────────────────────────
function openBudgetModal()  { document.getElementById('budgetOverlay').classList.add('open'); }
function closeBudgetModal() { document.getElementById('budgetOverlay').classList.remove('open'); }
function handleBudgetOverlayClick(e) { if (e.target.id === 'budgetOverlay') closeBudgetModal(); }

// ── Init ──────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    updateDateUILabel();
    await loadBudgetDataFromDB();
});