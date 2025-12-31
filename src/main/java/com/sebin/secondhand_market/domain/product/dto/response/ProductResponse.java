package com.sebin.secondhand_market.domain.product.dto.response;

import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.type.ProductCategory;
import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {

  private UUID sellerId;
  private String title;
  private int price;
  private String description;
  private ProductStatus productStatus;
  private ProductCategory productCategory;

  public static ProductResponse from(ProductEntity productEntity){
    return new ProductResponse(productEntity.getSeller().getId(),
        productEntity.getTitle(),
        productEntity.getPrice(),
        productEntity.getDescription(),
        productEntity.getProductStatus(),
        productEntity.getProductCategory()
    );
  }
}
