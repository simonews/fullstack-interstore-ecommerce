package com.interstore.interstore_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("Richiesta a: " + request.getMethod() + " " + request.getRequestURI());

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            System.out.println("Token ricevuto: " + token);

            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("Username estratto dal token: " + username);
            } catch (Exception e) {
                System.out.println("Errore estrazione username dal token: " + e.getMessage());
            }
        } else {
            System.out.println("⚠Nessun token trovato nell’header Authorization");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("UserDetails caricato da DB: " + userDetails.getUsername() +
                    " | Authorities: " + userDetails.getAuthorities());

            if (jwtUtil.validateToken(token)) {
                String role = jwtUtil.extractRole(token);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                System.out.println("Ruolo estratto dal token: " + role + " → Authority: " + authority);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, List.of(authority));

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("Authentication settata nel SecurityContext con authorities: " +
                        authToken.getAuthorities());
            } else {
                System.out.println("Token non valido");
            }
        } else {
            if (username != null) {
                System.out.println("⚠SecurityContext già popolato, skip filtro.");
            }
        }

        filterChain.doFilter(request, response);
    }
}
