package com.sebin.secondhand_market.global.exception.trade;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class SenderNotFoundException extends BusinessException {

  public SenderNotFoundException() {
    super(ErrorCode.SENDER_NOT_FOUND);
  }
}
