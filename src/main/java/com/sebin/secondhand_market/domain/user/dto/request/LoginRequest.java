package com.sebin.secondhand_market.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

  @Schema(description = "유저 ID", example = "test@test.com")
  @Email
  @NotBlank
  private String email;

  @Schema(description = "유저 비밀번호")
  @NotBlank
  private String password;
}
