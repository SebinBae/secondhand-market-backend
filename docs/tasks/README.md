# Phase 1 작업 문서 (Claude Code용)

Claude Code 세션에 작업을 위임할 때 사용하는 문서들. 세션 시작 시 해당 task 파일 하나를 컨텍스트로 제공한다.

## 사용법

```
docs/tasks/task-01-exception-relocation.md 읽고 작업 진행해줘.
루트 CLAUDE.md의 규칙을 준수하고, 문서와 충돌하는 판단이 필요하면 먼저 질문할 것.
```

## 공통 규칙 (모든 작업에 적용)

- 루트 `CLAUDE.md` 준수. 특히 단계 규칙(Kafka/Redis 캐시/ES/멀티모듈 선제 도입 금지).
- 브랜치 1개 = 세션 1개 = PR 1개. 문서에 명시된 브랜치명 사용.
- 완료 조건(DoD)을 전부 만족하기 전에 작업을 끝내지 않는다. 만족 불가능하다고 판단되면 이유를 보고한다.
- 테스트/규칙 코드를 수정해서 통과시키는 우회 금지.
- 작업 종료 시 PR 본문의 "AI 도구 사용 기록"에 넣을 요약(사용 범위 / 사용자가 검토해야 할 지점)을 출력한다.

## 작업 순서

| 순서 | 문서 | 브랜치 | 선행 조건 |
|---|---|---|---|
| 1 | task-01-exception-relocation.md | refactor/exception-relocation | 없음 |
| 2 | task-02-module-boundary-archunit.md | refactor/module-boundary | task-01 머지 |
| 3 | task-03-application-event.md | feat/product-created-event | task-02 머지 |
| 4 | task-04-k6-baseline.md | chore/k6-baseline | 없음 (병렬 가능) |

2주차 이후 작업(AI 등록 어시스턴트 등)은 해당 주차 시작 시점에 상세화해 추가한다.
