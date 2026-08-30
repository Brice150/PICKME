package com.packages.backend.service;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;

import java.util.List;

public interface NotificationService {

  /**
   * Returns the most recent notifications of the connected user.
   *
   * @return the notifications displayed in the menu
   */
  List<Notification> getAllUserNotifications();

  /**
   * Marks every notification of the connected user as seen.
   */
  void markUserNotificationsAsSeen();

  /**
   * Sends a notification to a user designated by its identifier.
   *
   * @param content text of the notification
   * @param link    screen the notification points to
   * @param userId  identifier of the receiver
   */
  void sendNotification(String content, String link, Long userId);

  /**
   * Sends a notification to an already loaded user.
   *
   * @param content text of the notification
   * @param link    screen the notification points to
   * @param user    receiver
   */
  void sendNotification(String content, String link, User user);
}
