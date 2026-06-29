package com.sebin.secondhand_market.domain.chat.dto.response;

import com.sebin.secondhand_market.domain.chat.entity.ChatRoomEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {

  UUID roomId;
  UUID productId;
  UUID sellerId;
  UUID buyerId;
  LocalDateTime lastMessageAt;

  public static ChatRoomResponse from(ChatRoomEntity chatRoom) {
    return new ChatRoomResponse(
        chatRoom.getId(),
        chatRoom.getProduct().getId(),
        chatRoom.getSeller().getId(),
        chatRoom.getBuyer().getId(),
        chatRoom.getLastMessageAt()
    );
  }

}
