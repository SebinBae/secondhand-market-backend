package com.sebin.secondhand_market.global.config;

import com.sebin.secondhand_market.global.security.websocket.JwtChannelInterceptor;
import com.sebin.secondhand_market.global.security.websocket.JwtHandShakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtHandShakeInterceptor jwtHandShakeInterceptor;
  private final JwtChannelInterceptor jwtChannelInterceptor;

  // client ---> server(Handshake URL)
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws-chat")
        .addInterceptors(jwtHandShakeInterceptor)
        .setAllowedOriginPatterns("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 클라이언트가 /topic/** 을 구독하면 simpleBroker 가 구독한 모든 클라이언트에게 전달
    registry.enableSimpleBroker("/topic");

    // client -> server 메시지를 SEND 하면 @MessageMapping 메소드로 전달됨.
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // client -> 서버 메시지에 인터셉터 적용
    registration.interceptors(jwtChannelInterceptor);
  }
}
