package com.sebin.secondhand_market.domain.chat.controller;

import com.sebin.secondhand_market.domain.chat.dto.request.ChatMessageSendRequest;
import com.sebin.secondhand_market.domain.chat.service.ChatMessageService;
import com.sebin.secondhand_market.global.exception.trade.StompAuthenticationRequiredException;
import com.sebin.secondhand_market.global.websocket.StompAppDestination;
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

  @MessageMapping(StompAppDestination.CHAT_SEND)
  public void sendMessage(
      ChatMessageSendRequest request,
      SimpMessageHeaderAccessor accessor
  ) {
    Principal principal = accessor.getUser();

    if (principal == null) {
      throw new StompAuthenticationRequiredException();
    }

    UUID senderId = UUID.fromString(principal.getName());

    chatMessageService.sendMessage(
        request.getRoomId(),
        senderId,
        request.getContent()
    );

  }
}
