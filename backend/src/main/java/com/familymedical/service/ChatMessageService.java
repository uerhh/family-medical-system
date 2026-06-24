package com.familymedical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {

    void saveMessage(ChatMessage message);

    List<ChatMessage> getChatHistory(Long userId1, Long userId2, int limit);

    List<ChatMessage> getUnreadMessages(Long userId);

    void markAsRead(Long senderId, Long receiverId);
}
