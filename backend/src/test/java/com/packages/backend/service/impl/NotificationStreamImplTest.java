package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationStreamImpl")
class NotificationStreamImplTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private NotificationStreamImpl notificationStream;

  /**
   * Stands in for the stream of a browser: it records what is written on it, can be made to fail
   * the way a closed connection does, and keeps the callbacks so that a test can play the events
   * the servlet container would normally raise.
   */
  private static class FakeStream extends SseEmitter {
    private final List<Object> events = new ArrayList<>();
    private boolean broken;
    private Runnable onCompletion;
    private Runnable onTimeout;
    private Consumer<Throwable> onError;

    @Override
    public void send(SseEventBuilder builder) throws IOException {
      if (broken) {
        throw new IOException("the browser is gone");
      }
      events.add(builder);
    }

    @Override
    public synchronized void onCompletion(Runnable callback) {
      onCompletion = callback;
    }

    @Override
    public synchronized void onTimeout(Runnable callback) {
      onTimeout = callback;
    }

    @Override
    public synchronized void onError(Consumer<Throwable> callback) {
      onError = callback;
    }
  }

  /** Opens a stream for a user, with an emitter the test can read back. */
  private FakeStream openStreamFor(Long userId) {
    FakeStream stream = new FakeStream();
    notificationStream.register(userId, stream);
    return stream;
  }

  @Test
  @DisplayName("greets the browser as soon as the stream is open")
  void openingAStreamGreetsTheBrowser() {
    when(userService.getConnectedUser()).thenReturn(TestFixtures.user(1L));

    SseEmitter emitter = notificationStream.openForConnectedUser();

    assertThat(emitter).isNotNull();
    assertThat(emitter.getTimeout()).isEqualTo(30 * 60 * 1000L);
  }

  @Test
  @DisplayName("pushes an event to every stream the receiver has open")
  void publishReachesEveryStreamOfTheReceiver() {
    FakeStream firstTab = openStreamFor(1L);
    FakeStream secondTab = openStreamFor(1L);

    notificationStream.publish(1L);

    // One greeting when the stream opened, then the notification.
    assertThat(firstTab.events).hasSize(2);
    assertThat(secondTab.events).hasSize(2);
  }

  @Test
  @DisplayName("never pushes a notification to somebody else")
  void publishOnlyReachesTheReceiver() {
    FakeStream mine = openStreamFor(1L);
    FakeStream somebodyElse = openStreamFor(2L);

    notificationStream.publish(1L);

    assertThat(mine.events).hasSize(2);
    assertThat(somebodyElse.events).hasSize(1);
  }

  @Test
  @DisplayName("stays quiet when the receiver is not listening")
  void publishDoesNothingWithoutAStream() {
    assertThatCode(() -> notificationStream.publish(99L)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("drops a stream the browser has left")
  void aBrokenStreamIsDropped() {
    FakeStream stream = openStreamFor(1L);
    stream.broken = true;

    notificationStream.publish(1L);

    // The stream has been forgotten, so a second push finds nobody left to write to.
    stream.broken = false;
    notificationStream.publish(1L);
    assertThat(stream.events).hasSize(1);
  }

  @Test
  @DisplayName("forgets a stream the browser closed, timed out or broke")
  void theClosingCallbacksForgetTheStream() {
    FakeStream closed = openStreamFor(1L);
    FakeStream timedOut = openStreamFor(2L);
    FakeStream failed = openStreamFor(3L);

    closed.onCompletion.run();
    timedOut.onTimeout.run();
    failed.onError.accept(new IOException("reset"));

    notificationStream.publish(1L);
    notificationStream.publish(2L);
    notificationStream.publish(3L);

    // Only the greeting each of them received when it opened.
    assertThat(closed.events).hasSize(1);
    assertThat(timedOut.events).hasSize(1);
    assertThat(failed.events).hasSize(1);
  }

  @Test
  @DisplayName("keeps the other tabs of a user when one of them closes")
  void closingOneTabKeepsTheOthers() {
    FakeStream closedTab = openStreamFor(1L);
    FakeStream remainingTab = openStreamFor(1L);

    closedTab.onCompletion.run();
    notificationStream.publish(1L);

    assertThat(closedTab.events).hasSize(1);
    assertThat(remainingTab.events).hasSize(2);
  }
}
