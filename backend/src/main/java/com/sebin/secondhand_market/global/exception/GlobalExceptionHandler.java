package com.sebin.secondhand_market.global.exception;

import com.sebin.secondhand_market.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(ApiResponse.error(e.getErrorCode()));
  }

  // 서블릿 multipart 한도 초과 시에도 일관된 ApiResponse로 응답 (앱 레벨 5MB 검증의 안전망)
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
    return ResponseEntity.status(ErrorCode.IMAGE_TOO_LARGE.getStatus())
        .body(ApiResponse.error(ErrorCode.IMAGE_TOO_LARGE));
  }
}
