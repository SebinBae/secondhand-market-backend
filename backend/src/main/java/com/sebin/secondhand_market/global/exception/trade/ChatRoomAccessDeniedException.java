package com.sebin.secondhand_market.global.exception.trade;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ChatRoomAccessDeniedException extends BusinessException {

  public ChatRoomAccessDeniedException() {
    super(ErrorCode.CHATROOM_ACCESS_DENIED);
  }
}
