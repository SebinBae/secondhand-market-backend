package com.sebin.secondhand_market.global.websocket;

import java.util.UUID;

public final class StompDestination {

  private StompDestination() {
  }

  public static final String CHAT_TOPIC_PREFIX = "/topic/chat.";

  public static String chatRoom(UUID roomId) {
    return CHAT_TOPIC_PREFIX + roomId;
  }
}
