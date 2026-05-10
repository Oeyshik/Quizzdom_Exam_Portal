# Quizzdom — Online Exam Portal

<div align="center">

![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)

**A full-stack online quiz and examination platform built with Angular and Spring Boot.**

[🚀 Live Demo](https://quizzdom-exam-portal.vercel.app)

</div>

---

## 📌 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Frontend Setup](#frontend-setup)
  - [Backend Setup](#backend-setup)
  - [Environment Variables](#environment-variables)
- [Deployment](#deployment)
- [Screenshots](#screenshots)
- [License](#license)

---

## Overview

Quizzdom is a full-stack online examination portal where users can register, select a subject, attempt timed quizzes, and instantly view their results with detailed breakdowns. Admins can manage quizzes, categories, and questions through a dedicated panel.

---

## Features

- 🔐 **User Authentication** — Secure signup and login with JWT-based authorization
- 📚 **Subject Selection** — Browse and choose from multiple quiz categories
- 📝 **Quiz Examination** — Timed MCQ-based quiz interface with smooth navigation
- 📊 **Results & Analytics** — Instant score display with per-question result breakdown
- 🗂️ **Results History** — View a list of all past quiz attempts from the dashboard
- 👤 **User Dashboard** — Personalized dashboard showing progress and activity
- 🛡️ **Admin Panel** — Create, update, and delete quizzes, categories, and questions
- 🔔 **SweetAlert2 Notifications** — Clean, user-friendly alerts and confirmations

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Frontend    | Angular 16+, TypeScript, HTML, CSS  |
| Backend     | Java, Spring Boot, Spring Security  |
| Database    | Supabase (PostgreSQL)               |
| Frontend Hosting | Vercel                         |
| Backend Hosting  | Render                         |

---

## Project Structure

```
Quizzdom_Exam_Portal/
├── examfront/                  # Angular frontend
│   └── src/
│       └── app/
│           └── pages/
│               ├── signup/
│               ├── user-dashboard/
│               ├── quiz-subject-selection/
│               ├── quiz-exam/
│               ├── quiz-result-detail/
│               └── quiz-results-list/
├── examserver/                 # Spring Boot backend
│   └── src/main/java/
│       └── com/exam/examserver/
├── vercel.json                 # Vercel SPA routing config
└── .gitignore
```

---

## Getting Started

### Prerequisites

- Node.js >= 18 and npm
- Angular CLI: `npm install -g @angular/cli`
- Java 17+
- Maven
- A [Supabase](https://supabase.com) project (PostgreSQL database)

### Frontend Setup

```bash
cd examfront
npm install
ng serve
```

The app will run at `http://localhost:4200`.

### Backend Setup

```bash
cd examserver
./mvnw spring-boot:run
```

The API will run at `http://localhost:8080`.

### Environment Variables

**Frontend** — create `examfront/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

**Backend** — configure `examserver/src/main/resources/application.properties`:

```properties
spring.datasource.url=<your_supabase_postgres_url>
spring.datasource.username=<your_supabase_username>
spring.datasource.password=<your_supabase_password>
jwt.secret=<your_jwt_secret>
```

---

## Deployment

| Service    | Purpose           | Configuration                                          |
|------------|-------------------|--------------------------------------------------------|
| **Vercel** | Angular frontend  | Root: `examfront/`, Build: `ng build --configuration production` |
| **Render** | Spring Boot API   | Root: `examserver/`, Build: `./mvnw package`, Start: `java -jar target/*.jar` |
| **Supabase** | PostgreSQL DB   | Connect via Supabase JDBC URL in `application.properties` |

The `vercel.json` at the repo root handles SPA routing:

```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```

> **Note:** If you encounter CSS budget errors during the Vercel build, increase the `anyComponentStyle` budget in `examfront/angular.json` under `projects > examfront > architect > build > configurations > production > budgets`.

---

## Screenshots

> *(Add screenshots of the dashboard, quiz screen, and results page here)*

---

## License

This project is open source and available under the [MIT License](LICENSE).