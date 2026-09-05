package com.packages.backend.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationStream {

  /**
   * Opens a stream on which the connected user receives their notifications as they are created,
   * instead of asking for them every few seconds.
   *
   * @return the stream to write the events to
   */
  SseEmitter openForConnectedUser();

  /**
   * Pushes a notification to every stream the receiver has open, one per tab. A receiver with no
   * open stream is simply skipped: the notification has been stored and will be read on the next
   * connection.
   *
   * @param userId identifier of the receiver
   */
  void publish(Long userId);
}
