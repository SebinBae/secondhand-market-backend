package com.sebin.secondhand_market.domain.chat.service;

import com.sebin.secondhand_market.domain.chat.dto.response.ChatRoomResponse;
import com.sebin.secondhand_market.domain.chat.entity.ChatRoomEntity;
import com.sebin.secondhand_market.domain.chat.repository.ChatRoomRepository;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.repository.ProductRepository;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import com.sebin.secondhand_market.global.exception.product.ProductNotFoundException;
import com.sebin.secondhand_market.global.exception.trade.BuyerNotFoundException;
import com.sebin.secondhand_market.global.exception.trade.InvalidChatTargetException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  /**
   * 상품과 구매자 기준 채팅방 생성(이미 존재하면 조회함)
   */
  public ChatRoomResponse createOrGetRoom(UUID productId, UUID buyerId) {
    // 상품 조회
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);

    // 구매자 조회
    UserEntity buyer = userRepository.findById(buyerId)
        .orElseThrow(BuyerNotFoundException::new);

    // 판매자
    UserEntity seller = product.getSeller();

    // 판매자가 자기 자신의 상품에 채팅 거는 것을 방지하기 위한 코드
    if (seller.getId().equals(buyerId)) {
      throw new InvalidChatTargetException();
    }

    // 이미 채팅방이 존재하는 경우 반환하고 없는 경우(null) 새로 생성함.
    return chatRoomRepository.findByProductIdAndBuyerId(productId, buyerId)
        .map(ChatRoomResponse::from)
        .orElseGet(() -> {
          ChatRoomEntity room = ChatRoomEntity.create(product, seller, buyer);
          ChatRoomEntity saved = chatRoomRepository.save(room);
          return ChatRoomResponse.from(saved);
        });
  }

  // 채팅방 조회
  public Page<ChatRoomResponse> getMyChatRooms(UUID userId, int page, int size) {

    Pageable pageable = PageRequest.of(
        page, size, Sort.by(Direction.DESC, "LastMessageAt")
    );

    return chatRoomRepository.findMyChatRooms(userId, pageable)
        .map(ChatRoomResponse::from);
  }

}
