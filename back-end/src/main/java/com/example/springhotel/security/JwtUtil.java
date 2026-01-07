package com.example.springhotel.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * ✅ Générer la clé de signature à partir du secret
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * ✅ Générer un token JWT
     * @param email Email de l'utilisateur
     * @param userId ID de l'utilisateur
     * @param roles Liste des rôles de l'utilisateur
     * @return Token JWT signé
     */
    public String generateToken(String email, Long userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * ✅ Extraire l'email (subject) du token
     * @param token Token JWT
     * @return Email de l'utilisateur
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ✅ Extraire l'ID utilisateur du token
     * @param token Token JWT
     * @return ID de l'utilisateur
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * ✅ Extraire les rôles du token
     * @param token Token JWT
     * @return Liste des rôles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    /**
     * ✅ Extraire la date d'expiration du token
     * @param token Token JWT
     * @return Date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * ✅ Extraire une claim spécifique du token
     * @param token Token JWT
     * @param claimsResolver Fonction pour extraire la claim
     * @return Valeur de la claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * ✅ Extraire toutes les claims du token (VERSION 0.12.5)
     * @param token Token JWT
     * @return Toutes les claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * ✅ Vérifier si le token est expiré
     * @param token Token JWT
     * @return true si expiré, false sinon
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * ✅ Valider le token
     * @param token Token JWT
     * @param email Email de l'utilisateur
     * @return true si valide, false sinon
     */
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
}