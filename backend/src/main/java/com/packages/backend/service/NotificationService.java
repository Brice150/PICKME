package com.packages.backend.service;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserService userService;

  @Autowired
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
}
