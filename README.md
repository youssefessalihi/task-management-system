# Project & Task Management System

A full-stack project management application built with Spring Boot 4 and React 19.

---

## Author

| Field | Value |
|-------|-------|
| **Name** | Youssef ESSALIHI |
| **Email** | youssefessalihi@hotmail.com |
| **GitHub** | [github.com/youssefessalihi](https://github.com/youssefessalihi) |
| **Company** | Hahn Software Morocco |
| **Context** | End of Studies Internship 2026 |

---

## Technology Stack

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21.0.9 | Programming Language |
| Spring Boot | 4.0.1 | Application Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Database ORM |
| PostgreSQL | 16-alpine | Database |
| JWT (jjwt) | 0.12.6 | Token Authentication |
| Maven | 3.9.12 | Build Tool |
| JaCoCo | 0.8.12 | Code Coverage |
| SpringDoc OpenAPI | 2.7.0 | API Documentation |

### Frontend

| Technology | Version | Purpose |
|------------|---------|---------|
| React | 19.2.0 | UI Library |
| TypeScript | 5.9.3 | Type Safety |
| Vite | 7.2.4 | Build Tool |
| React Router DOM | 7.11.0 | Routing |
| Axios | 1.13.2 | HTTP Client |
| Tailwind CSS | 3.4.19 | Styling |
| React Hook Form | 7.69.0 | Form Management |
| Sonner | 2.0.7 | Notifications |

---

## Features

| Feature | Description |
|---------|-------------|
| User Authentication | JWT-based login/register with BCrypt hashing |
| Project Management | Create, read, update, delete projects |
| Task Management | Full CRUD operations on tasks with due dates |
| Task Completion | Mark tasks as complete/incomplete |
| Progress Tracking | Real-time project progress calculation |
| Overdue Detection | Automatic overdue task identification |
| Search & Filter | Full-text search and filtering |
| Pagination | 9 projects per page, 10 tasks per page |
| API Documentation | Swagger UI |
| Docker Support | PostgreSQL containerization |
| Unit Tests | 85%+ code coverage |

---

## Prerequisites

| Software | Version | Check Command |
|----------|---------|---------------|
| Java JDK | 21+ | `java -version` |
| Maven | 3.9+ | `./mvnw -version` or `mvnw.cmd -version` |
| Node.js | 22+ | `node -v` |
| npm | 10+ | `npm -v` |
| PostgreSQL | 16+ | Docker or local installation |
| Docker | Latest | `docker --version` (optional) |

---

## Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/youssefessalihi/task-management-system.git
cd task-management-system
```

### 2. Start Database
```bash
cd task-management-backend
docker-compose up -d
```

### 3. Start Backend

**Linux/macOS:**
```bash
mvn spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

### 4. Start Frontend
```bash
cd task-management-frontend
npm install
npm run dev
```

### 5. Access Application

| Service | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

---

## Database Setup

### Option 1: Docker (Recommended)
```bash
cd task-management-backend
docker-compose up -d
```

**Configuration:**

| Setting | Value |
|---------|-------|
| Database | taskmanagement |
| Username | taskuser |
| Password | taskpass123 |
| Port | 5432 |

### Option 2: Manual PostgreSQL

**Linux:**
```bash
sudo apt update
sudo apt install postgresql
sudo systemctl start postgresql
sudo -u postgres psql
```

**Windows:**
Download from https://www.postgresql.org/download/windows/

**Create Database:**
```sql
CREATE DATABASE taskmanagement;
CREATE USER taskuser WITH ENCRYPTED PASSWORD 'taskpass123';
GRANT ALL PRIVILEGES ON DATABASE taskmanagement TO taskuser;
\c taskmanagement
GRANT ALL ON SCHEMA public TO taskuser;
\q
```

---

## Running Tests

**Linux/macOS:**
```bash
cd task-management-backend
mvn test
mvn test jacoco:report
xdg-open target/site/jacoco/index.html
```

**Windows:**
```cmd
cd task-management-backend
mvnw.cmd test
mvnw.cmd test jacoco:report
start target\site\jacoco\index.html
```

**Coverage:** 85%+

---

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/auth/register` | Register a new user | No |
| POST | `/api/v1/auth/login` | Login user | No |

### Projects

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/projects` | Get all projects | Yes |
| GET | `/api/v1/projects/{id}` | Get project by ID | Yes |
| POST | `/api/v1/projects` | Create project | Yes |
| PUT | `/api/v1/projects/{id}` | Update project | Yes |
| DELETE | `/api/v1/projects/{id}` | Delete project | Yes |
| GET | `/api/v1/projects/{id}/progress` | Get project progress | Yes |

### Tasks

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/projects/{projectId}/tasks` | Get all tasks | Yes |
| GET | `/api/v1/projects/{projectId}/tasks/{taskId}` | Get task by ID | Yes |
| POST | `/api/v1/projects/{projectId}/tasks` | Create task | Yes |
| PUT | `/api/v1/projects/{projectId}/tasks/{taskId}` | Update task | Yes |
| PATCH | `/api/v1/projects/{projectId}/tasks/{taskId}/complete` | Mark task as completed | Yes |
| DELETE | `/api/v1/projects/{projectId}/tasks/{taskId}` | Delete task | Yes |

---

## Project Structure
```
task-management-system/
├── task-management-backend/
│   ├── src/main/java/com/taskmanagement/
│   │   ├── domain/              # Entities & Repositories
│   │   ├── application/         # Services, DTO Requests & Responses
│   │   ├── presentation/        # Controllers (REST Endpoints)
│   │   └── infrastructure/      # Security, JWT, Config & Exceptions
│   ├── src/test/                # Unit & Integration tests
│   ├── docker-compose.yml       # Database orchestration
│   ├── pom.xml                  # Maven dependencies
│   └── mvnw / mvnw.cmd          # Maven wrapper
│
└── task-management-frontend/
    ├── src/
    │   ├── components/          # UI components (layout, projects, tasks, ui)
    │   ├── context/             # Authentication state
    │   ├── pages/               # Route-level views
    │   ├── services/            # Axios API client
    │   ├── hooks/               # Custom logic (Pagination)
    │   └── types/               # TypeScript interfaces
    ├── package.json             # Dependencies & Scripts
    └── vite.config.ts           # Build configuration
```

---

## Docker Commands

| Command | Description |
|---------|-------------|
| `docker-compose up -d` | Start container |
| `docker-compose down` | Stop container |
| `docker-compose logs -f` | View logs |
| `docker ps` | List containers |

---

## Troubleshooting

### Port Already in Use

**Linux:**
```bash
# Check port 8080
sudo lsof -i :8080
kill -9 <PID>

# Check port 5173
sudo lsof -i :5173
kill -9 <PID>
```

**Windows:**
```cmd
REM Check port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

REM Check port 5173
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

### Database Connection Failed
```bash
# Check PostgreSQL container
docker ps

# Restart container
docker-compose restart

# View logs
docker-compose logs postgres
```

### Build Fails

**Backend:**
```bash
./mvnw clean install
```

**Frontend:**
```bash
rm -rf node_modules package-lock.json
npm install
```

---

## License

This project was developed for educational purposes as part of an internship at Hahn Software Morocco.

---

**Developed by Youssef ESSALIHI | 2025**
