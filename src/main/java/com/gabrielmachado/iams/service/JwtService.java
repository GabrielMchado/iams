package com.gabrielmachado.iams.service;

import com.gabrielmachado.iams.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String jwtSecret) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(UserModel userModel) {
        long agora = System.currentTimeMillis();
        long expiracao = agora + 60;

        return Jwts.builder()
                .subject(userModel.getEmail())
                .claim("id", userModel.getId().toString())
                .issuedAt(new Date(agora))
                .expiration(new Date(expiracao))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}