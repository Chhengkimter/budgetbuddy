-- =====================================================
--  BudgetBuddy — Full Database Schema
--  Database: budgeting_db
-- =====================================================

DROP DATABASE IF EXISTS budgeting_db;
CREATE DATABASE budgeting_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE budgeting_db;

-- =====================================================
--  TABLE 1: User
-- =====================================================

CREATE TABLE User (
    UserID          BIGINT          AUTO_INCREMENT PRIMARY KEY,
    UserFirstName   VARCHAR(100)    NOT NULL,
    UserLastName    VARCHAR(100)    NOT NULL,
    UserEmail       VARCHAR(150)    NOT NULL UNIQUE,
    UserPhoneNumber VARCHAR(20),
    UserPassword    VARCHAR(255)    NOT NULL,
    UserCreated     DATETIME        DEFAULT CURRENT_TIMESTAMP,
    UserIsActive    BOOLEAN         NOT NULL DEFAULT TRUE,

    KEY idx_user_email (UserEmail)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
--  TABLE 2: Budget
-- =====================================================

CREATE TABLE Budget (
    BudgetID                BIGINT          AUTO_INCREMENT PRIMARY KEY,
    UserID                  BIGINT          NOT NULL,
    BudgetName              VARCHAR(100)    NOT NULL,
    BudgetLimit             DECIMAL(12,2)   NOT NULL,
    BudgetMonth             TINYINT         NOT NULL,
    BudgetYear              YEAR            NOT NULL,
    BudgetUpdated           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    BudgetIsRecurring       BOOLEAN         NOT NULL DEFAULT FALSE,
    BudgetLastGeneratedDate DATE,

    CONSTRAINT FK_Budget_User
        FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    CONSTRAINT CK_BudgetLimit CHECK (BudgetLimit > 0),
    CONSTRAINT CK_BudgetMonth CHECK (BudgetMonth BETWEEN 1 AND 12),

    KEY idx_budget_user       (UserID),
    KEY idx_budget_user_month (UserID, BudgetMonth, BudgetYear)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
--  TABLE 3: Goal
-- =====================================================

CREATE TABLE Goal (
    GoalID           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    UserID           BIGINT        NOT NULL,
    GoalName         VARCHAR(100)  NOT NULL,
    GoalAmount       DECIMAL(12,2) NOT NULL,
    GoalCreatedDate  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    GoalTargetDate   DATE,
    GoalFinishedDate DATE,

    CONSTRAINT FK_Goal_User
        FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,
    CONSTRAINT CK_GoalAmount CHECK (GoalAmount > 0),

    KEY idx_goal_user (UserID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
--  TABLE 4: Transaction
--  INCOME   -> BudgetID null, GoalID null
--  SPENDING -> BudgetID required, GoalID null
--  SAVING   -> GoalID required, BudgetID null
-- =====================================================

CREATE TABLE Transaction (
    TransactionID     BIGINT        AUTO_INCREMENT PRIMARY KEY,
    UserID            BIGINT        NOT NULL,
    BudgetID          BIGINT,
    GoalID            BIGINT,
    TransactionType   ENUM('INCOME','SPENDING','SAVING') NOT NULL,
    TransactionName   VARCHAR(150)  NOT NULL,
    TransactionNote   VARCHAR(255),
    TransactionAmount DECIMAL(12,2) NOT NULL,
    TransactionDate   DATE          NOT NULL,

    CONSTRAINT FK_Transaction_User
        FOREIGN KEY (UserID)   REFERENCES User(UserID)     ON DELETE CASCADE,
    CONSTRAINT FK_Transaction_Budget
        FOREIGN KEY (BudgetID) REFERENCES Budget(BudgetID) ON DELETE SET NULL,
    CONSTRAINT FK_Transaction_Goal
        FOREIGN KEY (GoalID)   REFERENCES Goal(GoalID)     ON DELETE SET NULL,
    CONSTRAINT CK_TransactionAmount CHECK (TransactionAmount > 0),

    KEY idx_tx_user      (UserID),
    KEY idx_tx_budget    (BudgetID),
    KEY idx_tx_goal      (GoalID),
    KEY idx_tx_user_date (UserID, TransactionDate),
    KEY idx_tx_type      (TransactionType)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
--  TABLE 5: RecurringTransaction
-- =====================================================

CREATE TABLE RecurringTransaction (
    RecurringID          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    UserID               BIGINT        NOT NULL,
    BudgetID             BIGINT,
    GoalID               BIGINT,
    RTransactionType     ENUM('INCOME','SPENDING','SAVING') NOT NULL,
    RTransactionName     VARCHAR(150)  NOT NULL,
    RTransactionNote     VARCHAR(255),
    RTransactionAmount   DECIMAL(12,2) NOT NULL,
    RecurringDay         TINYINT       NOT NULL,
    RTStartDate          DATE          NOT NULL,
    RTEndDate            DATE,
    RTIsActive           BOOLEAN       NOT NULL DEFAULT TRUE,
    RTLastGeneratedDate  DATE,

    CONSTRAINT FK_Recurring_User
        FOREIGN KEY (UserID)   REFERENCES User(UserID)     ON DELETE CASCADE,
    CONSTRAINT FK_Recurring_Budget
        FOREIGN KEY (BudgetID) REFERENCES Budget(BudgetID) ON DELETE SET NULL,
    CONSTRAINT FK_Recurring_Goal
        FOREIGN KEY (GoalID)   REFERENCES Goal(GoalID)     ON DELETE SET NULL,
    CONSTRAINT CK_RecurringAmount CHECK (RTransactionAmount > 0),
    CONSTRAINT CK_RecurringDay    CHECK (RecurringDay BETWEEN 1 AND 28),

    KEY idx_recurring_user   (UserID),
    KEY idx_recurring_active (RTIsActive),
    KEY idx_recurring_day    (RecurringDay)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
--  TABLE 6: Notification
-- =====================================================

CREATE TABLE Notification (
    NotificationID      BIGINT       AUTO_INCREMENT PRIMARY KEY,
    UserID              BIGINT       NOT NULL,
    NotificationType    ENUM(
                            'BUDGET_ALERT',
                            'GOAL_REACHED',
                            'GOAL_REMINDER',
                            'BILL_DUE',
                            'BILL_OVERDUE',
                            'GENERAL'
                        ) NOT NULL DEFAULT 'GENERAL',
    NotificationTitle   VARCHAR(150) NOT NULL,
    NotificationMessage VARCHAR(500) NOT NULL,
    IsRead              BOOLEAN      NOT NULL DEFAULT FALSE,
    ReferenceID         BIGINT,
    CreatedAt           DATETIME     DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_Notification_User
        FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE,

    KEY idx_notif_user    (UserID),
    KEY idx_notif_read    (IsRead),
    KEY idx_notif_type    (NotificationType),
    KEY idx_notif_created (CreatedAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
--  SAMPLE DATA
-- =====================================================

INSERT INTO User (UserFirstName, UserLastName, UserEmail, UserPhoneNumber, UserPassword) VALUES
('Alice', 'Johnson', 'alice@test.com', '555-0101', '$2a$12$placeholder.hashed.password.alice'),
('Bob',   'Smith',   'bob@test.com',   '555-0102', '$2a$12$placeholder.hashed.password.bob');

INSERT INTO Budget (UserID, BudgetName, BudgetLimit, BudgetMonth, BudgetYear, BudgetIsRecurring) VALUES
(1, 'Food & Groceries',  400.00, 5, 2026, TRUE),
(1, 'Transportation',    200.00, 5, 2026, TRUE),
(1, 'Entertainment',     150.00, 5, 2026, FALSE),
(1, 'Utilities & Bills', 300.00, 5, 2026, TRUE);

INSERT INTO Goal (UserID, GoalName, GoalAmount, GoalTargetDate) VALUES
(1, 'New Laptop Fund', 1200.00, '2026-09-01'),
(1, 'China Trip Fund', 3000.00, '2026-12-31');

INSERT INTO Transaction (UserID, BudgetID, GoalID, TransactionType, TransactionName, TransactionNote, TransactionAmount, TransactionDate) VALUES
(1, NULL, NULL, 'INCOME',   'Salary Payout',  'Primary job payment',       2500.00, '2026-05-01'),
(1, 1,    NULL, 'SPENDING', 'Grocery Run',    'Weekly dinner ingredients',   45.50, '2026-05-12'),
(1, 2,    NULL, 'SPENDING', 'Grab Ride',      'Office commute',              12.00, '2026-05-13'),
(1, 3,    NULL, 'SPENDING', 'Netflix',        'Monthly subscription',        15.00, '2026-05-05'),
(1, NULL, 1,    'SAVING',   'Laptop Deposit', 'Monthly allocation',         100.00, '2026-05-15'),
(1, NULL, 2,    'SAVING',   'China Trip',     'Automated deposit',          200.00, '2026-05-15');

INSERT INTO RecurringTransaction (UserID, BudgetID, GoalID, RTransactionType, RTransactionName, RTransactionAmount, RecurringDay, RTStartDate, RTIsActive) VALUES
(1, NULL, NULL, 'INCOME',   'Monthly Salary',       2500.00,  1, '2026-01-01', TRUE),
(1, 4,    NULL, 'SPENDING', 'Electricity Bill',       80.00,  5, '2026-01-01', TRUE),
(1, 3,    NULL, 'SPENDING', 'Netflix Subscription',   15.00,  8, '2026-01-01', TRUE),
(1, NULL, 1,    'SAVING',   'Laptop Fund Deposit',   100.00, 15, '2026-01-01', TRUE);

INSERT INTO Notification (UserID, NotificationType, NotificationTitle, NotificationMessage, IsRead, ReferenceID) VALUES
(1, 'BUDGET_ALERT',  'Budget Warning', 'Food & Groceries is at 80% of your limit.', FALSE, 1),
(1, 'BILL_DUE',      'Bill Due Soon',  'Electricity Bill is due in 3 days.',         FALSE, 2),
(1, 'GOAL_REMINDER', 'Goal Progress',  'You are 8% toward your New Laptop Fund.',    TRUE,  1);

-- =====================================================
--  END
-- =====================================================