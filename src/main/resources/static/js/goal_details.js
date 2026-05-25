const API = '/api';

function getUserID() {
    const uid = localStorage.getItem('userID') || localStorage.getItem('userId') || '1';
    console.log('goal_details.js getUserID ->', uid);
    return uid;
}

function getGoalIdFromQuery() {
    return new URLSearchParams(window.location.search).get('id');
}

function formatCurrency(value) {
    const amount = Number(value || 0);
    return `$${amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function formatDateDisplay(value) {
    if (!value) return '—';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return value;
    }
    return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

function calculateProgress(goal) {
    if (typeof goal.progressPercent === 'number') {
        return Math.min(Math.max(goal.progressPercent, 0), 100);
    }

    const saved = Number(goal.savedAmount || 0);
    const target = Number(goal.goalAmount || 0);
    return target > 0 ? Math.min(Math.round((saved / target) * 100), 100) : 0;
}

function getGoalStatus(goal) {
    const progress = calculateProgress(goal);
    if (goal.goalFinishedDate || progress >= 100) {
        const finishedDate = formatDateDisplay(goal.goalFinishedDate);
        return finishedDate && finishedDate !== '—'
            ? `Completed on ${finishedDate}`
            : 'Completed';
    }
    return 'In progress';
}

function updateGoalView(goal) {
    const progress = calculateProgress(goal);
    const savedAmount = Number(goal.savedAmount || 0);
    const targetAmount = Number(goal.goalAmount || 0);
    const remaining = Math.max(targetAmount - savedAmount, 0);

    document.getElementById('displayBannerTitle').innerText = goal.goalName || 'Goal details';
    document.getElementById('displayBannerEmoji').innerText = '🎯';
    document.getElementById('displayBannerProgressPct').innerText = `${progress}% complete`;
    document.getElementById('displayBannerProgressFill').style.width = `${progress}%`;
    document.getElementById('displayGoalStatus').innerText = getGoalStatus(goal);

    document.getElementById('valStatLeft').innerText = formatCurrency(savedAmount);
    document.getElementById('valStatMiddle').innerText = formatCurrency(remaining);
    document.getElementById('valStatRight').innerText = formatDateDisplay(goal.goalTargetDate);

    document.getElementById('goalName').value = goal.goalName || '';
    document.getElementById('targetAmount').value = goal.goalAmount || '';
    document.getElementById('monthlyTarget').value = '';
    document.getElementById('targetDate').value = goal.goalTargetDate || '';

    const completeButton = document.getElementById('btnCompleteGoal');
    if (goal.goalFinishedDate || progress >= 100) {
        completeButton.disabled = true;
        completeButton.innerText = 'Already complete';
    } else {
        completeButton.disabled = false;
        completeButton.innerText = 'Mark complete';
    }
}

async function fetchGoal(goalId) {
    const userID = getUserID();
    const res = await fetch(`${API}/goals/${encodeURIComponent(goalId)}?userID=${encodeURIComponent(userID)}`);
    if (!res.ok) {
        throw new Error(`Failed to load goal (${res.status})`);
    }
    return res.json();
}

async function completeGoal(goalId) {
    const userID = getUserID();
    const url = `${API}/goals/${encodeURIComponent(goalId)}/complete?userID=${encodeURIComponent(userID)}`;
    console.log('completeGoal -> PATCH', url);
    const res = await fetch(url, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' }
    });
    if (!res.ok) {
        throw new Error(`Could not complete goal (${res.status})`);
    }
    return res.json();
}

async function deleteGoal(goalId) {
    const userID = getUserID();
    const url = `${API}/goals/${encodeURIComponent(goalId)}?userID=${encodeURIComponent(userID)}`;
    console.log('deleteGoal -> DELETE', url);
    const res = await fetch(url, {
        method: 'DELETE'
    });
    if (!res.ok) {
        throw new Error(`Could not delete goal (${res.status})`);
    }
}

async function updateGoal(goalId) {
    const userID = getUserID();
    const goalName = document.getElementById('goalName').value.trim();
    const goalAmount = Number(document.getElementById('targetAmount').value);
    const goalTargetDate = document.getElementById('targetDate').value;

    if (!goalName) {
        throw new Error('Goal name is required.');
    }
    if (!goalAmount || goalAmount <= 0) {
        throw new Error('Goal amount must be a valid number.');
    }
    if (!goalTargetDate) {
        throw new Error('Target date is required.');
    }

    const payload = {
        goalName,
        goalAmount,
        goalTargetDate
    };
    const url = `${API}/goals/${encodeURIComponent(goalId)}?userID=${encodeURIComponent(userID)}`;
    console.log('updateGoal -> PUT', url, payload);
    const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (!res.ok) {
        throw new Error(`Could not save goal (${res.status})`);
    }
    return res.json();
}

function showMessage(message) {
    window.alert(message);
}

function bindGoalActions(goalId) {
    const saveBtn = document.getElementById('btnMainSettingsSubmit');
    if (saveBtn) saveBtn.addEventListener('click', async () => {
        console.log('btnMainSettingsSubmit clicked ->', goalId);
        try {
            await updateGoal(goalId);
            showMessage('Goal updated successfully.');
            const refreshed = await fetchGoal(goalId);
            updateGoalView(refreshed);
        } catch (error) {
            showMessage(error.message || 'Unable to save goal.');
            console.error(error);
        }
    });
    const delBtn = document.getElementById('btnDangerSettingsAction');
    if (delBtn) delBtn.addEventListener('click', async () => {
        console.log('btnDangerSettingsAction clicked ->', goalId);
        if (!window.confirm('Delete this goal? This cannot be undone.')) return;
        try {
            await deleteGoal(goalId);
            window.location.href = 'goals.html';
        } catch (error) {
            showMessage(error.message || 'Unable to delete goal.');
            console.error(error);
        }
    });

    const completeBtn = document.getElementById('btnCompleteGoal');
    if (completeBtn) completeBtn.addEventListener('click', async () => {
        console.log('btnCompleteGoal clicked ->', goalId);
        try {
            await completeGoal(goalId);
            showMessage('Goal marked complete.');
            const refreshed = await fetchGoal(goalId);
            updateGoalView(refreshed);
        } catch (error) {
            showMessage(error.message || 'Unable to complete goal.');
            console.error(error);
        }
    });

    // Deposit button (creates a quick contribution)
    const depositBtn = document.getElementById('btnDeposit');
    if (depositBtn) depositBtn.addEventListener('click', async (e) => {
        console.log('btnDeposit clicked ->', goalId);
        try {
            const amountEl = document.getElementById('depAmount');
            const dateEl = document.getElementById('depDate');
            const noteEl = document.getElementById('depNote');
            const amount = amountEl ? amountEl.value : null;
            const notes = noteEl ? noteEl.value : '';

            if (!amount || Number(amount) <= 0) {
                showMessage('Please enter a valid deposit amount.');
                return;
            }

            // Use quick contribution endpoint
            const url = `${API}/contributions/goal/${encodeURIComponent(goalId)}/quick`;
            console.log('deposit -> POST', url, { amount: String(amount), notes });
            const res = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ amount: String(amount), notes })
            });

            if (!res.ok) {
                const body = await res.json().catch(() => ({}));
                throw new Error(body.error || `Deposit failed (${res.status})`);
            }

            showMessage('Deposit saved.');
            // Refresh goal view after deposit
            const refreshed = await fetchGoal(goalId);
            updateGoalView(refreshed);
            // Optionally refresh history list
            try {
                const historyRes = await fetch(`${API}/contributions/goal/${encodeURIComponent(goalId)}`);
                if (historyRes.ok) {
                    const rows = await historyRes.json();
                    const container = document.getElementById('historyLedgerContainer');
                    if (container && Array.isArray(rows)) {
                        container.innerHTML = rows.map(r => `
                            <div class="ledger-row-item">
                                <div class="item-left-cell"><p class="main-title">Deposit</p><p class="sub-date">${formatDateDisplay(r.contributedAt || r.createdAt)}</p></div>
                                <div class="item-right-cell"><span class="green-tx">+$${Number(r.amount || r.contributionAmount || 0).toFixed(2)}</span></div>
                            </div>
                        `).join('');
                    }
                }
            } catch (err) {
                // non-fatal
                console.warn('Could not refresh history:', err);
            }

        } catch (error) {
            showMessage(error.message || 'Unable to save deposit.');
            console.error(error);
        }
    });
}

async function initGoalDetails() {
    const goalId = getGoalIdFromQuery();
    if (!goalId) {
        window.location.href = 'goals.html';
        return;
    }

    if (goalId === 'new') {
        document.getElementById('displayBannerTitle').innerText = 'Create New Saving Goal';
        document.getElementById('displayBannerEmoji').innerText = '✨';
        document.getElementById('displayBannerProgressPct').innerText = 'New goal';
        document.getElementById('displayGoalStatus').innerText = 'Draft';
        document.getElementById('bannerProgressWrapper').style.display = 'none';
        return;
    }

    bindGoalActions(goalId);

    try {
        const goal = await fetchGoal(goalId);
        updateGoalView(goal);
    } catch (error) {
        showMessage(error.message || 'Unable to load goal details.');
        console.error(error);
    }
}

window.addEventListener('DOMContentLoaded', initGoalDetails);
