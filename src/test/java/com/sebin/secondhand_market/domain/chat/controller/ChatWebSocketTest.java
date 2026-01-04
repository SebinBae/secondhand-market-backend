package com.sebin.secondhand_market.domain.chat.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.sebin.secondhand_market.domain.chat.dto.request.ChatMessageSendRequest;
import com.sebin.secondhand_market.domain.chat.dto.response.ChatMessageResponse;
import com.sebin.secondhand_market.domain.chat.entity.ChatRoomEntity;
import com.sebin.secondhand_market.domain.chat.repository.ChatRoomRepository;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.repository.ProductRepository;
import com.sebin.secondhand_market.domain.product.type.ProductCategory;
import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import com.sebin.secondhand_market.global.security.JwtProvider;
import com.sebin.secondhand_market.support.TestJwtHelper;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ChatWebSocketTest {

  @Autowired TestJwtHelper testJwtHelper;
  @Autowired ChatRoomRepository chatRoomRepository;
  @Autowired UserRepository userRepository;
  @Autowired ProductRepository productRepository;

  // 랜덤 포트 값을 port 필드값으로 받아옴.
  @LocalServerPort
  int port;

  // 테스트를 위해 브라우저 대신 WebSocket + STOMP 자체로 접속하는 클라이언트
  WebSocketStompClient stompClient;

  UUID roomId;
  UUID senderId;
  UUID buyerId;
  UUID sellerId;
  UUID productId;
  @Autowired
  private JwtProvider jwtProvider;

  @BeforeEach
  void setUp(){
    stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    // JSON <-> Java 객체 자동 변환
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    // 1. 유저 생성
    UserEntity seller = userRepository.save(
        new UserEntity(
        "codus@test.com",
        "codus",
        "판매자")
    );
    sellerId = seller.getId();

    UserEntity buyer = userRepository.save(
      new UserEntity(
       "sedus@test.com",
          "sedus",
          "세빈배"
      )
    );
    buyerId = buyer.getId();

    // 2. 제품 생성
    ProductEntity product = productRepository.save(
      new ProductEntity(
          "아이폰 18",
          10000000,
          "급전이 필요하여 판매합니다.",
          ProductCategory.DIGITAL,
          ProductStatus.SELLING,
          seller
      )
    );
    productId = product.getId();

    // 3. 채팅방 생성
    ChatRoomEntity room = chatRoomRepository.save(
      new ChatRoomEntity(
          product,
          seller,
          buyer)
      );
    roomId = room.getId();
  }

  @Test
  void sendMessageAndAfterSubscribe() throws Exception{

    String token = jwtProvider.createToken(sellerId);

    // WebSocket handshake를 위한 url
    String url = "ws://localhost:" + port + "/ws-chat";

    WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
    webSocketHttpHeaders.add("Authorization", "Bearer " + token);

    StompHeaders stompHeaders = new StompHeaders();
    stompHeaders.add("Authorization", "Bearer " + token);

    StompSession session =stompClient.connectAsync(
        url,
        webSocketHttpHeaders,
        stompHeaders,
        new StompSessionHandlerAdapter() {}
    ).get(3,TimeUnit.SECONDS);

    CompletableFuture<ChatMessageResponse> future = new CompletableFuture<>();

    //subscribe
    session.subscribe("/topic/chat." + roomId, new StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return ChatMessageResponse.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        future.complete((ChatMessageResponse) payload);
      }
    });

    // send
    StompHeaders sendHeaders = new StompHeaders();
    sendHeaders.setDestination("/app/chat.send");
    sendHeaders.add("Authorization", "Bearer " + token);

    session.send(sendHeaders, new ChatMessageSendRequest(roomId, "hello websocket!"));

    //then
    ChatMessageResponse response = future.get(5, TimeUnit.SECONDS);
    assertThat(response.getRoomId()).isEqualTo(roomId);
    assertThat(response.getContent()).isEqualTo("hello websocket!");

  }



}
