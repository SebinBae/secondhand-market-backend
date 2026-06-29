package com.sebin.secondhand_market.global.exception.user;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class InvalidPasswordException extends BusinessException {

  public InvalidPasswordException() {
    super(ErrorCode.INVALID_PASSWORD);
  }
}
