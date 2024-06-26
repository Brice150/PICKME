package com.packages.backend.service;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserService userService;

  public NotificationService(NotificationRepository notificationRepository, UserService userService) {
    this.notificationRepository = notificationRepository;
    this.userService = userService;
  }

  public List<Notification> getAllUserNotifications() {
    User connectedUser = userService.getConnectedUser();
    return notificationRepository.getAllUserNotifications(connectedUser.getId(), PageRequest.of(0, 6));
  }

  public void markUserNotificationsAsSeen() {
    User connectedUser = userService.getConnectedUser();
    connectedUser.getNotifications().forEach(notification -> notification.setSeen(true));
    notificationRepository.saveAll(connectedUser.getNotifications());
  }

  public void sendNotification(String content, String link, Long userId) {
    User user = userService.getUserById(userId);
    sendNotification(content, link, user);
  }

  public void sendNotification(String content, String link, User user) {
    Notification notification = new Notification(content, link, new Date(), false, user);
    notificationRepository.save(notification);
  }
}
