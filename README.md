# Audit Log Service

A Spring Boot 4.x application built with Java 17 and Maven. The project currently provides the base application structure for an audit log service assessment, including Spring Web MVC, validation, Actuator, and Spring Data JPA configuration.

## Stack

- Java 17
- Spring Boot 4.x
- Maven
- Spring Web MVC
- Spring Data JPA
- Validation
- Actuator
- PostgreSQL
- H2 database (configured for local/runtime support)

## Project layout

- `src/main/java/com/example/audit` - main application package
- `src/main/resources` - application configuration and resource files
- `src/test/java/com/example/audit` - test sources
- `pom.xml` - Maven project configuration and dependencies

## Run locally

```bash
./mvnw spring-boot:run
```

## Run tests

```bash
./mvnw test
```

## Notes

The repository currently contains the Spring Boot application bootstrap and initial project configuration. Functional API and persistence behavior are not yet implemented in this baseline state.