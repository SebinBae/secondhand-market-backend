package com.sebin.secondhand_market.global.common;

import com.sebin.secondhand_market.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

  private final boolean success;
  private final String code;
  private final String message;
  private final T data;

  // 성공시에 반환되는 ApiResponse
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, null, null, data);
  }

  // 에러 응답
  public static ApiResponse<Void> error(ErrorCode errorCode) {
    return new ApiResponse<>(
        false,
        errorCode.name(),
        errorCode.getMessage(),
        null
    );

  }
}
