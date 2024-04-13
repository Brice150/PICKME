package com.packages.backend.service;

import com.packages.backend.exception.MessageNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.entity.Message;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MessageService {
  private final MessageRepository messageRepository;
  private final UserService userService;
  private final NotificationService notificationService;

  public MessageService(MessageRepository messageRepository, UserService userService, NotificationService notificationService) {
    this.messageRepository = messageRepository;
    this.userService = userService;
    this.notificationService = notificationService;
  }

  public Optional<Message> addMessage(Message message) {
    User connectedUser = userService.getConnectedUser();
    List<Match> userMatches = userService.getAllUserMatches();
    if (!Objects.equals(connectedUser.getId(), message.getFkReceiver())
      && userMatches.stream()
      .anyMatch((match -> Objects.equals(match.getUser().id(), message.getFkReceiver())))) {
      message.setDate(new Date());
      message.setSender(connectedUser.getNickname());
      message.setFkSender(connectedUser.getId());
      notificationService.sendNotification(message.getContent(), connectedUser.getNickname(), message.getFkReceiver());
      Message newMessage = messageRepository.save(message);
      return Optional.of(newMessage);
    } else {
      return Optional.empty();
    }
  }

  public Optional<Message> updateMessage(Message message) {
    User connectedUser = userService.getConnectedUser();
    Message previousMessage = getMessageById(message.getId());
    if (Objects.equals(connectedUser.getId(), previousMessage.getFkSender())) {
      previousMessage.setContent(message.getContent());
      return Optional.of(messageRepository.save(previousMessage));
    } else {
      return Optional.empty();
    }
  }

  public Message getMessageById(Long messageId) {
    return messageRepository.findMessageById(messageId)
      .orElseThrow(() -> new MessageNotFoundException("Message by id " + messageId + " was not found"));
  }

  @Transactional
  public String deleteMessageById(Long messageId) {
    User connectedUser = userService.getConnectedUser();
    Message message = getMessageById(messageId);
    if (Objects.equals(connectedUser.getId(), message.getFkSender())) {
      message.setContent(null);
      messageRepository.save(message);
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

