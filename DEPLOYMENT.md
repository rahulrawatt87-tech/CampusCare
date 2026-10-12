# 🌐 Deploying CampusCare to Render (Complete Guide)

This guide walks you through deploying **CampusCare v2.0** (Frontend + Backend + Cloud Database) to **[Render](https://render.com/)** completely free with zero hassle.

---

## 🏗️ Architecture Overview

- **Web Application (Frontend + Backend)**: Runs on Render as a Docker Web Service running Embedded Tomcat 9 and Java 21.
- **Database**: Connects to a cloud-hosted MySQL 8.0 instance (e.g. Aiven, TiDB Cloud, or Clever Cloud).
- **Auto-Initialization**: When CampusCare connects to your cloud database for the first time, it **automatically runs the schema and seed scripts** to create all tables, categories, and initial demo accounts.

---

## ⚡ Step 1: Get a Free Cloud MySQL Database (Takes 1 Minute)

Render natively provides managed PostgreSQL, so for MySQL you can use any free cloud MySQL provider. **Aiven** or **TiDB Cloud** are recommended because they are completely free and require no credit card.

### Recommended: Aiven for MySQL (Free Forever)
1. Sign up for free at **[aiven.io](https://aiven.io/)**.
2. Click **Create Service** → Select **MySQL** → Select the **Free Plan**.
3. Choose any cloud region close to you and click **Create Service**.
4. Once created, note down:
   - **Host** (e.g., `mysql-3c12345-myproject.aivencloud.com`)
   - **Port** (e.g., `12345`)
   - **User** (e.g., `avnadmin`)
   - **Password** (e.g., `AVNS_xyz...`)
   - **Database Name** (usually `defaultdb`)

Your JDBC URL will look like:
```
jdbc:mysql://<host>:<port>/<database>?useSSL=true&allowPublicKeyRetrieval=true
```

*(Alternative: You can also use **[TiDB Cloud Serverless](https://tidbcloud.com/)** which gives 25GB free MySQL storage with 1-click).*

---

## 🚀 Step 2: Deploy to Render

1. Log in to your Render account at **[dashboard.render.com](https://dashboard.render.com/)**.
2. Click the **+ New** button in the top right and select **Web Service**.
3. Under **Connect a repository**, select your GitHub repository:
   ```
   rahulrawatt87-tech/CampusCare
   ```
4. Render will inspect the repo and automatically detect the **Dockerfile**.
5. Fill in the basic settings:
   - **Name**: `campuscare` (or any name you like)
   - **Region**: Select Oregon (US West) or Frankfurt (EU)
   - **Branch**: `main`
   - **Runtime**: `Docker` (automatically detected)
   - **Instance Type**: `Free`

---

## 🔑 Step 3: Add Environment Variables in Render

Scroll down to the **Environment Variables** section on the Render setup page and add the following:

| Key | Value | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://<host>:<port>/<dbname>?useSSL=true&allowPublicKeyRetrieval=true` | Your cloud MySQL JDBC URL |
| `DB_USER` | `<your-cloud-db-username>` | Database username |
| `DB_PASSWORD` | `<your-cloud-db-password>` | Database password |
| `SERVER_CONTEXT_PATH` | `""` | Leaves app at root (`/`) |
| `PORT` | `10000` | (Render will inject this automatically) |

---

## 🎉 Step 4: Click "Deploy Web Service"

1. Click **Deploy Web Service** at the bottom of the page.
2. Render will build the Docker container using Maven, compile all classes, and start the application.
3. You can watch the live build and deployment logs:
   ```
   [DatabaseInitializer] Fresh database detected. Creating tables from schema.sql...
   [DatabaseInitializer] Seeding initial data from seed.sql...
   [DatabaseInitializer] Database initialization and seed completed successfully!
   Server is running. Listening on port 10000...
   ```
4. Once the deployment finishes, Render will provide your live URL:
   ```
   https://campuscare-xxxx.onrender.com/
   ```

---

## 🔒 Post-Deployment Verification

1. Open your live Render URL in your browser.
2. You can immediately log in using the demo accounts:
   - **Student**: `alex@campuscare.edu` / `student123`
   - **Administrator**: `admin@campuscare.edu` / `admin123`
   - **Staff**: `it.staff@campuscare.edu` / `staff123`
3. Try lodging a ticket as a student, then sign in as admin to assign it to staff.
