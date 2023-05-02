package com.packages.backend.messages;

import com.packages.backend.user.User;
import com.packages.backend.user.UserRole;
import com.packages.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(message.getFkSender().getId())
      && !connectedUser.getId().equals(message.getFkReceiver().getId())) {
      message.setDate(new Date());
      Message newMessage = messageRepository.save(message);
      return Optional.of(newMessage);
    } else {
      return Optional.empty();
    }
  }

  public Optional<User> findMessageSender(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Message message = findMessageById(id);
    if (!Objects.equals(message.getFkSender().getId(), connectedUser.getId())
      && !Objects.equals(message.getFkReceiver().getId(), connectedUser.getId())) {
      return Optional.empty();
    }
    User messageSender = message.getFkSender();
    messageSender.setMessagesReceived(null);
    messageSender.setMessagesSent(null);
    messageSender.setLikes(null);
    messageSender.setMatches(null);
    messageSender.setPassword(null);
    messageSender.setTokens(null);
    messageSender.setUserRole(UserRole.HIDDEN);
    return Optional.of(messageSender);
  }

  public Integer findUserMessagesNumber(Long fkUser) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    return messageRepository.findUserMessagesNumber(fkUser, connectedUser.getId());
  }

  public List<Message> findAllMessages() {
    return messageRepository.findAll();
  }

  public List<Message> findAllMessagesByFk(Long fkUser) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    List<Message> messages = messageRepository.findAllMessagesByFk(fkUser, connectedUser.getId());
    Comparator<Message> messagesSort = Comparator
      .comparing(Message::getDate, Date::compareTo);
    messages.sort(messagesSort);
    return messages;
  }

  public Optional<Message> updateMessage(Message message) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(message.getFkSender().getId())) {
      message.setDate(findMessageById(message.getId()).getDate());
      Message updateMessage = messageRepository.save(message);
      return Optional.of(updateMessage);
    } else {
      return Optional.empty();
    }
  }

  public Message findMessageById(Long id) {
    return messageRepository.findMessageById(id)
      .orElseThrow(() -> new MessageNotFoundException("Message by id " + id + " was not found"));
  }

  @Transactional
  public String deleteMessageById(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Message message = findMessageById(id);
    if (connectedUser.getId().equals(message.getFkSender().getId())) {
      messageRepository.deleteMessageById(id);
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

