package com.sebin.secondhand_market.domain.user.controller;

import com.sebin.secondhand_market.domain.user.dto.request.LoginRequest;
import com.sebin.secondhand_market.domain.user.dto.request.SignupRequest;
import com.sebin.secondhand_market.domain.user.dto.response.SignUpResponse;
import com.sebin.secondhand_market.domain.user.dto.response.TokenPair;
import com.sebin.secondhand_market.domain.user.dto.response.TokenResponse;
import com.sebin.secondhand_market.domain.user.service.UserService;
import com.sebin.secondhand_market.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
  private static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(14);

  private final UserService userService;

  @Operation(summary = "유저 회원가입")
  @PostMapping("/signup")
  public ResponseEntity<SignUpResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
    userService.signup(signupRequest);

    SignUpResponse response = new SignUpResponse(
        "201",
        "회원 가입이 성공적으로 동작했습니다!",
        signupRequest.getEmail()
    );

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "유저 로그인")
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
    TokenPair tokenPair = userService.login(loginRequest);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(tokenPair.getRefreshToken()).toString())
        .body(new TokenResponse(tokenPair.getAccessToken()));
  }

  @Operation(summary = "Access Token 재발급 (Refresh Token 회전)")
  @PostMapping("/reissue")
  public ResponseEntity<TokenResponse> reissue(
      @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
    TokenPair tokenPair = userService.reissue(refreshToken);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(tokenPair.getRefreshToken()).toString())
        .body(new TokenResponse(tokenPair.getAccessToken()));
  }

  @Operation(summary = "로그아웃")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    String accessToken = (String) authentication.getCredentials();

    userService.logout(principal.getUserId(), accessToken);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, expireRefreshCookie().toString())
        .build();
  }

  private ResponseCookie buildRefreshCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
        .httpOnly(true)
        .path("/api/auth")
        .maxAge(REFRESH_TOKEN_MAX_AGE)
        .sameSite("Lax")
        .build();
  }

  private ResponseCookie expireRefreshCookie() {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
        .httpOnly(true)
        .path("/api/auth")
        .maxAge(Duration.ZERO)
        .sameSite("Lax")
        .build();
  }
}
