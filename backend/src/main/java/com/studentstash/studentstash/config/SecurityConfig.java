package com.studentstash.studentstash.config;

import com.studentstash.studentstash.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                // DISABLE CSRF
                .csrf(csrf -> csrf.disable())

                // DISABLE FORM LOGIN
                .formLogin(form -> form.disable())

                // DISABLE HTTP BASIC
                .httpBasic(httpBasic -> httpBasic.disable())

                // STATELESS SESSION
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // AUTHORIZE REQUESTS
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC AUTH APIS
                        .requestMatchers("/api/auth/**").permitAll()

                        // TEMP TEST API
                        .requestMatchers("/api/test").permitAll()

                        // ALL OTHER APIs NEED AUTH
                        .anyRequest().authenticated()
                )

                // ADD JWT FILTER
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}