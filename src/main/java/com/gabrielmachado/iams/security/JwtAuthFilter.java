package com.gabrielmachado.iams.security;

import com.gabrielmachado.iams.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println("Header recebido: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = jwtService.extractEmail(token);
                System.out.println("Token validado, email: " + email);
                var authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                System.out.println("Falha ao validar token: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}