package com.popcorn.soundcloudclone.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(1, 404, "Not Found"),
    VALIDATION_ERROR(2, 400, "Validation error"),

    DUPLICATED_USER(100, 409, "User already exists"), // 409 Conflict hợp lý hơn cho lỗi trùng lặp
    USER_NOT_FOUND(102, 404, "User not found"),
    TRACK_NOT_FOUND(1021, 404, "Track not found"),
    ALBUM_NOT_FOUND(1022, 404, "Album not found"),
    PLAYLIST_NOT_FOUND(1023, 404, "Playlist not found"),

    // Các lỗi Validation thông tin User -> 400 Bad Request
    INVALID_USERNAME(103, 400, "Username is invalid"),
    INVALID_PASSWORD(104, 400, "Password is invalid, must be at least 6 characters"),
    INVALID_EMAIL(105, 400, "Email is invalid"),
    INVALID_FIRSTNAME(106, 400, "First name is invalid"),
    INVALID_LASTNAME(107, 400, "Last name is invalid"),
    INVALID_ROLE(108, 400, "Role must be ADMIN, USER, ARTIST"),
    ARTIST_UPGRADE_REQUEST_NOT_FOUND(109, 404, "Artist upgrade request not found"),
    ARTIST_UPGRADE_REQUEST_ALREADY_PENDING(110, 409, "Artist upgrade request is already pending"),
    ARTIST_UPGRADE_REQUEST_ALREADY_PROCESSED(111, 409, "Artist upgrade request is already processed"),
    ARTIST_UPGRADE_NOT_ALLOWED(112, 403, "Artist upgrade request is not allowed"),

    // Các lỗi Validation dữ liệu bài hát/upload -> 400 Bad Request
    INVALID_STATUS(200, 400, "Status is invalid"),
    INVALID_TRACK_NAME(201, 400, "Track name is invalid"),
    INVALID_UPLOAD(202, 400, "File upload is invalid"),

    // Riêng lỗi kích thước file có thể dùng 413 (Payload Too Large), nhưng 400 vẫn
    // chấp nhận được
    INVALID_UPLOAD_SIZE(203, 400, "File upload size is invalid, max: 100MB"),
    INVALID_DURATION(204, 400, "Duration is invalid"),
    INVALID_FILE_EXTENSION(205, 400, "File extension is invalid"), // Hoặc 415 (Unsupported Media Type)
    INVALID_TAGS(206, 400, "Tag list is invalid, min 1"),

    INVALID_PRIVACY(207, 400, "Privacy is invalid"),
    INVALID_METHOD(208, 400, "Method is invalid"),

    // Lỗi bảo mật/Quyền hạn
    BAD_REQUEST(300, 400, "Bad Request"),
    UNAUTHORIZED(401, 401, "Unauthorized"), // Chưa đăng nhập hoặc Token sai
    UNAUTHENTICATED(402, 401, "Unauthenticated"), // Tương tự Unauthorized
    FORBIDDEN(403, 403, "Forbidden, due to not allowed or inactive account"), // Đã đăng nhập nhưng không có quyền

    // Lỗi tài nguyên khác
    TAG_NOT_FOUND(800, 404, "Tag not Found"),
    DUPLICATED_CONSTRAINT(900, 409, "Duplicated Constraint"),
    ;

    private final int code;
    private final int httpStatusCode;
    private final String message;
}
