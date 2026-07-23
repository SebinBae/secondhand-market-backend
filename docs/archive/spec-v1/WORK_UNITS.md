# Work Units - Secondhand Marketplace

작업을 모듈 단위로 쪼개서, 각 단위가 독립적으로 완료/검증 가능하도록 구성했습니다.
순서는 의존성 기준이며, 병렬 진행 가능한 단위는 표시했습니다.

## Phase 1 — Core Foundation
1. **User/Auth module**
   - Spring Security + JWT 인증 구조
   - 회원가입/로그인/토큰 재발급
   - 완료 기준: 인증 플로우 통합 테스트 통과

2. **Item/Listing CRUD** (Auth 완료 후)
   - 매물 등록/수정/삭제/조회 API
   - DTO-Entity 분리 구조 적용
   - 완료 기준: 기본 CRUD + 페이지네이션 동작

## Phase 2 — Infra Integration (병렬 가능)
3. **Redis 캐싱 적용**
   - 매물 상세 조회 캐싱
   - 캐시 무효화 전략 정의
   - 완료 기준: 캐시 적용 전/후 응답 시간 벤치마크 확보

4. **Kafka 이벤트 파이프라인**
   - 매물 등록/수정 이벤트 발행
   - 알림/검색 인덱스 갱신용 컨슈머 구조
   - 완료 기준: 장애 격리 시나리오 테스트 (컨슈머 다운 시 프로듀서 영향 없음 확인)

5. **Elasticsearch 검색 연동**
   - Kafka 컨슈머로 ES 인덱스 동기화
   - 검색 API (키워드, 카테고리, 가격 범위 필터)
   - 완료 기준: DB 검색 대비 응답 시간 비교 자료 확보

## Phase 3 — Payment & Transaction
6. **Toss Payments 연동**
   - 결제 요청/승인/웹훅 처리
   - 트랜잭션 정합성 보장 (결제-주문 상태 동기화)
   - 완료 기준: 결제 실패/취소 시나리오 포함 테스트

## Phase 4 — AI Listing Assistant
7. **FastAPI 서빙 레이어 구축**
   - LangGraph 에이전트를 API로 노출
   - Spring Boot ↔ FastAPI 내부 통신 구조

8. **LangGraph 매물 등록 어시스턴트**
   - State 스키마 설계
   - 이미지/텍스트 기반 카테고리·설명 자동 생성 노드
   - 완료 기준: 정확도/응답시간 샘플 테스트

## Phase 5 — Frontend
9. **React 프론트엔드** (백엔드 API 안정화 후 본격 진행, 초기 셋업은 병렬 가능)
   - TanStack Query (서버 상태) / Zustand (클라이언트 상태) 분리
   - 매물 목록/상세/등록/결제 플로우 UI

## Phase 6 — Documentation & Portfolio
10. **부하 테스트 및 성능 문서화**
    - k6 등으로 p50/p95/p99 측정
    - Redis/Kafka/ES 도입 전후 비교 자료 정리

11. **아키텍처 결정 기록 (ADR)**
    - 모듈러 모놀리스 선택 이유
    - 각 인프라 선택의 트레이드오프 문서화

---

## 사용 팁
- 각 Phase 시작 전 Claude Code에게 "Phase N 작업 단위 기준으로 진행해줘"라고 지정하면 컨텍스트가 명확해짐
- 완료된 작업 단위는 체크 표시하면서 진행 상황 추적 권장
