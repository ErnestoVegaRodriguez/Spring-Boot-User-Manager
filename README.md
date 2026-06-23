# Spring Boot User Manager

**REST API for managing users, roles, permissions, and API keys — with asynchronous event delivery via RabbitMQ.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk)](https://openjdk.org/projects/jdk/25/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.3-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.3-FF6600?logo=rabbitmq)](https://www.rabbitmq.com/)

---

## About

Building a user management system sounds simple — until you need roles, permissions, API keys, and a way to expose those events to external consumers asynchronously. This project started as a hands-on sandbox to explore Spring Boot's ecosystem while reinforcing **Hexagonal Architecture**: each feature (users, roles, API keys, webhooks) has its own domain, application, and infrastructure layers that communicate through well-defined ports.

The core offering is a RESTful API to manage **users**, **roles**, and **API keys**. Every time a user is created, an event flows through an asynchronous pipeline: the `WebhookPublisher` sends an HTTP POST to the suscriptor endpoint, which validates idempotency via a unique `requestId`, persists the delivery attempt, enqueues it in **RabbitMQ**, and responds with `200 OK`. A `@RabbitListener` consumer eventually processes the message and marks it as delivered. This decouples the HTTP response from the actual processing and guarantees delivery even if the processor is temporarily unavailable.

> **Disclaimer:** This is a **personal learning project**. It's intentionally over-engineered in places to experiment with patterns, libraries, and architectural decisions — not a production-ready SaaS.

### Key concepts

| Concept | Description |
|---------|-------------|
| **Users** | Core identity — name, email, password, active/deleted state, role assignment |
| **Roles** | Grouping layer — admin, moderator, viewer, or custom roles with permissions |
| **Permissions** | Fine-grained actions — `user:create`, `role:assign`, etc. |
| **API Keys** | Auto-generated keys tied to a user for webhook authentication |
| **Event Webhook** | Asynchronous pipeline: HTTP POST → idempotency check → RabbitMQ → event processor |

---

## Architecture

The project follows **Hexagonal Architecture** (ports & adapters):

```
┌──────────────────────────────────────────────────────────┐
│                       DOMAIN                              │
│  Entities · Value Objects · Enums · Patterns (Result)    │
│  Zero framework dependencies                              │
└──────────────────────────┬───────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────┐
│                    APPLICATION                            │
│  Driver Ports (inbound) · Driven Ports (outbound)        │
│  Use Cases · Services · DTOs · Mappers                   │
└──────────────────────────┬───────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────┐
│                    ADAPTER                                │
│  JPA Repositories · REST Controllers · Security          │
│  RabbitMQ Config · Webhook Publisher/Receptor            │
└──────────────────────────────────────────────────────────┘
```

**Dependency rule:** each layer only knows the layer directly below it. The domain is completely framework-agnostic — zero Spring annotations, zero JPA annotations.

Project structure:

```
src/main/java/com/ernesto/usermanagerapi/
├── domain/
│   ├── entities/           # Role, User, ApiKey, DeliveryAttempt
│   ├── enums/              # DeliveryType, DeliveryStatus, Permission, Scope, ErrorCode
│   ├── exceptions/         # DomainException, NotFoundException, ValidationException
│   ├── patterns/           # Result, Delivery
│   └── values/             # Email, Password, Telephone
├── application/
│   ├── dto/                # Request/Response records
│   ├── mappers/            # DTO ↔ Entity mappers
│   ├── ports/
│   │   ├── drivers/        # Inbound ports (use case interfaces)
│   │   └── drivens/        # Outbound ports (repository interfaces, etc.)
│   ├── services/           # Application services (IdempotentDeliveryService)
│   └── usecases/           # Use case implementations (role/, user/)
├── adapter/
│   ├── authentication/     # ApiKey generator
│   ├── encoding/           # Password and API key hashing
│   ├── messaging/          # RabbitMQ config, WebhookPublisher, WebhookReceptor
│   ├── persistence/        # JPA schemas, repositories, mappers
│   ├── verification/       # Phone number parsing (libphonenumber)
│   └── web/                # Controllers, security, exception handler, AppConfig
└── SpringBootUserManagerApplication.java
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Runtime** | Java 25 (OpenJDK) |
| **Framework** | Spring Boot 4.0.6 |
| **Web** | Spring Web MVC |
| **Database** | PostgreSQL 18.3 |
| **ORM** | Spring Data JPA (Hibernate 7) |
| **Message broker** | RabbitMQ 4.3 |
| **API docs** | SpringDoc OpenAPI (Swagger UI) |
| **Build** | Apache Maven (wrapper included) |
| **Containers** | Docker Compose (PostgreSQL + RabbitMQ + app) |

### Dependencies

| GroupId / Package | Purpose |
|-------------------|---------|
| `spring-boot-starter-data-jpa` | JPA / Hibernate ORM |
| `spring-boot-starter-webmvc` | REST controllers and middleware |
| `spring-boot-starter-amqp` | RabbitMQ integration (RabbitTemplate, @RabbitListener) |
| `springdoc-openapi-starter-webmvc-ui` | OpenAPI 3.0 + Swagger UI |
| `spring-boot-docker-compose` | Auto-start Docker Compose in dev mode |
| `spring-boot-starter-security` | Security auto-configuration |
| `postgresql` | PostgreSQL JDBC driver |
| `lombok` | Boilerplate reduction |
| `libphonenumber` | E.164 phone number parsing and validation |

---

## Quick Start

### Prerequisites

- [Docker](https://www.docker.com/) + Docker Compose
- [Java 25 JDK](https://openjdk.org/projects/jdk/25/) (if running without containers)
- [Apache Maven](https://maven.apache.org/) (optional — the wrapper `mvnw` is included)

### Option A — Docker Compose (recommended)

```bash
git clone https://github.com/your-username/Spring-Boot-User-Manager.git
cd Spring-Boot-User-Manager

# Start PostgreSQL, RabbitMQ, and the application
docker compose up --build
```

The API will be available at `http://localhost:8080`.  
RabbitMQ management UI at `http://localhost:15672` (user `admin`, password `admin123`).

### Option B — Run manually

```bash
# 1. Start PostgreSQL and RabbitMQ
docker compose up spring-database spring-queue -d

# 2. Build and run the application
./mvnw spring-boot:run
```

### Explore the API

```
http://localhost:8080/swagger-ui.html
```

SpringDoc OpenAPI provides an interactive Swagger UI where you can explore and test every endpoint.

---

## Developer Guide

### Project structure

```
Spring-Boot-User-Manager/
├── src/
│   ├── main/
│   │   ├── java/com/ernesto/usermanagerapi/
│   │   │   ├── adapter/          # Infrastructure adapters
│   │   │   ├── application/      # Use cases, ports, DTOs, services
│   │   │   ├── domain/           # Entities, value objects, patterns, enums
│   │   │   └── SpringBootUserManagerApplication.java
│   │   └── resources/
│   │       ├── application.yaml  # Spring Boot config
│   │       └── secrets.yaml      # Sensitive values (ignored by git)
│   └── test/
│       └── java/...
├── compose.yaml          # Docker Compose (PostgreSQL + RabbitMQ + app)
├── Dockerfile            # Multi-stage Docker image
├── pom.xml               # Maven project definition
├── mvnw / mvnw.cmd       # Maven wrapper scripts
└── compose.env           # PostgreSQL environment variables
```

### Running locally

```bash
# 1. Start the database and message broker
docker compose up spring-database spring-queue -d

# 2. Run the application
./mvnw spring-boot:run
```

### Configuration

The project uses `application.yaml` for configuration. Sensitive values should be externalised via environment variables or `secrets.yaml`.

| Property | Description | Example |
|----------|-------------|---------|
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/mydatabase` |
| `spring.datasource.username` | Database user | `myuser` |
| `spring.datasource.password` | Database password | `secret` |
| `spring.rabbitmq.host` | RabbitMQ host | `localhost` (or `spring-queue` in Docker) |
| `spring.rabbitmq.port` | RabbitMQ AMQP port | `5672` |
| `webhook.suscriptor.url` | Subscriber endpoint URL | `http://localhost:8080/api/v1/webhook/delivery` |

### Webhook flow

```
Create user → WebhookPublisher (HTTP POST, @Async)
                   ↓
        SuscriptorController (/api/v1/webhook/delivery)
                   ↓
        IdempotentDeliveryService
          ├── check requestId (idempotency)
          ├── persist DeliveryAttempt (PENDING)
          └── publish to RabbitMQ exchange
                   ↓
        WebhookReceptor (@RabbitListener)
          ├── markDelivered()
          └── update database
```

---

## Docker

### Building the image

```bash
docker build -t spring-boot-user-manager .
```

### Running with Docker

```bash
docker compose up --build
```

The multi-stage `Dockerfile` builds the application with Maven in the first stage and runs the resulting JAR with a lightweight JRE in the second stage. The Compose file includes PostgreSQL (port 5432) and RabbitMQ with management UI (ports 5672 and 15672).

---

<sub>Built with Spring Boot, Java, RabbitMQ, and a lot of curiosity.</sub>
