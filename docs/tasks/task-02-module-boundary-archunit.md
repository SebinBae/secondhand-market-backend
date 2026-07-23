# task-02: 크로스 도메인 repository 직접 참조 제거 + ArchUnit 경계 테스트 도입

- 브랜치: `refactor/module-boundary`
- 관련 이슈: #3
- 커밋 타입: `refactor:` (경계 정리), `test:` (ArchUnit 추가)
- 선행 조건: task-01 머지 완료

## 배경

모듈 경계 규칙 "다른 도메인은 서비스(공개 창구)만 사용한다"를 코드로 확립하고, ArchUnit 테스트로 CI에서 강제한다. 이 PR의 초록불이 곧 "모듈 경계 확립 완료"의 증거가 된다.

## 현재 상태 (위반 지점)

- `domain/chat/**` 이 `ProductRepository`, `UserRepository`를 직접 import해 사용
- `domain/product/**` 가 `UserRepository`를 직접 import해 사용
- ArchUnit 미도입

## 작업 단계

1. `build.gradle`에 의존성 추가: `testImplementation 'com.tngtech.archunit:archunit-junit5:1.3.0'`
2. `src/test/java/com/sebin/secondhand_market/architecture/ModuleBoundaryTest.java` 추가 — 파일 내용은 사용자가 제공 (이미 작성됨). 규칙: 의존 방향(chat → product → user 단방향), 타 도메인 repository 접근 금지, controller→repository 직접 접근 금지, global.exception→domain 참조 금지
3. `ModuleBoundaryTest`를 실행해 위반 목록 확보
4. chat의 타 도메인 repository 사용처를 product/user의 **기존 서비스 경유**로 전환. 필요한 조회 메서드가 서비스에 없으면 읽기 전용 메서드를 해당 도메인 서비스에 추가하는 것은 허용
5. product의 UserRepository 사용처도 동일하게 전환
6. controller→repository 위반이 발견되면 서비스 경유로 정리
7. 전체 테스트 통과 확인

## 하지 말 것

- **ModuleBoundaryTest의 규칙을 수정/완화해서 통과시키는 것 금지.** 규칙이 잘못됐다고 판단되면 근거를 들어 사용자에게 보고
- 엔티티 간 `@ManyToOne` 연관관계를 ID 참조로 바꾸는 것 금지 (Phase 3에서 분리 확정 모듈에만 적용하기로 결정됨 — 기획서 6번)
- 서비스에 쓰기(수정/삭제) 메서드를 새로 만드는 것 금지 — 이 작업에서 허용되는 추가는 읽기 전용 조회뿐

## 완료 조건 (DoD)

- [ ] `ModuleBoundaryTest` 전 규칙 통과 (규칙 코드 무수정)
- [ ] chat/product에 타 도메인 repository import가 존재하지 않음
- [ ] `./gradlew clean build` 초록불
