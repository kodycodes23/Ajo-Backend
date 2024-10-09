package com.ecobank.ecounion.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final SecurityAppConfig securityAppConfig;
    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, SecurityAppConfig securityAppConfig, JWTUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.securityAppConfig = securityAppConfig;
        this.jwtUtil = jwtUtil;


    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Get Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;

        // if Authorization header does not exist, then skip this filter
        // and continue to execute next filter class
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token
        jwtToken = authHeader.substring(7);
        String userEmail = jwtUtil.extractUsername(jwtToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Validate jwt token and update security context
            if (jwtUtil.isTokenValid(jwtToken, userDetails)) {

                // Create authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Add additional details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store the auth object in context holder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // end of the method, so go for next filter class
        filterChain.doFilter(request, response);
    }


}