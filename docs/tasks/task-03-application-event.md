# task-03: 상품 등록 ApplicationEvent 발행 뼈대

- 브랜치: `feat/product-created-event`
- 관련 이슈: #4
- 커밋 타입: `feat:`
- 선행 조건: task-02 머지 완료

## 배경

도메인 간 부수효과(알림, 검색 색인 등)를 직접 호출 대신 이벤트로 처리하는 구조의 시작점. 지금은 프로세스 내 이벤트(Spring ApplicationEvent)로 시작하며, 이것이 이후 Kafka 전환 시의 Before 상태이자 비교 근거가 된다. **Kafka를 도입하는 작업이 아니다.**

## 작업 단계

1. `domain/product/event/ProductCreatedEvent` 정의 — record, 필드는 최소로: `productId`, `sellerId`, `title`, `occurredAt`. 엔티티를 이벤트에 담지 않는다 (직렬화 가능한 값만 — Kafka 전환 대비)
2. 상품 등록 서비스에서 저장 성공 후 `ApplicationEventPublisher`로 발행
3. `global/event/ProductEventLoggingListener` 추가 — `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async` 없이, 수신 내용을 log.info로 기록하는 수준
4. 테스트 작성:
   - 상품 등록 성공 시 이벤트가 발행되고 리스너가 커밋 이후에 호출됨을 검증
   - 상품 등록이 실패(롤백)하면 리스너가 호출되지 않음을 검증

## 하지 말 것

- Kafka, @Async, 스레드풀 설정 등 선제 도입 금지 — 이 작업은 발행-구독 패턴의 뼈대만
- 알림/색인 등 실제 부수효과 구현 금지 (리스너는 로그만)
- 상품 등록의 기존 응답/트랜잭션 동작 변경 금지

## 완료 조건 (DoD)

- [ ] 상품 등록 → 커밋 후 이벤트 수신이 테스트로 검증됨
- [ ] 롤백 시 이벤트 미수신이 테스트로 검증됨
- [ ] 이벤트 클래스에 엔티티 참조 없음 (값 필드만)
- [ ] `./gradlew clean build` 초록불
