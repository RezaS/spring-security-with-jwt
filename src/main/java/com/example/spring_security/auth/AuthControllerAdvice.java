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
        Response response = new Response(ERROR_CODE, HttpStatus.BAD_REQUEST, LocalDateTime.now(), List.of("Username not found"));
        return ResponseEntity.badRequest().body(response);
    }

    // Password exception
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<Object> handleBadCredentialsException(Exception exception) {
        Response response = new Response(ERROR_CODE, HttpStatus.BAD_REQUEST, LocalDateTime.now(), List.of(exception.getMessage()));
        return ResponseEntity.badRequest().body(response);
    }
}