package com.packages.backend.repository;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.Like;
import com.packages.backend.model.entity.Message;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the queries the conversations and the notifications rely on.
 */
@RepositoryTest
@DisplayName("conversation repositories")
class ConversationRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private LikeRepository likeRepository;
  @Autowired
  private DislikeRepository dislikeRepository;
  @Autowired
  private NotificationRepository notificationRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EntityManager entityManager;

  private User connectedUser;
  private User other;

  private static Date daysAgo(int days) {
    return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private User persistUser() {
    User user = TestFixtures.user(null);
    user.setEmail("user" + System.nanoTime() + "@pickme.com");
    User saved = userRepository.saveAndFlush(user);
    entityManager.clear();
    return saved;
  }

  @BeforeEach
  void setUp() {
    connectedUser = persistUser();
    other = persistUser();
  }

  @Test
  @DisplayName("reads both sides of a conversation, oldest message first")
  void getUserMessagesReadsBothSidesInOrder() {
    User stranger = persistUser();
    messageRepository.saveAndFlush(
      new Message("second", daysAgo(1), "them", other.getId(), connectedUser.getId()));
    messageRepository.saveAndFlush(
      new Message("first", daysAgo(2), "me", connectedUser.getId(), other.getId()));
    messageRepository.saveAndFlush(
      new Message("elsewhere", daysAgo(1), "me", connectedUser.getId(), stranger.getId()));

    List<Message> conversation =
      messageRepository.getUserMessagesByFk(connectedUser.getId(), other.getId());

    assertThat(conversation).extracting(Message::getContent).containsExactly("first", "second");
  }

  @Test
  @DisplayName("drops a whole conversation without touching the others")
  void deleteMessagesOnlyDropsTheConversation() {
    User stranger = persistUser();
    messageRepository.saveAndFlush(
      new Message("kept", new Date(), "me", connectedUser.getId(), stranger.getId()));
    messageRepository.saveAndFlush(
      new Message("dropped", new Date(), "me", connectedUser.getId(), other.getId()));

    messageRepository.deleteMessagesByFk(connectedUser.getId(), other.getId());
    entityManager.flush();

    assertThat(messageRepository.getUserMessagesByFk(connectedUser.getId(), other.getId())).isEmpty();
    assertThat(messageRepository.getUserMessagesByFk(connectedUser.getId(), stranger.getId()))
      .hasSize(1);
  }

  @Test
  @DisplayName("finds a like in the direction it was sent, and only that one")
  void getLikeByFkIsDirected() {
    likeRepository.saveAndFlush(new Like(new Date(), connectedUser.getId(), other.getId()));

    assertThat(likeRepository.getLikeByFk(connectedUser.getId(), other.getId())).isPresent();
    assertThat(likeRepository.getLikeByFk(other.getId(), connectedUser.getId())).isEmpty();
  }

  @Test
  @DisplayName("finds a dislike in the direction it was sent, and only that one")
  void getDislikeByFkIsDirected() {
    dislikeRepository.saveAndFlush(new Dislike(new Date(), connectedUser.getId(), other.getId()));

    assertThat(dislikeRepository.getDislikeByFk(connectedUser.getId(), other.getId())).isPresent();
    assertThat(dislikeRepository.getDislikeByFk(other.getId(), connectedUser.getId())).isEmpty();
  }

  @Test
  @DisplayName("lists the accounts that liked the connected user, which the selection marks as gold")
  void getGoldReturnsTheAccountsThatLikedFirst() {
    User admirer = persistUser();
    likeRepository.saveAndFlush(new Like(new Date(), admirer.getId(), connectedUser.getId()));
    likeRepository.saveAndFlush(new Like(new Date(), connectedUser.getId(), other.getId()));

    assertThat(likeRepository.getGoldByConnectedUserId(connectedUser.getId()))
      .containsExactly(admirer.getId());
  }

  @Test
  @DisplayName("returns the notifications of one account, most recent first")
  void getAllUserNotificationsReturnsTheMostRecentFirst() {
    notificationRepository.saveAndFlush(
      new Notification("older", "match", daysAgo(2), false, connectedUser));
    notificationRepository.saveAndFlush(
      new Notification("newer", "match", daysAgo(1), false, connectedUser));
    notificationRepository.saveAndFlush(
      new Notification("somebody else", "match", new Date(), false, other));

    List<Notification> notifications =
      notificationRepository.getAllUserNotifications(connectedUser.getId(), PageRequest.of(0, 6));

    assertThat(notifications).extracting(Notification::getContent)
      .containsExactly("newer", "older");
  }

  @Test
  @DisplayName("only returns the page of notifications the menu displays")
  void getAllUserNotificationsIsPaged() {
    for (int index = 0; index < 8; index++) {
      notificationRepository.saveAndFlush(
        new Notification("content" + index, "match", daysAgo(index), false, connectedUser));
    }

    assertThat(notificationRepository.getAllUserNotifications(
      connectedUser.getId(), PageRequest.of(0, 6))).hasSize(6);
  }
}
