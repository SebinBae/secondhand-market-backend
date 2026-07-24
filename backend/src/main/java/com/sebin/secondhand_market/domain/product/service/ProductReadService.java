package com.sebin.secondhand_market.domain.product.service;

import com.sebin.secondhand_market.domain.product.dto.request.ProductSearchRequest;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.exception.ProductNotFoundException;
import com.sebin.secondhand_market.domain.product.repository.ProductRepository;
import com.sebin.secondhand_market.domain.product.repository.search.ProductQueryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductReadService {

  private final ProductQueryRepository productQueryRepository;
  private final ProductRepository productRepository;

  public Page<ProductEntity> search(ProductSearchRequest request, int page, int size){
    Pageable pageable = PageRequest.of(page, size);

    return productQueryRepository.search(request, pageable);
  }

  // 타 도메인 공개 조회 창구 — 상품 단건 조회(없으면 예외)
  @Transactional(readOnly = true)
  public ProductEntity getProductById(UUID productId) {
    return productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);
  }
}
