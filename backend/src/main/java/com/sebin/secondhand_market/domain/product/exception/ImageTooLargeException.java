package com.sebin.secondhand_market.domain.product.exception;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ImageTooLargeException extends BusinessException {

  public ImageTooLargeException() {
    super(ErrorCode.IMAGE_TOO_LARGE);
  }
}
