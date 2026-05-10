# Architecture

**Date:** 2026-05-10

## Architectural Pattern
The application follows a standard Spring Boot tiered architecture (MVC / Layered Architecture).
- **Controllers** (Presentation Layer)
- **Services** (Business Logic Layer) - presumed based on standard structure
- **Repositories** (Data Access Layer) - via Spring Data JPA
- **Domain/Entities** (Data Models)
- **Mappers** (DTO to Entity mapping using MapStruct)

## Data Flow
1. Client request enters through a `@RestController` in the `controller` package.
2. Request payload is validated (via `spring-boot-starter-validation`).
3. Request is likely passed to a Service layer component.
4. Data is fetched or persisted using Spring Data JPA Repositories.
5. `MapStruct` mappers transform between Entities and DTOs.
6. Response is sent back to the client.

## Entry Points
- `src/main/java/com/popcorn/SoundCloudClone/SoundCloudCloneApplication.java`: Main Spring Boot entry point.
