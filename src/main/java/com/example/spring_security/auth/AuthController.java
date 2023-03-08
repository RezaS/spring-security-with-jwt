package com.example.spring_security.auth;

import com.example.spring_security.config.CustomUserDetails;
import com.example.spring_security.config.Jwt;
import com.example.spring_security.exception.CustomAuthenticationException;
import com.example.spring_security.exception.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final Jwt jwt;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserDetailsManager userDetailsManager;
    private final UserRepository userRepository;

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
    public ResponseEntity<?> register(@Valid @RequestBody User userDto) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UserDetails user = new CustomUserDetails(
                userDto.getUsername(),
                new BCryptPasswordEncoder().encode(userDto.getPassword()),
                authorities,
                false
        );

        // Check if user exists
        if (!userRepository.existsByUsername(userDto.getUsername())) {
            userDetailsManager.createUser(user);
            Response response = new Response("USER_CREATED_SUCCESSFULLY", HttpStatus.CREATED, LocalDateTime.now(), List.of("User registered successfully"));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        else {
            Response response = new Response("USER_ALREADY_EXISTS", HttpStatus.CONFLICT, LocalDateTime.now(), List.of("User already exists"));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * User Login
     * @param user A JSON that takes the keys "username" and "password"
     * @return A token
     */

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody CustomUserDetails user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        if (userDetails == null || !passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            throw new CustomAuthenticationException("Password is not correct");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, String> token = new HashMap<>();
        token.put("token", jwt.generateToken(user));

        return ResponseEntity.ok(token);
    }

}