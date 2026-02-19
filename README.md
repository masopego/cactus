# Cactus API 🌵

Your AlmeríaTech community backend

Welcome to **Cactus**! 🚀 A RESTful API backend designed by María Soledad Peña Gómez for the **AlmeríaTech** community.
Manage meetups, attendances, feedback, and user authentication with a modern, scalable architecture.

<br>

**Cactus** is a backend API developed with **Kotlin + Spring Boot** that powers the Savia mobile application. It
provides a complete REST API for managing community events, user attendances, feedback system with ratings,
participation statistics, and secure authentication via Supabase integration.

## ▶️ Getting Started

It's time to power your tech community platform. Here's how to set it up:

### Prerequisites

Before running the backend, make sure you have:

- **Java 21** or higher installed
- **Docker & Docker Compose** (for PostgreSQL database)
- **Supabase account**: Authentication is handled through Supabase. Create a project on [Supabase](https://supabase.com)
  and obtain your credentials.

### Installation

1. **Clone the backend project**:
   ```bash
   git clone git@github.com:masopego/cactus.git
   cd cactus
   ```

2. **Configure environment variables**: Create or update the `application.yaml` file in `src/main/resources/`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/cactus_db
       username: cactus_user
       password: your_secure_password
   
   jwt:
     secret: your_jwt_secret_key_here
     expiration: 86400000  # 24 hours
   
   supabase:
     url: https://your-project.supabase.co
     anonKey: your_supabase_anon_key
   ```

3. **Start the PostgreSQL database** with Docker Compose:
   ```bash
   docker-compose up -d
   ```

4. **Build and run the application**:
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

5. **Access the API**: The server will start at `http://localhost:8080`

<br><br>

**Let the tech backend power begin! 🎷**

The API is ready to handle all your community events, user registrations, and feedback management.

## 🌵 Technologies Used

- **Kotlin**: Modern JVM language for concise and safe code
- **Spring Boot 3.5**: Production-ready framework for building REST APIs
- **Spring Security**: JWT-based authentication and authorization
- **Exposed ORM**: Type-safe SQL DSL for Kotlin
- **PostgreSQL**: Robust relational database for production use
- **Supabase**: Backend as a Service for user authentication integration
- **JWT (JSON Web Tokens)**: Stateless authentication mechanism
- **Testcontainers**: Integration testing with real database containers
- **MockK**: Mocking library for Kotlin unit tests
- **Dokka**: Documentation generation tool for Kotlin (KDoc)
- **Docker Compose**: Container orchestration for development environment
- **Gradle**: Build automation and dependency management

## 🐜 Functional Requirements

The API provides the following capabilities:

### Authentication & Users

- Users can authenticate via Supabase tokens and receive JWT tokens ✅
- Users can retrieve their profile information ✅
- Users can update their profile (nickname, avatar) ✅
- JWT tokens are validated on each protected endpoint ✅

### Meetups

- Users can view a list of all meetups ✅
- Users can view detailed information about a specific meetup ✅
- Meetups include venue and speaker information ✅

### Attendances

- Users can register interest in attending a meetup ✅
- Users can confirm their attendance to a meetup ✅
- Users can cancel unconfirmed attendances ✅
- Users can view their attendance statistics ✅
- Only confirmed attendances can't be deleted ✅

### Feedback

- Users can submit feedback for confirmed attendances ✅
- Feedback includes ratings (1-5 stars) and comments ✅
- Users can view their feedback history ✅
- Users can view feedback statistics and completion percentage ✅
- Duplicate feedback is prevented ✅

### Technical Requirements

- The API follows REST principles ✅
- All endpoints are secured except authentication ✅
- The code follows Clean Architecture principles ✅
- The code implements Domain-Driven Design patterns ✅
- All classes, interfaces, and methods are documented with KDoc ✅
- Comprehensive unit and integration tests ✅
- Database schema is automatically created on startup ✅
- Fixture data is loaded for development ✅

## 📁 Project Structure

```
cactus/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── es/masopego/cactus/
│   │   │       ├── attendances/         # Attendance management module
│   │   │       │   ├── application/     # Use cases & services
│   │   │       │   ├── domain/          # Entities & repositories (interfaces)
│   │   │       │   └── infrastructure/  # Repository implementations & HTTP
│   │   │       ├── auth/                # Authentication & authorization
│   │   │       │   ├── application/     # Auth use cases
│   │   │       │   ├── domain/          # User entity & interfaces
│   │   │       │   └── infrastructure/  # JWT, security config, Supabase
│   │   │       ├── feedback/            # Feedback & ratings module
│   │   │       │   ├── application/     # Feedback use cases
│   │   │       │   ├── domain/          # Feedback entity & repository
│   │   │       │   └── infrastructure/  # Repository & HTTP controllers
│   │   │       ├── meetups/             # Meetup management module
│   │   │       │   ├── application/     # Meetup services
│   │   │       │   ├── domain/          # Meetup entity & repository
│   │   │       │   └── infrastructure/  # Repository with complex JOINs
│   │   │       ├── speakers/            # Speaker information module
│   │   │       │   ├── domain/          # Speaker entity
│   │   │       │   └── persistence/     # Speaker data access
│   │   │       ├── venues/              # Venue/location module
│   │   │       │   ├── domain/          # Venue entity
│   │   │       │   └── infrastructure/  # Venue data access
│   │   │       └── shared/              # Shared infrastructure
│   │   │           └── infrastructure/  # Database config & fixtures
│   │   └── resources/
│   │       ├── application.yaml         # Application configuration
│   │       └── static/                  # Static resources
│   └── test/
│       └── kotlin/                      # Unit & integration tests
├── build.gradle.kts                     # Gradle configuration
├── compose.yaml                         # Docker Compose for PostgreSQL
└── README.md                            # This file
```

## 🏗️ Architecture

The project implements **Clean Architecture** with **Domain-Driven Design** organized by bounded contexts:

```
┌─────────────────────────────────────────────────────┐
│            Infrastructure Layer                      │
│   HTTP Controllers + Repositories + External APIs   │
├─────────────────────────────────────────────────────┤
│            Application Layer                         │
│   Use Cases + Services + DTOs                       │
├─────────────────────────────────────────────────────┤
│            Domain Layer                              │
│   Entities + Repository Interfaces + Business Logic │
└─────────────────────────────────────────────────────┘
```

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint                      | Description                     | Auth Required |
|--------|-------------------------------|---------------------------------|---------------|
| POST   | `/api/auth/validate-supabase` | Exchange Supabase token for JWT | No            |

### Meetup Endpoints

| Method | Endpoint           | Description      | Auth Required |
|--------|--------------------|------------------|---------------|
| GET    | `/api/meetups`     | Get all meetups  | Yes           |
| GET    | `/api/meetups/:id` | Get meetup by ID | Yes           |

### Attendance Endpoints

| Method | Endpoint                            | Description                    | Auth Required |
|--------|-------------------------------------|--------------------------------|---------------|
| POST   | `/api/meetups/:meetupId/attendance` | Register interest in meetup    | Yes           |
| POST   | `/api/meetups/:meetupId/confirm`    | Confirm attendance             | Yes           |
| DELETE | `/api/meetups/:meetupId/attendance` | Cancel unconfirmed attendance  | Yes           |
| GET    | `/api/attendances/stats`            | Get user attendance statistics | Yes           |

### Feedback Endpoints

| Method | Endpoint              | Description                  | Auth Required |
|--------|-----------------------|------------------------------|---------------|
| POST   | `/api/feedback`       | Submit feedback for meetup   | Yes           |
| GET    | `/api/feedback`       | Get user's feedback history  | Yes           |
| GET    | `/api/feedback/stats` | Get user feedback statistics | Yes           |

### User Endpoints

| Method | Endpoint             | Description         | Auth Required |
|--------|----------------------|---------------------|---------------|
| GET    | `/api/users/profile` | Get user profile    | Yes           |
| PUT    | `/api/users/profile` | Update user profile | Yes           |



### Generating Documentation

```bash
# Generate HTML documentation with Dokka
./gradlew dokkaHtml

# Open generated documentation
open build/dokka/html/index.html
```

## 🗄️ Database Schema

The application uses **PostgreSQL** with the following schema:

### Tables:

- **users**: User accounts (synced with Supabase)
- **venues**: Meetup locations
- **meetups**: Community events
- **speakers**: Event presenters
- **meetup_speakers**: Many-to-many relationship (meetups ↔ speakers)
- **attendances**: User event registrations
- **feedbacks**: User event reviews


## 🧑‍💻 Developer's Conclusions

This project has been an enriching experience in building a production-ready REST API with modern Kotlin and Spring
Boot. Key learnings include:

### Technical Achievements:

- **Clean Architecture Implementation**: Clear separation of concerns with domain, application, and infrastructure
  layers
- **Exposed ORM Mastery**: Type-safe SQL with complex JOINs and many-to-many relationships
- **Security Best Practices**: Stateless JWT authentication with Spring Security
- **Comprehensive Testing**: Unit and integration tests with Testcontainers
- **Professional Documentation**: 1000+ lines of KDoc documentation

### Challenges Overcome:

- **Many-to-Many Relationships**: Implementing efficient JOIN operations for meetups and speakers
- **Authentication Integration**: Hybrid Supabase + JWT authentication flow
- **N+1 Query Problem**: Documented performance trade-offs and optimization strategies
- **Transaction Management**: Ensuring data consistency across multiple entities
- **Test Container Setup**: Real database integration testing

### Best Practices Applied:

- **SOLID Principles**: Single responsibility, dependency inversion
- **Repository Pattern**: Domain-driven data access abstraction
- **Use Case Pattern**: Isolated business logic components
- **Defensive Programming**: mapNotNull and null safety throughout
- **Code Documentation**: Every interface, class, and method documented

The modular structure and clean architecture make it easy to add new features, maintain existing code, and onboard new
developers.



## 🔗 Related Projects

- **Savia Mobile App**: [https://github.com/masopego/savia](https://github.com/masopego/savia) - Kotlin Multiplatform
  mobile application
- **AlmeríaTech Community**: [https://almeriatech.es](https://almeriatech.es) - Official community website


---

**Developed with 💜 by María Soledad Peña Gómez for the AlmeríaTech community**

