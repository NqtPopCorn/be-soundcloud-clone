# Directory Structure

**Date:** 2026-05-10

## High-Level Layout
- `src/main/java/com/popcorn/SoundCloudClone/`: Root package
  - `config/`: Configuration classes (e.g., security, swagger)
  - `controller/`: REST API controllers (e.g., `track`, `album`, `PlaylistController`)
  - `domain/`: JPA Entities and core models
  - `exception/`: Global exception handlers and custom exception classes
  - `mapper/`: MapStruct interfaces for DTO transformations
  - `security/`: Spring Security and JWT configuration/filters
- `src/main/resources/`: Configuration files (`application.properties` or `application.yml`)
- `src/test/`: Test source files

## Key Locations
- **`pom.xml`**: Maven build and dependency configurations.
- **`SoundCloudCloneApplication.java`**: Application bootstrap class.
