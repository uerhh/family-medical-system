package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.Notification;

public interface NotificationService extends IService<Notification> {
    IPage<Notification> getByUserId(Long userId, int page, int size);
    void markAsRead(Long id);
    void createNotification(Notification notification);
}
