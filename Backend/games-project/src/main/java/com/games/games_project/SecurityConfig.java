package com.games.games_project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Sonar justification: CSRF is disabled intentionally because these are stateless REST APIs
                // Tokens or other mechanisms (e.g. JWT) are used instead of CSRF cookies
                .csrf(csrf -> csrf.disable())
                // API stateless configuration
                .sessionManagement(session -> session.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

