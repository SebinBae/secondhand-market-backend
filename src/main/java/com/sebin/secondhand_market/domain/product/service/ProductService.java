package com.sebin.secondhand_market.domain.product.service;

import com.sebin.secondhand_market.domain.product.dto.request.ProductCreateRequest;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.repository.ProductRepository;
import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.domain.user.repository.UserRepository;
import com.sebin.secondhand_market.global.exception.product.ProductAccessDeniedException;
import com.sebin.secondhand_market.global.exception.product.ProductNotFoundException;
import com.sebin.secondhand_market.global.exception.user.UserNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  // 상품 등록 처리
  public UUID create(ProductCreateRequest request, UUID userId) {
    UserEntity seller = userRepository.findById(userId).orElseThrow(
        UserNotFoundException::new
    );

    ProductEntity product = new ProductEntity(
        request.getTitle(),
        request.getPrice(),
        request.getDescription(),
        request.getProductCategory(),
        ProductStatus.SELLING,
        seller
    );

    return productRepository.save(product).getId();

  }

  // 상품 정보 수정
  public void update(UUID productId, ProductCreateRequest request, UUID userId) {

    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);

    // 판매자 본인 상품이 아닌 경우 로직 처리
    if (!product.getSeller().getId().equals(userId)) {
      throw new ProductAccessDeniedException();
    }

    product.update(
        request.getTitle(),
        request.getPrice(),
        request.getDescription(),
        request.getProductCategory()
    );

  }

  // 상품 정보 삭제
  public void delete(UUID productId, UUID userId) {

    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);

    // 판매자 본인 상품이 아닌 경우 로직 처리
    if (!product.getSeller().getId().equals(userId)) {
      throw new ProductAccessDeniedException();
    }

    productRepository.delete(product);

  }

  // 상품 상태 변경(판매중 -> 판매완료 / 판매완료 -> 판매중)
  public void changeStatus(
      UUID productId, ProductStatus productStatus, UUID userId
  ) {
    ProductEntity product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);

    // 판매자 본인 상품이 아닌 경우 로직 처리
    if (!product.getSeller().getId().equals(userId)) {
      throw new ProductAccessDeniedException();
    }

    product.changeStatus(productStatus);
  }


}
