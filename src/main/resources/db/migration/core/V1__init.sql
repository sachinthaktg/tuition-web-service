-- V1__init.sql
CREATE TABLE tenants
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,

    center_name   VARCHAR(255) NOT NULL,
    domain_prefix VARCHAR(255) NOT NULL UNIQUE,
    admin_name    VARCHAR(255) NOT NULL,
    admin_email   VARCHAR(255) NOT NULL UNIQUE,
    admin_phone   VARCHAR(50)  NOT NULL,

    setup_fee     DOUBLE       NOT NULL,
    revenue_share DOUBLE       NOT NULL,

    status        VARCHAR(50)  NOT NULL,

    created_at    DATETIME     NOT NULL
);

CREATE INDEX idx_domain_prefix ON tenants (domain_prefix);
CREATE INDEX idx_admin_email ON tenants (admin_email);

CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,

    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,

    role       VARCHAR(50)  NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_username ON users (username);

INSERT INTO users (username, password, role)
VALUES ('superadmin',
        '$2a$10$a4cmb1YDI71SKNxqaXV1xexmM3qVj02Y3otVBgYtDTpq9bfi0DsSm', -- bcrypt password 1234
        'SUPER_ADMIN');