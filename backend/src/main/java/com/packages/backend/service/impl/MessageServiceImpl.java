package com.packages.backend.service.impl;

import com.packages.backend.exception.MessageNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.entity.Message;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.service.MessageService;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final UserService userService;
  private final NotificationService notificationService;

  public MessageServiceImpl(MessageRepository messageRepository, UserService userService, NotificationService notificationService) {
    this.messageRepository = messageRepository;
    this.userService = userService;
    this.notificationService = notificationService;
  }

  @Override
  @Transactional
  public Optional<Message> addMessage(Message message) {
    User connectedUser = userService.getConnectedUser();
    if (!isSentToAMatch(connectedUser, message)) {
      return Optional.empty();
    }
    message.setDate(new Date());
    message.setSender(connectedUser.getNickname());
    message.setFkSender(connectedUser.getId());
    notificationService.sendNotification(message.getContent(), connectedUser.getNickname(), message.getFkReceiver());
    return Optional.of(messageRepository.save(message));
  }

  /**
   * Tells whether the receiver of a message is a match of the connected user, which forbids
   * writing to a profile that has not been matched or to oneself.
   *
   * @param connectedUser connected user
   * @param message       message being sent
   * @return true when the message can be sent
   */
  private boolean isSentToAMatch(User connectedUser, Message message) {
    if (Objects.equals(connectedUser.getId(), message.getFkReceiver())) {
      return false;
    }
    List<Match> userMatches = userService.getAllUserMatches();
    return userMatches.stream()
      .anyMatch(match -> Objects.equals(match.getUser().id(), message.getFkReceiver()));
  }

  @Override
  @Transactional
  public Optional<Message> updateMessage(Message message) {
    User connectedUser = userService.getConnectedUser();
    Message previousMessage = getMessageById(message.getId());
    if (Objects.equals(connectedUser.getId(), previousMessage.getFkSender())) {
      previousMessage.setContent(message.getContent());
      return Optional.of(messageRepository.save(previousMessage));
    }
    return Optional.empty();
  }

  @Override
  public Message getMessageById(Long messageId) {
    return messageRepository.findMessageById(messageId)
      .orElseThrow(() -> new MessageNotFoundException("Message by id " + messageId + " was not found"));
  }

  @Override
  @Transactional
  public String deleteMessageById(Long messageId) {
    User connectedUser = userService.getConnectedUser();
    Message message = getMessageById(messageId);
    if (Objects.equals(connectedUser.getId(), message.getFkSender())) {
      message.setContent(null);
      messageRepository.save(message);
      return ServiceStatus.OK;
    }
    return ServiceStatus.FORBIDDEN;
  }
}
