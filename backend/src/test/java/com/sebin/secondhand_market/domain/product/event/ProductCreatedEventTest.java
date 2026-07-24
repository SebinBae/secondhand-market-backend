package com.sebin.secondhand_market.domain.product.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sebin.secondhand_market.domain.product.dto.request.ProductCreateRequest;
import com.sebin.secondhand_market.domain.product.repository.ProductRepository;
import com.sebin.secondhand_market.domain.product.service.ProductService;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 상품 등록 이벤트 발행-구독 검증.
 *
 * - 등록 커밋 후 AFTER_COMMIT 리스너가 이벤트를 수신하는지
 * - 등록이 롤백되면 이벤트가 수신되지 않는지(부수효과 억제)
 */
@SpringBootTest
class ProductCreatedEventTest {

  @Autowired
  private ProductService productService;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private EventRecorder recorder;
  @Autowired
  private TxHelper txHelper;

  private UUID sellerId;

  @BeforeEach
  void setUp() {
    recorder.clear();
    UserEntity seller = userRepository.save(
        new UserEntity("product-event-seller@test.com", "pw", "판매자"));
    sellerId = seller.getId();
  }

  @AfterEach
  void tearDown() {
    productRepository.deleteAll();
    userRepository.deleteById(sellerId);
    recorder.clear();
  }

  @Test
  @DisplayName("상품 등록이 커밋되면 커밋 이후 ProductCreatedEvent가 수신된다")
  void publishesEventAfterCommit() {
    ProductCreateRequest request = request("맥북 프로");

    UUID productId = productService.create(request, sellerId);

    List<ProductCreatedEvent> received = recorder.events();
    assertThat(received).hasSize(1);
    ProductCreatedEvent event = received.get(0);
    assertThat(event.productId()).isEqualTo(productId);
    assertThat(event.sellerId()).isEqualTo(sellerId);
    assertThat(event.title()).isEqualTo("맥북 프로");
    assertThat(event.occurredAt()).isNotNull();
  }

  @Test
  @DisplayName("상품 등록 트랜잭션이 롤백되면 이벤트가 수신되지 않는다")
  void doesNotPublishEventWhenRolledBack() {
    ProductCreateRequest request = request("롤백될 상품");

    assertThatThrownBy(() -> txHelper.createThenRollback(request, sellerId))
        .isInstanceOf(IllegalStateException.class);

    assertThat(recorder.events()).isEmpty();
    assertThat(productRepository.count()).isZero();
  }

  private ProductCreateRequest request(String title) {
    return objectMapper.convertValue(
        Map.of(
            "title", title,
            "price", 1_000_000,
            "description", "테스트 상품입니다.",
            "productCategory", "DIGITAL"
        ),
        ProductCreateRequest.class);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    EventRecorder eventRecorder() {
      return new EventRecorder();
    }

    @Bean
    TxHelper txHelper(ProductService productService) {
      return new TxHelper(productService);
    }
  }

  /**
   * 프로덕션 리스너와 동일하게 AFTER_COMMIT으로 수신해 전달 시점을 검증하는 테스트용 기록기.
   */
  static class EventRecorder {

    private final List<ProductCreatedEvent> events = new CopyOnWriteArrayList<>();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ProductCreatedEvent event) {
      events.add(event);
    }

    List<ProductCreatedEvent> events() {
      return events;
    }

    void clear() {
      events.clear();
    }
  }

  /**
   * create()를 트랜잭션 안에서 호출한 뒤 예외를 던져 롤백을 유도한다.
   * create()는 propagation REQUIRED로 이 트랜잭션에 참여하므로 발행된 이벤트도 함께 롤백된다.
   */
  static class TxHelper {

    private final ProductService productService;

    TxHelper(ProductService productService) {
      this.productService = productService;
    }

    @Transactional
    public void createThenRollback(ProductCreateRequest request, UUID sellerId) {
      productService.create(request, sellerId);
      throw new IllegalStateException("force rollback");
    }
  }
}
