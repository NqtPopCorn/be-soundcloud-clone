package com.popcorn.soundcloudclone.common.exception;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        ApiResponse body = ApiResponse.builder()
                .message("Internal Server Error")
                .statusCode(500)
                .build();
        e.printStackTrace(System.err);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthorizationException(AuthorizationDeniedException e) {
        ApiResponse body = ApiResponse.builder()
                .message("Unauthorized")
                .statusCode(ErrorCode.UNAUTHORIZED.getCode())
                .build();
        log.error("Unauthorized access: {}", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ApiResponse> handleApplicationException(ApplicationException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse body = ApiResponse.builder()
                .message(e.getMessage())
                .statusCode(errorCode.getCode())
                .build();
        return new ResponseEntity<>(body, HttpStatus.valueOf(errorCode.getHttpStatusCode()));
    }

    // handle vaidation errors
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        String message;
        try {
            errorCode = ErrorCode.valueOf(e.getFieldError().getDefaultMessage());
            message = errorCode.getMessage();
        } catch (IllegalArgumentException iae) {
            message = e.getFieldError().getDefaultMessage();
        }
        ApiResponse body = ApiResponse.builder()
                .message(message)
                .statusCode(errorCode.getHttpStatusCode())
                .build();

        return new ResponseEntity<>(body, HttpStatusCode.valueOf(errorCode.getHttpStatusCode()));
    }

    // validate enum error
    // @ExceptionHandler(HttpMessageNotReadableException.class)
    // public ResponseEntity<ApiResponse>
    // handleInvalidFormatException(HttpMessageNotReadableException e) {
    // String message = ((InvalidFormatException)e.getCause()).get;
    // ApiResponse body = ApiResponse.builder()
    // .message(message)
    // .code(ErrorCode.VALIDATION_ERROR.getCode())
    // .build();
    //
    // return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    // }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingRequestParamException(MissingServletRequestParameterException e) {

        ApiResponse body = ApiResponse.builder()
                .message(e.getMessage())
                .statusCode(401)
                .result(e.getParameterName())
                .build();
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
