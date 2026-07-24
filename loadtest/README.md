# loadtest — k6 부하 테스트

Phase 2 Before/After 성능 측정을 위한 도구/시나리오. **지금은 도구 준비 단계이며, 실제 Before 기준선 측정은 MVP 완성 시점(8/30~31)에 수행한다.**

기획서 기준: 평시 100 VU, 스파이크 300 VU, 핵심 지표는 응답시간 p95.

## 시나리오

| 파일 | 부하 | 용도 |
|------|------|------|
| `smoke.js` | 1 VU, 30초 | 파이프라인 검증 (로그인 → 목록 → 검색이 2xx인지) |
| `baseline-100vu.js` | 0→100 VU (2m 램프업 / 5m 유지 / 1m 램프다운) | 평시 부하 기준선 측정 |

두 시나리오 모두 대상 API는 동일하다:
- 로그인 `POST /api/auth/login` — `setup()`에서 1회만 호출해 토큰 확보 후 재사용
- 상품 목록 조회 `GET /api/products?page=0&size=10`
- 상품 검색 `GET /api/products?keyword=<키워드>&page=0&size=10` (Phase 2 개선 대상)

> `http_req_duration: p(95)<300` 임계값은 **가설 수치**다. Before 측정 이전이라 통과가 목적이 아니라 기준선을 기록하는 것이 목적이며, 실패해도 그 자체가 데이터다.

## 사전 조건

1. **k6 설치** — https://k6.io/docs/get-started/installation/
   ```bash
   # 예: Debian/Ubuntu
   sudo gpg -k
   sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
   echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
   sudo apt-get update && sudo apt-get install k6
   ```
2. **애플리케이션 + 인프라 기동** (백엔드는 Redis에 의존)
   ```bash
   cd ../backend
   docker compose up -d      # 앱 + Redis + Prometheus + Grafana
   # 또는 앱만 별도 실행하려면 Redis가 떠 있는 상태에서:
   ./gradlew bootRun
   ```
3. **테스트 계정 생성** — 로그인에 사용할 계정을 미리 만들어 둔다.
   ```bash
   curl -X POST http://localhost:8080/api/auth/signup \
     -H 'Content-Type: application/json' \
     -d '{"email":"loadtest@example.com","password":"<password>","nickname":"부하테스트"}'
   ```

## 실행

계정 정보는 **환경변수로만 주입**한다 (스크립트에 하드코딩하지 않는다).

```bash
export K6_TEST_EMAIL="loadtest@example.com"
export K6_TEST_PASSWORD="<password>"

# 스모크 (파이프라인 점검)
k6 run smoke.js

# 평시 100 VU 기준선
k6 run baseline-100vu.js
```

### 선택 환경변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `BASE_URL` | `http://localhost:8080` | 대상 서버 |
| `K6_TEST_EMAIL` | (필수) | 로그인 계정 |
| `K6_TEST_PASSWORD` | (필수) | 로그인 비밀번호 |
| `K6_SEARCH_KEYWORD` | `노트북` | 검색 시나리오 키워드 |

## 결과 저장

측정 결과는 요약 JSON으로 남긴다. 파일명은 `<날짜>-<시나리오>` 규칙을 따른다.

```bash
k6 run --summary-export=results/2026-08-30-baseline-100vu.json baseline-100vu.js
```

요약에는 요청별(`name` 태그: `auth_login` / `products_list` / `products_search`) 지표와 전체 `http_req_duration` p95가 포함된다. Before/After 비교 시 같은 파일명 규칙으로 저장해 나란히 둔다.
