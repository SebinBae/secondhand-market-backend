package com.sebin.secondhand_market.domain.chat.dto.response;

import com.sebin.secondhand_market.domain.chat.entity.ChatMessageEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

  UUID messageId;
  UUID roomId;
  UUID senderId;
  String content;
  LocalDateTime createdAt;

  public static ChatMessageResponse from(ChatMessageEntity message) {
    return new ChatMessageResponse(
        message.getId(),
        message.getChatRoom().getId(),
        message.getSender().getId(),
        message.getContent(),
        message.getCreatedAt()
    );
  }

}
