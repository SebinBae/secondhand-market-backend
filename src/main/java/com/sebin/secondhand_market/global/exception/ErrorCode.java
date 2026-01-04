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

  PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
  PRODUCT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "상품 수정 권한이 없습니다."),

  BUYER_NOT_FOUND(HttpStatus.NOT_FOUND, "판매자가 존재하지 않습니다."),
  INVALID_CHAT_TARGET(HttpStatus.BAD_REQUEST, "자신의 상품에는 채팅할 수 없습니다."),
  CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
  CHATROOM_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "채팅방 접근 권한이 없습니다."),
  SENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방 참가자를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String message;

}
