package com.sebin.secondhand_market.domain.chat.entity;

import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

@Getter
@NoArgsConstructor
@Entity
@Table(
    name = "chat_rooms",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"product_id", "buyer_id"}
        )
    }
)
public class ChatRoomEntity {

  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)
  private UserEntity seller;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id", nullable = false)
  private UserEntity buyer;

  private LocalDateTime lastMessageAt;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public ChatRoomEntity(ProductEntity product, UserEntity seller, UserEntity buyer) {
    this.product = product;
    this.seller = seller;
    this.buyer = buyer;
  }

  public static ChatRoomEntity create(ProductEntity product, UserEntity seller, UserEntity buyer) {

    ChatRoomEntity room = new ChatRoomEntity();
    room.product = product;
    room.seller = seller;
    room.buyer = buyer;
    room.lastMessageAt = LocalDateTime.now();
    room.createdAt = LocalDateTime.now();

    return room;
  }

  public void updateLastMessageTime() {
    this.lastMessageAt = LocalDateTime.now();
  }

}
