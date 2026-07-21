package com.sebin.secondhand_market.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 인증 플로우(회원가입 → 로그인 → 재발급/회전 → 재사용 탐지 → 로그아웃) 통합 테스트.
 * docs/WORK_UNITS.md Phase 1 "User/Auth module" 완료 기준(인증 플로우 통합 테스트 통과)을 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties =
    "spring.jwt.secret=sKsge6S00rQEM/Ucm3OxoGupWsr4A7spJOg71dXEoZNsBW+hpt1dUXJaCXea8hl2")
class AuthFlowIntegrationTest {

  private static final String REFRESH_TOKEN_PREFIX = "RT:";
  private static final String BLACKLIST_PREFIX = "BL:";
  private static final String TEST_PASSWORD = "test-password-123";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StringRedisTemplate redisTemplate;

  private UUID createdUserId;

  @AfterEach
  void cleanUp() {
    if (createdUserId != null) {
      userRepository.deleteById(createdUserId);
      redisTemplate.delete(REFRESH_TOKEN_PREFIX + createdUserId);
      createdUserId = null;
    }
  }

  @Test
  void signupThenLogin_returnsAccessTokenAndSetsRefreshCookie() throws Exception {
    String email = uniqueEmail();

    signup(email, TEST_PASSWORD, "테스트유저");

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson(email, TEST_PASSWORD)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(cookie().exists("refreshToken"))
        .andReturn();

    rememberCreatedUser(email);

    Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");
    assertThat(refreshCookie).isNotNull();
    assertThat(refreshCookie.isHttpOnly()).isTrue();
    assertThat(accessTokenOf(loginResult)).isNotBlank();

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson(email, "wrong-password")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void reissue_rotatesRefreshTokenAndRejectsInvalidToken() throws Exception {
    String email = uniqueEmail();
    signup(email, TEST_PASSWORD, "테스트유저");
    MvcResult loginResult = login(email);
    rememberCreatedUser(email);

    String originalAccessToken = accessTokenOf(loginResult);
    Cookie originalRefreshCookie = loginResult.getResponse().getCookie("refreshToken");

    MvcResult reissueResult = mockMvc.perform(post("/api/auth/reissue")
            .cookie(originalRefreshCookie))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("refreshToken"))
        .andReturn();

    String rotatedAccessToken = accessTokenOf(reissueResult);
    Cookie rotatedRefreshCookie = reissueResult.getResponse().getCookie("refreshToken");

    assertThat(rotatedAccessToken).isNotEqualTo(originalAccessToken);
    assertThat(rotatedRefreshCookie.getValue()).isNotEqualTo(originalRefreshCookie.getValue());

    mockMvc.perform(post("/api/auth/reissue")
            .cookie(new Cookie("refreshToken", "invalid-token")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void reusingRotatedRefreshToken_isDetectedAndInvalidatesWholeSession() throws Exception {
    String email = uniqueEmail();
    signup(email, TEST_PASSWORD, "테스트유저");
    MvcResult loginResult = login(email);
    rememberCreatedUser(email);

    Cookie staleRefreshCookie = loginResult.getResponse().getCookie("refreshToken");

    MvcResult reissueResult = mockMvc.perform(post("/api/auth/reissue")
            .cookie(staleRefreshCookie))
        .andExpect(status().isOk())
        .andReturn();
    Cookie latestRefreshCookie = reissueResult.getResponse().getCookie("refreshToken");

    // 이미 회전되어 무효화된 이전 refreshToken을 재사용 -> 탈취로 간주, 401
    mockMvc.perform(post("/api/auth/reissue")
            .cookie(staleRefreshCookie))
        .andExpect(status().isUnauthorized());

    // 재사용 탐지로 세션 전체가 무효화되어, 직전까지 유효했던 최신 refreshToken도 더 이상 통하지 않아야 함
    mockMvc.perform(post("/api/auth/reissue")
            .cookie(latestRefreshCookie))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void logout_blacklistsAccessTokenAndClearsRefreshToken() throws Exception {
    String email = uniqueEmail();
    signup(email, TEST_PASSWORD, "테스트유저");
    MvcResult loginResult = login(email);
    rememberCreatedUser(email);

    String accessToken = accessTokenOf(loginResult);
    Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");

    mockMvc.perform(post("/api/auth/logout")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(cookie().maxAge("refreshToken", 0));

    // 블랙리스트에 등록된 accessToken으로는 더 이상 인증된 요청을 보낼 수 없어야 함
    mockMvc.perform(post("/api/auth/logout")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized());

    // 로그아웃 시 삭제된 refreshToken으로는 재발급이 불가능해야 함
    mockMvc.perform(post("/api/auth/reissue")
            .cookie(refreshCookie))
        .andExpect(status().isUnauthorized());
  }

  private void signup(String email, String password, String nickname) throws Exception {
    mockMvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(signupJson(email, password, nickname)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(email));
  }

  private MvcResult login(String email) throws Exception {
    return mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson(email, TEST_PASSWORD)))
        .andExpect(status().isOk())
        .andReturn();
  }

  private void rememberCreatedUser(String email) {
    createdUserId = userRepository.findByEmail(email)
        .map(UserEntity::getId)
        .orElseThrow();
  }

  private String accessTokenOf(MvcResult result) throws Exception {
    return objectMapper.readTree(result.getResponse().getContentAsString())
        .get("accessToken").asText();
  }

  private String uniqueEmail() {
    return "auth-flow-test-" + UUID.randomUUID() + "@test.com";
  }

  private String signupJson(String email, String password, String nickname) {
    return """
        {"email":"%s","password":"%s","nickname":"%s"}
        """.formatted(email, password, nickname);
  }

  private String loginJson(String email, String password) {
    return """
        {"email":"%s","password":"%s"}
        """.formatted(email, password);
  }
}
