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
        HttpStatus status;
        switch (exception.getClass().getName()) {
            case "IllegalArgumentException":
                status = HttpStatus.BAD_REQUEST;
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }
        List<String> errorList = new ArrayList<>();
        errorList.add(exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("INVALID_SQL", status, LocalDateTime.now(), errorList);
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
