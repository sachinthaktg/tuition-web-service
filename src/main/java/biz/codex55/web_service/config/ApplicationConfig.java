package biz.codex55.web_service.config;

import biz.codex55.web_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    // 1. Tells Spring how to find the user in the database
    @Bean
    public UserDetailsService userDetailsService() {

        return username -> userRepository.findByUsername(username) // Ensure this matches your repository method
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
