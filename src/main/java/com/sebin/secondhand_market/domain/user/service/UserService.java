package com.sebin.secondhand_market.domain.user.service;

import com.sebin.secondhand_market.domain.user.dto.request.LoginRequest;
import com.sebin.secondhand_market.domain.user.dto.request.SignupRequest;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import com.sebin.secondhand_market.global.security.JwtProvider;
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
  private final PasswordEncoder passwordEncoder;

  // 회원 등록 로직
  public void signup(@Valid SignupRequest signupRequest) {
    if (userRepository.existsByEmail((signupRequest.getEmail()))) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    UserEntity userEntity = new UserEntity(
        signupRequest.getEmail(),
        passwordEncoder.encode(signupRequest.getPassword()),
        signupRequest.getNickname()
    );

    userRepository.save(userEntity);

  }


  // 로그인 로직
  public String login(LoginRequest loginRequest) {
    UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다!"));

    if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치 하지 않습니다!");
    }

    return jwtProvider.createToken(userEntity.getId());
  }
}
