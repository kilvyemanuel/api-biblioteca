package com.projetos.biblioteca.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expiracaoMs
    ) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(String email) {
        Date agora = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    public Instant extrairExpiracao(String token) {
        return lerClaims(token).getExpiration().toInstant();
    }

    public String extrairEmail(String token) {
        return lerClaims(token).getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            lerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims lerClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
