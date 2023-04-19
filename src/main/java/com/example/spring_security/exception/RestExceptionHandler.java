package com.example.spring_security.exception;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, PSQLException.class})
    private ResponseEntity<Object> handleIllegalArgumentException(Exception exception) {
        HttpStatus status = switch (exception.getClass().getName()) {
            case "IllegalArgumentException" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        List<String> errorList = new ArrayList<>();
        errorList.add(exception.getMessage());
        Response response = Response.builder()
                .code("INVALID_SQL")
                .status(status)
                .timestamp(LocalDateTime.now())
                .messages(errorList)
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}

