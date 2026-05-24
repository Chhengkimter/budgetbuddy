-- =====================================================
-- FIX 1: Properly integrate categories table
-- =====================================================
USE budgeting_db;
-- ... rest of your SQL
-- Drop and recreate categories with timestamps + index
DROP TABLE IF EXISTS categories;

CREATE TABLE IF NOT EXISTS categories (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    user_id    BIGINT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FK_categories_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_categories_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- FIX 2: Link budgets and transactions to categories
-- =====================================================

-- Replace VARCHAR category with a FK (add nullable first for existing data)
ALTER TABLE budgets
    ADD COLUMN category_id BIGINT NULL,
    ADD CONSTRAINT FK_budgets_categories
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    ADD KEY idx_budgets_category_id (category_id);

ALTER TABLE transactions
    ADD COLUMN category_id BIGINT NULL,
    ADD CONSTRAINT FK_transactions_categories
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    ADD KEY idx_transactions_category_id (category_id);

-- NOTE: Once you've migrated existing VARCHAR data into the categories table
-- and backfilled category_id, you can drop the old columns:
-- ALTER TABLE budgets DROP COLUMN category;
-- ALTER TABLE transactions DROP COLUMN category_tag;

-- =====================================================
-- FIX 3: Keep spent_amount in sync on soft-delete
-- =====================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS SoftDeleteTransaction$$

CREATE PROCEDURE SoftDeleteTransaction(
    IN p_transaction_id BIGINT
)
BEGIN
    DECLARE v_budget_id BIGINT;
    DECLARE v_amount    DECIMAL(12,2);
    DECLARE v_type      ENUM('INCOME','EXPENSE');

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Delete failed';
    END;

    SELECT budget_id, amount, type
      INTO v_budget_id, v_amount, v_type
      FROM transactions
     WHERE id = p_transaction_id AND is_deleted = FALSE;

    START TRANSACTION;

    UPDATE transactions SET is_deleted = TRUE WHERE id = p_transaction_id;

    -- Roll back the spent_amount if it was an expense
    IF v_type = 'EXPENSE' THEN
        UPDATE budgets
           SET spent_amount = GREATEST(0, spent_amount - v_amount)
         WHERE id = v_budget_id;
    END IF;

    COMMIT;
END$$

DELIMITER ;

-- =====================================================
-- FIX 4: Seed default categories for existing users
-- =====================================================

INSERT INTO categories (name, user_id, is_default)
SELECT 'General',   id, TRUE FROM users
UNION ALL
SELECT 'Food',      id, TRUE FROM users
UNION ALL
SELECT 'Travel',    id, TRUE FROM users
UNION ALL
SELECT 'Housing',   id, TRUE FROM users
UNION ALL
SELECT 'Utilities', id, TRUE FROM users
UNION ALL
SELECT 'Salary',    id, TRUE FROM users;