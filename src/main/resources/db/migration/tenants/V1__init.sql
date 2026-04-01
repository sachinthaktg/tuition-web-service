-- V1__init.sql
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    is_active  BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grades
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE, -- e.g. Grade 6, Grade 7
    description VARCHAR(255)
);

CREATE TABLE subjects
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE, -- e.g. Mathematics
    code        VARCHAR(50),                  -- optional
    description VARCHAR(255)
);

CREATE TABLE classrooms
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL, -- Room A, Hall 1
    capacity INT,
    location VARCHAR(255)
);

-- V1__init.sql
CREATE TABLE students
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id      BIGINT UNIQUE, -- optional login account

    first_name   VARCHAR(100),
    middle_name  VARCHAR(100),
    last_name    VARCHAR(100),

    photo_url    TEXT,

    address      TEXT,
    phone        VARCHAR(20),
    birthday     DATE,
    grade_id     BIGINT NOT NULL,

    parent_name  VARCHAR(150),
    parent_phone VARCHAR(20),

    qr_code      VARCHAR(255) UNIQUE,

    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (grade_id) REFERENCES grades (id)
);

CREATE TABLE teachers
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id                BIGINT UNIQUE,

    first_name             VARCHAR(100),
    last_name              VARCHAR(100),
    phone                  VARCHAR(20),

    subject_specialization VARCHAR(100),

    salary_percentage      DOUBLE, -- e.g. 60%

    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE classes
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,

    subject_id   BIGINT NOT NULL,
    grade_id     BIGINT NOT NULL,
    teacher_id   BIGINT NOT NULL,
    classroom_id BIGINT,

    schedule_day VARCHAR(20),
    start_time   TIME,
    end_time     TIME,

    class_fee    DOUBLE,

    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (subject_id) REFERENCES subjects (id),
    FOREIGN KEY (grade_id) REFERENCES grades (id),
    FOREIGN KEY (teacher_id) REFERENCES teachers (id),
    FOREIGN KEY (classroom_id) REFERENCES classrooms (id)
);

CREATE TABLE enrollments
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,

    student_id  BIGINT,
    class_id    BIGINT,

    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (student_id, class_id),

    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (class_id) REFERENCES classes (id)
);

CREATE TABLE payments
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,

    student_id   BIGINT,
    class_id     BIGINT,

    amount       DOUBLE,
    payment_date DATE,

    month        VARCHAR(20), -- e.g. "2026-03"

    status       VARCHAR(20), -- PAID, PENDING

    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (class_id) REFERENCES classes (id)
);

CREATE TABLE attendance
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,

    student_id BIGINT,
    class_id   BIGINT,

    date       DATE,
    status     VARCHAR(20), -- PRESENT / ABSENT

    scanned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (class_id) REFERENCES classes (id)
);

CREATE TABLE teacher_payments
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,

    teacher_id    BIGINT,

    month         VARCHAR(20),

    total_income  DOUBLE,
    teacher_share DOUBLE,

    paid          BOOLEAN   DEFAULT FALSE,

    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (teacher_id) REFERENCES teachers (id)
);