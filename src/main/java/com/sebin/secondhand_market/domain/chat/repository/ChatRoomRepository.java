package com.sebin.secondhand_market.domain.chat.repository;

import com.sebin.secondhand_market.domain.chat.entity.ChatRoomEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {

  Optional<ChatRoomEntity> findByProductIdAndBuyerId(UUID productId, UUID buyerId);

  // 사용자가 판매자 혹은 구매자인 채팅방 모두를 불러오기 위한 JPQL
  @Query("""
      select r from ChatRoomEntity r
      where r.seller.id = :userId or r.buyer.id = :userId
      order by r.lastMessageAt desc
      """)
  Page<ChatRoomEntity> findMyChatRooms(@Param("userId") UUID userId, Pageable pageable);
}
