-- Copy of database seed data for documentation reference
-- Original maintained in src/main/resources/db/seed.sql
-- Run: mysql -u root -proot < docs/seed.sql

USE campuscare_db;

-- 1. Seed Departments
INSERT INTO departments (department_id, department_name, description, contact_email, contact_phone, is_active) VALUES
(1, 'Facilities / Maintenance', 'Campus physical infrastructure, classrooms, furniture and building maintenance', 'facilities@campuscare.edu', '+1-555-0101', TRUE),
(2, 'Electrical Maintenance', 'Campus electrical fixtures, lighting, fans, power sockets and generators', 'electrical@campuscare.edu', '+1-555-0102', TRUE),
(3, 'Plumbing / Maintenance', 'Water pipelines, restrooms, drinking water stations and drainage', 'plumbing@campuscare.edu', '+1-555-0103', TRUE),
(4, 'IT Support', 'Campus Wi-Fi networks, lab computers, projectors, smart boards and network portals', 'itsupport@campuscare.edu', '+1-555-0104', TRUE),
(5, 'Hostel Administration', 'Student accommodation, room allocations, hostel facilities and rules', 'hostel@campuscare.edu', '+1-555-0105', TRUE),
(6, 'Library Administration', 'Library facilities, study rooms, digital book terminals and subscriptions', 'library@campuscare.edu', '+1-555-0106', TRUE),
(7, 'Canteen Management', 'Campus cafeteria, dining halls, food cleanliness and pricing standards', 'canteen@campuscare.edu', '+1-555-0107', TRUE),
(8, 'Housekeeping', 'Campus cleanliness, waste disposal, floor sanitation and groundskeeping', 'housekeeping@campuscare.edu', '+1-555-0108', TRUE),
(9, 'Transport Office', 'College shuttle buses, vehicle parking permits and transit timing', 'transport@campuscare.edu', '+1-555-0109', TRUE),
(10, 'Academic Administration', 'Class schedules, exam hall arrangements and academic support offices', 'academic@campuscare.edu', '+1-555-0110', TRUE),
(11, 'General Administration', 'Campus security, lost & found, ID cards and visitor services', 'general@campuscare.edu', '+1-555-0111', TRUE)
ON DUPLICATE KEY UPDATE department_name = VALUES(department_name);

-- 2. Seed Categories
INSERT INTO categories (category_id, category_name, description, default_department_id, is_active) VALUES
(1, 'Classroom / Infrastructure', 'Broken benches, whiteboard damage, doors and windows', 1, TRUE),
(2, 'Electricity', 'Power cuts, flickering lights, damaged switches, fans', 2, TRUE),
(3, 'Water supply / leakage', 'Tap leaks, drinking water purifiers, washroom plumbing', 3, TRUE),
(4, 'Wi-Fi / Internet', 'Slow internet, access point downtime, portal login errors', 4, TRUE),
(5, 'Hostel', 'Hostel room repairs, geyser issues, mess problems', 5, TRUE),
(6, 'Library', 'Reading room lighting, AC malfunction, terminal access', 6, TRUE),
(7, 'Canteen / Food', 'Food quality, hygiene, seating cleanliness, billing', 7, TRUE),
(8, 'Cleanliness', 'Littered corridors, unclean washrooms, dustbin clearance', 8, TRUE),
(9, 'Transportation', 'Bus delay, route changes, college shuttle issues', 9, TRUE),
(10, 'Academic service', 'Lecture hall acoustics, mic issues, notice boards', 10, TRUE),
(11, 'Other', 'General administrative assistance and unlisted inquiries', 11, TRUE)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- 3. Seed Users (BCrypt hashes with 12 rounds)
INSERT INTO users (user_id, full_name, email, student_id, department_id, course_year, phone, password_hash, role, is_active) VALUES
(1, 'Prof. Robert Vance', 'admin@campuscare.edu', NULL, 11, NULL, '+1-555-0199', '$2a$12$viyxt20vFPFKu4Yn.OprLOYavrzM2GcG7bsewiexnayg2DXX96cb.', 'ADMIN', TRUE),
(2, 'Priya Sharma', 'it.staff@campuscare.edu', NULL, 4, NULL, '+1-555-0144', '$2a$12$yVAcLXPK2OYeGEoLw.QIZe5q94aXtaVxv6TlKWstX345/cIFxbsve', 'STAFF', TRUE),
(3, 'Mark Reynolds', 'facilities.staff@campuscare.edu', NULL, 1, NULL, '+1-555-0133', '$2a$12$yVAcLXPK2OYeGEoLw.QIZe5q94aXtaVxv6TlKWstX345/cIFxbsve', 'STAFF', TRUE),
(4, 'Vikram Singh', 'electrical.staff@campuscare.edu', NULL, 2, NULL, '+1-555-0122', '$2a$12$yVAcLXPK2OYeGEoLw.QIZe5q94aXtaVxv6TlKWstX345/cIFxbsve', 'STAFF', TRUE),
(5, 'Alex Morgan', 'alex@campuscare.edu', 'STU-2024-0042', 4, 'B.Tech Computer Science - 3rd Year', '+1-555-0188', '$2a$12$mBNfYh6klgJRAHfH3cOGAO7l72o7xkXFp1prenN.ekfnOwk5R4.1O', 'STUDENT', TRUE),
(6, 'Sarah Jenkins', 'sarah@campuscare.edu', 'STU-2024-0105', 2, 'B.Tech Electronics - 2nd Year', '+1-555-0177', '$2a$12$mBNfYh6klgJRAHfH3cOGAO7l72o7xkXFp1prenN.ekfnOwk5R4.1O', 'STUDENT', TRUE),
(7, 'Dev Patel', 'dev@campuscare.edu', 'STU-2023-0891', 1, 'B.Sc Data Science - Final Year', '+1-555-0166', '$2a$12$mBNfYh6klgJRAHfH3cOGAO7l72o7xkXFp1prenN.ekfnOwk5R4.1O', 'STUDENT', TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);
