-- V2__update_user_table.sql
ALTER TABLE users
    ADD COLUMN full_name VARCHAR(150) NOT NULL,
    ADD COLUMN photo_url TEXT;

-- first_name
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'students'
      AND COLUMN_NAME = 'first_name'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql = IF(@col_exists > 0,
              'ALTER TABLE students DROP COLUMN first_name',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- middle_name
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'students'
      AND COLUMN_NAME = 'middle_name'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql = IF(@col_exists > 0,
              'ALTER TABLE students DROP COLUMN middle_name',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- last_name
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'students'
      AND COLUMN_NAME = 'last_name'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql = IF(@col_exists > 0,
              'ALTER TABLE students DROP COLUMN last_name',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- photo_url
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'students'
      AND COLUMN_NAME = 'photo_url'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql = IF(@col_exists > 0,
              'ALTER TABLE students DROP COLUMN photo_url',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- first_name
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'teachers'
      AND COLUMN_NAME = 'first_name'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql = IF(@col_exists > 0,
              'ALTER TABLE teachers DROP COLUMN first_name',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- last_name
SET @col_exists = (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'teachers'
      AND COLUMN_NAME = 'last_name'
      AND TABLE_SCHEMA = DATABASE()
);

SET @sql = IF(@col_exists > 0,
              'ALTER TABLE teachers DROP COLUMN last_name',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;