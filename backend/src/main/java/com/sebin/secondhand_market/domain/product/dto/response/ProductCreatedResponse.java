package com.sebin.secondhand_market.domain.product.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ProductCreatedResponse {

  private HttpStatus httpStatus;
  private String message;
  private UUID productId;

}
