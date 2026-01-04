package com.sebin.secondhand_market.domain.chat.service;

import com.sebin.secondhand_market.domain.chat.dto.response.ChatMessageResponse;
import com.sebin.secondhand_market.domain.chat.entity.ChatMessageEntity;
import com.sebin.secondhand_market.domain.chat.entity.ChatRoomEntity;
import com.sebin.secondhand_market.domain.chat.repository.ChatMessageRepository;
import com.sebin.secondhand_market.domain.chat.repository.ChatRoomRepository;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import com.sebin.secondhand_market.global.exception.trade.ChatRoomAccessDeniedException;
import com.sebin.secondhand_market.global.exception.trade.ChatRoomNotFoundException;
import com.sebin.secondhand_market.global.exception.trade.SenderNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * 메시지 전송(WebSocket)
   */
  public void sendMessage(UUID roomId, UUID senderId, String content) {

    ChatRoomEntity room = chatRoomRepository.findById(roomId)
        .orElseThrow(ChatRoomNotFoundException::new);

    // 채팅방 참여자 검증(sender : 판매자, 구매자)
    if (!room.getSeller().getId().equals(senderId) && !room.getBuyer().getId().equals(senderId)) {
      throw new ChatRoomAccessDeniedException();
    }

    UserEntity sender = userRepository.findById(senderId)
        .orElseThrow(SenderNotFoundException::new);

    ChatMessageEntity message = new ChatMessageEntity(room, sender, content);
    chatMessageRepository.save(message);

    // 마지막 메시지 시간 갱신
    room.updateLastMessageTime();

    messagingTemplate.convertAndSend(
        "/topic/chat." + roomId,
        ChatMessageResponse.from(message)
    );
  }

  /**
   * 채팅 메시지 조회(REST)
   */
  @Transactional(readOnly = true)
  public Page<ChatMessageResponse> getMessages(
      UUID roomId,
      UUID userId,
      Pageable pageable
  ) {
    ChatRoomEntity room = chatRoomRepository.findById(roomId)
        .orElseThrow(ChatRoomNotFoundException::new);

    if (!room.getSeller().getId().equals(userId) && !room.getBuyer().getId().equals(userId)) {
      throw new ChatRoomAccessDeniedException();
    }
    return chatMessageRepository.findByChatRoomId(roomId, pageable).map(ChatMessageResponse::from);

  }
}
