# task-05: 상품 이미지 업로드

- 브랜치: `feat/product-image-upload`
- 커밋 타입: `feat:`
- 선행 조건: 없음

## 배경

상품에 이미지가 아직 없다. AI 등록 어시스턴트(비전 분석)의 선행 조건이며, React 상품 화면에도 필요하다.

## 설계 결정 (변경 금지)

- 저장소: **Supabase Storage** (public bucket `product-images`). Render 배포 환경은 디스크가 휘발성이라 로컬 파일 저장 불가. 접속 정보는 `.env`(`SUPABASE_URL`, `SUPABASE_SERVICE_KEY`) 주입.
- 코드에서는 저장소를 `ImageStorage` 인터페이스로 추상화하고 Supabase 구현체를 둔다 (추후 교체 가능).
- DB에는 URL만 저장: `ProductImageEntity` (product 1:N, 최대 10장, 표시 순서 필드). <!-- 최대 장수 3→10, 사용자 결정 -->>

## 작업 단계

1. `domain/product`에 `ProductImageEntity` + 연관관계 (Product 1:N)
2. `global/storage` 또는 `domain/product/storage`에 `ImageStorage` 인터페이스 + Supabase 구현체 (REST API 사용, 별도 SDK 의존성 최소화)
3. 업로드 API: `POST /api/products/{id}/images` (List<MultipartFile>, 본인 상품만) — 검증: jpg/png/webp, 5MB 이하, 상품당 최대 10장
4. 상품 상세/목록 응답 DTO에 이미지 URL 포함
5. 테스트: 검증 규칙(형식·크기·개수·본인 확인) 단위 테스트. Supabase 호출은 `ImageStorage` 목으로 대체

## 하지 말 것

- 이미지 리사이징/썸네일 생성 금지 (현 단계 범위 밖)
- 실 Supabase에 의존하는 테스트 금지 (인터페이스 목 사용)

## 완료 조건 (DoD)

- [ ] 이미지 업로드 → 상품 조회 시 URL 반환이 로컬에서 동작
- [ ] 검증 규칙 4종(형식/크기/개수/본인)이 테스트로 증명됨
- [ ] ArchUnit 통과 (storage 추상화가 경계 규칙 위반하지 않음)
- [ ] `.env` 없이도 애플리케이션 기동 가능 (Storage 빈은 지연 실패)
