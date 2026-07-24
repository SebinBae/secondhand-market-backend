package com.sebin.secondhand_market.domain.product.entity;

import com.sebin.secondhand_market.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_images")
public class ProductImageEntity extends BaseEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  @Column(nullable = false)
  private String url;

  // 상품 내 이미지 표시 순서 (0부터)
  @Column(nullable = false)
  private int displayOrder;

  public ProductImageEntity(ProductEntity product, String url, int displayOrder) {
    this.product = product;
    this.url = url;
    this.displayOrder = displayOrder;
  }
}
