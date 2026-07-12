package com.microfinance.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiry-ms:900000}")
    private long accessTokenExpiryMs;

    @Value("${jwt.refresh-token-expiry-ms:604800000}")
    private long refreshTokenExpiryMs;

    @Value("${spring.application.name:microfinance}")
    private String issuer;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId, String phone, List<String> roles,
                                       Map<String, Object> extra) {
        Date now = new Date();
        JwtBuilder b = Jwts.builder().subject(userId).issuer(issuer)
                .issuedAt(now).expiration(new Date(now.getTime() + accessTokenExpiryMs))
                .claim("phone", phone).claim("roles", roles).claim("type", "ACCESS");
        if (extra != null) extra.forEach(b::claim);
        return b.signWith(key()).compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        return Jwts.builder().subject(userId).issuer(issuer)
                .issuedAt(now).expiration(new Date(now.getTime() + refreshTokenExpiryMs))
                .claim("type", "REFRESH").signWith(key()).compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    public String extractUserId(String token) { return extractAllClaims(token).getSubject(); }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    public boolean validateToken(String token) {
        try { extractAllClaims(token); return true; }
        catch (ExpiredJwtException e) { log.warn("JWT expired"); }
        catch (Exception e) { log.warn("JWT invalid: {}", e.getMessage()); }
        return false;
    }

    public long getAccessTokenExpiryMs() { return accessTokenExpiryMs; }
}
