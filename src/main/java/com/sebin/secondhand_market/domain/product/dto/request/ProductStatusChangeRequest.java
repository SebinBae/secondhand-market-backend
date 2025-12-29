package com.sebin.secondhand_market.domain.product.dto.request;

import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStatusChangeRequest {

  @NotNull
  private ProductStatus productStatus;

}
