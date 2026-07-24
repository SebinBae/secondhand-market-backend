package com.sebin.secondhand_market.domain.product.exception;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class InvalidImageFormatException extends BusinessException {

  public InvalidImageFormatException() {
    super(ErrorCode.IMAGE_INVALID_FORMAT);
  }
}
