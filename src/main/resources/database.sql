-- Run this file in your MySQL database to create the necessary tables

-- ==========================================
-- 1. Employee Management System
-- ==========================================
CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    position VARCHAR(100),
    email VARCHAR(100)
);

