package com.packages.backend.service.impl;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.NotificationRepository;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

  // Number of notifications displayed in the menu.
  private static final int NOTIFICATION_PAGE_SIZE = 6;

  private final NotificationRepository notificationRepository;
  private final UserService userService;

  public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService) {
    this.notificationRepository = notificationRepository;
    this.userService = userService;
  }

  @Override
  public List<Notification> getAllUserNotifications() {
    User connectedUser = userService.getConnectedUser();
    return notificationRepository.getAllUserNotifications(connectedUser.getId(), PageRequest.of(0, NOTIFICATION_PAGE_SIZE));
  }

  @Override
  @Transactional
  public void markUserNotificationsAsSeen() {
    User connectedUser = userService.getConnectedUser();
    List<Notification> notifications = connectedUser.getNotifications();
    notifications.forEach(notification -> notification.setSeen(true));
    notificationRepository.saveAll(notifications);
  }

  @Override
  public void sendNotification(String content, String link, Long userId) {
    sendNotification(content, link, userService.getUserById(userId));
  }

  @Override
  public void sendNotification(String content, String link, User user) {
    notificationRepository.save(new Notification(content, link, new Date(), false, user));
  }
}
