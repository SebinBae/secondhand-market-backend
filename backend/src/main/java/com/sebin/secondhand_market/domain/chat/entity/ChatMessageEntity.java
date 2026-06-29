package com.sebin.secondhand_market.domain.chat.entity;

import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "chat_messages")
@NoArgsConstructor
@Getter
public class ChatMessageEntity {

  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoomEntity chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private UserEntity sender;

  @Column(nullable = false, length = 1000)
  private String content;

  private LocalDateTime createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public ChatMessageEntity(ChatRoomEntity room, UserEntity sender, String content) {
    this.chatRoom = room;
    this.sender = sender;
    this.content = content;
  }

}
