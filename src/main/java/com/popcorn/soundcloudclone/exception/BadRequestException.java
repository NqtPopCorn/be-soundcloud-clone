package com.popcorn.soundcloudclone.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private ErrorCode errorCode;

    public BadRequestException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }
}
