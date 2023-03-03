package com.example.spring_security.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private HttpStatus status;
    @JsonFormat(pattern = "dd.MM.YYYY, HH:mm:ss")
    private LocalDateTime timestamp;
    private List<?> messages;
}
