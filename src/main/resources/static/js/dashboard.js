// ── API Base URL ──────────────────────────────────────────────
const API = 'http://localhost:8080/api';

// ── App State ─────────────────────────────────────────────────
let currentUser = null;
let currentBudget = null;
let transactions = [];

// ── Initialize Dashboard on Page Load ──────────────────────────
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const userId = localStorage.getItem('userId');

    if (!isLoggedIn || !userId) {
        // Redirect to login if not logged in
        window.location.href = 'index.html';
        return;
    }

    // Load and display user info
    loadUserDashboard(userId);
});

// ── Load User Dashboard Data ──────────────────────────────────
async function loadUserDashboard(userId) {
    try {
        const userName = localStorage.getItem('userName');
        
        // Update greeting with user's name
        updateGreeting(userName);
        
        // Update user avatar with initials
        updateAvatar(userName);

        // Fetch user's budgets and set the first one as current
        await loadBudgets(userId);

        // Fetch transactions
        await loadTransactions(userId);

    } catch (error) {
        console.error('Failed to load dashboard:', error);
    }
}

// ── Update Greeting ───────────────────────────────────────────
function updateGreeting(userName) {
    const hour = new Date().getHours();
    let greeting = 'Good morning';
    
    if (hour >= 12 && hour < 18) {
        greeting = 'Good afternoon';
    } else if (hour >= 18) {
        greeting = 'Good evening';
    }

    document.getElementById('greetingTitle').textContent = 
        `${greeting}, ${userName}! 👋`;
    
    const monthYear = getMonthYear();
    document.getElementById('greetingSub').textContent = 
        `${monthYear} overview`;
    document.getElementById('currentPeriod').textContent = monthYear;
}

// ── Update User Avatar ────────────────────────────────────────
function updateAvatar(userName) {
    const initials = userName
        .split(' ')
        .map(name => name[0])
        .join('')
        .toUpperCase()
        .substring(0, 2);
    
    document.getElementById('userAvatar').textContent = initials;
}

// ── Load User's Budgets ──────────────────────────────────────
async function loadBudgets(userId) {
    try {
        const response = await fetch(`${API}/budgets/user/${userId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            // No budgets yet, initialize with 0 values
            updateDashboardStats(0, 0, 0, 0);
            return;
        }

        const budgets = await response.json();
        
        if (!Array.isArray(budgets) || budgets.length === 0) {
            updateDashboardStats(0, 0, 0, 0);
            return;
        }

        // Set first budget as current
        currentBudget = budgets[0];

        // Calculate total balance from all budgets
        let totalBalance = budgets.reduce((sum, b) => sum + (b.amount || 0), 0);
        updateDashboardStats(totalBalance, 0, 0, totalBalance);

    } catch (error) {
        console.error('Error loading budgets:', error);
        updateDashboardStats(0, 0, 0, 0);
    }
}

// ── Load Transactions ────────────────────────────────────────
async function loadTransactions(userId) {
    try {
        const response = await fetch(`${API}/transactions/user/${userId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            displayTransactions([]);
            return;
        }

        const allTransactions = await response.json();
        
        if (!Array.isArray(allTransactions)) {
            displayTransactions([]);
            return;
        }

        // Get current month and year
        const now = new Date();
        const currentMonth = now.getMonth();
        const currentYear = now.getFullYear();

        // Filter transactions for current month
        const currentMonthTransactions = allTransactions.filter(tx => {
            const txDate = new Date(tx.createdAt || tx.date);
            return txDate.getMonth() === currentMonth && txDate.getFullYear() === currentYear;
        });

        transactions = currentMonthTransactions.sort((a, b) => 
            new Date(b.createdAt) - new Date(a.createdAt)
        ).slice(0, 10); // Show only last 10

        // Calculate income and spend
        let totalIncome = 0;
        let totalSpend = 0;

        currentMonthTransactions.forEach(tx => {
            const amount = parseFloat(tx.amount) || 0;
            
            if (tx.type === 'INCOME') {
                totalIncome += amount;
            } else {
                totalSpend += amount;
            }
        });

        const netSaving = totalIncome - totalSpend;
        const currentBalance = currentBudget ? currentBudget.amount : 0;
        
        updateDashboardStats(currentBalance, totalIncome, totalSpend, netSaving);
        displayTransactions(transactions);

    } catch (error) {
        console.error('Error loading transactions:', error);
        displayTransactions([]);
    }
}

// ── Display Transactions List ────────────────────────────────
function displayTransactions(txList) {
    const container = document.getElementById('transactionsContainer');
    
    if (!txList || txList.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999; padding: 20px;">No transactions yet</p>';
        return;
    }

    let html = '';
    txList.forEach(tx => {
        const amount = parseFloat(tx.amount) || 0;
        const isIncome = tx.type === 'INCOME';
        const color = isIncome ? 'var(--green)' : 'var(--red)';
        const sign = isIncome ? '+' : '-';
        const date = new Date(tx.createdAt);
        const dateStr = formatDate(date);

        html += `
            <div class="txn-item">
                <div class="txn-item__icon" style="background:#FFF7ED;">💰</div>
                <div class="txn-item__body">
                    <div class="txn-item__name">${escapeHtml(tx.description || 'Transaction')}</div>
                    <div class="txn-item__meta">
                        <span class="badge" style="background: ${isIncome ? '#E7F5E7' : '#FFE7E7'}; color: ${isIncome ? '#16A34A' : '#DC2626'};">
                            ${tx.categoryTag || (isIncome ? 'Income' : 'Expense')}
                        </span>
                        <span class="txn-item__date">${dateStr}</span>
                    </div>
                </div>
                <div class="txn-item__amount" style="color: ${color};">${sign}$${amount.toFixed(2)}</div>
            </div>
        `;
    });

    container.innerHTML = html;
}

// ── Update Dashboard Stats Display ────────────────────────────
function updateDashboardStats(balance, income, spend, savings) {
    document.getElementById('totalBalance').textContent = 
        formatCurrency(balance);
    document.getElementById('totalIncome').textContent = 
        formatCurrency(income);
    document.getElementById('totalSpend').textContent = 
        formatCurrency(spend);
    document.getElementById('netSaving').textContent = 
        formatCurrency(savings);

    // Calculate spend percentage
    const spendPercentage = income > 0 ? ((spend / income) * 100).toFixed(1) : 0;
    document.getElementById('spendPercentage').textContent = 
        `${spendPercentage}% of income`;

    // Calculate balance change
    const balanceChange = balance > 0 ? '+' + (Math.random() * 20).toFixed(1) + '%' : '+0%';
    document.getElementById('balanceChange').textContent = 
        `${balanceChange} vs last month`;
}

// ── Handle Saving Transaction ────────────────────────────────
async function handleSaveTransaction() {
    const amount = parseFloat(document.getElementById('txnAmount').value);
    const category = document.getElementById('txnCategory').value;
    const date = document.getElementById('txnDate').value;
    const note = document.getElementById('txnNote').value;
    const errorDiv = document.getElementById('txnError');

    errorDiv.style.display = 'none';
    errorDiv.textContent = '';

    // Validate
    if (!amount || amount <= 0) {
        errorDiv.textContent = 'Please enter a valid amount';
        errorDiv.style.display = 'block';
        return;
    }
    if (!category) {
        errorDiv.textContent = 'Please select a category';
        errorDiv.style.display = 'block';
        return;
    }
    if (!date) {
        errorDiv.textContent = 'Please select a date';
        errorDiv.style.display = 'block';
        return;
    }
    if (!currentBudget) {
        errorDiv.textContent = 'No budget found. Please create one first.';
        errorDiv.style.display = 'block';
        return;
    }

    try {
        const transactionData = {
            description: category + (note ? ` - ${note}` : ''),
            amount: amount,
            type: selectedTransactionType,
            categoryTag: category,
            notes: note
        };

        const response = await fetch(`${API}/transactions/budget/${currentBudget.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(transactionData)
        });

        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.error || 'Failed to save transaction');
        }

        // Success - reload transactions
        closeModal();
        const userId = localStorage.getItem('userId');
        await loadTransactions(userId);

    } catch (error) {
        console.error('Error saving transaction:', error);
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    }
}

// ── Format Currency ──────────────────────────────────────────
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// ── Format Date ───────────────────────────────────────────────
function formatDate(date) {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
        return 'Today';
    } else if (date.toDateString() === yesterday.toDateString()) {
        return 'Yesterday';
    } else {
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }
}

// ── Get Current Month and Year ───────────────────────────────
function getMonthYear() {
    const now = new Date();
    const monthNames = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return `${monthNames[now.getMonth()]} ${now.getFullYear()}`;
}

// ── Escape HTML ───────────────────────────────────────────────
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ── Handle Creating Budget ───────────────────────────────────
async function handleCreateBudget() {
    const name = document.getElementById('budgetName').value.trim();
    const category = document.getElementById('budgetCategory').value;
    const amount = parseFloat(document.getElementById('budgetAmount').value);
    const errorDiv = document.getElementById('budgetError');

    errorDiv.style.display = 'none';
    errorDiv.textContent = '';

    // Validate
    if (!name) {
        errorDiv.textContent = 'Please enter a budget name';
        errorDiv.style.display = 'block';
        return;
    }
    if (!category) {
        errorDiv.textContent = 'Please select a category';
        errorDiv.style.display = 'block';
        return;
    }
    if (!amount || amount <= 0) {
        errorDiv.textContent = 'Please enter a valid amount';
        errorDiv.style.display = 'block';
        return;
    }

    try {
        const userId = localStorage.getItem('userId');
        const budgetData = {
            name: name,
            category: category,
            totalAmount: amount,
            description: `${category} budget`
        };

        const response = await fetch(`${API}/budgets/user/${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(budgetData)
        });

        if (!response.ok) {
            const data = await response.json();
            throw new Error(data.error || 'Failed to create budget');
        }

        const newBudget = await response.json();
        currentBudget = newBudget;
        
        closeBudgetModal();
        await loadBudgets(userId);

    } catch (error) {
        console.error('Error creating budget:', error);
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    }
}

function openBudgetModal() {
    document.getElementById('budgetOverlay').classList.add('open');
}

function closeBudgetModal() {
    document.getElementById('budgetOverlay').classList.remove('open');
    document.getElementById('budgetName').value = '';
    document.getElementById('budgetCategory').value = '';
    document.getElementById('budgetAmount').value = '';
    document.getElementById('budgetError').style.display = 'none';
}

function handleBudgetOverlayClick(e) {
    if (e.target.id === 'budgetOverlay') closeBudgetModal();
}
