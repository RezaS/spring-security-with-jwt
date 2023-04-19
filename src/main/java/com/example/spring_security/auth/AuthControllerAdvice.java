package com.example.spring_security.auth;

import com.example.spring_security.exception.CustomAuthenticationException;
import com.example.spring_security.exception.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class AuthControllerAdvice extends ResponseEntityExceptionHandler {
    private static final String ERROR_CODE = "INVALID_CREDENTIALS";
    // Username exception
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleBadCredentialsException() {
        Response response = Response.builder()
                .code(ERROR_CODE)
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .messages(List.of("Username not found"))
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    // Password exception
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<Object> handleBadCredentialsException(Exception exception) {
        Response response = Response.builder()
                .code(ERROR_CODE)
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .messages(List.of(exception.getMessage()))
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}