package com.sebin.secondhand_market.domain.product.repository.search;

import com.sebin.secondhand_market.domain.product.dto.request.ProductSearchRequest;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {
  Page<ProductEntity> search(ProductSearchRequest request, Pageable pageable);
}
