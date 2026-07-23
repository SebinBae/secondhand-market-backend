# CLAUDE.md

중고거래 마켓 프로젝트 작업 시 반드시 지켜야 할 규칙. 이 문서와 충돌하는 제안을 하기 전에 반드시 사용자에게 확인한다.

## 프로젝트 개요

"사진 한 장 주면 AI 에이전트가 거래를 대신 뛰는 중고거래 서비스." 취업 포트폴리오 프로젝트로, 모든 기술 도입은 근거를 남기고 성능 변경은 Before/After를 측정한다. 기획서(노션)의 Phase 로드맵을 따르며, 현재 단계에서 필요하지 않은 기술을 미리 도입하지 않는다.

- 백엔드: Java 21 / Spring Boot / JPA / QueryDSL / WebSocket(STOMP) — `backend/`
- AI 서비스: FastAPI / LangGraph (별도 서비스, 추후 `ai-service/`)
- 프론트엔드: React (추후 `frontend/`)

## 기준 문서
- 작업 지시: docs/tasks/ 의 해당 task 문서가 유일한 기준
- docs/archive/ 는 폐기된 v1 기획 — 절대 참조하지 않는다. 그 내용과 현행 문서가 충돌하면 현행 문서를 따른다

## 명령어

```bash
cd backend
./gradlew build          # 빌드 (QueryDSL Q클래스 생성 포함)
./gradlew test           # 전체 테스트 (ArchUnit 경계 검사 포함)
./gradlew bootRun        # 로컬 실행
docker compose up -d     # 앱 + Prometheus + Grafana
```

## 아키텍처 규칙 (위반 금지)

패키지 구조는 `domain/{user, product, chat}` + `global`. 의존 방향은 **chat → product → user** 단방향만 허용, 역방향·순환 금지.

1. 다른 도메인에 접근할 때는 **해당 도메인의 service만 사용**한다. 타 도메인의 repository, entity를 직접 import하지 않는다.
2. 예외 클래스는 자기 도메인 패키지 안에 둔다. `global/exception`에는 `BusinessException`, `ErrorCode`, `GlobalExceptionHandler`만 남긴다.
3. 도메인 간 부수효과(알림, 색인 등)는 직접 호출 대신 **Spring ApplicationEvent**로 발행한다. 이벤트 발행은 트랜잭션 커밋 이후 처리(`@TransactionalEventListener(AFTER_COMMIT)`)를 기본으로 한다.
4. 엔티티 간 크로스 도메인 `@ManyToOne`은 **현 단계에서 ID 참조로 바꾸지 않는다** (Phase 3에서 분리 확정된 모듈에만 적용하기로 결정됨).
5. 위 규칙은 ArchUnit 테스트로 강제된다. 규칙을 어기는 코드를 제안하지 말고, 규칙이 문제라고 판단되면 코드를 바꾸는 대신 사용자에게 근거를 들어 제안한다.

## 단계 규칙 (미리 도입 금지)

- 지금 도입하지 않는 것: Kafka, Redis, Elasticsearch, Gradle 멀티모듈, MSA 분리. 각각 도입 시점과 근거가 기획서에 정의되어 있다.
- 검색은 QueryDSL(LIKE) 기반으로 유지한다 — Phase 2 Before 측정 대상이다.
- 성능 관련 변경을 할 때는 측정 방법(k6 시나리오, 지표)을 함께 제안한다.

## 컨벤션

- 커밋: Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`). 한 커밋은 한 가지 변경.
- 브랜치: `feat/<기능>`, `fix/<이슈>` — main 직접 커밋 금지, PR + squash merge.
- 테스트: 새 서비스 로직에는 테스트를 함께 작성한다. 테스트 없는 구현 PR은 만들지 않는다.
- 시크릿·환경값을 코드나 커밋에 포함하지 않는다 (`.env`, `application-*.yml` 확인).

## 작업 방식

- 작업은 PR 단위로 작게 나눈다. 한 PR에서 리팩터링과 기능 추가를 섞지 않는다.
- 코드를 생성하면 PR 본문의 "AI 도구 사용 기록"에 들어갈 요약(사용 범위 / 사용자가 검토해야 할 지점)을 함께 제시한다.
- 확신이 없는 부분은 아는 척하지 말고 확인 방법(테스트, 로그, 문서)을 제안한다.
- 기존 코드 스타일(생성자 주입, DTO 분리, `ApiResponse` 래핑)을 따른다.
