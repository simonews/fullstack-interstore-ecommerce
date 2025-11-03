package com.interstore.interstore_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "YjNkZmQ5M2VjNzViM2YyMTM5ZDdlYmJhNTI5Y2Y1ZGYzYmE3NjQ0ZjY4YjlkOTI2";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 ora

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }


    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }


    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); //se non va in eccezione è valido
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //Metodo per estrarre tutti i claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

