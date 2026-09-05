package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.NotificationRepository;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl")
class NotificationServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private UserService userService;

  @Captor
  private ArgumentCaptor<Notification> notificationCaptor;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @Test
  @DisplayName("returns the first page of the notifications of the connected user")
  void getAllUserNotificationsReturnsTheFirstPage() {
    User connectedUser = TestFixtures.user(1L);
    List<Notification> notifications = List.of(new Notification("content", "match", new Date(), false, connectedUser));
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(notificationRepository.getAllUserNotifications(1L, PageRequest.of(0, 6))).thenReturn(notifications);

    assertThat(notificationService.getAllUserNotifications()).isEqualTo(notifications);
  }

  @Test
  @DisplayName("marks every notification of the connected user as seen")
  void markUserNotificationsAsSeenMarksThemAll() {
    User connectedUser = TestFixtures.user(1L);
    Notification first = new Notification("first", "match", new Date(), false, connectedUser);
    Notification second = new Notification("second", "unmatch", new Date(), false, connectedUser);
    connectedUser.setNotifications(List.of(first, second));
    when(userService.getConnectedUser()).thenReturn(connectedUser);

    notificationService.markUserNotificationsAsSeen();

    assertThat(first.getSeen()).isTrue();
    assertThat(second.getSeen()).isTrue();
    verify(notificationRepository).saveAll(connectedUser.getNotifications());
  }

  @Test
  @DisplayName("sends a notification to a user designated by its identifier")
  void sendNotificationResolvesTheReceiverFromItsIdentifier() {
    User receiver = TestFixtures.user(2L);
    when(userService.getUserById(2L)).thenReturn(receiver);

    notificationService.sendNotification("content", "match", 2L);

    verify(notificationRepository).save(notificationCaptor.capture());
    assertThat(notificationCaptor.getValue().getFkUser()).isSameAs(receiver);
  }

  @Test
  @DisplayName("sends an unseen notification to an already loaded user")
  void sendNotificationSavesAnUnseenNotification() {
    User receiver = TestFixtures.user(2L);

    notificationService.sendNotification("content", "match", receiver);

    verify(notificationRepository).save(notificationCaptor.capture());
    Notification saved = notificationCaptor.getValue();
    assertThat(saved.getContent()).isEqualTo("content");
    assertThat(saved.getLink()).isEqualTo("match");
    assertThat(saved.getSeen()).isFalse();
    assertThat(saved.getDate()).isNotNull();
    assertThat(saved.getFkUser()).isSameAs(receiver);
  }
}
