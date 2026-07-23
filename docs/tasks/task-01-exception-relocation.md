# task-01: 도메인 예외 재배치

- 브랜치: `refactor/exception-relocation`
- 관련 이슈: #2
- 커밋 타입: `refactor:`

## 배경

`global/exception/{user, product, trade}` 하위에 도메인별 예외가 모여 있어 global에 도메인 지식이 새어 나와 있다. 모듈 경계 원칙상 예외는 자기 도메인 소유여야 한다. 이 작업은 task-02(ArchUnit)의 `global_exception은_도메인을_참조하지_않는다` 규칙의 선행 작업이다.

## 현재 상태

- `global/exception/user/*` — user 도메인 예외
- `global/exception/product/*` — product 도메인 예외
- `global/exception/trade/*` — 이름은 trade지만 실제로는 채팅방 관련 예외 (ChatRoom*)
- `global/exception/` 직속 — `BusinessException`, `ErrorCode`, `GlobalExceptionHandler` 등 공통 클래스

## 작업 단계

1. `domain/user/exception`, `domain/product/exception`, `domain/chat/exception` 패키지 생성
2. 각 예외 클래스를 소속 도메인으로 이동 (trade 하위는 `domain/chat/exception`으로)
3. 패키지 선언과 전체 import 갱신
4. `global/exception`에는 공통 클래스만 남김
5. `./gradlew clean build` 통과 확인

## 하지 말 것

- 클래스 이름 변경, 예외 계층 구조 변경, 메시지/ErrorCode 값 변경 — 이 작업은 순수 이동만
- 이동 외의 리팩터링을 같은 PR에 섞지 않기

## 완료 조건 (DoD)

- [ ] `global/exception` 하위에 도메인 하위 패키지가 존재하지 않음
- [ ] 각 예외가 자기 도메인의 `exception` 패키지에 위치
- [ ] `./gradlew clean build` 초록불 (기존 테스트 전체 통과)
