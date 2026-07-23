package com.sebin.secondhand_market.domain.user.exception;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class RefreshTokenReusedException extends BusinessException {

  public RefreshTokenReusedException() {
    super(ErrorCode.REFRESH_TOKEN_REUSED);
  }
}
