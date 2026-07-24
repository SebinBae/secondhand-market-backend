package com.sebin.secondhand_market.domain.product.repository;

import com.sebin.secondhand_market.domain.product.entity.ProductImageEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {

  long countByProductId(UUID productId);

  List<ProductImageEntity> findByProductIdOrderByDisplayOrderAsc(UUID productId);
}
