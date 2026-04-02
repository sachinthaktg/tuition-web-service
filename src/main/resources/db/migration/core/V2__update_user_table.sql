-- V2__update_user_table.sql
ALTER TABLE users
    ADD COLUMN full_name VARCHAR(150) NOT NULL,
    ADD COLUMN photo_url TEXT;
