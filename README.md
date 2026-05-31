# Spring Boot User Manager

**REST API for managing users, roles, and permissions — with API key generation and event-driven webhooks.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk)](https://openjdk.org/projects/jdk/25/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.3-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

---

## About

Building a user management system sounds simple — until you need roles, and permissions, and the ability to expose those events to external consumers. This project started as a hands-on sandbox to explore Spring Boot's ecosystem while reinforcing **Hexagonal Architecture** combined with **Vertical Slices**: each feature (users, roles, permissions, API keys, webhooks) is self-contained with its own domain, application, and infrastructure layers.

The core offering is straightforward — a RESTful API to manage **users**, **roles**, and **permissions** — but the differentiator is the **API key system** and the **event webhook**. Every user assigned to a specific role with the right permissions gets an API key that grants access to a webhook. That webhook streams real-time events triggered by the CRUD operations happening inside the system: user created, user deleted, role updated, permission revoked — all of it, pushed to authorized consumers based on what they're allowed to see.

> **Disclaimer:** This is a **personal learning project**. It's intentionally over-engineered in places to experiment with patterns, libraries, and architectural decisions — not a production-ready SaaS. Expect evolving structure, incremental refactors, and the occasional experiment.

### Key concepts

| Concept | Description |
|---------|-------------|
| **Users** | Core identity — username, email, password, active/deleted state |
| **Roles** | Grouping layer — admin, moderator, viewer, or custom roles |
| **Permissions** | Fine-grained actions — `user:create`, `user:read`, `webhook:consume`, etc. |
| **API Keys** | Auto-generated keys tied to a user + role + permission set |
| **Event Webhook** | Push-based endpoint that delivers CRUD events to authorized API key holders |

---

## Architecture

The project follows **Hexagonal Architecture** (ports & adapters) organised as **Vertical Slices** by feature:

```
┌──────────────────────────────────────────────────────────┐
│                    FEATURE / USERS                        │
│                                                          │
│  ┌──────────────────────────────────────────────────┐    │
│  │                 Domain Layer                      │    │
│  │  Entities  ·  ValueObjects  ·  Enums  ·  Ports   │    │
│  └──────────────────────────────────────────────────┘    │
│                           │                               │
│  ┌──────────────────────────────────────────────────┐    │
│  │             Application Layer                     │    │
│  │  UseCases  ·  Services  ·  DTOs  ·  Validators   │    │
│  └──────────────────────────────────────────────────┘    │
│                           │                               │
│  ┌──────────────────────────────────────────────────┐    │
│  │           Infrastructure Layer                    │    │
│  │  Repositories  ·  JPA  ·  Security  ·  Webhooks  │    │
│  └──────────────────────────────────────────────────┘    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

**Dependency rule:** each layer only knows the layer directly below it. The domain is completely framework-agnostic — zero Spring annotations, zero JPA annotations. Frameworks live in the infrastructure layer where they belong.

Each feature (vertical slice) stands on its own:

```
src/main/java/usermanagerapi/
├── features/
│   ├── users/               # User management — CRUD, activation, deletion
│   │   ├── domain/          #   User entity, enums, repository ports
│   │   └── infrastructure/  #   JPA adapter, controllers, security config
│   ├── roles/               # (planned) Role definitions and assignment
│   ├── permissions/         # (planned) Permission registry and role mapping
│   ├── apikeys/             # (planned) API key generation and validation
│   └── webhooks/            # (planned) Event emitter and webhook delivery
├── database/                # Migrations, seeders, DB config
├── exceptions/              # Global exception handling
└── SpringBootUserManagerApplication.java
```

> **Note:** Roles, permissions, API keys, and webhooks are planned features. The project is actively evolving.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Runtime** | Java 25 (OpenJDK) |
| **Framework** | Spring Boot 4.0.6 |
| **Web** | Spring Web MVC |
| **Database** | PostgreSQL 18.3 |
| **ORM** | Spring Data JPA (Hibernate) |
| **API docs** | SpringDoc OpenAPI (Swagger UI) |
| **Build** | Apache Maven (wrapper included) |
| **Containers** | Docker Compose (PostgreSQL + app) |

### Dependencies

| GroupId / Package | Purpose |
|-------------------|---------|
| `spring-boot-starter-data-jpa` | JPA / Hibernate ORM |
| `spring-boot-starter-webmvc` | REST controllers and middleware |
| `springdoc-openapi-starter-webmvc-ui` | OpenAPI 3.0 + Swagger UI |
| `spring-boot-docker-compose` | Auto-start Docker Compose in dev mode |
| `postgresql` | PostgreSQL JDBC driver |
| `lombok` | Boilerplate reduction |
| `spring-boot-starter-data-jpa-test` | Test slices for JPA |
| `spring-boot-starter-webmvc-test` | Test slices for web layer |

---

## Quick Start (end user)

### Prerequisites

- [Docker](https://www.docker.com/) + Docker Compose
- [Java 25 JDK](https://openjdk.org/projects/jdk/25/) (if running without containers)
- [Apache Maven](https://maven.apache.org/) (optional — the wrapper `mvnw` is included)

### Option A — Docker Compose (recommended)

```bash
# Clone the repository
git clone https://github.com/your-username/Spring-Boot-User-Manager.git
cd Spring-Boot-User-Manager

# Start PostgreSQL and the application
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Option B — Run manually

```bash
# 1. Start PostgreSQL
docker compose up postgres -d

# 2. Build and run the application
./mvnw spring-boot:run
```

### Explore the API

Once running, open the OpenAPI documentation in your browser:

```
http://localhost:8080/swagger-ui.html
```

SpringDoc OpenAPI provides an interactive Swagger UI where you can explore and test every endpoint.

---

## Developer Guide

### Prerequisites

| Tool | Version | Why |
|------|---------|-----|
| Java JDK | 25 | Required by the project (see `pom.xml`) |
| Docker + Compose | Latest | PostgreSQL container (optional if you have a local PG instance) |
| Maven | 3.9+ | Build and dependency management (`mvnw` included) |
| An IDE | — | IntelliJ IDEA, VS Code, or Eclipse with Spring tooling |

### Project structure

```
Spring-Boot-User-Manager/
├── src/
│   ├── main/
│   │   ├── java/usermanagerapi/
│   │   │   ├── SpringBootUserManagerApplication.java   # Entry point
│   │   │   ├── database/                                # DB config and migrations
│   │   │   ├── exceptions/                              # Global exception handler
│   │   │   └── features/                                # Vertical slices
│   │   │       ├── users/                               # User feature
│   │   │       │   ├── domain/
│   │   │       │   │   ├── entities/                    # Domain entities (POJOs)
│   │   │       │   │   ├── enums/                       # Business enums
│   │   │       │   │   └── interfaces/                  # Repository ports (contracts)
│   │   │       │   └── infrastructure/                  # Adapters (JPA, controllers, etc.)
│   │   │       ├── roles/                               # (planned)
│   │   │       ├── permissions/                         # (planned)
│   │   │       ├── apikeys/                             # (planned)
│   │   │       └── webhooks/                            # (planned)
│   │   └── resources/
│   │       ├── application.yaml                         # Spring Boot config
│   │       ├── static/                                  # Static assets
│   │       └── templates/                               # Server-side templates
│   └── test/
│       └── java/usermanagerapi/
│           └── SpringBootUserManagerApplicationTests.java
├── compose.yaml                                          # Docker Compose (PostgreSQL + app)
├── Dockerfile                                            # Multi-stage Docker image
├── pom.xml                                               # Maven project definition
├── mvnw / mvnw.cmd                                       # Maven wrapper scripts
└── compose.env                                           # PostgreSQL environment variables
```

### Running locally

```bash
# 1. Start the database (PostgreSQL)
docker compose up postgres -d

# 2. Run the application with dev tools
./mvnw spring-boot:run

# Or build and run the JAR
./mvnw clean package -DskipTests
java -jar target/Spring-Boot-User-Manager-0.0.1-SNAPSHOT.jar
```

### Running tests

```bash
./mvnw test
```

### Configuration

The project uses `application.yaml` for configuration. Sensitive values should be externalised via environment variables or a `.env` file (see `compose.env` for reference).

| Property | Description | Example |
|----------|-------------|---------|
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/mydatabase` |
| `spring.datasource.username` | Database user | `myuser` |
| `spring.datasource.password` | Database password | `secret` |
| `spring.jpa.hibernate.ddl-auto` | Schema generation strategy | `update` |

> **Note:** With `spring-boot-docker-compose` on the classpath, Docker Compose is started automatically in dev mode — no manual `docker compose up` needed when running via the IDE or Maven plugin.

### Docker

#### Building the image

```bash
docker build -t spring-boot-user-manager .
```

#### Running with Docker

```bash
docker compose up --build
```

The multi-stage `Dockerfile` builds the application with Maven in the first stage and runs the resulting JAR with a lightweight JRE in the second stage.

---

## Project status

This project is in **active early development**. The current implementation includes:

- ✅ Project skeleton with Spring Boot 4.0.6 + Java 25
- ✅ Hexagonal architecture with vertical slice layout
- ✅ Docker Compose setup (PostgreSQL + app)
- ✅ User entity and feature structure
- 🔲 Role management — *in progress*
- 🔲 Permission system — *planned*
- 🔲 API key generation — *planned*
- 🔲 Event webhook — *planned*
- 🔲 OpenAPI endpoint documentation — *pending implementation*

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<sub>Built with Spring Boot, Java, and a lot of curiosity.</sub>
