-- V1__init_tenant_schema.sql
CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    is_active     BOOLEAN   DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classes
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject       VARCHAR(100) NOT NULL,
    grade         VARCHAR(50)  NOT NULL,
    teacher_name  VARCHAR(100) NOT NULL,
    schedule_day  VARCHAR(20),
    schedule_time VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);