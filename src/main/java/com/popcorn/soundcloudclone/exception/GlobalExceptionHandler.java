package com.popcorn.soundcloudclone.exception;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        ApiResponse body = ApiResponse.builder()
                .message(e.getMessage())
                .code(1101)
                .build();
        e.printStackTrace(System.err);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationException(AuthorizationDeniedException e) {
        ApiResponse body = ApiResponse.builder()
                .message(e.getMessage())
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .build();
        e.printStackTrace(System.err);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse body = ApiResponse.builder()
                .message(e.getMessage())
                .code(errorCode.getCode())
                .build();
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // handle vaidation errors
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        String message = errorCode.getMessage();
        try {
            errorCode = ErrorCode.valueOf(e.getFieldError().getDefaultMessage());
            message = errorCode.getMessage();
        } catch (IllegalArgumentException iae) {
            message = e.getFieldError().getDefaultMessage();
        }
        ApiResponse body = ApiResponse.builder()
                .message(message)
                .code(errorCode.getCode())
                .build();

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // validate enum error
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ApiResponse> handleInvalidFormatException(HttpMessageNotReadableException e) {
//        String message = ((InvalidFormatException)e.getCause()).get;
//        ApiResponse body = ApiResponse.builder()
//                .message(message)
//                .code(ErrorCode.VALIDATION_ERROR.getCode())
//                .build();
//
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }
}
