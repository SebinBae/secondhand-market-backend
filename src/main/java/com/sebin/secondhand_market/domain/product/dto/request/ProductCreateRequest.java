package com.sebin.secondhand_market.domain.product.dto.request;

import com.sebin.secondhand_market.domain.product.type.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

  @NotBlank
  @Size(max = 100)
  private String title;

  @Min(0)
  private int price;

  @NotBlank
  private String description;

  @NotNull
  private ProductCategory productCategory;

}
