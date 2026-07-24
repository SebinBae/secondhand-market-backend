package com.sebin.secondhand_market.domain.user.service;

import com.sebin.secondhand_market.domain.user.dto.request.LoginRequest;
import com.sebin.secondhand_market.domain.user.dto.request.SignupRequest;
import com.sebin.secondhand_market.domain.user.dto.response.TokenPair;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import com.sebin.secondhand_market.domain.user.exception.EmailAlreadyExistsException;
import com.sebin.secondhand_market.domain.user.exception.InvalidPasswordException;
import com.sebin.secondhand_market.domain.user.exception.InvalidRefreshTokenException;
import com.sebin.secondhand_market.domain.user.exception.RefreshTokenReusedException;
import com.sebin.secondhand_market.domain.user.exception.UserNotFoundException;
import com.sebin.secondhand_market.global.security.JwtProvider;
import com.sebin.secondhand_market.global.security.TokenRedisService;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  private final TokenRedisService tokenRedisService;
  private final PasswordEncoder passwordEncoder;

  // 타 도메인 공개 조회 창구 — 사용자 단건 조회(not-found 판단은 호출 도메인에 위임)
  @Transactional(readOnly = true)
  public Optional<UserEntity> findById(UUID userId) {
    return userRepository.findById(userId);
  }

  // 회원 등록 로직
  public void signup(@Valid SignupRequest signupRequest) {
    if (userRepository.existsByEmail((signupRequest.getEmail()))) {
      throw new EmailAlreadyExistsException();
    }

    UserEntity userEntity = new UserEntity(
        signupRequest.getEmail(),
        passwordEncoder.encode(signupRequest.getPassword()),
        signupRequest.getNickname()
    );
    userRepository.save(userEntity);
  }


  // 로그인 로직
  public TokenPair login(LoginRequest loginRequest) {
    UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(UserNotFoundException::new);

    if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
      throw new InvalidPasswordException();
    }

    return issueTokenPair(userEntity.getId());
  }

  // Refresh Token 회전 — 재사용(탈취)이 감지되면 세션 전체를 무효화
  public TokenPair reissue(String refreshToken) {
    if (refreshToken == null || !jwtProvider.validate(refreshToken)
        || !jwtProvider.isRefreshToken(refreshToken)) {
      throw new InvalidRefreshTokenException();
    }

    UUID userId = jwtProvider.getUserId(refreshToken);
    userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    String savedRefreshToken = tokenRedisService.getRefreshToken(userId);
    if (savedRefreshToken == null) {
      throw new InvalidRefreshTokenException();
    }
    if (!savedRefreshToken.equals(refreshToken)) {
      tokenRedisService.deleteRefreshToken(userId);
      throw new RefreshTokenReusedException();
    }

    return issueTokenPair(userId);
  }

  // 로그아웃 — Refresh Token 삭제 + 현재 Access Token 블랙리스트 등록
  public void logout(UUID userId, String accessToken) {
    tokenRedisService.deleteRefreshToken(userId);
    tokenRedisService.blacklistAccessToken(accessToken, jwtProvider.getRemainingExpiration(accessToken));
  }

  private TokenPair issueTokenPair(UUID userId) {
    String accessToken = jwtProvider.createToken(userId);
    String refreshToken = jwtProvider.createRefreshToken(userId);
    tokenRedisService.saveRefreshToken(userId, refreshToken);
    return new TokenPair(accessToken, refreshToken);
  }
}
