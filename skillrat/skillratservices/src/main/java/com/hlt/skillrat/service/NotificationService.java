package com.hlt.skillrat.service;

import com.hlt.skillrat.model.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationModel save(NotificationModel jtNotification);

    NotificationModel findById(Long id);

    void removeNotification(NotificationModel notificationModel);

    Page<NotificationModel> findByUser(Long userId, Pageable pageable);

    void clearAllNotifications(Long userId);
}
