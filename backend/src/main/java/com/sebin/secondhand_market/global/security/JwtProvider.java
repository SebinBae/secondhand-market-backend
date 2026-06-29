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

  private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 20; // 유효 시간 : 20분!

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public String createToken(UUID userId) {

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRED_TIME);

    return Jwts.builder()
        .setSubject(userId.toString())
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

  public UUID getUserId(String token) {
    return UUID.fromString(Jwts.parser().
        setSigningKey(this.secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody().getSubject());
  }

  public Authentication getAuthentication(String token) {
    UUID userId = getUserId(token);

    return new UsernamePasswordAuthenticationToken(userId.toString(),
        null,
        Collections.emptyList()
    );

  }

}
