# CampusCare — College Complaint Management System
**Version 2.0 | Java + JSP + Servlets (javax.servlet) + Apache Tomcat 9 + JDBC + MySQL 8.0 + Bootstrap 5**

---

## 📌 Executive Summary
**CampusCare** is an enterprise-grade, centralized web application designed for higher education institutions. It streamlines the student complaint lifecycle from submission to resolution, ensuring accountability, traceability, and fast turnaround.

### Key Capabilities
- **End-to-End Workflow**: `SUBMITTED` → `UNDER_REVIEW` → `ASSIGNED` → `IN_PROGRESS` → `RESOLVED` (plus `REJECTED` with reason and `REOPENED` with preserved history).
- **Strict Role-Based Access Control**: Discrete workspaces for **Students**, **Administrators**, and **Department Staff**.
- **Audit Traceability**: Immutable event timeline logging actor, old/new states, comments, visibility (`PUBLIC` vs `INTERNAL`), and timestamps.
- **Overdue SLA Escalation**: Dynamic overdue detection based on ticket priority thresholds (Urgent: 24h, High: 48h, Medium: 72h, Low: 120h).
- **Security & Privacy**:
  - Salted one-way adaptive password hashing using **BCrypt** (12 rounds).
  - 100% Parameterized queries via **JDBC `PreparedStatement`** (SQL Injection prevention).
  - Session Fixation protection via session renewal upon login.
  - **CSRF Protection** using Synchronizer Tokens across state-changing actions.
  - Ownership authorization preventing IDOR (Insecure Direct Object References).
  - Safe CSV Export neutralizing spreadsheet formula injection (`=`, `+`, `-`, `@`).

---

## 🏗️ System Architecture & MVC Pattern

CampusCare strictly follows the standard enterprise Java MVC multi-tier architectural pattern:

```
[ Browser / Client ]
        │  HTTP / HTTPS
        ▼
[ Security Filters ] (EncodingFilter, AuthenticationFilter, RoleFilter, CsrfFilter)
        │
        ▼
[ Controller Layer ] (Servlets in com.campuscare.controller)
        │
        ▼
[ Service Layer ] (Business rules, transitions & transactions in com.campuscare.service)
        │
        ▼
[ DAO Layer ] (JDBC PreparedStatements in com.campuscare.dao)
        │
        ▼
[ MySQL 8.0 InnoDB Database ] (campuscare_db)
```

### Layer Breakdown
| Layer | Package | Responsibilities |
|---|---|---|
| **View** | `src/main/webapp/WEB-INF/views/` | Responsive JSP pages rendering presentation logic with JSTL `<c:out>` output escaping. |
| **Controller** | `com.campuscare.controller` | Servlets parsing requests, managing HTTP sessions, and dispatching to views. |
| **Filter** | `com.campuscare.filter` | Authentication, role permissions, CSRF token validation, and character encoding. |
| **Service** | `com.campuscare.service` | Status transition validation, audit event coordination, and multi-write DB transactions. |
| **DAO** | `com.campuscare.dao` | CRUD operations using PreparedStatements and resource safety (`try-with-resources`). |
| **Model** | `com.campuscare.model` | POJO domain entities (`User`, `Complaint`, `Department`, `ComplaintUpdate`, `Feedback`, etc.). |
| **Utility** | `com.campuscare.util` | Database connection manager, BCrypt hashing, CSV escaping, and input validation. |

---

## 🗄️ Database Schema & Entities

The relational database `campuscare_db` is implemented on MySQL 8.0 using InnoDB with `utf8mb4`:

1. `departments`: ID, name, description, contact details, active flag.
2. `categories`: ID, category name, description, default department mapping, active flag.
3. `users`: ID, full name, email, student ID, department FK, course/year, phone, password hash, role (`STUDENT`, `ADMIN`, `STAFF`), active flag.
4. `complaints`: ID, ticket number (`CC-YYYY-XXXXXX` UNIQUE), student FK, title, category FK, location, description, priority, status, assigned department FK, assigned staff FK, notes, timestamps.
5. `complaint_updates`: ID, complaint FK, updated_by FK, event type, old/new status, comment, visibility (`PUBLIC` / `INTERNAL`), created_at.
6. `notifications`: ID, user FK, complaint FK, message, is_read, created_at.
7. `feedback`: ID, complaint FK (UNIQUE), student FK, rating (1–5), comment, created_at.
8. `attachments`: ID, complaint FK, uploaded_by FK, stored filename, original filename, MIME type, file size, created_at.

---

## 👥 Demo Accounts for Viva & Evaluation

Pre-seeded accounts configured for immediate testing:

| Role | Email | Password | Name / Details |
|---|---|---|---|
| **Student** | `alex@campuscare.edu` | `student123` | Alex Morgan (B.Tech CSE - 3rd Year, `STU-2024-0042`) |
| **Student** | `sarah@campuscare.edu` | `student123` | Sarah Jenkins (B.Tech ECE - 2nd Year, `STU-2024-0105`) |
| **Administrator** | `admin@campuscare.edu` | `admin123` | Prof. Robert Vance (Chief Campus Administrator) |
| **Staff (IT)** | `it.staff@campuscare.edu` | `staff123` | Priya Sharma (IT Support Lead) |
| **Staff (Facilities)**| `facilities.staff@campuscare.edu` | `staff123` | Mark Reynolds (Facilities Supervisor) |
| **Staff (Electrical)**| `electrical.staff@campuscare.edu` | `staff123` | Vikram Singh (Electrical Maintenance) |

---

## 🚀 How to Run the Application

### Option A: 1-Click Embedded Execution (Recommended)
You do not need to install an external standalone Tomcat server.
1. Ensure MySQL is running on `localhost:3306` with database `campuscare_db` (configured in `application.properties`).
2. Double click **`run.bat`** or run:
   ```cmd
   mvn compile exec:java
   ```
3. Open your browser and navigate to:
   ```
   http://localhost:8080/CampusCare/
   ```
   *(Note: `http://localhost:8080/` automatically redirects to `/CampusCare/`)*

### Option B: Deploying WAR to External Tomcat 9
1. Build the production WAR bundle:
   ```cmd
   mvn clean package -DskipTests
   ```
2. Copy `target/CampusCare.war` into your Apache Tomcat 9 `webapps/` folder.
3. Start Tomcat: `bin/startup.bat` (or `startup.sh`).
4. Access at `http://localhost:8080/CampusCare/`.

---

## 🎬 5–7 Minute College Demo Walkthrough Script

Follow this script during project presentations or viva evaluation:

1. **Landing Page**:
   - Open `http://localhost:8080/CampusCare/`.
   - Explain the core problem: paper-based complaints lack visibility and ownership. Show the 3-step workflow (Submit → Track → Resolve).
2. **Student Login & Dashboard**:
   - Click "Sign in" and use quick sign-in: **Student (Alex)** (`alex@campuscare.edu`).
   - Show the personalized greeting, the 4 live KPI metric cards, and recent complaints table.
3. **Complaint Submission**:
   - Click **"+ New complaint"**.
   - Fill in:
     - Title: `Library Wi-Fi disconnects on 2nd floor`
     - Category: `Wi-Fi / Internet`
     - Location: `Central Library, 2nd floor study alcove`
     - Priority: `High`
     - Description: `Signal drops every 5 minutes while accessing IEEE papers.`
   - Click **Submit complaint**.
   - Observe the immediate redirection to **My complaints** showing the new unique ticket ID (e.g. `CC-2026-000086`).
4. **Ticket Inspection**:
   - Click **View details** on the new ticket to observe the initial status (`SUBMITTED`) and the timeline event.
   - Click the User menu at top-right and select **Sign Out**.
5. **Administrator Queue & Triage**:
   - Sign in as **Administrator** (`admin@campuscare.edu` / `admin123`).
   - Walk through the **Administration Overview** dashboard: KPI counts, live category distribution chart, and the **Needs attention** overdue box.
   - Click **Manage complaints** in the sidebar. Show the multi-facet filters (Status, Category, Priority, Department, Overdue only).
   - Find the newly created ticket and click **Process**.
   - Click **Assign**: Assign department to `IT Support` and staff member `Priya Sharma`.
   - Click **Update Status**: Change to `In Progress` with comment *"Technician investigating AP firmware"*.
   - Click **Update Status** again: Select `Resolved`. Enter mandatory Resolution Note: *"Replaced faulty Cisco AP transceiver on 2nd floor. Speed test verified at 120 Mbps."*
6. **Student Verification & Feedback**:
   - Sign out from Admin and log back in as **Student (Alex)**.
   - Click the **Notifications** bell to show the status transition alerts received in real time.
   - Open the resolved ticket. Show the complete timeline with all actor names and timestamps.
   - Fill out the **Resolution Feedback** form: Select **5 stars** and enter *"Fast turnaround, Wi-Fi is working great now!"*.
   - Submit and verify the feedback is rendered on the ticket.
7. **Security Demonstration**:
   - Attempt to access an admin URL directly as student: `http://localhost:8080/CampusCare/admin/dashboard` → Server returns **403 Forbidden**.
   - Attempt to open another student's ticket URL (e.g. `CC-2026-000103`) → Server returns **403 Forbidden**.

---

## 🎓 College Viva Preparation Q&A

### Q1: What problem does CampusCare solve and who are its users?
> **Answer**: CampusCare eliminates untracked paper complaints and informal messages by centralizing complaint lodging, department dispatch, status timelines, and feedback. Its users are **Students** (who submit and track tickets), **Administrators** (who oversee the queue, assign departments, and inspect reports), and **Department Staff** (technicians who add work notes and resolve tickets).

### Q2: What is MVC? Which classes belong to Model, View, and Controller in this project?
> **Answer**: MVC is an architectural pattern separating data representation, user presentation, and application control:
> - **Model**: `User.java`, `Complaint.java`, `Department.java`, `ComplaintUpdate.java`, `Feedback.java`, `Notification.java`.
> - **View**: Dynamic JSP templates (`WEB-INF/views/student/dashboard.jsp`, `shared/complaint-details.jsp`, `admin/complaint-queue.jsp`, etc.).
> - **Controller**: Java Servlets (`LoginServlet.java`, `ComplaintServlet.java`, `ComplaintDetailServlet.java`, `AdminComplaintServlet.java`, etc.).

### Q3: Why use JSP/Servlets with Tomcat 9 instead of Spring Boot for this brief?
> **Answer**: The brief specifically required a classic, server-rendered Java EE web application using the `javax.servlet` API (Servlet 4.0 / JSP 2.3) on Tomcat 9. This demonstrates core understanding of HTTP request life-cycles, filter chains, session management, and JDBC transactions without reliance on high-level framework abstractions.

### Q4: What is the role of DAO and Service layers?
> **Answer**:
> - **DAO (Data Access Object)**: Encapsulates all SQL execution, PreparedStatement parameter binding, and ResultSet mapping into Java models (`ComplaintDAO`, `UserDAO`).
> - **Service Layer**: Houses institutional business rules, status transition validation (e.g., verifying resolution notes exist before marking resolved), and wraps multi-table writes in atomic database transactions (`ComplaintService`, `AuthService`).

### Q5: How does JDBC connect Java to MySQL and how do PreparedStatements prevent SQL Injection?
> **Answer**: JDBC uses `com.mysql.cj.jdbc.Driver` to open a TCP connection to the MySQL server using credentials specified in `application.properties`. `PreparedStatement` pre-compiles the SQL query template on the database server with `?` placeholders. User input is transferred as literal data parameters, never concatenated into the SQL statement, making SQL injection mathematically impossible.

### Q6: Why should passwords be hashed with BCrypt instead of MD5 or SHA-256?
> **Answer**: MD5 and SHA-256 are fast hash algorithms vulnerable to modern GPU brute-force attacks and rainbow tables. BCrypt is a slow, salted, adaptive cryptographic hash based on the Blowfish cipher. It incorporates a per-password salt and a configurable work factor (12 rounds in CampusCare) that exponentially increases the computational effort required for offline cracking.

### Q7: What is session-based authentication and how are admin routes protected?
> **Answer**: Upon successful credential verification, the servlet creates an `HttpSession` stored in server memory and transmits an opaque `JSESSIONID` cookie flagged `HttpOnly`. `AuthenticationFilter` checks every incoming request for an active session. `RoleFilter` inspects the user's role from the session and returns HTTP `403 Forbidden` if an unauthorized user attempts to access `/admin/*` or `/staff/*`.

### Q8: How is ticket number uniqueness enforced?
> **Answer**: In addition to application-level sequential generation (`generateNextTicketNumber()`), the database enforces a `UNIQUE` index constraint on the `ticket_number` column in the `complaints` table (`ticket_number VARCHAR(30) NOT NULL UNIQUE`).

### Q9: Why keep `complaint_updates` instead of only updating the latest status?
> **Answer**: Accountability and non-repudiation. Storing an append-only audit trail preserves the complete timeline (who changed the status, from what state to what state, at what exact timestamp, and with what comment). This allows students to follow progress and enables administrators to monitor delays.

### Q10: How do you prevent a student from viewing another student's complaint?
> **Answer**: In `ComplaintService.getComplaintWithDetails()`, the authenticated `currentUser` object is retrieved from the trusted HTTP session (not from client-supplied request parameters). If `currentUser.isStudent()` and `complaint.getUserId() != currentUser.getUserId()`, the server immediately throws an `IllegalAccessException`, resulting in an HTTP `403 Forbidden` response without leaking ticket contents.

### Q11: What happens if saving the complaint succeeds but saving history fails?
> **Answer**: Both operations run within a single database transaction. `conn.setAutoCommit(false)` is initiated before writing. If any operation fails, `conn.rollback()` is executed, reverting all writes to guarantee zero orphaned complaints or corrupted database states.

### Q12: How are overdue complaints calculated?
> **Answer**: Overdue tickets are computed dynamically in MySQL based on priority thresholds:
> ```sql
> (priority = 'URGENT' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 24) OR
> (priority = 'HIGH'   AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 48) OR
> (priority = 'MEDIUM' AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 72) OR
> (priority = 'LOW'    AND TIMESTAMPDIFF(HOUR, created_at, NOW()) > 120)
> ```
> Excluded from overdue checks: `RESOLVED` and `REJECTED` tickets.

---

## 🧪 Acceptance Test Matrix (TC-01 to TC-20)

| Test ID | Scenario | Expected Result | Status |
|---|---|---|---|
| **TC-01** | Register with valid student data | Account created and login succeeds | **PASS** |
| **TC-02** | Duplicate email/student ID | Server rejects duplicate registration | **PASS** |
| **TC-03** | Invalid password at login | Generic error displayed; no session created | **PASS** |
| **TC-04** | Student requests admin route (`/admin/dashboard`) | Access denied (403 Forbidden) | **PASS** |
| **TC-05** | Submit valid complaint | Ticket saved once with unique ID `CC-YYYY-XXXXXX` | **PASS** |
| **TC-06** | Missing required fields | Server-side validation blocks submission | **PASS** |
| **TC-07** | Student requests another student's ticket | Access denied (403 Forbidden) | **PASS** |
| **TC-08** | Admin assigns department & staff | Assignment and timeline history persist | **PASS** |
| **TC-09** | Resolve without resolution note | Request rejected with validation message | **PASS** |
| **TC-10** | Status transitions | Timeline logs actor, old/new state, timestamp | **PASS** |
| **TC-11** | Unsafe/oversized upload (>10MB / disallowed ext) | File rejected safely with field error | **PASS** |
| **TC-12** | Submit feedback twice | Second submission blocked (Unique constraint) | **PASS** |
| **TC-13** | Filter admin queue by category/status/priority | Only matching records returned | **PASS** |
| **TC-14** | Logout and revisit protected page | Invalids session; redirected to `/login` | **PASS** |
| **TC-15** | SQL injection-like input in title/search | Handled as safe text via PreparedStatement | **PASS** |
| **TC-16** | Submit text containing `<script>` tags | Safely escaped in JSP via JSTL `<c:out>` | **PASS** |
| **TC-17** | Search returns zero matches | Helpful empty state with "Clear filters" link | **PASS** |
| **TC-18** | Mobile viewport | Responsive sidebar and tables adapt gracefully | **PASS** |
| **TC-19** | Database connection failure | Generic friendly 500 error; no stack trace | **PASS** |
| **TC-20** | CSV export cell starting with `=`, `+`, `-`, `@` | Escaped with leading single quote | **PASS** |

---
**CampusCare v2.0 • Google DeepMind Agentic Pair Programming**
