package com.sebin.secondhand_market.global.exception.user;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ProductModifiedAccessDeniedException extends BusinessException {

  public ProductModifiedAccessDeniedException() {
    super(ErrorCode.PRODUCT_ACCESS_DENIED);
  }
}
