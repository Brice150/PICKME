package com.packages.backend.repository;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeRepository;
import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchRepository;
import com.packages.backend.messages.Message;
import com.packages.backend.messages.MessageRepository;
import com.packages.backend.pictures.Picture;
import com.packages.backend.pictures.PictureRepository;
import com.packages.backend.registration.token.ConfirmationToken;
import com.packages.backend.registration.token.ConfirmationTokenRepository;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepoTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private MatchRepository matchRepository;
  @Autowired
  private LikeRepository likeRepository;
  @Autowired
  private PictureRepository pictureRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;
  private User user1;
  private User user2;
  private ConfirmationToken confirmationToken;
  private Message message1;
  private Message message2;
  private Match match1;
  private Like like1;
  private Like like2;
  private Picture picture1;
  private Picture picture2;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setEmail("user1@gmail.com");
    user1.setNickname("user1");
    user1.setPassword("password");

    user2 = new User();
    user2.setEmail("user2@gmail.com");
    user2.setNickname("user2");
    user2.setPassword("password");

    userRepository.save(user1);
    userRepository.save(user2);
  }

  void specificSetUp(String classTest) {
    if ("message".equals(classTest)) {
      message1 = new Message(
        "content1", new Date(), user1, user2
      );
      message2 = new Message(
        "content2", new Date(), user2, user1
      );
      messageRepository.save(message1);
      messageRepository.save(message2);
    } else if ("like".equals(classTest)) {
      like1 = new Like(
        new Date(), user1, user2
      );
      like2 = new Like(
        new Date(), user2, user1
      );
      likeRepository.save(like1);
      likeRepository.save(like2);
    } else if ("match".equals(classTest)) {
      match1 = new Match(
        new Date(), user1, user2
      );
      matchRepository.save(match1);
    } else if ("picture".equals(classTest)) {
      picture1 = new Picture(
        "content1", user1
      );
      picture2 = new Picture(
        "content1", user1
      );
      pictureRepository.save(picture1);
      pictureRepository.save(picture2);
    } else if ("token".equals(classTest)) {
      String token = UUID.randomUUID().toString();
      confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        user1
      );
      confirmationTokenRepository.save(confirmationToken);
    }
  }

  @Test
  void testFindUserByEmail() {
    // When
    Optional<User> foundUser = userRepository.findUserByEmail(user1.getEmail());

    // Then
    assertThat(foundUser).isPresent().contains(user1);
  }

  @Test
  void testEnableUser() {
    // When
    int updatedUser = userRepository.enableUser(user1.getEmail());

    // Then
    assertThat(updatedUser).isEqualTo(1);
    Optional<User> enabledUser = userRepository.findUserByEmail(user1.getEmail());
    assertThat(enabledUser).isPresent();
    assertThat(enabledUser.get().isEnabled()).isTrue();
  }

  @Test
  void testDeleteTokenByFk() {
    // Given
    specificSetUp("token");

    // When
    userRepository.deleteTokenByFk(user1.getId());

    // Then
    Optional<ConfirmationToken> deletedToken = confirmationTokenRepository.findByToken(confirmationToken.getToken());
    assertThat(deletedToken).isEmpty();
  }

  @Test
  void testDeleteMessageById() {
    // Given
    specificSetUp("message");

    // When
    userRepository.deleteMessageById(message1.getId());

    // Then
    Optional<Message> deletedMessage = messageRepository.findMessageById(message1.getId());
    assertThat(deletedMessage).isEmpty();
  }

  @Test
  void testDeleteMatchById() {
    // Given
    specificSetUp("match");

    // When
    userRepository.deleteMatchById(match1.getId());

    // Then
    Optional<Match> deletedMatch = matchRepository.findMatchById(match1.getId());
    assertThat(deletedMatch).isEmpty();
  }

  @Test
  void testDeleteLikeById() {
    // Given
    specificSetUp("like");

    // When
    userRepository.deleteLikeById(like1.getId());

    // Then
    Optional<Like> deletedLike = likeRepository.findLikeById(like1.getId());
    assertThat(deletedLike).isEmpty();
  }

  @Test
  void testDeletePictureById() {
    // Given
    specificSetUp("picture");

    // When
    userRepository.deletePictureById(picture1.getId());

    // Then
    Optional<Picture> deletedPicture = userRepository.findPictureById(picture1.getId());
    assertThat(deletedPicture).isEmpty();
  }

  @Test
  void testDeleteUserByEmail() {
    // When
    userRepository.deleteUserByEmail(user1.getEmail());

    // Then
    Optional<User> deletedUser = userRepository.findUserByEmail(user1.getEmail());
    assertThat(deletedUser).isEmpty();
  }

  @Test
  void testFindAllMessagesByFk() {
    // Given
    specificSetUp("message");

    // When
    List<Message> foundMessages = userRepository.findAllMessagesByFk(user1.getId());

    // Then
    assertThat(foundMessages).isNotNull().hasSize(2);
  }

  @Test
  void testFindAllMatchesByFk() {
    // Given
    specificSetUp("match");

    // When
    List<Match> foundMatches = userRepository.findAllMatchesByFk(user1.getId());

    // Then
    assertThat(foundMatches).isNotNull().hasSize(1);
  }
}
