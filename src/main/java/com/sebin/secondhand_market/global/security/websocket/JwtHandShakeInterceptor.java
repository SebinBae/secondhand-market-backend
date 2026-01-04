package com.sebin.secondhand_market.global.security.websocket;

import com.sebin.secondhand_market.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {

    if (!(request instanceof ServletServerHttpRequest servletRequest)) {
      return false;
    }

    HttpServletRequest httpRequest = servletRequest.getServletRequest();
    String token = resolveToken(httpRequest);

    // JWT 여부만 수행
    return token != null && jwtProvider.validate(token);
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
      Exception exception) {
  }

  private String resolveToken(HttpServletRequest request) {
    final String BEARER_PREFIX = "Bearer ";
    final int BEARER_PREFIX_LENGTH = 7;

    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith(BEARER_PREFIX)) {
      return bearer.substring(BEARER_PREFIX_LENGTH);
    }
    return null;
  }
}
