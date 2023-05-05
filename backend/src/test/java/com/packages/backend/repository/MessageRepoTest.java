package com.packages.backend.repository;

import com.packages.backend.messages.Message;
import com.packages.backend.messages.MessageRepository;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageRepoTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  private User user1;
  private User user2;

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

  @Test
  void testFindAllMessagesByFk() {
    // given
    Message message1 = new Message(
      "message1", new Date(), user1, user2
    );
    Message message2 = new Message(
      "message2", new Date(), user1, user2
    );
    messageRepository.save(message1);
    messageRepository.save(message2);

    // when
    Long fkUser = user1.getId();
    Long connectedId = user2.getId();
    List<Message> found = messageRepository.findAllMessagesByFk(fkUser, connectedId);

    // then
    assertThat(found).isNotNull().hasSize(2);
  }

  @Test
  void testFindUserMessagesNumber() {
    // given
    Message message1 = new Message(
      "message1", new Date(), user1, user2
    );
    messageRepository.save(message1);

    // when
    Long fkUser = user1.getId();
    Long connectedId = user2.getId();
    Integer found = messageRepository.findUserMessagesNumber(fkUser, connectedId);

    // then
    assertThat(found).isEqualTo(1);
  }

  @Test
  void testFindMessageById() {
    // given
    Message message1 = new Message(
      "message1", new Date(), user1, user2
    );
    messageRepository.save(message1);

    // when
    Optional<Message> found = messageRepository.findMessageById(message1.getId());

    // then
    assertThat(found).isPresent().contains(message1);
  }

  @Test
  void testDeleteMessageById() {
    // given
    Message message1 = new Message(
      "message1", new Date(), user1, user2
    );
    messageRepository.save(message1);

    // when
    messageRepository.deleteMessageById(message1.getId());

    // then
    Optional<Message> found = messageRepository.findMessageById(message1.getId());
    assertThat(found).isEmpty();
  }
}
