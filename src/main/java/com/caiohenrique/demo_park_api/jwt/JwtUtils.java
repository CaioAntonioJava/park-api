package com.caiohenrique.demo_park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";

    private final SecretKey key;
    private final long expireDays;
    private final long expireHours;
    private final long expireMinutes;

    public JwtUtils(@Value("${jwt.secret-key}") String secretKey,
                    @Value("${jwt.expire-days}") long expireDays,
                    @Value("${jwt.expire-hours}") long expireHours,
                    @Value("${jwt.expire-minutes}") long expireMinutes) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expireDays = expireDays;
        this.expireHours = expireHours;
        this.expireMinutes = expireMinutes;
    }

    private Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(expireDays).plusHours(expireHours).plusMinutes(expireMinutes);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public JwtToken createToken(String username, String role) {
        Date issuedAt = new Date();
        Date limit = toExpireDate(issuedAt);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(limit)
                .claim("role", role)
                .signWith(key)
                .compact();
        return new JwtToken(token);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refactorToken(token))
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refactorToken(token));
            return true;
        } catch (JwtException exception) {
            log.warn("Token inválido ou expirado.");
        }
        return false;
    }

    private static String refactorToken(String token) {
        if (token.startsWith(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length());
        }
        return token;
    }
}
