# Artist Upgrade Request Design

## Goal

Prevent regular users from spamming track uploads by requiring an approved artist role before upload. Regular users can request an upgrade, and admins review those requests manually.

## Current Context

- Users already have a `User.Role` enum with `ADMIN`, `ARTIST`, and `USER`.
- Admins can update a user's role through the existing admin user authorization endpoint.
- `POST /tracks` currently requires only authentication, so any logged-in user can upload.
- `UpgradeRequestDto` exists, but there is no persisted request workflow yet.

## User-Facing Behavior

Regular users cannot upload tracks. Track creation is allowed only for users with role `ARTIST` or `ADMIN`.

Users with role `USER` can create one pending artist upgrade request. If a pending request already exists, the API rejects duplicate submissions. If the user is already an artist or admin, the API rejects the request because no upgrade is needed.

Admins can list pending upgrade requests and approve or reject them. Approval changes the requester role to `ARTIST`; rejection keeps the requester as `USER`.

## Data Model

Add an `ArtistUpgradeRequest` entity backed by `artist_upgrade_requests`.

Fields:

- `id`: generated primary key.
- `user`: requester, many-to-one to `User`, required.
- `status`: enum `PENDING`, `APPROVED`, `REJECTED`, required.
- `createdAt`: request creation timestamp.
- `reviewedAt`: nullable review timestamp.
- `reviewedBy`: nullable admin user who reviewed the request.
- `note`: nullable text field for admin rejection reason or review note.

There must be at most one `PENDING` request per user. This can be enforced in service logic and, if the database supports it cleanly, by an index or constraint.

## API Design

User endpoints under `/user/me`:

- `POST /user/me/artist-upgrade-request`
  - Requires authentication.
  - Creates a `PENDING` request for the current user.
  - Rejects if the current user is inactive, already `ARTIST` or `ADMIN`, or already has a `PENDING` request.

- `GET /user/me/artist-upgrade-request`
  - Requires authentication.
  - Returns the current user's latest request, or an empty/not-found response if no request exists.

Admin endpoints under `/users`:

- `GET /users/artist-upgrade-requests?status=PENDING`
  - Requires `ADMIN`.
  - Returns paged requests, filtered by status when provided.

- `PATCH /users/artist-upgrade-requests/{requestId}/approve`
  - Requires `ADMIN`.
  - Transitions a `PENDING` request to `APPROVED`.
  - Sets the requester role to `ARTIST`.
  - Stores `reviewedAt` and `reviewedBy`.

- `PATCH /users/artist-upgrade-requests/{requestId}/reject`
  - Requires `ADMIN`.
  - Transitions a `PENDING` request to `REJECTED`.
  - Keeps the requester role unchanged.
  - Stores `reviewedAt`, `reviewedBy`, and optional note.

## Authorization Rules

Track upload must be blocked for regular users at the controller or service boundary. The preferred rule is `hasAnyRole('ARTIST', 'ADMIN')` on `POST /tracks`, plus a service-level check if the existing security configuration still permits all requests globally.

Inactive users cannot create upgrade requests or upload tracks.

Admin review endpoints require `ADMIN`.

## Error Handling

Use existing `ApplicationException` and `ErrorCode` patterns.

Required error cases:

- Requester not found or inactive.
- Requester is already `ARTIST` or `ADMIN`.
- Requester already has a `PENDING` request.
- Request not found.
- Request is no longer `PENDING` when approve/reject is attempted.
- Regular user attempts track upload.

Add new error codes only where existing codes do not express the condition clearly.

## Testing Scope

Tests are required before implementation for these behaviors:

- A regular authenticated user cannot create a track.
- An artist can create a track.
- A regular user can create an artist upgrade request.
- A user cannot create a duplicate pending request.
- Admin approval marks the request approved and changes the requester role to `ARTIST`.
- Admin rejection marks the request rejected and does not change requester role.
- A processed request cannot be approved or rejected again.

## Out of Scope

- Public artist verification profile fields.
- File attachments or portfolio links in the request.
- Email or notification delivery.
- Automatic approval logic.
- Frontend UI changes, unless requested separately.
