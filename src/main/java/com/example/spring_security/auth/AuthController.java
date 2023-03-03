package com.example.spring_security.auth;

import com.example.spring_security.config.CustomUserDetails;
import com.example.spring_security.config.Jwt;
import com.example.spring_security.exception.CustomAuthenticationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AuthController {
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
    public ResponseEntity<String> register(@Valid @RequestBody User userDto) {
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
    public ResponseEntity<Object> login(@Valid @RequestBody CustomUserDetails user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        if (userDetails == null || !passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            throw new CustomAuthenticationException("Password is not correct");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwt.generateToken(user);

        return ResponseEntity.ok(token);
    }

}