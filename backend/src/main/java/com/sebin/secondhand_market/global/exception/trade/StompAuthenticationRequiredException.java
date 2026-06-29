package com.sebin.secondhand_market.global.exception.trade;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class StompAuthenticationRequiredException extends BusinessException {

  public StompAuthenticationRequiredException() {
    super(ErrorCode.UNAUTHENTICATED_STOMP_USER);
  }
}
