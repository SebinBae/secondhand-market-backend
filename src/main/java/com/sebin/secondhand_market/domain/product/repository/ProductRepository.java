package com.sebin.secondhand_market.domain.product.repository;

import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {


}
