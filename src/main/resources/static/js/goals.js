// Goals Management Script
// Handles all API interactions for goals CRUD, deposits, and UI rendering

let userID = null;
let allGoals = [];
let currentDepositGoalID = null;

// ==========================================
// Initialization
// ==========================================
document.addEventListener('DOMContentLoaded', async () => {
    // Retrieve userID from localStorage
    userID = localStorage.getItem('userID');
    if (!userID) {
        alert('User ID not found. Please log in first.');
        window.location.href = 'index.html';
        return;
    }

    // Initialize Flatpickr for date inputs
    flatpickr('#targetDate', {
        dateFormat: 'Y-m-d',
        minDate: 'today'
    });

    flatpickr('#depositDate', {
        dateFormat: 'Y-m-d',
        defaultDate: 'today'
    });

    // Setup event listeners
    setupEventListeners();

    // Load goals data
    await loadGoals();
});

// ==========================================
// Event Listeners
// ==========================================
function setupEventListeners() {
    // New Goal Modal
    document.getElementById('newGoalBtn').addEventListener('click', () => {
        openModal('newGoalModal');
        document.getElementById('goalForm').reset();
        document.getElementById('formMessage').innerHTML = '';
    });

    document.getElementById('closeModal').addEventListener('click', () => closeModal('newGoalModal'));
    document.getElementById('cancelBtn').addEventListener('click', () => closeModal('newGoalModal'));
    document.getElementById('emptyStateBtn').addEventListener('click', () => openModal('newGoalModal'));

    // Goal Form Submission
    document.getElementById('goalForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await createGoal();
    });

    // Deposit Modal
    document.getElementById('closeDepositModal').addEventListener('click', () => closeModal('depositModal'));
    document.getElementById('cancelDepositBtn').addEventListener('click', () => closeModal('depositModal'));

    // Deposit Form Submission
    document.getElementById('depositForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await addDeposit();
    });

    // Details Modal
    document.getElementById('closeDetailsModal').addEventListener('click', () => closeModal('detailsModal'));

    // Close modals when clicking outside
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        });
    });
}

// ==========================================
// Load Goals from API
// ==========================================
async function loadGoals() {
    try {
        const response = await fetch(`/api/goals?userID=${userID}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        allGoals = await response.json();
        renderGoals();
        updateSummaryCards();
    } catch (error) {
        console.error('Error loading goals:', error);
        showError('Failed to load goals. Please try again.');
    }
}

// ==========================================
// Render Goals Grid
// ==========================================
function renderGoals() {
    const goalsGrid = document.getElementById('goalsGrid');
    const emptyState = document.getElementById('emptyState');

    if (!allGoals || allGoals.length === 0) {
        goalsGrid.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }

    emptyState.style.display = 'none';
    goalsGrid.innerHTML = allGoals.map(goal => createGoalCard(goal)).join('');
}

// ==========================================
// Create Goal Card HTML
// ==========================================
function createGoalCard(goal) {
    const progress = goal.goalAmount > 0 ? (goal.currentSaved / goal.goalAmount) * 100 : 0;
    const isCompleted = goal.isCompleted;
    const targetDateObj = new Date(goal.goalTargetDate);
    const targetDateStr = targetDateObj.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

    return `
        <div class="goal-card">
            <div class="goal-card-header">
                <h3 class="goal-name">${escapeHtml(goal.goalName)}</h3>
                ${isCompleted ? '<span class="completed-badge">✓ Completed</span>' : ''}
            </div>
            
            <div class="goal-amounts">
                <span class="goal-amounts-value">$${formatCurrency(goal.currentSaved)}</span> / $${formatCurrency(goal.goalAmount)}
            </div>
            
            <div class="progress-bar">
                <div class="progress-fill" style="width: ${Math.min(progress, 100)}%"></div>
            </div>
            
            <div class="goal-details">
                Target: ${targetDateStr} • Progress: ${Math.round(progress)}%
            </div>
            
            ${isCompleted ? `
                <div style="padding: 12px; background: #D1FAE5; border-radius: 8px; text-align: center; color: #047857; font-weight: 600; font-size: 14px;">
                    🎉 Goal Completed
                </div>
            ` : `
                <div class="goal-actions">
                    <a href="goal_details.html?goalID=${goal.goalID}&action=deposit" class="btn-small btn-deposit" style="text-decoration: none; display: flex; align-items: center; justify-content: center;">💰 Add Deposit</a>
                    <a href="goal_details.html?goalID=${goal.goalID}" class="btn-small btn-view" style="text-decoration: none; display: flex; align-items: center; justify-content: center;">📋 Details</a>
                </div>
            `}
        </div>
    `;
}

// ==========================================
// Update Summary Cards
// ==========================================
function updateSummaryCards() {
    const totalGoals = allGoals.length;
    let totalSaved = 0;
    let totalAmount = 0;

    allGoals.forEach(goal => {
        totalSaved += parseFloat(goal.currentSaved) || 0;
        totalAmount += parseFloat(goal.goalAmount) || 0;
    });

    const totalRemaining = totalAmount - totalSaved;

    document.getElementById('totalGoals').textContent = totalGoals;
    document.getElementById('totalSaved').textContent = `$${formatCurrency(totalSaved)}`;
    document.getElementById('totalRemaining').textContent = `$${formatCurrency(Math.max(totalRemaining, 0))}`;
}

// ==========================================
// Create Goal (POST)
// ==========================================
async function createGoal() {
    const goalName = document.getElementById('goalName').value.trim();
    const targetAmount = parseFloat(document.getElementById('targetAmount').value);
    const targetDate = document.getElementById('targetDate').value;
    const monthlyTarget = parseFloat(document.getElementById('monthlyTarget').value) || null;
    const autoMonthly = document.getElementById('autoMonthly').checked;

    if (!goalName || !targetAmount || !targetDate) {
        showFormMessage('formMessage', 'Please fill in all required fields.', 'error');
        return;
    }

    const requestBody = {
        goalName,
        goalAmount: targetAmount,
        goalTargetDate: targetDate,
        monthlyTarget: monthlyTarget,
        autoMonthly
    };

    try {
        const response = await fetch(`/api/goals?userID=${userID}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to create goal');
        }

        const newGoal = await response.json();
        allGoals.push(newGoal);
        renderGoals();
        updateSummaryCards();
        closeModal('newGoalModal');
        showFormMessage('formMessage', 'Goal created successfully! ✓', 'success');
        setTimeout(() => loadGoals(), 1500);
    } catch (error) {
        console.error('Error creating goal:', error);
        showFormMessage('formMessage', `Error: ${error.message}`, 'error');
    }
}

// ==========================================
// Add Deposit (POST to deposit endpoint)
// ==========================================
async function addDeposit() {
    const depositAmount = parseFloat(document.getElementById('depositAmount').value);
    const depositDate = document.getElementById('depositDate').value || new Date().toISOString().split('T')[0];

    if (!depositAmount || depositAmount <= 0) {
        showFormMessage('depositFormMessage', 'Please enter a valid deposit amount.', 'error');
        return;
    }

    if (!currentDepositGoalID) {
        showFormMessage('depositFormMessage', 'Goal ID not found.', 'error');
        return;
    }

    const requestBody = {
        amount: depositAmount,
        depositDate
    };

    try {
        const response = await fetch(`/api/goals/${currentDepositGoalID}/deposit?userID=${userID}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to add deposit');
        }

        const updatedGoal = await response.json();
        
        // Update goal in array
        const goalIndex = allGoals.findIndex(g => g.goalID === currentDepositGoalID);
        if (goalIndex !== -1) {
            allGoals[goalIndex] = updatedGoal;
        }

        renderGoals();
        updateSummaryCards();
        closeModal('depositModal');
        showFormMessage('depositFormMessage', `Deposit of $${formatCurrency(depositAmount)} added successfully! ✓`, 'success');
        setTimeout(() => {
            document.getElementById('depositForm').reset();
            loadGoals();
        }, 1500);
    } catch (error) {
        console.error('Error adding deposit:', error);
        showFormMessage('depositFormMessage', `Error: ${error.message}`, 'error');
    }
}

// ==========================================
// Modal Management
// ==========================================
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// ==========================================
// Deposit Modal
// ==========================================
function openDepositModal(goalID) {
    currentDepositGoalID = goalID;
    const goal = allGoals.find(g => g.goalID === goalID);
    
    if (goal) {
        // Set a helpful label
        const modal = document.getElementById('depositModal');
        const header = modal.querySelector('.modal-header h2');
        header.textContent = `Add Deposit - ${goal.goalName}`;
        
        // Reset form
        document.getElementById('depositForm').reset();
        document.getElementById('depositFormMessage').innerHTML = '';
        
        // Set today's date as default
        flatpickr('#depositDate', {
            dateFormat: 'Y-m-d',
            defaultDate: 'today'
        });
        
        openModal('depositModal');
    }
}

// ==========================================
// Details Modal
// ==========================================
function openDetailsModal(goal) {
    const modal = document.getElementById('detailsModal');
    document.getElementById('detailsTitle').textContent = goal.goalName;
    
    const progress = goal.goalAmount > 0 ? (goal.currentSaved / goal.goalAmount) * 100 : 0;
    const remaining = goal.goalAmount - goal.currentSaved;
    const targetDateObj = new Date(goal.goalTargetDate);
    const createdDateObj = new Date(goal.goalCreatedDate);
    
    const detailsHtml = `
        <div class="details-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 20px;">
            <div style="padding: 16px; background: #F3F4F6; border-radius: 8px;">
                <div style="font-size: 12px; color: #6B7280; font-weight: 600; margin-bottom: 4px;">Target Amount</div>
                <div style="font-size: 24px; font-weight: 700; color: #1F2937;">$${formatCurrency(goal.goalAmount)}</div>
            </div>
            <div style="padding: 16px; background: #F3F4F6; border-radius: 8px;">
                <div style="font-size: 12px; color: #6B7280; font-weight: 600; margin-bottom: 4px;">Amount Saved</div>
                <div style="font-size: 24px; font-weight: 700; color: #10B981;">$${formatCurrency(goal.currentSaved)}</div>
            </div>
            <div style="padding: 16px; background: #F3F4F6; border-radius: 8px;">
                <div style="font-size: 12px; color: #6B7280; font-weight: 600; margin-bottom: 4px;">Remaining</div>
                <div style="font-size: 24px; font-weight: 700; color: #EF4444;">$${formatCurrency(Math.max(remaining, 0))}</div>
            </div>
            <div style="padding: 16px; background: #F3F4F6; border-radius: 8px;">
                <div style="font-size: 12px; color: #6B7280; font-weight: 600; margin-bottom: 4px;">Progress</div>
                <div style="font-size: 24px; font-weight: 700; color: #3B82F6;">${Math.round(progress)}%</div>
            </div>
        </div>

        <div style="margin-bottom: 20px;">
            <div style="font-size: 12px; color: #6B7280; font-weight: 600; margin-bottom: 8px;">Progress Bar</div>
            <div class="progress-bar" style="height: 12px;">
                <div class="progress-fill" style="width: ${Math.min(progress, 100)}%; height: 12px;"></div>
            </div>
        </div>

        <div class="details-info" style="font-size: 14px; color: #4B5563; line-height: 1.8;">
            <div style="margin-bottom: 12px;">
                <strong style="color: #1F2937;">Target Date:</strong> ${targetDateObj.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
            </div>
            <div style="margin-bottom: 12px;">
                <strong style="color: #1F2937;">Created:</strong> ${createdDateObj.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}
            </div>
            ${goal.monthlyTarget ? `
                <div style="margin-bottom: 12px;">
                    <strong style="color: #1F2937;">Monthly Target:</strong> $${formatCurrency(goal.monthlyTarget)}
                </div>
            ` : ''}
            <div style="margin-bottom: 12px;">
                <strong style="color: #1F2937;">Auto-deposit Reminder:</strong> ${goal.autoMonthly ? '✓ Enabled' : '✗ Disabled'}
            </div>
            <div>
                <strong style="color: #1F2937;">Status:</strong> ${goal.isCompleted ? '<span style="color: #10B981;">✓ Completed</span>' : '<span style="color: #6B7280;">In Progress</span>'}
            </div>
        </div>

        <div style="border-top: 1px solid #E5E7EB; margin-top: 20px; padding-top: 16px; display: flex; gap: 12px;">
            <button class="btn-small btn-deposit" style="flex: 1;" onclick="openDepositFromDetails(${goal.goalID})">💰 Add Deposit</button>
            ${!goal.isCompleted ? `
                <button class="btn-small" style="flex: 1; background: #10B981; color: white;" onclick="completeGoal(${goal.goalID})">✓ Mark Complete</button>
            ` : ''}
        </div>
    `;
    
    document.getElementById('detailsContent').innerHTML = detailsHtml;
    openModal('detailsModal');
}

function openDepositFromDetails(goalID) {
    closeModal('detailsModal');
    openDepositModal(goalID);
}

// ==========================================
// Complete Goal (PATCH)
// ==========================================
async function completeGoal(goalID) {
    if (!confirm('Are you sure you want to mark this goal as completed?')) {
        return;
    }

    try {
        const response = await fetch(`/api/goals/${goalID}/complete?userID=${userID}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to complete goal');
        }

        const result = await response.json();
        console.log('Goal completion result:', result);

        // Show completion message with transferred amount
        if (result.transferredAmount && result.transferredAmount > 0) {
            alert(`🎉 Goal Completed!\n\nA SAVING transaction of $${formatCurrency(result.transferredAmount)} was automatically created for the remaining balance.\n\n${result.message}`);
        } else {
            alert(`🎉 Goal Completed!\n\n${result.message}`);
        }

        closeModal('detailsModal');
        await loadGoals();
    } catch (error) {
        console.error('Error completing goal:', error);
        alert(`Error: ${error.message}`);
    }
}

// ==========================================
// Utility Functions
// ==========================================
function formatCurrency(value) {
    if (!value) return '0.00';
    return parseFloat(value).toFixed(2);
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

function showFormMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `<div class="${type}-message">${message}</div>`;
        if (type === 'success') {
            setTimeout(() => {
                element.innerHTML = '';
            }, 3000);
        }
    }
}

function showError(message) {
    const messageDiv = document.createElement('div');
    messageDiv.className = 'error-message';
    messageDiv.textContent = message;
    document.body.insertBefore(messageDiv, document.body.firstChild);
    setTimeout(() => messageDiv.remove(), 5000);
}
