package com.packages.backend.service.impl;

import com.packages.backend.service.NotificationStream;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Keeps the streams the connected users are listening on, and warns them when something happens.
 * <p>
 * The event carries no payload: it only tells the browser that its notifications changed, and the
 * browser reads them back over the regular endpoint. That keeps a single place where the
 * notifications are read and filtered, and nothing sensitive travels on a connection the server
 * cannot re-check.
 * <p>
 * The registry lives in memory, so it warns the users connected to this instance. A deployment
 * spread over several of them would need the events to travel between the instances first.
 */
@Service
public class NotificationStreamImpl implements NotificationStream {

  // Long enough for the browser not to reconnect all the time, short enough for a dead connection
  // to be noticed rather than held forever.
  private static final long STREAM_TIMEOUT_MS = 30 * 60 * 1000L;

  private final Map<Long, List<SseEmitter>> streamsByUserId = new ConcurrentHashMap<>();
  private final UserService userService;

  public NotificationStreamImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public SseEmitter openForConnectedUser() {
    Long userId = userService.getConnectedUser().getId();
    return register(userId, new SseEmitter(STREAM_TIMEOUT_MS));
  }

  /**
   * Starts following a stream on behalf of a user. Package private so that a test can hand in an
   * emitter it can read back.
   *
   * @param userId  identifier of the listener
   * @param emitter stream to follow
   * @return the stream that has been registered
   */
  SseEmitter register(Long userId, SseEmitter emitter) {
    streamsByUserId.computeIfAbsent(userId, id -> new CopyOnWriteArrayList<>()).add(emitter);
    emitter.onCompletion(() -> close(userId, emitter));
    emitter.onTimeout(() -> close(userId, emitter));
    emitter.onError(error -> close(userId, emitter));
    // Sent right away so that the browser knows the stream is live rather than still connecting.
    send(emitter, "connected");
    return emitter;
  }

  @Override
  public void publish(Long userId) {
    List<SseEmitter> streams = streamsByUserId.get(userId);
    if (streams == null) {
      return;
    }
    streams.forEach(emitter -> {
      if (!send(emitter, "notification")) {
        close(userId, emitter);
      }
    });
  }

  /**
   * Writes an event on a stream.
   *
   * @param emitter stream to write to
   * @param name    name of the event
   * @return false when the stream is broken and has to be dropped
   */
  private boolean send(SseEmitter emitter, String name) {
    try {
      emitter.send(SseEmitter.event().name(name).data(""));
      return true;
    } catch (IOException | IllegalStateException brokenStream) {
      // The browser is gone, or the response has already been closed: nothing to report, the
      // notification stays readable on the regular endpoint.
      return false;
    }
  }

  private void close(Long userId, SseEmitter emitter) {
    streamsByUserId.computeIfPresent(userId, (id, streams) -> {
      streams.remove(emitter);
      return streams.isEmpty() ? null : streams;
    });
  }
}
