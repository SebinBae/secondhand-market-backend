package com.sebin.secondhand_market.global.exception.trade;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class InvalidChatTargetException extends BusinessException {

  public InvalidChatTargetException() {
    super(ErrorCode.INVALID_CHAT_TARGET);
  }
}
