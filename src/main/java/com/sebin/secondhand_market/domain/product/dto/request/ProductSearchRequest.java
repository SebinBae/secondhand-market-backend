package com.sebin.secondhand_market.domain.product.dto.request;

import com.sebin.secondhand_market.domain.product.type.ProductSortType;
import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ProductSearchRequest {

  // 상품명 검색
  private String keyword;
  // 상품 상태 검색
  private ProductStatus productStatus;
  // 정렬 기준
  private ProductSortType productSortType;

  public ProductStatus getResolvedStatus(){
    return productStatus != null ? productStatus : ProductStatus.SELLING;
  }

}
