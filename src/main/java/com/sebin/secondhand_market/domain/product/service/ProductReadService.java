package com.sebin.secondhand_market.domain.product.service;

import com.sebin.secondhand_market.domain.product.dto.request.ProductSearchRequest;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.repository.search.ProductQueryRepository;
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

  public Page<ProductEntity> search(ProductSearchRequest request, int page, int size){
    Pageable pageable = PageRequest.of(page, size);

    return productQueryRepository.search(request, pageable);
  }
}
