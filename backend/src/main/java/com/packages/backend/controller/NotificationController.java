package com.packages.backend.controller;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.NotificationStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notification")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class NotificationController {
  private final NotificationService notificationService;
  private final NotificationStream notificationStream;

  public NotificationController(NotificationService notificationService, NotificationStream notificationStream) {
    this.notificationService = notificationService;
    this.notificationStream = notificationStream;
  }

  /**
   * Opens the stream the menu listens on. The event carries no payload: the browser reads its
   * notifications back from /notification/all, so nothing bypasses the usual checks.
   *
   * @return the stream of the connected user
   */
  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamUserNotifications() {
    return notificationStream.openForConnectedUser();
  }

  @GetMapping("/all")
  public ResponseEntity<List<Notification>> getAllUserNotifications() {
    return new ResponseEntity<>(notificationService.getAllUserNotifications(), HttpStatus.OK);
  }

  @PutMapping()
  public ResponseEntity<Void> markUserNotificationsAsSeen() {
    notificationService.markUserNotificationsAsSeen();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
