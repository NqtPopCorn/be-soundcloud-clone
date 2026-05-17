package com.popcorn.soundcloudclone.common.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public ApplicationException(String message, ErrorCode code) {
        super(message);
        this.errorCode = code;
    }
}
