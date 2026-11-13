-- Campus Skill Swap - MySQL database script
-- Run this ONCE in MySQL Workbench / MySQL command line:  source sql/schema.sql

CREATE DATABASE IF NOT EXISTS campus_skill_swap;
USE campus_skill_swap;

DROP TABLE IF EXISTS swap_requests;
DROP TABLE IF EXISTS student_skills;
DROP TABLE IF EXISTS preferences;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS students;

-- 1. students
CREATE TABLE students (
    student_id    INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(60)  NOT NULL,
    email         VARCHAR(80)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,
    department    VARCHAR(40),
    year_of_study INT,
    gender        VARCHAR(10),
    phone         VARCHAR(15),
    bio           VARCHAR(300),
    photo_path    VARCHAR(255)
);

-- 2. skills (catalog of all skills)
CREATE TABLE skills (
    skill_id   INT AUTO_INCREMENT PRIMARY KEY,
    skill_name VARCHAR(50) NOT NULL UNIQUE,
    category   VARCHAR(30) NOT NULL
);

-- 3. student_skills (which student offers which skill)
CREATE TABLE student_skills (
    offer_id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id       INT NOT NULL,
    skill_id         INT NOT NULL,
    level            VARCHAR(15) NOT NULL,
    description      VARCHAR(200),
    experience_years INT DEFAULT 0,
    weekends_only    BOOLEAN DEFAULT FALSE,
    UNIQUE (student_id, skill_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id)   REFERENCES skills(skill_id)     ON DELETE CASCADE
);

-- 4. preferences (one row per student)
CREATE TABLE preferences (
    student_id        INT PRIMARY KEY,
    mode              VARCHAR(10) DEFAULT 'Both',
    weekends          BOOLEAN DEFAULT FALSE,
    email_alerts      BOOLEAN DEFAULT TRUE,
    hours_per_week    INT DEFAULT 5,
    interest_category VARCHAR(30),
    available_now     BOOLEAN DEFAULT TRUE,
    notes             VARCHAR(200),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 5. swap_requests (the main feature)
CREATE TABLE swap_requests (
    request_id   INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    offer_id     INT NOT NULL,
    message      VARCHAR(200),
    status       VARCHAR(10) NOT NULL DEFAULT 'PENDING',   -- PENDING / ACCEPTED / REJECTED
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES students(student_id)      ON DELETE CASCADE,
    FOREIGN KEY (offer_id)     REFERENCES student_skills(offer_id)  ON DELETE CASCADE
);

-- ---------------- sample data ----------------
INSERT INTO skills (skill_name, category) VALUES
 ('Java Programming', 'Programming'),
 ('Python',           'Programming'),
 ('Web Development',  'Programming'),
 ('UI/UX Design',     'Design'),
 ('Graphic Design',   'Design'),
 ('Public Speaking',  'Communication'),
 ('Content Writing',  'Communication'),
 ('Digital Marketing','Marketing');

-- sample students (password for all three:  pass123)
INSERT INTO students (full_name, email, password_hash, department, year_of_study, gender, phone, bio) VALUES
 ('Rahul Kumar',  'rahul@campus.edu', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'CSE', 3, 'Male',   '9876543210', 'Loves coding and teaching.'),
 ('Priya Sharma', 'priya@campus.edu', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'IT',  2, 'Female', '9123456780', 'Designer and writer.'),
 ('Arjun Reddy',  'arjun@campus.edu', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'ECE', 4, 'Male',   '9988776655', 'Debate club president.');

INSERT INTO student_skills (student_id, skill_id, level, description, experience_years, weekends_only) VALUES
 (1, 1, 'Intermediate', 'Core Java, OOP and collections for beginners.', 2, FALSE),
 (1, 2, 'Beginner',     'Python basics and simple scripts.',             1, FALSE),
 (2, 3, 'Advanced',     'HTML, CSS, JavaScript and React projects.',     3, FALSE),
 (2, 4, 'Intermediate', 'Figma wireframes and app design.',              2, TRUE),
 (3, 5, 'Beginner',     'Posters and logos using Canva.',                1, FALSE),
 (3, 6, 'Advanced',     'Stage confidence and presentation skills.',     3, FALSE),
 (2, 7, 'Intermediate', 'Blogs and technical article writing.',          2, FALSE),
 (1, 8, 'Beginner',     'Social media basics.',                          1, TRUE);

-- one sample request: Priya asks Rahul to teach Java
INSERT INTO swap_requests (requester_id, offer_id, message) VALUES
 (2, 1, 'Hi Rahul, I would like to learn Java from you.');
