package com.fastshop.services;

import com.fastshop.dto.AuthResponseDTO;
import com.fastshop.entities.Role;
import com.fastshop.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    // CHAVE SECRETA: Injetada do application.properties
    @Value("${jwt.secret}")
    private String secret;

    // TEMPO DE EXPIRAÇÃO (em milissegundos): Injetado do application.properties
    @Value("${jwt.expiration}")
    private Long expirationTime; // Assume que o valor será lido como Long

    // Nome do emissor do token
    private static final String ISSUER = "FastShop-Auth-API";

    /**
     * Gera o JWT a partir do objeto User.
     * @param user O usuário autenticado.
     * @return AuthResponseDTO contendo o token JWT e detalhes de expiração.
     */
    public AuthResponseDTO generateToken(User user) {
        // 1. Definição do Assunto e Chave
        Instant now = Instant.now();
        Instant expiryDate = now.plus(expirationTime, ChronoUnit.MILLIS);

        // Converte a chave secreta de String para um formato SecretKey seguro
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Extrai as roles do usuário
        List<String> userRoles = user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());

        // 2. Geração do Token JWT (o core da função)
        String token = Jwts.builder()
                .issuer(ISSUER) // Emissor
                .subject(user.getUsername()) // Dono do Token (username)
                .claim("roles", userRoles) // Adiciona as roles como uma claim (informação extra)
                .issuedAt(Date.from(now)) // Data de emissão
                .expiration(Date.from(expiryDate)) // Data de expiração
                .signWith(key) // Assinatura com a chave secreta (algoritmo derivado da chave)
                .compact(); // Constrói a string final

        // 3. Monta e Retorna o DTO de Resposta
        return AuthResponseDTO.builder()
                .accessToken(token)
                .expiresIn(expirationTime / 1000) // Retorna o tempo em SEGUNDOS (para o frontend)
                .username(user.getUsername())
                .roles(userRoles)
                .build();
    }

    // --- Métodos auxiliares para validação/extracção ---
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Date expiration = claims.getExpiration();
            boolean valid = expiration != null && expiration.after(new Date());
            if (valid) {
                log.debug("JWT validate: subject={}, expiresAt={}", claims.getSubject(), expiration);
            } else {
                log.warn("JWT validate: token expired or missing expiration; subject={}, expiresAt={}", claims.getSubject(), expiration);
            }
            return valid;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT extract username failed: {}", e.getMessage());
            return null;
        }
    }
}