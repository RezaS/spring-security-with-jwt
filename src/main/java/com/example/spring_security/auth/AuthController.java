package com.example.spring_security.auth;

import com.example.spring_security.config.CustomUserDetails;
import com.example.spring_security.exception.Response;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome! You are logged in.";
    }

    @GetMapping("/home")
    public String home() {
        return "Home - Please register!";
    }

    /**
     * Registers a user
     * @param userDto A JSON that takes the keys "username" and "password"
     * @return A string
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User userDto) throws MessagingException {
        if (authService.registeredSuccessfully(userDto)) {
            Response response = Response.builder()
                    .code("USER_CREATED_SUCCESSFULLY")
                    .status(HttpStatus.CREATED)
                    .timestamp(LocalDateTime.now())
                    .messages(List.of("User registered successfully"))
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        Response response = Response.builder()
                .code("USER_ALREADY_EXISTS")
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .messages(List.of("User already exists"))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * User Login
     * @param user A JSON that takes the keys "username" and "password"
     * @return A token
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody CustomUserDetails user) {
        String token = authService.login(user);
        if (token == null) {
            Response response = Response.builder()
                    .code("LOGIN_FAILED")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .messages(List.of("User could not be logged in. Possibly reason could be that the account is inactive."))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        Response response = Response.builder()
                .code("LOGIN_SUCCESS")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .messages(List.of("User has logged in successfully."))
                .token(token)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Verifies user's account from a link that was sent by email
     * @param token a token in a GET parameter
     * @return Returns a token
     */
    @GetMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        if (authService.accountIsVerified(token)) {
            Response response = Response.builder()
                    .code("USER_VERIFIED_SUCCESSFULLY")
                    .status(HttpStatus.OK)
                    .timestamp(LocalDateTime.now())
                    .messages(List.of("Account has been verified successfully."))
                    .token(token)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        Response response = Response.builder()
                .code("USER_VERIFICATION_FAILED")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .messages(List.of("Token could not be validated, so account could not verified."))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}