package com.ecobank.ecounion.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

// Bean responsible for configuring security of our application
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private static final String[] AUTH_WHITELIST = {

            // for Swagger UI v2
            "/v2/api-docs",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",

            // for Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .requestMatchers("/projects/**", "/contributions/**", "/reports/**").authenticated()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/error").permitAll()
                                .anyRequest().permitAll()
                )

                // Handle exceptions with security filters
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, accessDeniedException) -> {
                            // Set the response headers
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            // Write the JSON response
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(String.format(
                                    "{\"apiPath\": \"%s\", \"errorCode\": %s, \"errorMessage\": \"%s\", \"errorTime\": \"%s\"}",
                                    request.getRequestURI(),
                                    HttpStatus.UNAUTHORIZED,
                                    accessDeniedException.getMessage(),
                                    LocalDateTime.now()
                            ));
                        })

                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            // Set the response headers
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            // Write the JSON response
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(String.format(
                                    "{\"apiPath\": \"%s\", \"errorCode\": %s, \"errorMessage\": \"%s\", \"errorTime\": \"%s\"}",
                                    request.getRequestURI(),
                                    HttpStatus.UNAUTHORIZED,
                                    accessDeniedException.getMessage(),
                                    LocalDateTime.now()
                            ));
                        }))
                )
                // Don't store session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Provide Authentication handler
                .authenticationProvider(authenticationProvider)

                // Add custom filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}