# Concerns & Known Issues

**Date:** 2026-05-10

## Technical Debt & TODOs
There are several pending `TODO` comments in the codebase that require attention:
- `src/main/java/com/popcorn/soundcloudclone/mapper/UserMapper.java`
- `src/main/java/com/popcorn/soundcloudclone/controller/track/TrackController.java`
- `src/main/java/com/popcorn/soundcloudclone/controller/track/CommentController.java`
- `src/main/java/com/popcorn/soundcloudclone/controller/PlaylistController.java`
- `src/main/java/com/popcorn/soundcloudclone/controller/GenreController.java`
- `src/main/java/com/popcorn/soundcloudclone/controller/album/AlbumController.java`

## Security
- JWT implementation is present but needs verification for proper token validation and expiration handling.
- OAuth2 and Redis dependencies are currently commented out in `pom.xml`, suggesting incomplete or deferred features.

## Architecture
- Missing visible service layer in initial inspection (might be grouped within another directory or needs consistent separation).
