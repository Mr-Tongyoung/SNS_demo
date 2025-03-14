package com.example.junho.sns_demo.global.jwt;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class  JWTUtil {

  private final SecretKey secretKey;
  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
        .getPayload().get("username", String.class);
  }

  public Long getUserIdFromToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
        .getPayload().get("id", Long.class);
  }

  public String getRole(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
        .getPayload().get("role", String.class);
  }

  public Boolean isExpired(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
        .getPayload().getExpiration().before(new Date());
  }

  public String createJwt(Long userId, String username, String role, Long expiredMs) {

    return Jwts.builder()
        .claim("id", userId)
        .claim("username", username)
        .claim("role", role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiredMs))
        .signWith(secretKey)
        .compact();
  }
}
