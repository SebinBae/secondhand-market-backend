package com.sebin.secondhand_market.global.storage;

/**
 * 이미지 저장소 추상화. 구현체(Supabase 등)를 교체할 수 있도록 인터페이스로 분리한다.
 */
public interface ImageStorage {

  /**
   * 이미지를 저장하고 공개 접근 URL을 반환한다.
   *
   * @param content          파일 바이트
   * @param originalFilename 원본 파일명 (확장자 추출용)
   * @param contentType      MIME 타입
   * @return 저장된 이미지의 공개 URL
   */
  String upload(byte[] content, String originalFilename, String contentType);
}
