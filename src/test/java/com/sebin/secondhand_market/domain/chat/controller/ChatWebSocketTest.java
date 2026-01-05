package com.sebin.secondhand_market.domain.chat.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class ChatWebSocketTest {

  @Autowired ChatRoomRepository chatRoomRepository;
  @Autowired UserRepository userRepository;
  @Autowired ProductRepository productRepository;

  // 랜덤 포트 값을 port 필드값으로 받아옴.
  @LocalServerPort
  int port;

  // 테스트를 위해 브라우저 대신 WebSocket + STOMP 자체로 접속하는 클라이언트
  WebSocketStompClient stompClient = createStompClient();

  UUID roomId;
  UUID buyerId;
  UUID sellerId;
  UUID productId;
  @Autowired
  private JwtProvider jwtProvider;

  @BeforeEach
  void setUp(){
    stompClient = createStompClient();

    // 1. 유저 생성
    UserEntity seller = userRepository.save(
        new UserEntity(
        "codus@testtest.com",
        "codus",
        "판매자")
    );
    sellerId = seller.getId();

    UserEntity buyer = userRepository.save(
      new UserEntity(
       "sedus@testtest.com",
          "sedus",
          "세빈배"
      )
    );
    buyerId = buyer.getId();

    // 2. 제품 생성
    ProductEntity product = productRepository.save(
      new ProductEntity(
          "아이폰 20",
          10000000,
          "급전이 필요하여 판매합니다. 판매합니다.",
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
        new StompSessionHandlerAdapter() {

          @Override
          public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
              Throwable exception) {
            System.err.println("STOMP handleException: cmd=" + command + ", headers=" + headers);
            exception.printStackTrace();
          }

          @Override
          public void handleTransportError(StompSession session, Throwable exception) {
            System.err.println("STOMP transportError:");
            exception.printStackTrace();
          }
        }
    ).get(3,TimeUnit.SECONDS);

    BlockingQueue<ChatMessageResponse> queue =
        new LinkedBlockingQueue<>();

    session.subscribe("/topic/chat." + roomId, new StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return ChatMessageResponse.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        queue.offer((ChatMessageResponse) payload);
      }
    });

    // 클라이언트 subscribe / publish 간에 타이밍 이슈로 인해 잠깐 정지
    Thread.sleep(200);

    // send
    StompHeaders sendHeaders = new StompHeaders();
    sendHeaders.setDestination("/app/chat.send");
    sendHeaders.add("Authorization", "Bearer " + token);
    sendHeaders.setContentType(MimeTypeUtils.APPLICATION_JSON);

    session.send(sendHeaders, new ChatMessageSendRequest(roomId, "hello websocket!"));

    //then
    ChatMessageResponse response = queue.poll(5, TimeUnit.SECONDS);
    assertThat(response).isNotNull();
    assertThat(response.getRoomId()).isEqualTo(roomId);
    assertThat(response.getContent()).isEqualTo("hello websocket!");

  }

  private WebSocketStompClient createStompClient() {
    //  클라이언트를 가정한 코드
    WebSocketStompClient stompclient = new WebSocketStompClient(new StandardWebSocketClient());

    // 직렬화 관련 코드 추가
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

    ObjectMapper om = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 클라이언트의 헤더에 contentType : Application/Json 추가
    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
    converter.setContentTypeResolver(resolver);

    converter.setObjectMapper(om);
    stompclient.setMessageConverter(converter);

    return stompclient;
  }

}
