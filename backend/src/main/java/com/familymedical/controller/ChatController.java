package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.service.ChatMessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/history")
    public Result<?> history(HttpServletRequest request,
                             @RequestParam Long otherUserId,
                             @RequestParam(defaultValue = "50") int limit) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(chatMessageService.getChatHistory(userId, otherUserId, limit));
    }

    @GetMapping("/unread")
    public Result<?> unread(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(chatMessageService.getUnreadMessages(userId));
    }

    @PutMapping("/read/{senderId}")
    public Result<?> markAsRead(@PathVariable Long senderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        chatMessageService.markAsRead(senderId, userId);
        return Result.success();
    }
}
