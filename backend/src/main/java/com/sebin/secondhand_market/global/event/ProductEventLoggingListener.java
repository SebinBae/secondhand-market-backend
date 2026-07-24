package com.sebin.secondhand_market.global.event;

import com.sebin.secondhand_market.domain.product.event.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 상품 관련 이벤트를 로그로 남기는 리스너 (발행-구독 뼈대).
 *
 * 트랜잭션 커밋 이후에만 수신한다(AFTER_COMMIT) — 롤백된 등록은 부수효과를 일으키지 않는다.
 * 지금은 로그만 남기며, 실제 알림/색인 등의 부수효과는 이후 단계에서 구현한다.
 */
@Slf4j
@Component
public class ProductEventLoggingListener {

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onProductCreated(ProductCreatedEvent event) {
    log.info(
        "[ProductCreatedEvent] productId={}, sellerId={}, title={}, occurredAt={}",
        event.productId(), event.sellerId(), event.title(), event.occurredAt()
    );
  }
}
