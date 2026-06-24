package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.Notification;
import com.familymedical.mapper.NotificationMapper;
import com.familymedical.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public IPage<Notification> getByUserId(Long userId, int page, int size) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.orderByDesc(Notification::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public void markAsRead(Long id) {
        lambdaUpdate()
                .eq(Notification::getId, id)
                .set(Notification::getIsRead, 1)
                .update();
    }

    @Override
    public void createNotification(Notification notification) {
        notification.setIsRead(0);
        save(notification);
    }
}
