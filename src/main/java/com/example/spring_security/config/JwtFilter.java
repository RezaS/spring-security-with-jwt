package com.example.spring_security.config;

import com.example.spring_security.exception.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@AllArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final CustomUserService customUserService;
    private Jwt jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;


        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            // Check if token is valid
            try {
                jwt.isTokenValid(token, customUserService.loadUserByUsername(jwt.extractUsername(token)));
            }
            catch (Exception e) {
                Response responseObj = Response.builder()
                        .code("UNAUTHORIZED")
                        .status(HttpStatus.UNAUTHORIZED)
                        .timestamp(LocalDateTime.now())
                        .messages(Collections.singletonList("Invalid token"))
                        .build();
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                String json = mapper.writeValueAsString(responseObj);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(json);
                return;
            }
            username = jwt.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserService.loadUserByUsername(username);

            if (jwt.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}