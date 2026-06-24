package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.ChatMessage;
import com.familymedical.entity.User;
import com.familymedical.mapper.ChatMessageMapper;
import com.familymedical.mapper.UserMapper;
import com.familymedical.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
        implements ChatMessageService {

    private final UserMapper userMapper;

    @Override
    public void saveMessage(ChatMessage message) {
        message.setIsRead(0);
        save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long userId1, Long userId2, int limit) {
        List<ChatMessage> messages = lambdaQuery()
                .and(w -> w
                        .and(inner -> inner.eq(ChatMessage::getSenderId, userId1).eq(ChatMessage::getReceiverId, userId2))
                        .or(inner -> inner.eq(ChatMessage::getSenderId, userId2).eq(ChatMessage::getReceiverId, userId1)))
                .orderByDesc(ChatMessage::getCreateTime)
                .last("LIMIT " + limit)
                .list();
        fillSenderInfo(messages);
        return messages;
    }

    @Override
    public List<ChatMessage> getUnreadMessages(Long userId) {
        List<ChatMessage> messages = lambdaQuery()
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .orderByDesc(ChatMessage::getCreateTime)
                .list();
        fillSenderInfo(messages);
        return messages;
    }

    @Override
    public void markAsRead(Long senderId, Long receiverId) {
        lambdaUpdate()
                .eq(ChatMessage::getSenderId, senderId)
                .eq(ChatMessage::getReceiverId, receiverId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1)
                .update();
    }

    private void fillSenderInfo(List<ChatMessage> messages) {
        if (messages.isEmpty()) return;
        Set<Long> senderIds = messages.stream()
                .map(ChatMessage::getSenderId)
                .collect(Collectors.toSet());
        var userMap = userMapper.selectBatchIds(senderIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        for (ChatMessage msg : messages) {
            User sender = userMap.get(msg.getSenderId());
            if (sender != null) {
                msg.setSenderName(sender.getRealName());
                msg.setSenderAvatar(sender.getAvatar());
            }
        }
    }
}
