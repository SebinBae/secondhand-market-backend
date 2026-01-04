package com.sebin.secondhand_market.global.exception.product;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ProductAccessDeniedException extends BusinessException {

  public ProductAccessDeniedException() {
    super(ErrorCode.PRODUCT_ACCESS_DENIED);
  }
}
