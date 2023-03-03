package com.example.spring_security.config;

import com.example.spring_security.auth.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.example.spring_security.auth.UserRepository;
import java.util.Collections;

@Component
@AllArgsConstructor
public class CustomUserService implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (null == user || ! user.getUsername().equals(username)) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }
        else {
            return new CustomUserDetails(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), true);
        }
    }

}
