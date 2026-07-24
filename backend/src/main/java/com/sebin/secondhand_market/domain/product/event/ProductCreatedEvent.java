package com.sebin.secondhand_market.domain.product.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 상품 등록 완료 이벤트.
 *
 * 도메인 간 부수효과(알림, 검색 색인 등)를 직접 호출하지 않고 이벤트로 전달하기 위한 시작점.
 * 엔티티를 담지 않고 직렬화 가능한 값만 보관한다 — 이후 Kafka 전환 시 그대로 메시지가 될 수 있도록.
 */
public record ProductCreatedEvent(
    UUID productId,
    UUID sellerId,
    String title,
    Instant occurredAt
) {

}
