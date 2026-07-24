package com.sebin.secondhand_market.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
  REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "토큰 재사용이 감지되어 세션이 종료되었습니다. 다시 로그인해주세요."),

  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
  PRODUCT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "상품 수정 권한이 없습니다."),

  IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (jpg, png, webp만 허용)"),
  IMAGE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 크기는 5MB를 초과할 수 없습니다."),
  IMAGE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "상품당 이미지는 최대 10장까지 등록할 수 있습니다."),
  IMAGE_STORAGE_NOT_CONFIGURED(HttpStatus.SERVICE_UNAVAILABLE, "이미지 저장소가 구성되지 않았습니다."),
  IMAGE_UPLOAD_FAILED(HttpStatus.BAD_GATEWAY, "이미지 업로드에 실패했습니다."),

  BUYER_NOT_FOUND(HttpStatus.NOT_FOUND, "판매자가 존재하지 않습니다."),
  INVALID_CHAT_TARGET(HttpStatus.BAD_REQUEST, "자신의 상품에는 채팅할 수 없습니다."),
  CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
  CHATROOM_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "채팅방 접근 권한이 없습니다."),
  SENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방 참가자를 찾을 수 없습니다."),

  UNAUTHENTICATED_STOMP_USER(HttpStatus.UNAUTHORIZED,"인증되지 않는 STOMP 사용자(Principal) 입니다.");

  private final HttpStatus status;
  private final String message;

}
