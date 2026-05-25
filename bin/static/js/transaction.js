// ─────────────────────────────────────────────
// State
// ─────────────────────────────────────────────
let currentFilter     = 'ALL';
let currentActiveDate = new Date();
let transactionsData  = [];

const monthNamesList = [
    "January","February","March","April","May","June",
    "July","August","September","October","November","December"
];

// Budget + Goal options — fetched from /api/budgets and /api/goals in production
const dbOptionsSource = {
    BUDGETS: [
        { value: "1", text: "Food & Groceries" },
        { value: "2", text: "Transportation" },
        { value: "3", text: "Entertainment" },
        { value: "4", text: "Utilities & Bills" }
    ],
    SAVING_BUDGETS: [
        { value: "5", text: "Monthly Saving Goal" }
    ],
    GOALS: [
        { value: "1", text: "Retirement Fund" },
        { value: "2", text: "New Dress" },
        { value: "3", text: "Trip to China" }
    ]
};

// ─────────────────────────────────────────────
// Nav label
// ─────────────────────────────────────────────
function updateNavLabel() {
    document.getElementById('txCalendarNavLabel').innerText =
        `${monthNamesList[currentActiveDate.getMonth()]} ${currentActiveDate.getFullYear()}`;
}

async function changeMonth(direction) {
    currentActiveDate.setMonth(currentActiveDate.getMonth() + direction);
    updateNavLabel();
    await fetchTransactionsFromDB();
}

// ─────────────────────────────────────────────
// Data fetch
// ─────────────────────────────────────────────
async function fetchTransactionsFromDB() {
    const qMonth = currentActiveDate.getMonth() + 1;
    const qYear  = currentActiveDate.getFullYear();
    const userID = getUserID();

    try {
        const res = await fetch(
            `/api/transactions?userID=${userID}&month=${qMonth}&year=${qYear}`
        );
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        transactionsData = await res.json();
    } catch (e) {
        console.warn("API unavailable, using mock data:", e.message);
        transactionsData = getMockData();
    }

    renderTransactions();
}

// ─────────────────────────────────────────────
// Filter pill handler
// ─────────────────────────────────────────────
function filterTransactions(type, clickedBtn) {
    currentFilter = type;
    document.querySelectorAll('.filter-pill').forEach(p => p.classList.remove('active'));
    if (clickedBtn) clickedBtn.classList.add('active');
    renderTransactions();
}

// ─────────────────────────────────────────────
// Client-side filter
// ─────────────────────────────────────────────
function applyClientFilter(data) {
    if (currentFilter === 'ALL')       return data;
    if (currentFilter === 'RECURRING') return data.filter(tx => !!tx.recurringID);
    return data.filter(tx => tx.transactionType === currentFilter);
}

// ─────────────────────────────────────────────
// Render
// ─────────────────────────────────────────────
function renderTransactions() {
    const tbody = document.getElementById('transactionTableBody');
    tbody.innerHTML = '';

    const filtered = applyClientFilter(transactionsData);

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="empty-table-msg">No transaction records found.</td></tr>`;
        return;
    }

    filtered.forEach(tx => {
        const tr          = document.createElement('tr');
        const isRecurring = !!tx.recurringID;

        let amountClass, sign;
        sign = tx.transactionType === 'INCOME' ? '+' : '-';    
        if (isRecurring) {
            amountClass = 'tx-amount-recurring';               // always purple if recurring
        } // NEW
        if (isRecurring) {
            amountClass = 'tx-amount-recurring';               // ALL recurring amounts are purple
        } else if (tx.transactionType === 'SAVING') {
            amountClass = 'tx-amount-saving';                  // non-recurring saving = blue
        } else {
            amountClass = `tx-amount-${tx.transactionType.toLowerCase()}`;
        }
       // NEW
        const typeTagHtml = `<span class="type-tag tag-${tx.transactionType.toLowerCase()}">${tx.transactionType}</span>`;

        tr.innerHTML = `
            <td>${tx.transactionDate ?? '—'}</td>
            <td><strong>${tx.transactionName ?? '—'}</strong></td>
            <td><span class="${amountClass}">${sign}$${Number(tx.transactionAmount ?? 0).toFixed(2)}</span></td>
            <td>${typeTagHtml}</td>
            <td><span class="column-meta-text">${tx.budgetName ?? '—'}</span></td>
            <td><span class="column-meta-text">${tx.goalName   ?? '—'}</span></td>
            <td class="column-note-text">${tx.transactionNote ?? '—'}</td>
        `;
        tbody.appendChild(tr);
    });
}

// ─────────────────────────────────────────────
// Modal helpers
// ─────────────────────────────────────────────
function openTxModal()  { document.getElementById('txOverlay').classList.add('open'); }
function closeTxModal() { document.getElementById('txOverlay').classList.remove('open'); }
function handleTxOverlayClick(e) { if (e.target.id === 'txOverlay') closeTxModal(); }

function toggleRecurringSetup(checked) {
    document.getElementById('recurringSetupBox').classList.toggle('visible', checked);
}

function toggleMonthlySavingSetup(checked) {
    document.getElementById('monthlySavingSetupBox').classList.toggle('visible', checked);
}

// ─────────────────────────────────────────────
// Modal type tabs
// ─────────────────────────────────────────────
function setModalTxType(type) {
    document.querySelectorAll('.type-tab').forEach(b => b.classList.remove('active'));
    document.getElementById(`modalTab${type.charAt(0) + type.slice(1).toLowerCase()}`).classList.add('active');
    document.getElementById('modalTxTypeTracker').value = type;

    const fieldsWrapper     = document.getElementById('modalDynamicFieldsWrapper');
    const autoSaveWrapper   = document.getElementById('modalAutoSaveFeatureWrapper');
    const recurringCheckbox = document.getElementById('txAutoSaveRecurring');
    fieldsWrapper.innerHTML = '';

    if (recurringCheckbox) {
        recurringCheckbox.checked = false;
        toggleRecurringSetup(false);
    }

    if (type === 'EXPENSE') {
        autoSaveWrapper.style.display = 'block';
        fieldsWrapper.innerHTML = `
            <div class="form-group">
                <label>Link to Budget</label>
                <select id="txBudgetSelect" required></select>
            </div>`;
        populateSelect('txBudgetSelect', dbOptionsSource.BUDGETS);
    }
    else if (type === 'INCOME') {
        autoSaveWrapper.style.display = 'block';
    }
    else if (type === 'SAVING') {
        autoSaveWrapper.style.display = 'none';
        fieldsWrapper.innerHTML = `
            <div class="form-row-2">
                <div class="form-group">
                    <label>Saving Budget</label>
                    <select id="txSavingBudgetSelect" required></select>
                </div>
                <div class="form-group">
                    <label>Goal</label>
                    <select id="txGoalSelect"></select>
                </div>
            </div>
            <div style="margin-top:12px;">
                <label class="modal-checkbox-container">
                    <input type="checkbox" id="txMonthlySavingGoalToggle"
                           onchange="toggleMonthlySavingSetup(this.checked)">
                    <span class="modal-custom-checkmark"></span>
                    Make this a <strong>Monthly Saving Goal</strong> — repeat every month
                </label>
                <div class="recurring-setup-box" id="monthlySavingSetupBox">
                    <div class="form-row-2">
                        <div class="form-group">
                            <label>Deposit on day</label>
                            <input type="number" id="savingRecurringDayInput" min="1" max="28" placeholder="e.g. 1" value="1"/>
                        </div>
                        <div class="form-group">
                            <label>End date <span style="color:#9CA3AF;font-weight:400">(optional)</span></label>
                            <input type="date" id="savingRecurringEndDateInput"/>
                        </div>
                    </div>
                    <p style="font-size:12px;color:#6B7280;margin-top:6px;">
                        A recurring template will auto-deposit this amount toward your goal every month.
                        It will appear in the <strong>Recurring</strong> filter — not counted in your balance.
                    </p>
                </div>
            </div>`;
        populateSelect('txSavingBudgetSelect', dbOptionsSource.SAVING_BUDGETS);
        populateSelectWithEmpty('txGoalSelect', dbOptionsSource.GOALS, '— No specific goal —');
    }
}

// ─────────────────────────────────────────────
// Select helpers
// ─────────────────────────────────────────────
function populateSelect(id, options) {
    const sel = document.getElementById(id);
    options.forEach(opt => {
        const el = document.createElement('option');
        el.value = opt.value;
        el.text  = opt.text;
        sel.appendChild(el);
    });
}

function populateSelectWithEmpty(id, options, emptyLabel) {
    const sel   = document.getElementById(id);
    const blank = document.createElement('option');
    blank.value = '';
    blank.text  = emptyLabel;
    sel.appendChild(blank);
    options.forEach(opt => {
        const el = document.createElement('option');
        el.value = opt.value;
        el.text  = opt.text;
        sel.appendChild(el);
    });
}

// ─────────────────────────────────────────────
// Form submit
// ─────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('addTransactionForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const txType = document.getElementById('modalTxTypeTracker').value;
        const userID = getUserID();

        const txPayload = {
            transactionName:   document.getElementById('txTitleInput').value,
            transactionAmount: parseFloat(document.getElementById('txAmountInput').value),
            transactionDate:   document.getElementById('txRealtimeDateInput').value,
            transactionType:   txType,
            transactionNote:   document.getElementById('txNoteInput').value,
            budgetID: txType === 'EXPENSE' ? document.getElementById('txBudgetSelect')?.value       : null,
            goalID:   txType === 'SAVING'  ? document.getElementById('txGoalSelect')?.value || null : null
        };

        try {
            const res = await fetch(`/api/transactions?userID=${userID}`, {
                method:  'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify(txPayload)
            });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
        } catch (err) {
            console.error("Transaction save failed:", err);
        }

        // ── Recurring for EXPENSE / INCOME ─────────────────────────────────
        const isRecurringExpenseIncome =
            (txType === 'EXPENSE' || txType === 'INCOME') &&
            document.getElementById('txAutoSaveRecurring')?.checked;

        if (isRecurringExpenseIncome) {
            const recurringPayload = {
                budgetID:           txType === 'EXPENSE' ? document.getElementById('txBudgetSelect')?.value : null,
                goalID:             null,
                rTransactionType:   txType,
                rTransactionName:   document.getElementById('txTitleInput').value,
                rTransactionNote:   document.getElementById('txNoteInput').value,
                rTransactionAmount: parseFloat(document.getElementById('txAmountInput').value),
                recurringDay:       parseInt(document.getElementById('recurringDayInput').value) || 1,
                rtStartDate:        document.getElementById('txRealtimeDateInput').value,
                rtEndDate:          document.getElementById('recurringEndDateInput').value || null
            };
            await saveRecurringTemplate(userID, recurringPayload);
        }

        // ── Monthly Saving Goal ────────────────────────────────────────────
        const isMonthlySaving =
            txType === 'SAVING' &&
            document.getElementById('txMonthlySavingGoalToggle')?.checked;

        if (isMonthlySaving) {
            const monthlySavingPayload = {
                budgetID:           document.getElementById('txSavingBudgetSelect')?.value || null,
                goalID:             document.getElementById('txGoalSelect')?.value         || null,
                rTransactionType:   'SAVING',
                rTransactionName:   document.getElementById('txTitleInput').value,
                rTransactionNote:   document.getElementById('txNoteInput').value,
                rTransactionAmount: parseFloat(document.getElementById('txAmountInput').value),
                recurringDay:       parseInt(document.getElementById('savingRecurringDayInput').value) || 1,
                rtStartDate:        document.getElementById('txRealtimeDateInput').value,
                rtEndDate:          document.getElementById('savingRecurringEndDateInput').value || null
            };
            await saveRecurringTemplate(userID, monthlySavingPayload);
        }

        closeTxModal();
        await fetchTransactionsFromDB();
    });
});

async function saveRecurringTemplate(userID, payload) {
    try {
        const res = await fetch(`/api/recurring?userID=${userID}`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });
        if (!res.ok) throw new Error(`Recurring save failed HTTP ${res.status}`);
    } catch (err) {
        console.error("Recurring template save failed:", err);
    }
}

// ─────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────
function getUserID() {
    return localStorage.getItem('userID') || 1;
}

function getMockData() {
    return [
        {
            transactionID: 1, transactionName: "Salary Payout",
            transactionDate: "2026-05-01", transactionAmount: 2500.00,
            transactionType: "INCOME", transactionNote: "Primary job",
            budgetName: "—", goalName: "—", recurringID: null
        },
        {
            transactionID: 2, transactionName: "Grocery Run",
            transactionDate: "2026-05-12", transactionAmount: 45.50,
            transactionType: "EXPENSE", transactionNote: "Weekly groceries",
            budgetName: "Food & Groceries", goalName: "—", recurringID: null
        },
        {
            transactionID: 3, transactionName: "Monthly Saving Deposit",
            transactionDate: "2026-05-01", transactionAmount: 300.00,
            transactionType: "SAVING", transactionNote: "Auto monthly saving",
            budgetName: "Monthly Saving Goal", goalName: "Trip to China",
            recurringID: 7
        },
        {
            transactionID: 4, transactionName: "Netflix Subscription",
            transactionDate: "2026-05-05", transactionAmount: 15.99,
            transactionType: "EXPENSE", transactionNote: "Monthly streaming",
            budgetName: "Entertainment", goalName: "—",
            recurringID: 3
        }
    ];
}

// ─────────────────────────────────────────────
// Boot
// ─────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", async () => {
    document.getElementById('txRealtimeDateInput').valueAsDate = new Date();
    updateNavLabel();
    setModalTxType('EXPENSE');
    await fetchTransactionsFromDB();
});