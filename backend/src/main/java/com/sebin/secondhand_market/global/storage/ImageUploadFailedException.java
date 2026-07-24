package com.sebin.secondhand_market.global.storage;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ImageUploadFailedException extends BusinessException {

  public ImageUploadFailedException() {
    super(ErrorCode.IMAGE_UPLOAD_FAILED);
  }
}
