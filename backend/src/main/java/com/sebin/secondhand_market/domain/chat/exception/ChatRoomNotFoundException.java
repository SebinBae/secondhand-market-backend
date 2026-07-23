package com.sebin.secondhand_market.domain.chat.exception;

import com.sebin.secondhand_market.global.exception.BusinessException;
import com.sebin.secondhand_market.global.exception.ErrorCode;

public class ChatRoomNotFoundException extends BusinessException {

  public ChatRoomNotFoundException() {
    super(ErrorCode.CHATROOM_NOT_FOUND);
  }
}
