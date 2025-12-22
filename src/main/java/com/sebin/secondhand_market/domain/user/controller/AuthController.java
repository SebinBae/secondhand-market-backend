package com.sebin.secondhand_market.domain.user.controller;

import com.sebin.secondhand_market.domain.user.dto.request.LoginRequest;
import com.sebin.secondhand_market.domain.user.dto.request.SignupRequest;
import com.sebin.secondhand_market.domain.user.dto.response.TokenResponse;
import com.sebin.secondhand_market.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<Map<String, String>> signup(@RequestBody @Valid SignupRequest signupRequest) {
    userService.signup(signupRequest);
    return ResponseEntity.ok(Map.of("status", "200", "message", "회원가입 성공!", "email", signupRequest.getEmail()));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
    String token = userService.login(loginRequest);
    return ResponseEntity.ok(new TokenResponse(token));
  }
}
