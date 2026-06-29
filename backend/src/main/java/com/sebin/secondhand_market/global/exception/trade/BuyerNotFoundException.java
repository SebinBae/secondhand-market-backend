package com.sebin.secondhand_market.global.exception.trade;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class BuyerNotFoundException extends BusinessException {

  public BuyerNotFoundException() {
    super(ErrorCode.BUYER_NOT_FOUND);
  }
}
