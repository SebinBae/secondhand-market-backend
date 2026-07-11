package com.sebin.secondhand_market.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProvider {

  private static final long TOKEN_EXPIRED_TIME = 1000L * 60 * 20; // 유효 시간 : 20분!
  private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 14; // 유효 시간 : 14일!

  private static final String CLAIM_TYPE = "type";
  private static final String TYPE_ACCESS = "access";
  private static final String TYPE_REFRESH = "refresh";

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public String createToken(UUID userId) {
    return createToken(userId, TYPE_ACCESS, TOKEN_EXPIRED_TIME);
  }

  public String createRefreshToken(UUID userId) {
    return createToken(userId, TYPE_REFRESH, REFRESH_TOKEN_EXPIRED_TIME);
  }

  private String createToken(UUID userId, String type, long expiredTime) {

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + expiredTime);

    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setSubject(userId.toString())
        .claim(CLAIM_TYPE, type)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.HS256, this.secretKey)
        .compact();

  }

  public boolean validate(String token) {

    try {
      Jwts.parser().setSigningKey(this.secretKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isAccessToken(String token) {
    return TYPE_ACCESS.equals(getClaim(token).get(CLAIM_TYPE, String.class));
  }

  public boolean isRefreshToken(String token) {
    return TYPE_REFRESH.equals(getClaim(token).get(CLAIM_TYPE, String.class));
  }

  // 남은 유효 시간(ms) — 만료된 경우 0. Access Token 블랙리스트 TTL 산정에 사용
  public long getRemainingExpiration(String token) {
    long remaining = getClaim(token).getExpiration().getTime() - System.currentTimeMillis();
    return Math.max(remaining, 0);
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getClaim(token).getSubject());
  }

  public long getRefreshTokenExpiredTime() {
    return REFRESH_TOKEN_EXPIRED_TIME;
  }

  private Claims getClaim(String token) {
    return Jwts.parser()
        .setSigningKey(this.secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Authentication getAuthentication(String token) {
    UUID userId = getUserId(token);

    return new UsernamePasswordAuthenticationToken(userId.toString(),
        null,
        Collections.emptyList()
    );

  }

}
