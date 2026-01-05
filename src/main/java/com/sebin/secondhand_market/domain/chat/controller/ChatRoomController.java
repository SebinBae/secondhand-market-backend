package com.sebin.secondhand_market.domain.chat.controller;

import com.sebin.secondhand_market.domain.chat.dto.request.ChatRoomCreateRequest;
import com.sebin.secondhand_market.domain.chat.dto.response.ChatRoomResponse;
import com.sebin.secondhand_market.domain.chat.service.ChatRoomService;
import com.sebin.secondhand_market.global.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  // 채팅방 생성 또는 조회
  @PostMapping
  public ChatRoomResponse createRoom(
      @RequestBody ChatRoomCreateRequest request,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    return chatRoomService.createOrGetRoom(
        request.getProductId(),
        principal.getUserId()
    );
  }

  // 유저 채팅 목록 조회
  @GetMapping
  public Page<ChatRoomResponse> myRooms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    return chatRoomService.getMyChatRooms(principal.getUserId(), page, size);
  }

}
