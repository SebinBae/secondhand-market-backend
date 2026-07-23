# task-04: k6 기초 부하 시나리오 작성

- 브랜치: `chore/k6-baseline`
- 관련 이슈: #5
- 커밋 타입: `chore:`
- 선행 조건: 없음 (task-01~03과 병렬 가능)

## 배경

Phase 2 Before/After 측정의 도구 준비. 기획서 기준: 평시 100 VU, 스파이크 300 VU, 핵심 지표는 p95. 지금은 측정 도구와 시나리오만 준비하고, 실제 Before 기준선 측정은 MVP 완성 시점(8/30~31)에 수행한다.

## 작업 단계

1. `loadtest/` 디렉토리 생성 (저장소 루트)
2. `loadtest/smoke.js` — 1 VU, 30초. 대상: 로그인 → 상품 목록 조회 → 상품 검색. 파이프라인 검증용
3. `loadtest/baseline-100vu.js` — ramping-vus (0→100 VU 2분 램프업, 100 VU 5분 유지, 1분 램프다운). 동일 대상 API
4. 인증 처리: `setup()` 단계에서 테스트 계정 로그인 → 토큰 획득 → 이후 요청의 Authorization 헤더에 사용. 테스트 계정 정보는 환경변수(`K6_TEST_EMAIL`, `K6_TEST_PASSWORD`)로 주입 — 하드코딩 금지
5. thresholds 설정: `http_req_duration: ['p(95)<300']` (가설 수치, 실패해도 기록이 목적임을 주석으로 명시)
6. `loadtest/README.md` 작성 — 사전 조건(로컬 기동, Redis, 테스트 계정 생성), 실행 명령, 결과 저장 방법(`--summary-export=results/<날짜>-<시나리오>.json`)

## 하지 말 것

- 애플리케이션 코드 수정 금지 — 이 작업은 측정 도구만
- 결과가 나쁘다고 캐시/인덱스 등 개선 작업 착수 금지 (Phase 2의 일)
- CI에 부하 테스트 추가 금지 (로컬 실행 전용)

## 완료 조건 (DoD)

- [ ] `k6 run loadtest/smoke.js` 가 로컬에서 성공 (전 요청 2xx)
- [ ] `k6 run loadtest/baseline-100vu.js` 실행 시 p95가 요약에 출력됨
- [ ] README에 처음 보는 사람이 따라 할 수 있는 실행 가이드 존재
- [ ] 스크립트에 계정/토큰 하드코딩 없음
