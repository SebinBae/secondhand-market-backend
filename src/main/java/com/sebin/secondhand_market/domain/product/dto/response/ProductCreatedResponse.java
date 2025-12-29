package com.sebin.secondhand_market.domain.product.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductCreatedResponse {

  private String status;
  private String message;
  private UUID productId;

}
