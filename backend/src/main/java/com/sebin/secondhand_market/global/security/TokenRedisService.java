package com.sebin.secondhand_market.global.security;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenRedisService {

  private static final String REFRESH_TOKEN_PREFIX = "RT:";
  private static final String BLACKLIST_PREFIX = "BL:";
  private static final String BLACKLISTED = "1";

  private final StringRedisTemplate redisTemplate;
  private final JwtProvider jwtProvider;

  public void saveRefreshToken(UUID userId, String refreshToken) {
    redisTemplate.opsForValue().set(
        REFRESH_TOKEN_PREFIX + userId,
        refreshToken,
        Duration.ofMillis(jwtProvider.getRefreshTokenExpiredTime())
    );
  }

  public String getRefreshToken(UUID userId) {
    return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
  }

  public void deleteRefreshToken(UUID userId) {
    redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
  }

  public void blacklistAccessToken(String accessToken, long remainingMillis) {
    redisTemplate.opsForValue().set(
        BLACKLIST_PREFIX + accessToken,
        BLACKLISTED,
        Duration.ofMillis(remainingMillis)
    );
  }

  public boolean isAccessTokenBlacklisted(String accessToken) {
    return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
  }
}
