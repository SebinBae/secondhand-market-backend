package com.sebin.secondhand_market.domain.product.entity;

import com.sebin.secondhand_market.domain.product.type.ProductCategory;
import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import com.sebin.secondhand_market.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "seller_id")
  private UserEntity seller;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false)
  private int price;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ProductCategory productCategory;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ProductStatus productStatus;

  // 기본 생성자
  public ProductEntity(
      String title,
      int price,
      String description,
      ProductCategory productCategory,
      ProductStatus productStatus,
      UserEntity seller
  ) {
    this.title = title;
    this.price = price;
    this.description = description;
    this.productCategory = productCategory;
    this.productStatus = productStatus;
    this.seller = seller;
  }

  // 수정
  public void update(
      String title,
      int price,
      String description,
      ProductCategory productCategory
  ) {
    this.title = title;
    this.price = price;
    this.description = description;
    this.productCategory = productCategory;

  }

  // 제품 상태 변경
  public void changeStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }

}
