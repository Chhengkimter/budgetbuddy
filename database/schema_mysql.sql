-- =====================================================
--  BudgetBuddy - MySQL Database Schema
--  Complete schema with tables, indexes, and stored procedures
-- =====================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS budgeting_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE budgeting_db;

-- =====================================================
--  TABLE 1: USERS
-- =====================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 2: BUDGETS
-- =====================================================

CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    spent_amount DECIMAL(12,2) DEFAULT 0.00,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT FK_budgets_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_user_id (user_id),
    KEY idx_category (category),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 3: TRANSACTIONS
-- =====================================================

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    budget_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    category_tag VARCHAR(50),
    notes VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT FK_transactions_budgets FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE CASCADE,
    KEY idx_budget_id (budget_id),
    KEY idx_type (type),
    KEY idx_created_at (created_at),
    KEY idx_budget_date (budget_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  DATA INTEGRITY CONSTRAINTS
-- =====================================================

ALTER TABLE budgets ADD CONSTRAINT CK_budgets_total_amount CHECK (total_amount > 0);
ALTER TABLE transactions ADD CONSTRAINT CK_transactions_amount CHECK (amount > 0);

-- =====================================================
--  SAMPLE DATA (Optional - for testing)
-- =====================================================

INSERT INTO users (name, email, password) VALUES
    ('Alice Johnson', 'alice@example.com', 'hashed_password_123'),
    ('Bob Smith', 'bob@example.com', 'hashed_password_456');

INSERT INTO budgets (user_id, name, total_amount, category, description) VALUES
    (1, 'Monthly Budget', 2000.00, 'GENERAL', 'Main monthly budget allocation'),
    (1, 'Vacation Fund', 500.00, 'TRAVEL', 'Savings for vacation'),
    (2, 'Groceries', 300.00, 'FOOD', 'Weekly grocery budget');

INSERT INTO transactions (budget_id, description, amount, type, category_tag, notes) VALUES
    (1, 'Salary', 3000.00, 'INCOME', 'SALARY', 'Monthly paycheck'),
    (1, 'Rent', 800.00, 'EXPENSE', 'HOUSING', 'Monthly rent payment'),
    (1, 'Electricity', 50.00, 'EXPENSE', 'UTILITIES', 'Monthly electric bill'),
    (2, 'Flight Ticket', 200.00, 'EXPENSE', 'TRAVEL', 'Round trip flight'),
    (3, 'Supermarket', 75.50, 'EXPENSE', 'FOOD', 'Weekly groceries');

-- =====================================================
--  STORED PROCEDURES
-- =====================================================

DELIMITER $$

-- Get user budget summary
DROP PROCEDURE IF EXISTS GetUserBudgetSummary$$

CREATE PROCEDURE GetUserBudgetSummary(
    IN p_user_id BIGINT
)
BEGIN
    SELECT 
        b.id,
        b.name,
        b.category,
        b.total_amount,
        COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS total_expenses,
        COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS total_income,
        (b.total_amount - COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)) AS remaining_balance,
        ROUND((COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) / b.total_amount * 100), 2) AS percent_used
    FROM budgets b
    LEFT JOIN transactions t ON b.id = t.budget_id AND t.is_deleted = FALSE
    WHERE b.user_id = p_user_id AND b.is_active = TRUE
    GROUP BY b.id, b.name, b.category, b.total_amount
    ORDER BY b.created_at DESC;
END$$

-- Get transactions for a budget with date range
DROP PROCEDURE IF EXISTS GetTransactionsByBudgetDateRange$$

CREATE PROCEDURE GetTransactionsByBudgetDateRange(
    IN p_budget_id BIGINT,
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT 
        id,
        budget_id,
        description,
        amount,
        type,
        category_tag,
        notes,
        created_at,
        updated_at
    FROM transactions
    WHERE budget_id = p_budget_id 
        AND created_at BETWEEN p_start_date AND p_end_date
        AND is_deleted = FALSE
    ORDER BY created_at DESC;
END$$

-- Get income vs expenses summary
DROP PROCEDURE IF EXISTS GetIncomeExpenseSummary$$

CREATE PROCEDURE GetIncomeExpenseSummary(
    IN p_user_id BIGINT,
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT 
        t.type,
        SUM(t.amount) AS total_amount,
        COUNT(*) AS transaction_count
    FROM transactions t
    INNER JOIN budgets b ON t.budget_id = b.id
    WHERE b.user_id = p_user_id
        AND t.created_at BETWEEN p_start_date AND p_end_date
        AND t.is_deleted = FALSE
    GROUP BY t.type;
END$$

-- Get budget details with transaction count
DROP PROCEDURE IF EXISTS GetBudgetDetails$$

CREATE PROCEDURE GetBudgetDetails(
    IN p_budget_id BIGINT
)
BEGIN
    SELECT 
        b.id,
        b.user_id,
        b.name,
        b.category,
        b.total_amount,
        b.spent_amount,
        COUNT(t.id) AS transaction_count,
        COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS total_income,
        COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS total_expenses,
        b.created_at,
        b.updated_at
    FROM budgets b
    LEFT JOIN transactions t ON b.id = t.budget_id AND t.is_deleted = FALSE
    WHERE b.id = p_budget_id
    GROUP BY b.id, b.user_id, b.name, b.category, b.total_amount, b.spent_amount, b.created_at, b.updated_at;
END$$

-- Get all transactions for a user with budget info
DROP PROCEDURE IF EXISTS GetUserTransactions$$

CREATE PROCEDURE GetUserTransactions(
    IN p_user_id BIGINT,
    IN p_limit INT DEFAULT 100
)
BEGIN
    SELECT 
        t.id,
        t.description,
        t.amount,
        t.type,
        t.category_tag,
        t.notes,
        b.name AS budget_name,
        b.category AS budget_category,
        t.created_at,
        t.updated_at
    FROM transactions t
    INNER JOIN budgets b ON t.budget_id = b.id
    WHERE b.user_id = p_user_id AND t.is_deleted = FALSE
    ORDER BY t.created_at DESC
    LIMIT p_limit;
END$$

-- Create new transaction and update budget spent_amount
DROP PROCEDURE IF EXISTS CreateTransaction$$

CREATE PROCEDURE CreateTransaction(
    IN p_budget_id BIGINT,
    IN p_description VARCHAR(255),
    IN p_amount DECIMAL(12,2),
    IN p_type ENUM('INCOME', 'EXPENSE'),
    IN p_category_tag VARCHAR(50),
    IN p_notes VARCHAR(500),
    OUT p_transaction_id BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Transaction creation failed';
    END;
    
    START TRANSACTION;
    
    -- Insert transaction
    INSERT INTO transactions (budget_id, description, amount, type, category_tag, notes)
    VALUES (p_budget_id, p_description, p_amount, p_type, p_category_tag, p_notes);
    
    SET p_transaction_id = LAST_INSERT_ID();
    
    -- Update budget spent_amount only for EXPENSE type
    IF p_type = 'EXPENSE' THEN
        UPDATE budgets 
        SET spent_amount = spent_amount + p_amount
        WHERE id = p_budget_id;
    END IF;
    
    COMMIT;
END$$

DELIMITER ;

-- =====================================================
--  VIEWS
-- =====================================================

-- View: Budget Status with calculations
CREATE OR REPLACE VIEW vw_budget_status AS
SELECT 
    b.id,
    b.name,
    b.category,
    u.email AS user_email,
    b.total_amount,
    COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS total_spent,
    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS total_income,
    (b.total_amount - COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)) AS remaining_balance,
    ROUND(
        (COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) / 
        NULLIF(b.total_amount, 0) * 100),
        2
    ) AS percent_used,
    b.created_at,
    b.updated_at
FROM budgets b
LEFT JOIN users u ON b.user_id = u.id
LEFT JOIN transactions t ON b.id = t.budget_id AND t.is_deleted = FALSE
WHERE b.is_active = TRUE
GROUP BY b.id, b.name, b.category, u.email, b.total_amount, b.created_at, b.updated_at;

-- View: Recent Transactions
CREATE OR REPLACE VIEW vw_recent_transactions AS
SELECT 
    t.id,
    t.description,
    t.amount,
    t.type,
    t.category_tag,
    b.name AS budget_name,
    u.name AS user_name,
    t.created_at
FROM transactions t
INNER JOIN budgets b ON t.budget_id = b.id
INNER JOIN users u ON b.user_id = u.id
WHERE t.is_deleted = FALSE AND b.is_active = TRUE AND u.is_active = TRUE;

-- View: User Financial Summary
CREATE OR REPLACE VIEW vw_user_financial_summary AS
SELECT 
    u.id,
    u.name,
    u.email,
    COUNT(DISTINCT b.id) AS total_budgets,
    COUNT(DISTINCT t.id) AS total_transactions,
    COALESCE(SUM(b.total_amount), 0) AS total_budget_amount,
    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) AS total_income,
    COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS total_expenses
FROM users u
LEFT JOIN budgets b ON u.id = b.user_id AND b.is_active = TRUE
LEFT JOIN transactions t ON b.id = t.budget_id AND t.is_deleted = FALSE
WHERE u.is_active = TRUE
GROUP BY u.id, u.name, u.email;

-- =====================================================
--  USEFUL QUERIES FOR REFERENCE
-- =====================================================

-- Get all users with their budget count
-- SELECT u.id, u.name, u.email, COUNT(b.id) as budget_count
-- FROM users u
-- LEFT JOIN budgets b ON u.id = b.user_id AND b.is_active = TRUE
-- WHERE u.is_active = TRUE
-- GROUP BY u.id, u.name, u.email;

-- Get expense breakdown by category for a user
-- SELECT 
--     b.category,
--     SUM(t.amount) AS total_spent,
--     COUNT(t.id) AS transaction_count
-- FROM transactions t
-- INNER JOIN budgets b ON t.budget_id = b.id
-- WHERE b.user_id = 1 AND t.type = 'EXPENSE' AND t.is_deleted = FALSE
-- GROUP BY b.category
-- ORDER BY total_spent DESC;

-- Get budgets exceeding their limits
-- SELECT b.id, b.name, b.total_amount, b.spent_amount, (b.spent_amount - b.total_amount) AS overage
-- FROM budgets b
-- WHERE b.spent_amount > b.total_amount AND b.is_active = TRUE;

-- Get daily expense trend
-- SELECT DATE(created_at) AS expense_date, SUM(amount) AS daily_total
-- FROM transactions
-- WHERE type = 'EXPENSE' AND is_deleted = FALSE
-- GROUP BY DATE(created_at)
-- ORDER BY expense_date DESC;

-- =====================================================
--  NOTES FOR JAVA/SPRING INTEGRATION
-- =====================================================

/*
MySQL to Java Type Mapping:

BIGINT AUTO_INCREMENT       → long with @GeneratedValue(strategy = GenerationType.IDENTITY)
VARCHAR(255)                → String
DECIMAL(12,2)               → BigDecimal
DATETIME                    → LocalDateTime (java.time)
BOOLEAN                     → boolean or Boolean
ENUM('INCOME', 'EXPENSE')   → Enum with @Enumerated(EnumType.STRING)

Spring Boot MySQL Configuration:
Driver: com.mysql.cj.jdbc.Driver
URL: jdbc:mysql://localhost:3306/budgeting_db?serverTimezone=UTC&useSSL=false

Example application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/budgeting_db?serverTimezone=UTC&useSSL=false
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

Required Maven Dependency:
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.2.0</version>
</dependency>
*/

-- =====================================================
--  END OF SCHEMA
-- =====================================================
