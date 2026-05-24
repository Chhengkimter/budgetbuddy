-- =====================================================
--  BudgetBuddy - New Tables Migration
--  Run this AFTER the original schema is already in place.
--  Safe to run: uses IF NOT EXISTS — will NOT drop or
--  affect your existing users / budgets / transactions.
-- =====================================================

USE budgeting_db;

-- =====================================================
--  TABLE 4: USER_SETTINGS
--  One row per user. Stores preferences like currency,
--  language, alert thresholds, and notification toggles.
-- =====================================================

CREATE TABLE IF NOT EXISTS user_settings (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                 BIGINT NOT NULL UNIQUE,
    currency                VARCHAR(10)  NOT NULL DEFAULT 'USD',
    language                VARCHAR(10)  NOT NULL DEFAULT 'en',
    email_alerts            BOOLEAN      NOT NULL DEFAULT TRUE,
    budget_alerts           BOOLEAN      NOT NULL DEFAULT TRUE,
    budget_alert_threshold  DECIMAL(5,2) NOT NULL DEFAULT 80.00
                                COMMENT 'Alert when % of budget spent reaches this value (e.g. 80 = 80%)',
    updated_at              DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_user_settings_users
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_user_settings_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 5: SAVINGS_GOALS
--  A user sets a savings target (e.g. "Buy laptop - $800
--  by December"). Tracks progress toward that target.
-- =====================================================

CREATE TABLE IF NOT EXISTS savings_goals (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    description     VARCHAR(255),
    target_amount   DECIMAL(12,2)   NOT NULL,
    current_amount  DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    target_date     DATE,
    status          ENUM('IN_PROGRESS', 'COMPLETED', 'CANCELLED')
                        NOT NULL DEFAULT 'IN_PROGRESS',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_savings_goals_users
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT CK_savings_goals_target  CHECK (target_amount > 0),
    CONSTRAINT CK_savings_goals_current CHECK (current_amount >= 0),
    KEY idx_savings_goals_user_id (user_id),
    KEY idx_savings_goals_status  (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 6: GOAL_CONTRIBUTIONS
--  Every time a user puts money toward a savings goal,
--  it is logged here. Allows progress history tracking.
-- =====================================================

CREATE TABLE IF NOT EXISTS goal_contributions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    goal_id         BIGINT          NOT NULL,
    amount          DECIMAL(12,2)   NOT NULL,
    notes           VARCHAR(255),
    contributed_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_goal_contributions_goals
        FOREIGN KEY (goal_id) REFERENCES savings_goals(id) ON DELETE CASCADE,
    CONSTRAINT CK_goal_contributions_amount CHECK (amount > 0),
    KEY idx_goal_contributions_goal_id      (goal_id),
    KEY idx_goal_contributions_contributed  (contributed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 7: RECURRING_TRANSACTIONS
--  Subscriptions, rent, salary — things that repeat on
--  a schedule. A background job reads next_due_date and
--  auto-creates a transaction in the transactions table.
-- =====================================================

CREATE TABLE IF NOT EXISTS recurring_transactions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    budget_id       BIGINT          NOT NULL,
    description     VARCHAR(255)    NOT NULL,
    amount          DECIMAL(12,2)   NOT NULL,
    type            ENUM('INCOME', 'EXPENSE') NOT NULL,
    category_tag    VARCHAR(50),
    frequency       ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')
                        NOT NULL DEFAULT 'MONTHLY',
    next_due_date   DATE            NOT NULL,
    end_date        DATE            COMMENT 'NULL means recurring indefinitely',
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_recurring_transactions_budgets
        FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE CASCADE,
    CONSTRAINT CK_recurring_amount CHECK (amount > 0),
    KEY idx_recurring_budget_id    (budget_id),
    KEY idx_recurring_next_due     (next_due_date),
    KEY idx_recurring_is_active    (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 8: NOTIFICATIONS
--  System alerts sent to users: budget exceeded,
--  goal reached, upcoming bill due, etc.
-- =====================================================

CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    title       VARCHAR(150)    NOT NULL,
    message     VARCHAR(500)    NOT NULL,
    type        ENUM(
                    'BUDGET_ALERT',
                    'GOAL_REACHED',
                    'GOAL_REMINDER',
                    'BILL_DUE',
                    'BILL_OVERDUE',
                    'GENERAL'
                ) NOT NULL DEFAULT 'GENERAL',
    is_read     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_notifications_users
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_notifications_user_id  (user_id),
    KEY idx_notifications_is_read  (is_read),
    KEY idx_notifications_type     (type),
    KEY idx_notifications_created  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  TABLE 9: AUDIT_LOGS
--  Tracks every important create / update / delete
--  across the app. Can be written to by application
--  code (service layer) or by MySQL triggers.
--  old_value and new_value store JSON snapshots.
-- =====================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT          COMMENT 'NULL if system-generated action',
    action      ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL,
    table_name  VARCHAR(100)    NOT NULL,
    record_id   BIGINT          NOT NULL,
    old_value   JSON            COMMENT 'Snapshot before change (NULL for CREATE)',
    new_value   JSON            COMMENT 'Snapshot after change (NULL for DELETE)',
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_audit_logs_users
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    KEY idx_audit_user_id    (user_id),
    KEY idx_audit_table      (table_name),
    KEY idx_audit_record     (table_name, record_id),
    KEY idx_audit_created    (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
--  DATA INTEGRITY CONSTRAINTS (new tables)
-- =====================================================

ALTER TABLE user_settings
    ADD CONSTRAINT CK_budget_alert_threshold
        CHECK (budget_alert_threshold BETWEEN 1 AND 100);

ALTER TABLE savings_goals
    ADD CONSTRAINT CK_savings_goal_dates
        CHECK (end_date IS NULL OR target_date IS NULL);
-- =====================================================
--  SAMPLE DATA (optional — matches existing test users)
-- =====================================================

-- User settings for Alice (user_id=1) and Bob (user_id=2)
INSERT IGNORE INTO user_settings (user_id, currency, language, email_alerts, budget_alerts, budget_alert_threshold)
VALUES
    (1, 'USD', 'en', TRUE,  TRUE,  80.00),
    (2, 'USD', 'en', TRUE,  TRUE,  90.00);

-- Savings goals for Alice
INSERT IGNORE INTO savings_goals (user_id, name, description, target_amount, current_amount, target_date, status)
VALUES
    (1, 'Emergency Fund',   '3 months of expenses saved',  3000.00, 500.00,  '2026-12-31', 'IN_PROGRESS'),
    (1, 'New Laptop',       'Work laptop upgrade',          1200.00, 200.00,  '2026-09-01', 'IN_PROGRESS');

-- Contributions toward Alice's emergency fund (goal_id=1)
INSERT IGNORE INTO goal_contributions (goal_id, amount, notes)
VALUES
    (1, 300.00, 'First deposit'),
    (1, 200.00, 'Bonus from work');

-- Recurring transactions for Alice's Monthly Budget (budget_id=1)
INSERT IGNORE INTO recurring_transactions (budget_id, description, amount, type, category_tag, frequency, next_due_date)
VALUES
    (1, 'Monthly Salary',       3000.00, 'INCOME',  'SALARY',    'MONTHLY', '2026-06-01'),
    (1, 'Rent Payment',          800.00, 'EXPENSE', 'HOUSING',   'MONTHLY', '2026-06-01'),
    (1, 'Netflix Subscription',   15.00, 'EXPENSE', 'UTILITIES', 'MONTHLY', '2026-06-05'),
    (1, 'Gym Membership',         40.00, 'EXPENSE', 'HEALTH',    'MONTHLY', '2026-06-10');

-- Sample notifications for Alice
INSERT IGNORE INTO notifications (user_id, title, message, type, is_read)
VALUES
    (1, 'Budget Alert',    'Your Monthly Budget has reached 80% of the limit.', 'BUDGET_ALERT',  FALSE),
    (1, 'Bill Due Soon',   'Rent Payment is due in 3 days.',                    'BILL_DUE',      FALSE),
    (1, 'Keep it up!',     'You are 16% toward your Emergency Fund goal.',      'GOAL_REMINDER', TRUE);

-- =====================================================
--  STORED PROCEDURES (new tables)
-- =====================================================

DELIMITER $$

-- Add a contribution to a savings goal and update current_amount
DROP PROCEDURE IF EXISTS AddGoalContribution$$
CREATE PROCEDURE AddGoalContribution(
    IN  p_goal_id   BIGINT,
    IN  p_amount    DECIMAL(12,2),
    IN  p_notes     VARCHAR(255),
    OUT p_new_total DECIMAL(12,2)
)
BEGIN
    DECLARE v_target DECIMAL(12,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Contribution failed';
    END;

    START TRANSACTION;

    INSERT INTO goal_contributions (goal_id, amount, notes)
    VALUES (p_goal_id, p_amount, p_notes);

    UPDATE savings_goals
    SET current_amount = current_amount + p_amount
    WHERE id = p_goal_id;

    SELECT current_amount, target_amount
    INTO p_new_total, v_target
    FROM savings_goals WHERE id = p_goal_id;

    -- Auto-complete the goal if target is reached
    IF p_new_total >= v_target THEN
        UPDATE savings_goals SET status = 'COMPLETED' WHERE id = p_goal_id;
    END IF;

    COMMIT;
END$$

-- Get all unread notifications for a user
DROP PROCEDURE IF EXISTS GetUnreadNotifications$$
CREATE PROCEDURE GetUnreadNotifications(
    IN p_user_id BIGINT
)
BEGIN
    SELECT id, title, message, type, created_at
    FROM notifications
    WHERE user_id = p_user_id AND is_read = FALSE
    ORDER BY created_at DESC;
END$$

-- Mark all notifications as read for a user
DROP PROCEDURE IF EXISTS MarkNotificationsRead$$
CREATE PROCEDURE MarkNotificationsRead(
    IN p_user_id BIGINT
)
BEGIN
    UPDATE notifications
    SET is_read = TRUE
    WHERE user_id = p_user_id AND is_read = FALSE;
END$$

-- Get all recurring transactions due today or overdue
DROP PROCEDURE IF EXISTS GetDueRecurringTransactions$$
CREATE PROCEDURE GetDueRecurringTransactions()
BEGIN
    SELECT
        rt.id,
        rt.budget_id,
        rt.description,
        rt.amount,
        rt.type,
        rt.category_tag,
        rt.frequency,
        rt.next_due_date,
        b.user_id
    FROM recurring_transactions rt
    INNER JOIN budgets b ON rt.budget_id = b.id
    WHERE rt.is_active = TRUE
        AND rt.next_due_date <= CURDATE()
        AND (rt.end_date IS NULL OR rt.end_date >= CURDATE());
END$$

-- Get savings goals summary for a user
DROP PROCEDURE IF EXISTS GetSavingsGoalsSummary$$
CREATE PROCEDURE GetSavingsGoalsSummary(
    IN p_user_id BIGINT
)
BEGIN
    SELECT
        sg.id,
        sg.name,
        sg.target_amount,
        sg.current_amount,
        sg.target_date,
        sg.status,
        ROUND((sg.current_amount / sg.target_amount * 100), 2) AS percent_complete,
        COUNT(gc.id) AS total_contributions,
        DATEDIFF(sg.target_date, CURDATE())                    AS days_remaining
    FROM savings_goals sg
    LEFT JOIN goal_contributions gc ON sg.id = gc.goal_id
    WHERE sg.user_id = p_user_id
    GROUP BY sg.id, sg.name, sg.target_amount, sg.current_amount, sg.target_date, sg.status
    ORDER BY sg.created_at DESC;
END$$

DELIMITER ;

-- =====================================================
--  VIEWS (new tables)
-- =====================================================

-- View: Savings goals progress
CREATE OR REPLACE VIEW vw_savings_goals_progress AS
SELECT
    sg.id,
    sg.name,
    u.email          AS user_email,
    sg.target_amount,
    sg.current_amount,
    ROUND((sg.current_amount / sg.target_amount * 100), 2) AS percent_complete,
    sg.target_date,
    sg.status,
    DATEDIFF(sg.target_date, CURDATE()) AS days_remaining
FROM savings_goals sg
INNER JOIN users u ON sg.user_id = u.id
WHERE sg.status = 'IN_PROGRESS';

-- View: Upcoming recurring transactions (next 30 days)
CREATE OR REPLACE VIEW vw_upcoming_bills AS
SELECT
    rt.id,
    rt.description,
    rt.amount,
    rt.type,
    rt.frequency,
    rt.next_due_date,
    b.name      AS budget_name,
    u.email     AS user_email,
    DATEDIFF(rt.next_due_date, CURDATE()) AS days_until_due
FROM recurring_transactions rt
INNER JOIN budgets b ON rt.budget_id = b.id
INNER JOIN users  u ON b.user_id = u.id
WHERE rt.is_active = TRUE
    AND rt.next_due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY);

-- =====================================================
--  NOTES FOR JAVA/SPRING INTEGRATION
-- =====================================================

/*
New Java model files to create (one per table):

1. UserSettings.java   → @Table(name = "user_settings")
2. SavingsGoal.java    → @Table(name = "savings_goals")
     status field      → enum Status { IN_PROGRESS, COMPLETED, CANCELLED }
                          @Enumerated(EnumType.STRING)
3. GoalContribution.java → @Table(name = "goal_contributions")
4. RecurringTransaction.java → @Table(name = "recurring_transactions")
     frequency field   → enum Frequency { DAILY, WEEKLY, MONTHLY, YEARLY }
     type field        → reuse Transaction.Type enum
5. Notification.java   → @Table(name = "notifications")
     type field        → enum NotificationType { BUDGET_ALERT, GOAL_REACHED, ... }
6. AuditLog.java       → @Table(name = "audit_logs")
     old/new value     → String (stored as JSON text)

Relationships to add in existing models:
- User.java:   add @OneToOne(mappedBy="user") UserSettings settings;
               add @OneToMany(mappedBy="user") List<SavingsGoal> savingsGoals;
               add @OneToMany(mappedBy="user") List<Notification> notifications;
- Budget.java: add @OneToMany(mappedBy="budget") List<RecurringTransaction> recurringTransactions;

application.properties reminder:
spring.jpa.hibernate.ddl-auto=update
(Hibernate will auto-add new tables/columns when you add the new @Entity classes)
*/

-- =====================================================
--  END OF MIGRATION
-- =====================================================