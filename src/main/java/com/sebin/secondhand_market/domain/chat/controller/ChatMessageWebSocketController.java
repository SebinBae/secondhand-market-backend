package com.sebin.secondhand_market.domain.chat.controller;

import com.sebin.secondhand_market.domain.chat.dto.request.ChatMessageSendRequest;
import com.sebin.secondhand_market.domain.chat.service.ChatMessageService;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageWebSocketController {

  private final ChatMessageService chatMessageService;

  @MessageMapping("/chat.send")
  public void sendMessage(
      ChatMessageSendRequest request,
      SimpMessageHeaderAccessor accessor
  ) {
    log.info("chat.send 진입 requset = {}", request);

    Principal principal = accessor.getUser();
    log.info("principal : {}", principal);

    if (principal == null) {
      throw new IllegalStateException("STOMP user(principal)이 없음. Connect 인증 전파 확인 필요함.");
    }

    UUID senderId = UUID.fromString(principal.getName());
    log.info("senderId = {}", senderId);

    chatMessageService.sendMessage(
        request.getRoomId(),
        senderId,
        request.getContent()
    );

    log.info("chatMessageService.sendMessage 호출 완료");
  }
}
