package com.packages.backend.service;

import com.packages.backend.exception.MessageNotFoundException;
import com.packages.backend.model.Message;
import com.packages.backend.model.user.User;
import com.packages.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class MessageService {
  private final MessageRepository messageRepository;
  private final UserService userService;

  @Autowired
  public MessageService(MessageRepository messageRepository, UserService userService) {
    this.messageRepository = messageRepository;
    this.userService = userService;
  }

  public Optional<Message> addMessage(Message message) {
    User connectedUser = userService.getConnectedUser();
    if (!Objects.equals(connectedUser.getId(), message.getFkReceiver())) {
      message.setDate(new Date());
      message.setSender(connectedUser.getNickname());
      message.setFkSender(connectedUser.getId());
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
      Message updateMessage = messageRepository.save(previousMessage);
      return Optional.of(updateMessage);
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
      messageRepository.deleteMessageById(messageId);
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

