package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.exception.MessageNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.entity.Message;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageServiceImpl")
class MessageServiceImplTest {

  private static final UserDTOMapperRestricted MAPPER = new UserDTOMapperRestricted();

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserService userService;
  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private MessageServiceImpl messageService;

  @Test
  @DisplayName("stamps a message sent to a match and notifies its receiver")
  void addMessageStampsAndNotifies() {
    User connectedUser = TestFixtures.user(1L);
    User receiver = TestFixtures.user(2L);
    Message message = new Message("hello", null, null, null, 2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getAllUserMatches()).thenReturn(List.of(new Match(MAPPER.apply(receiver), List.of())));
    when(messageRepository.save(message)).thenReturn(message);

    Optional<Message> sent = messageService.addMessage(message);

    assertThat(sent).contains(message);
    assertThat(message.getSender()).isEqualTo(connectedUser.getNickname());
    assertThat(message.getFkSender()).isEqualTo(1L);
    assertThat(message.getDate()).isNotNull();
    verify(notificationService).sendNotification("hello", connectedUser.getNickname(), 2L);
  }

  @Test
  @DisplayName("refuses a message sent to a profile that is not a match")
  void addMessageRefusesAProfileThatIsNotAMatch() {
    User connectedUser = TestFixtures.user(1L);
    User stranger = TestFixtures.user(3L);
    Message message = new Message("hello", null, null, null, 2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getAllUserMatches()).thenReturn(List.of(new Match(MAPPER.apply(stranger), List.of())));

    assertThat(messageService.addMessage(message)).isEmpty();
    verify(messageRepository, never()).save(any());
    verify(notificationService, never()).sendNotification(anyString(), anyString(), anyLong());
  }

  @Test
  @DisplayName("refuses a message the connected user sends to themselves")
  void addMessageRefusesAMessageSentToOneself() {
    User connectedUser = TestFixtures.user(1L);
    Message message = new Message("hello", null, null, null, 1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);

    assertThat(messageService.addMessage(message)).isEmpty();
    verify(userService, never()).getAllUserMatches();
    verify(messageRepository, never()).save(any());
  }

  @Test
  @DisplayName("edits the content of a message owned by the connected user")
  void updateMessageEditsAnOwnedMessage() {
    User connectedUser = TestFixtures.user(1L);
    Message previousMessage = message(10L, "old", 1L);
    Message submitted = message(10L, "new", 1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(messageRepository.findMessageById(10L)).thenReturn(Optional.of(previousMessage));
    when(messageRepository.save(previousMessage)).thenReturn(previousMessage);

    Optional<Message> updated = messageService.updateMessage(submitted);

    assertThat(updated).contains(previousMessage);
    assertThat(previousMessage.getContent()).isEqualTo("new");
  }

  @Test
  @DisplayName("refuses to edit a message written by somebody else")
  void updateMessageRefusesAMessageOfAnotherUser() {
    User connectedUser = TestFixtures.user(1L);
    Message previousMessage = message(10L, "old", 2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(messageRepository.findMessageById(10L)).thenReturn(Optional.of(previousMessage));

    assertThat(messageService.updateMessage(message(10L, "new", 1L))).isEmpty();
    assertThat(previousMessage.getContent()).isEqualTo("old");
    verify(messageRepository, never()).save(any());
  }

  @Test
  @DisplayName("finds a message from its identifier")
  void getMessageByIdReturnsTheMessage() {
    Message message = message(10L, "content", 1L);
    when(messageRepository.findMessageById(10L)).thenReturn(Optional.of(message));

    assertThat(messageService.getMessageById(10L)).isSameAs(message);
  }

  @Test
  @DisplayName("fails when no message matches the identifier")
  void getMessageByIdFailsWhenTheMessageIsUnknown() {
    when(messageRepository.findMessageById(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.getMessageById(10L))
      .isInstanceOf(MessageNotFoundException.class)
      .hasMessage("Message by id 10 was not found");
  }

  @Test
  @DisplayName("empties a message owned by the connected user instead of removing it")
  void deleteMessageByIdEmptiesTheContent() {
    User connectedUser = TestFixtures.user(1L);
    Message message = message(10L, "content", 1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(messageRepository.findMessageById(10L)).thenReturn(Optional.of(message));

    assertThat(messageService.deleteMessageById(10L)).isEqualTo(ServiceStatus.OK);
    assertThat(message.getContent()).isNull();
    verify(messageRepository).save(message);
  }

  @Test
  @DisplayName("refuses to delete a message written by somebody else")
  void deleteMessageByIdRefusesAMessageOfAnotherUser() {
    User connectedUser = TestFixtures.user(1L);
    Message message = message(10L, "content", 2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(messageRepository.findMessageById(10L)).thenReturn(Optional.of(message));

    assertThat(messageService.deleteMessageById(10L)).isEqualTo(ServiceStatus.FORBIDDEN);
    assertThat(message.getContent()).isEqualTo("content");
    verify(messageRepository, never()).save(any());
  }

  private Message message(Long id, String content, Long fkSender) {
    Message message = new Message(content, new Date(), "sender", fkSender, 2L);
    message.setId(id);
    return message;
  }
}
