package com.sebin.secondhand_market.global.exception.user;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
