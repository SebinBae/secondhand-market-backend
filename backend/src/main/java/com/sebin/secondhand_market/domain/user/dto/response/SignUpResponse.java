package com.sebin.secondhand_market.domain.user.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {

  private String status;
  private String message;
  private String email;

}
