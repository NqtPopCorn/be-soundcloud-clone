package com.popcorn.soundcloudclone.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Code < 100: loi chung chung
// > 100: loi service

@Getter @AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404, "Not Found"),
    VALIDATION_ERROR(99, "Validation error"),

    USER_EXISTS(100, "User already exists"),
    USER_NOT_FOUND(102, "User not found"),
    INVALID_USERNAME(103, "Username is invalid"),
    INVALID_PASSWORD(104, "Password is invalid, must be at least 6 characters"),
    INVALID_EMAIL(105, "Email is invalid"),
    INVALID_FIRSTNAME(106, "First name is invalid"),
    INVALID_LASTNAME(107, "Last name is invalid"),
    INVALID_ROLE(108, "Role must be ADMIN, USER, ARTIST"),

    INVALID_STATUS(200, "Status is invalid"),
    INVALID_TRACK_NAME(201, "Track name is invalid"),
    INVALID_UPLOAD(202, "File upload is invalid"),
    INVALID_UPLOAD_SIZE(203, "File upload size is invalid, max: 100MB"),
    INVALID_DURATION(204, "Duration is invalid"),
    INVALID_FILE_EXTENSION(205, "File extension is invalid"),
    INVALID_TAGS(206, "Tag list is invalid, min 1"),
    INVALID_PRIVACY(200, "Privacy is invalid"),


    UNAUTHORIZED(401, "Unauthorized"),
    UNAUTHENTICATED(402, "Unauthenticated"),

    TAG_NOT_FOUND(800, "Tag not Found"),
    ;

    private int code;
    private String message;
}
