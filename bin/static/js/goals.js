// ── API Base URL ──────────────────────────────────────────────
const API = '/api';

let currentActiveDate = new Date();

function getUserID() {
    const uid = localStorage.getItem('userID') || localStorage.getItem('userId') || '1';
    console.log('goals.js getUserID ->', uid);
    return uid;
}

function formatCurrency(value) {
    const amount = Number(value || 0);
    return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function calculateProgress(goal) {
    if (typeof goal.progressPercent === 'number' && goal.progressPercent >= 0) {
        return Math.min(Math.max(goal.progressPercent, 0), 100);
    }

    const saved = Number(goal.savedAmount || 0);
    const target = Number(goal.goalAmount || 0);
    return target > 0 ? Math.min(Math.round((saved / target) * 100), 100) : 0;
}

function createGoalCard(goal) {
    const progressPercent = calculateProgress(goal);
    const savedAmount = formatCurrency(goal.savedAmount || 0);
    const goalAmount = formatCurrency(goal.goalAmount || 0);
    const title = goal.goalName || 'Untitled goal';

    return `
        <div class="card goal-card goal-interactive-card" onclick="navigateToGoalDetails(${goal.goalID})">
            <div class="goal-title-row">
                <span class="goal-emoji">🎯</span>
                <span class="goal-name">${escapeHtml(title)}</span>
            </div>
            <div class="goal-amounts-label">${savedAmount} / ${goalAmount}</div>
            <div class="progress-container">
                <div class="progress-bar-fill" style="width: ${progressPercent}%;"></div>
            </div>
            <div class="goal-card-footer-text">${progressPercent}% complete</div>
        </div>
    `;
}

function escapeHtml(text) {
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

async function fetchGoals() {
    const userID = getUserID();
    const response = await fetch(`${API}/goals?userID=${encodeURIComponent(userID)}`);
    if (!response.ok) {
        throw new Error(`Failed to load goals: ${response.status}`);
    }
    return response.json();
}

function updateSummary(goals) {
    const totalSaved = goals.reduce((sum, goal) => sum + Number(goal.savedAmount || 0), 0);
    const totalTarget = goals.reduce((sum, goal) => sum + Number(goal.goalAmount || 0), 0);
    const totalPercent = totalTarget > 0 ? Math.min(Math.round((totalSaved / totalTarget) * 100), 100) : 0;

    document.getElementById('displayTotalSaved').innerText = formatCurrency(totalSaved);
    document.getElementById('displayMonthlyTarget').innerText = formatCurrency(totalTarget);
    document.getElementById('displayMonthlyDeposited').innerText = `${formatCurrency(totalSaved)} saved across goals`;
    document.getElementById('monthlyGoalProgressBarFill').style.width = `${totalPercent}%`;
}

function renderGoals(goals) {
    const container = document.getElementById('goalsList');
    if (!container) return;

    if (!Array.isArray(goals) || goals.length === 0) {
        container.innerHTML = `
            <div class="card empty-state-card">
                <p class="empty-state-text">No goals found. Create your first savings goal to track progress.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = goals.map(createGoalCard).join('');
}

function setActiveMonthLabel() {
    const label = currentActiveDate.toLocaleString('default', { month: 'long', year: 'numeric' });
    document.getElementById('activeMonthLabel').innerText = label;
    document.getElementById('liveTransactionDateDisplay').innerText = `Date: ${label}`;
}

async function renderGoalsInterface() {
    try {
        const goals = await fetchGoals();
        renderGoals(goals);
        updateSummary(goals);
    } catch (error) {
        console.error('Could not load goals:', error);
        renderGoals([]);
    }
    setActiveMonthLabel();
}

function adjustSelectedMonth(directionStep) {
    currentActiveDate.setMonth(currentActiveDate.getMonth() + directionStep);
    setActiveMonthLabel();
}

function navigateToGoalDetails(goalId) {
    console.log('navigateToGoalDetails click ->', goalId);
    window.location.href = `goal_details.html?id=${encodeURIComponent(goalId)}`;
}

window.addEventListener('DOMContentLoaded', () => {
    console.log('goals.js DOMContentLoaded');
    renderGoalsInterface();
});
