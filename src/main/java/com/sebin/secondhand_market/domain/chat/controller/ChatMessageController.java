package com.sebin.secondhand_market.domain.chat.controller;

import com.sebin.secondhand_market.domain.chat.dto.response.ChatMessageResponse;
import com.sebin.secondhand_market.domain.chat.service.ChatMessageService;
import com.sebin.secondhand_market.global.security.UserPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms/{roomId}/messages")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ChatMessageController {

  private final ChatMessageService chatMessageService;

  @GetMapping
  public Page<ChatMessageResponse> messages(
      @PathVariable UUID roomId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

    return chatMessageService.getMessages(
        roomId,
        principal.getUserId(),
        pageable
    );
  }
}
