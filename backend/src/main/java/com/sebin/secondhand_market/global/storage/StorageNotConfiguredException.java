package com.sebin.secondhand_market.global.storage;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class StorageNotConfiguredException extends BusinessException {

  public StorageNotConfiguredException() {
    super(ErrorCode.IMAGE_STORAGE_NOT_CONFIGURED);
  }
}
