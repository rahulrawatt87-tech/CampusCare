-- =====================================================================
-- CampusCare Seed Data
-- =====================================================================

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

-- 3. Seed Users
-- Password for all seed users:
-- admin: admin123
-- staff: staff123
-- student: student123
INSERT INTO users (user_id, full_name, email, student_id, department_id, course_year, phone, password_hash, role, is_active) VALUES
(1, 'Prof. Robert Vance', 'admin@campuscare.edu', NULL, 11, NULL, '+1-555-0199', '$2a$12$viyxt20vFPFKu4Yn.OprLOYavrzM2GcG7bsewiexnayg2DXX96cb.', 'ADMIN', TRUE),
(2, 'Priya Sharma', 'it.staff@campuscare.edu', NULL, 4, NULL, '+1-555-0144', '$2a$12$yVAcLXPK2OYeGEoLw.QIZe5q94aXtaVxv6TlKWstX345/cIFxbsve', 'STAFF', TRUE),
(3, 'Mark Reynolds', 'facilities.staff@campuscare.edu', NULL, 1, NULL, '+1-555-0133', '$2a$12$yVAcLXPK2OYeGEoLw.QIZe5q94aXtaVxv6TlKWstX345/cIFxbsve', 'STAFF', TRUE),
(4, 'Vikram Singh', 'electrical.staff@campuscare.edu', NULL, 2, NULL, '+1-555-0122', '$2a$12$yVAcLXPK2OYeGEoLw.QIZe5q94aXtaVxv6TlKWstX345/cIFxbsve', 'STAFF', TRUE),
(5, 'Alex Morgan', 'alex@campuscare.edu', 'STU-2024-0042', 4, 'B.Tech Computer Science - 3rd Year', '+1-555-0188', '$2a$12$mBNfYh6klgJRAHfH3cOGAO7l72o7xkXFp1prenN.ekfnOwk5R4.1O', 'STUDENT', TRUE),
(6, 'Sarah Jenkins', 'sarah@campuscare.edu', 'STU-2024-0105', 2, 'B.Tech Electronics - 2nd Year', '+1-555-0177', '$2a$12$mBNfYh6klgJRAHfH3cOGAO7l72o7xkXFp1prenN.ekfnOwk5R4.1O', 'STUDENT', TRUE),
(7, 'Dev Patel', 'dev@campuscare.edu', 'STU-2023-0891', 1, 'B.Sc Data Science - Final Year', '+1-555-0166', '$2a$12$mBNfYh6klgJRAHfH3cOGAO7l72o7xkXFp1prenN.ekfnOwk5R4.1O', 'STUDENT', TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

-- 4. Seed Complaints (matches PRD mockups)
INSERT INTO complaints (complaint_id, ticket_number, user_id, title, category_id, location, description, priority, status, assigned_department_id, assigned_staff_id, rejection_reason, resolution_notes, created_at, updated_at, resolved_at) VALUES
(1, 'CC-2026-000101', 5, 'Library Wi-Fi is not working', 4, 'Central Library, second floor', 'Wi-Fi disconnects every few minutes and students cannot access course material and research papers.', 'MEDIUM', 'IN_PROGRESS', 4, 2, NULL, NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL),
(2, 'CC-2026-000102', 5, 'Broken classroom fan', 2, 'Block C, Room 302', 'Ceiling fan makes loud grinding noises and wobbles dangerously during lectures.', 'HIGH', 'ASSIGNED', 2, 4, NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), NULL),
(3, 'CC-2026-000098', 5, 'Water cooler issue', 3, 'Block B, 2nd floor corridor', 'Water cooler filter is clogged and water is warm and slightly murky.', 'MEDIUM', 'RESOLVED', 3, NULL, NULL, 'Replaced carbon filter cartridge and repaired refrigeration unit. Water quality tested safe.', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 'CC-2026-000093', 5, 'Hostel corridor light', 5, 'Hostel 3, 2nd floor wing B', 'Corridor lights are flickering and causing complete darkness at night in the east wing.', 'LOW', 'UNDER_REVIEW', 5, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), NULL),
(5, 'CC-2026-000103', 6, 'Water leak near Chemistry Lab 2', 3, 'Ramanujan Block, Ground Floor', 'Major pipe leakage causing flooding near student lockers outside Lab 2.', 'HIGH', 'SUBMITTED', NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), NULL),
(6, 'CC-2026-000104', 7, 'ID card issue at North Gate', 11, 'North Security Checkpoint', 'Scanner fails to read RFID chip on several student identity cards causing long queues.', 'LOW', 'ASSIGNED', 11, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),
(7, 'CC-2026-000085', 6, 'Cafeteria food hygiene and temperature', 7, 'Main Dining Hall Counter 2', 'Warm food served cold on Thursday evening, and utensils were greasy.', 'MEDIUM', 'RESOLVED', 7, NULL, NULL, 'Spoke with cafeteria vendor. Enforced hot-holding temperature protocols and inspected dishwashing unit.', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY))
ON DUPLICATE KEY UPDATE title = VALUES(title);

-- 5. Seed Complaint Updates (Timeline / Audit History)
INSERT INTO complaint_updates (update_id, complaint_id, updated_by, event_type, old_status, new_status, comment, visibility, created_at) VALUES
-- CC-2026-000101
(1, 1, 5, 'CREATED', NULL, 'SUBMITTED', 'Complaint submitted: Ticket created successfully.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 1, 1, 'STATUS_CHANGE', 'SUBMITTED', 'UNDER_REVIEW', 'Administration reviewed the report.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 4 HOUR),
(3, 1, 1, 'ASSIGNED', 'UNDER_REVIEW', 'ASSIGNED', 'Assigned to IT Support. Department assignment recorded.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 6 HOUR),
(4, 1, 2, 'STATUS_CHANGE', 'ASSIGNED', 'IN_PROGRESS', 'Team is checking the access point and replacement hardware.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 1, 2, 'INTERNAL_NOTE', 'IN_PROGRESS', 'IN_PROGRESS', 'AP Cisco 3702 in east alcove needs firmware flash or port swap.', 'INTERNAL', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 1 HOUR),

-- CC-2026-000102
(6, 2, 5, 'CREATED', NULL, 'SUBMITTED', 'Complaint submitted: Ticket created successfully.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 2, 1, 'ASSIGNED', 'SUBMITTED', 'ASSIGNED', 'Assigned to Electrical Maintenance for fan motor replacement.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- CC-2026-000098
(8, 3, 5, 'CREATED', NULL, 'SUBMITTED', 'Complaint submitted.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(9, 3, 1, 'ASSIGNED', 'SUBMITTED', 'ASSIGNED', 'Assigned to Plumbing / Maintenance.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(10, 3, 1, 'STATUS_CHANGE', 'ASSIGNED', 'IN_PROGRESS', 'Technician dispatched with spare filter.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(11, 3, 1, 'RESOLVED', 'IN_PROGRESS', 'RESOLVED', 'Replaced carbon filter cartridge and repaired refrigeration unit. Water quality tested safe.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- CC-2026-000093
(12, 4, 5, 'CREATED', NULL, 'SUBMITTED', 'Complaint submitted.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 13 DAY)),
(13, 4, 1, 'STATUS_CHANGE', 'SUBMITTED', 'UNDER_REVIEW', 'Under administrative review by hostel supervisor.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 12 DAY)),

-- CC-2026-000103
(14, 5, 6, 'CREATED', NULL, 'SUBMITTED', 'High priority report submitted.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- CC-2026-000104
(15, 6, 7, 'CREATED', NULL, 'SUBMITTED', 'Complaint registered.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(16, 6, 1, 'ASSIGNED', 'SUBMITTED', 'ASSIGNED', 'Assigned to General Administration.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- CC-2026-000085
(17, 7, 6, 'CREATED', NULL, 'SUBMITTED', 'Canteen complaint submitted.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 16 DAY)),
(18, 7, 1, 'ASSIGNED', 'SUBMITTED', 'ASSIGNED', 'Assigned to Canteen Management.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(19, 7, 1, 'RESOLVED', 'ASSIGNED', 'RESOLVED', 'Spoke with cafeteria vendor. Enforced hot-holding temperature protocols and inspected dishwashing unit.', 'PUBLIC', DATE_SUB(NOW(), INTERVAL 14 DAY))
ON DUPLICATE KEY UPDATE comment = VALUES(comment);

-- 6. Seed Notifications
INSERT INTO notifications (notification_id, user_id, complaint_id, message, is_read, created_at) VALUES
(1, 5, 1, 'Your complaint CC-2026-000101 was moved to In Progress by IT Support.', FALSE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 5, 2, 'Your complaint CC-2026-000102 was assigned to Electrical Maintenance.', TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 5, 3, 'Your complaint CC-2026-000098 has been marked as Resolved. Please provide feedback!', TRUE, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 6, 7, 'Your complaint CC-2026-000085 has been marked as Resolved.', TRUE, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(5, 1, 5, 'New high-priority complaint CC-2026-00103 requires review.', FALSE, DATE_SUB(NOW(), INTERVAL 4 DAY))
ON DUPLICATE KEY UPDATE message = VALUES(message);

-- 7. Seed Feedback
INSERT INTO feedback (feedback_id, complaint_id, user_id, rating, comment, created_at) VALUES
(1, 3, 5, 5, 'Very quick resolution! The water cooler is working great now. Thank you!', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 7, 6, 4, 'Resolved properly, though response took a couple of days. Staff was courteous.', DATE_SUB(NOW(), INTERVAL 13 DAY))
ON DUPLICATE KEY UPDATE rating = VALUES(rating);
