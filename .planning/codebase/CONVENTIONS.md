# Coding Conventions

**Date:** 2026-05-10

## Framework Conventions
- **Spring Boot**: Standard Spring Boot annotations (`@RestController`, `@Service`, `@Entity`, etc.) are used for bean management and request routing.
- **Lombok**: Used to reduce boilerplate code (Getters, Setters, Constructors, Builders) in domain models and DTOs.
- **MapStruct**: Used for object mapping between Entities and DTOs. Interfaces are defined in the `mapper` package.
- **API Documentation**: OpenAPI (Swagger) annotations are expected via `springdoc-openapi`.

## Naming
- Packages are lowercase (`com.popcorn.soundcloudclone.controller.track`).
- Classes are PascalCase.
- Configuration classes reside in `config`.

## Error Handling
- A dedicated `exception` package indicates centralized error handling (likely using `@ControllerAdvice` or `@ExceptionHandler`).
