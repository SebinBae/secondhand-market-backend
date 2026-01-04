package com.sebin.secondhand_market.domain.product.repository.search;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sebin.secondhand_market.domain.product.dto.request.ProductSearchRequest;
import com.sebin.secondhand_market.domain.product.entity.ProductEntity;
import com.sebin.secondhand_market.domain.product.entity.QProductEntity;
import com.sebin.secondhand_market.domain.product.type.ProductSortType;
import com.sebin.secondhand_market.domain.product.type.ProductStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ProductEntity> search(ProductSearchRequest request,
      Pageable pageable) {

    QProductEntity product = QProductEntity.productEntity;

    List<ProductEntity> content = queryFactory.
        selectFrom(product)
        .where(
            titleContains(request.getKeyword())
            , statusEq(request.getResolvedStatus())
        )
        .orderBy(sortCondition(request.getProductSortType()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(product.count())
        .from(product)
        .where(
            titleContains(request.getKeyword()),
            statusEq(request.getResolvedStatus())
        )
        .fetchOne();

    return new PageImpl<>(content, pageable, total);
  }

  // 상품명 검색
  private BooleanExpression titleContains(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }

    return QProductEntity.productEntity.title.containsIgnoreCase(keyword);

  }

  // 상태 필터
  private BooleanExpression statusEq(ProductStatus status) {
    if (status == null) {
      return null;
    }
    return QProductEntity.productEntity.productStatus.eq(status);
  }

  // 정렬 조건
  private OrderSpecifier<?> sortCondition(ProductSortType sortType) {
    if (sortType == null) {
      return QProductEntity.productEntity.createdAt.desc();
    }

    return switch (sortType) {
      case LATEST -> QProductEntity.productEntity.createdAt.desc(); // 상품 등록일이 가장 최신 순서로 정렬을 원하는 경우
      case PRICE_ASC -> QProductEntity.productEntity.price.asc(); // 상품 가격이 가장 낮은 순서대로 정렬을 원하는 경우
      case PRICE_DESC -> QProductEntity.productEntity.price.desc(); // 상품 가격이 가장 높은 순서대로 정렬을 원하는 경우
    };
  }

}
