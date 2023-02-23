package com.example.spring_security.auth;

import com.example.spring_security.exception.ErrorResponse;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ErrorResponse> handlePSQLException() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), "User already exists");
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}