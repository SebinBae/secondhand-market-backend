package com.sebin.secondhand_market.domain.chat.dto.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomCreateRequest {

  UUID productId;
}
