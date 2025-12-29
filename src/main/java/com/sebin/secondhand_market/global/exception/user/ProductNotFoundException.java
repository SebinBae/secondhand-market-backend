package com.sebin.secondhand_market.global.exception.user;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ProductNotFoundException extends BusinessException {

  public ProductNotFoundException() {
    super(ErrorCode.PRODUCT_NOT_FOUND);
  }
}
