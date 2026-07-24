package com.sebin.secondhand_market.global.storage;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Supabase Storage(public bucket) 기반 이미지 저장소 구현.
 *
 * 접속 정보(url/service-key)는 환경변수로 주입되며, 값이 없으면 빈은 정상 생성되지만
 * 실제 업로드 시점에 {@link StorageNotConfiguredException}으로 실패한다(지연 실패).
 * → .env 없이도 애플리케이션이 기동 가능하다.
 */
@Slf4j
@Component
public class SupabaseImageStorage implements ImageStorage {

  private final String url;
  private final String serviceKey;
  private final String bucket;
  private final RestClient restClient;

  public SupabaseImageStorage(
      @Value("${supabase.url:}") String url,
      @Value("${supabase.service-key:}") String serviceKey,
      @Value("${supabase.bucket:product-images}") String bucket
  ) {
    this.url = url;
    this.serviceKey = serviceKey;
    this.bucket = bucket;
    this.restClient = RestClient.create();
  }

  @Override
  public String upload(byte[] content, String originalFilename, String contentType) {
    if (!StringUtils.hasText(url) || !StringUtils.hasText(serviceKey)) {
      throw new StorageNotConfiguredException();
    }

    String objectKey = UUID.randomUUID() + extension(originalFilename);
    String uploadUrl = url + "/storage/v1/object/" + bucket + "/" + objectKey;

    try {
      restClient.post()
          .uri(uploadUrl)
          .header("Authorization", "Bearer " + serviceKey)
          .header("apikey", serviceKey)
          .contentType(resolveMediaType(contentType))
          .body(content)
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientException e) {
      log.error("Supabase 이미지 업로드 실패: key={}", objectKey, e);
      throw new ImageUploadFailedException();
    }

    // public bucket 공개 URL
    return url + "/storage/v1/object/public/" + bucket + "/" + objectKey;
  }

  private MediaType resolveMediaType(String contentType) {
    try {
      return MediaType.parseMediaType(contentType);
    } catch (Exception e) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }

  private String extension(String originalFilename) {
    if (originalFilename == null) {
      return "";
    }
    int dot = originalFilename.lastIndexOf('.');
    return dot >= 0 ? originalFilename.substring(dot) : "";
  }
}
