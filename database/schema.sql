-- =============================================
--  Budgeting App - MySQL Database Schema
--  Run this in MySQL Workbench before starting
-- =============================================

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS budgeting_db;
USE budgeting_db;

-- 2. Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL
);

-- 3. Budgets table
CREATE TABLE IF NOT EXISTS budgets (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100)   NOT NULL,
    total_amount DOUBLE         NOT NULL,
    user_id      BIGINT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255)  NOT NULL,
    amount      DOUBLE        NOT NULL,
    type        ENUM('INCOME', 'EXPENSE') NOT NULL,
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    budget_id   BIGINT        NOT NULL,
    FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE CASCADE
);

-- =============================================
--  Sample Data (optional - for testing)
-- =============================================

INSERT INTO users (name, email, password) VALUES
    ('Alice Johnson', 'alice@example.com', 'password123'),
    ('Bob Smith',     'bob@example.com',   'password456');

INSERT INTO budgets (name, total_amount, user_id) VALUES
    ('Monthly Budget', 2000.00, 1),
    ('Vacation Fund',  500.00,  1),
    ('Groceries',      300.00,  2);

INSERT INTO transactions (description, amount, type, budget_id) VALUES
    ('Salary',        3000.00, 'INCOME',  1),
    ('Rent',          800.00,  'EXPENSE', 1),
    ('Electricity',   50.00,   'EXPENSE', 1),
    ('Flight Ticket', 200.00,  'EXPENSE', 2),
    ('Supermarket',   75.50,   'EXPENSE', 3);
