package com.sebin.secondhand_market.domain.product.controller;

import com.sebin.secondhand_market.domain.product.dto.request.ProductCreateRequest;
import com.sebin.secondhand_market.domain.product.dto.request.ProductStatusChangeRequest;
import com.sebin.secondhand_market.domain.product.dto.response.ProductCreatedResponse;
import com.sebin.secondhand_market.domain.product.service.ProductService;
import com.sebin.secondhand_market.global.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  // 상품 등록
  @PostMapping
  public ResponseEntity<ProductCreatedResponse> create(
      @RequestBody @Valid ProductCreateRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    UUID productId = productService.create(request, userPrincipal.getUserId());

    ProductCreatedResponse response = new ProductCreatedResponse(
        HttpStatus.CREATED,
        "상품이 성공적으로 등록완료 되었습니다!",
        productId
    );

    return ResponseEntity.ok(response);
  }

  // 상품 수정
  @PutMapping("/{productId}")
  public ResponseEntity<ProductCreatedResponse> update(
      @PathVariable UUID productId,
      @RequestBody @Valid ProductCreateRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    productService.update(productId, request, userPrincipal.getUserId());

    ProductCreatedResponse response = new ProductCreatedResponse(
        HttpStatus.OK,
        "상품이 성공적으로 수정되었습니다!",
        productId
    );

    return ResponseEntity.ok(response);
  }

  // 상품 삭제
  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID productId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    productService.delete(productId, userPrincipal.getUserId());

    return ResponseEntity.noContent().build();
  }

  // 상품 상태 변경
  @PatchMapping("/{productId}/status")
  public ResponseEntity<ProductCreatedResponse> changeStatus(
      @PathVariable UUID productId,
      @RequestBody @Valid ProductStatusChangeRequest request,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    productService.changeStatus(productId, request.getProductStatus(), userPrincipal.getUserId());

    ProductCreatedResponse response = new ProductCreatedResponse(
        HttpStatus.OK,
        "상품 상태가 성공적으로 수정되었습니다!",
        productId
    );

    return ResponseEntity.ok(response);
  }

}
