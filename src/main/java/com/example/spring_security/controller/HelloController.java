package com.example.spring_security.controller;

import com.example.spring_security.config.CustomUserDetails;
import com.example.spring_security.config.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class HelloController {
    @Autowired
    private Jwt jwt;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserDetailsManager userDetailsManager;

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
    public ResponseEntity<String> register(@RequestBody com.example.spring_security.entity.User userDto) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
        }
        catch (UsernameNotFoundException e) {
            userDetails = null;
        }

        if (userDetails != null && userDetails.getUsername().equals(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username may not be empty or null");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UserDetails user = new CustomUserDetails(
                userDto.getUsername(),
                new BCryptPasswordEncoder().encode(userDto.getPassword()),
                authorities,
                false
        );
        userDetailsManager.createUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * User Login
     * @param user A JSON that takes the keys "username" and "password"
     * @return A token
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody CustomUserDetails user) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            if (userDetails == null || !passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
            }

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwt.generateToken(user);

            return ResponseEntity.ok(token);
        }
        catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }
}