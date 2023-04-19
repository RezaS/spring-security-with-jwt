package com.example.spring_security.auth;

import com.example.spring_security.config.CustomUserDetails;
import com.example.spring_security.config.Jwt;
import com.example.spring_security.exception.CustomAuthenticationException;
import com.example.spring_security.service.EmailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserDetailsManager userDetailsManager;
    private final Jwt jwt;
    private final EmailService emailService;
    private final HttpServletRequest request;

    public boolean registeredSuccessfully(User userDto) throws MessagingException {
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
            String token = jwt.generateToken(user);
            String domain = request.getRequestURL().toString();
            domain = domain.replace(request.getRequestURI(), "");
            emailService.sendEmail(userDto.getUsername(), "Please verify your account", "Please complete your registration and verify your account by clicking on the following link: " +
                    "<br><br><a href='" + domain + "/api/verify-account?token=" + token + "'>"+ domain + "/api/verify-account?token=" + token + "</a>");
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * A Login Service to retrieve a token
     * @param user a User
     * @return Returns a token
     */
    public String login(CustomUserDetails user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        if (!userDetails.isEnabled()) {
            return null;
        }

        if (!passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            throw new CustomAuthenticationException("Password is not correct");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwt.generateToken(user);
    }

    /**
     * Checks whether the token is valid
     * @param token a token that can be generated with jwt.generateToken()
     * @return Returns true, if token exists
     */
    public boolean accountIsVerified(String token) {
        String username;
        try {
            username = jwt.extractUsername(token);
        }
        catch (ExpiredJwtException e) {
            throw new CustomAuthenticationException("Token is invalid or has expired");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwt.isTokenValid(token, userDetails)) {
            User user = userRepository.findByUsername(username);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
