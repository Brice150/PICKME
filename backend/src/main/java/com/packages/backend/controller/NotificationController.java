package com.packages.backend.controller;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class NotificationController {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<Notification>> getAllUserNotifications() {
    return new ResponseEntity<>(notificationService.getAllUserNotifications(), HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<Void> markUserNotificationsAsSeen() {
    notificationService.markUserNotificationsAsSeen();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
