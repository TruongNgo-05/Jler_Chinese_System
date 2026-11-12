package com.example.jler_chinese_system.security;

// Project Entities
import com.example.jler_chinese_system.entity.User;
import io.jsonwebtoken.Claims; // Thêm nếu bạn muốn thao tác sâu với Body
// JJWT Libraries
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
// Java Security & Util
import java.security.Key;
import java.util.Date;
// Spring Framework
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @org.springframework.beans.factory.annotation.Value("${app.jwt-secret}")
  private String SERCRET_KEY;

  private final long ACCESS_TOKEN_EXP = 15 * 60 * 1000; /// 15phuts
  private final long REFRESH_TOKEN_EXP = 7 * 24 * 60 * 60 * 1000; // 7 ngày

  @jakarta.annotation.PostConstruct
  public void validateSecret() {
    if (
      SERCRET_KEY.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32
    ) throw new IllegalArgumentException("JWT_SECRET cần ít nhất 32 bytes");
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(
      SERCRET_KEY.getBytes(java.nio.charset.StandardCharsets.UTF_8)
    );
  }

  public String generateAccessToken(User user) {
    return Jwts.builder()
      .setId(java.util.UUID.randomUUID().toString())
      .setSubject(user.getUserName())
      .claim("userId", user.getUserId())
      .claim("role", user.getRole().getRoleName())
      .claim("version", user.getTokenVersion())

      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  public String generateRefreshToken(User user) {
    return Jwts.builder()
      .setId(java.util.UUID.randomUUID().toString())
      .setSubject(user.getUserName())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  // ================= PARSE =================

  public String extractUsername(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  public boolean validateToken(String token, User user) {
    String username = extractUsername(token);
    return username.equals(user.getUserName()) && !isTokenExpired(token);
  }
}
