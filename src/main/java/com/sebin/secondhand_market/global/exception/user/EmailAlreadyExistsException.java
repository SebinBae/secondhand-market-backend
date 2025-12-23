package com.sebin.secondhand_market.global.exception.user;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException() {
    super(ErrorCode.EMAIL_ALREADY_EXISTS);
  }
}
