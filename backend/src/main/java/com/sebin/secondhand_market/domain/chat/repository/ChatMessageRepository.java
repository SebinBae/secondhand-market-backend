package com.sebin.secondhand_market.domain.chat.repository;

import com.sebin.secondhand_market.domain.chat.entity.ChatMessageEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {

  Page<ChatMessageEntity> findByChatRoomId(UUID roomId, Pageable pageable);
}
