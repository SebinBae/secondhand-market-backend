package com.sebin.secondhand_market.domain.product.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductImageUploadResponse {

  private UUID productId;
  private List<String> imageUrls;
}
