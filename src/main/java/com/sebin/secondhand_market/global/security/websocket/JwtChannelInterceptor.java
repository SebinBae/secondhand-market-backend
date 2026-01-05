package com.sebin.secondhand_market.global.security.websocket;

import com.sebin.secondhand_market.global.security.JwtProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

  private static final Logger log = LogManager.getLogger(JwtChannelInterceptor.class);
  private final JwtProvider jwtProvider;

  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = 7;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // STOMP CONNECT 시점
    if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {

      if(accessor.getUser()!= null) return message;

      String authHeader = accessor.getFirstNativeHeader("Authorization");
      log.info("CONNECT auth={}", accessor.getFirstNativeHeader("Authorization"));

      if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
        String token = authHeader.substring(BEARER_PREFIX_LENGTH);

        if(jwtProvider.validate(token)){
          Authentication authentication = jwtProvider.getAuthentication(token);

          // UUID 기반 Principal 주입
          accessor.setUser(authentication);
          accessor.setLeaveMutable(true);

          log.info("CONNECT user={}", accessor.getUser());
          return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

      }
    }

    return message;
  }
}
