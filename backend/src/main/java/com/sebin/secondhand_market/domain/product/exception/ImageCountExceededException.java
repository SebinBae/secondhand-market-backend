package com.sebin.secondhand_market.domain.product.exception;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ImageCountExceededException extends BusinessException {

  public ImageCountExceededException() {
    super(ErrorCode.IMAGE_COUNT_EXCEEDED);
  }
}
