import http from 'k6/http';
import { check, sleep } from 'k6';

// 파이프라인 검증용 스모크 테스트 (1 VU, 30초).
// 로그인(setup) → 상품 목록 조회 → 상품 검색 경로가 정상 동작(2xx)하는지 확인한다.
// 부하 측정이 목적이 아니라, 이후 baseline 시나리오를 돌리기 전 배관 점검용.

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SEARCH_KEYWORD = __ENV.K6_SEARCH_KEYWORD || '노트북';

export const options = {
  vus: 1,
  duration: '30s',
  thresholds: {
    // 가설 수치. Phase 2 Before 측정 이전이라 통과가 목적이 아니라 기준선 기록이 목적.
    // 실패해도 그 자체가 데이터이므로 이 임계값은 참고용이다.
    http_req_duration: ['p(95)<300'],
    http_req_failed: ['rate<0.01'],
  },
};

// 테스트 계정으로 1회 로그인해 액세스 토큰을 확보한다. 계정 정보는 환경변수로만 주입한다.
export function setup() {
  const email = __ENV.K6_TEST_EMAIL;
  const password = __ENV.K6_TEST_PASSWORD;
  if (!email || !password) {
    throw new Error('환경변수 K6_TEST_EMAIL / K6_TEST_PASSWORD 를 설정하세요.');
  }

  const res = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ email, password }),
    { headers: { 'Content-Type': 'application/json' }, tags: { name: 'auth_login' } }
  );
  check(res, { 'login is 200': (r) => r.status === 200 });

  const token = res.json('accessToken');
  if (!token) {
    throw new Error(`로그인 실패: status=${res.status}, body=${res.body}`);
  }
  return { token };
}

export default function (data) {
  const authParams = {
    headers: { Authorization: `Bearer ${data.token}` },
  };

  // 상품 목록 조회 (keyword 없이 조회)
  const list = http.get(`${BASE_URL}/api/products?page=0&size=10`, {
    ...authParams,
    tags: { name: 'products_list' },
  });
  check(list, { 'list is 200': (r) => r.status === 200 });

  // 상품 검색 (keyword 지정)
  const keyword = encodeURIComponent(SEARCH_KEYWORD);
  const search = http.get(`${BASE_URL}/api/products?keyword=${keyword}&page=0&size=10`, {
    ...authParams,
    tags: { name: 'products_search' },
  });
  check(search, { 'search is 200': (r) => r.status === 200 });

  sleep(1);
}
