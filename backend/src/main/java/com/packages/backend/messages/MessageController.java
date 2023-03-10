package com.packages.backend.messages;

import com.packages.backend.user.User;
import com.packages.backend.user.UserRole;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/message")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class MessageController {
  private final MessageService messageService;
  private final UserService userService;

  public MessageController(MessageService messageService, UserService userService) {
    this.messageService = messageService;
    this.userService = userService;
  }

  @GetMapping("/all/{fkUser}")
  public ResponseEntity<List<Message>> getAllUserMessages(@PathVariable("fkUser") Long fkUser) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    List<Message> messages = messageService.findAllMessages();
    messages.removeIf(message ->
      (!connectedUser.getId().equals(message.getFkReceiver().getId())
      && !connectedUser.getId().equals(message.getFkSender().getId()))
      || (!fkUser.equals(message.getFkReceiver().getId())
      && !fkUser.equals(message.getFkSender().getId())));
    Comparator<Message> messagesSort = Comparator
      .comparing(Message::getDate, (date1, date2) -> date1.compareTo(date2));
    Collections.sort(messages, messagesSort);
    return new ResponseEntity<>(messages, HttpStatus.OK);
  }

  @GetMapping("/sender/{id}")
  public ResponseEntity<User> getMessageSender(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Message message = messageService.findMessageById(id);
    if (message.getFkSender().getId() != connectedUser.getId()
        && message.getFkReceiver().getId() != connectedUser.getId()) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User messageSender = message.getFkSender();
    messageSender.setMessagesReceived(null);
    messageSender.setMessagesSent(null);
    messageSender.setLikes(null);
    messageSender.setMatches(null);
    messageSender.setPassword(null);
    messageSender.setTokens(null);
    messageSender.setUserRole(UserRole.HIDDEN);
    return new ResponseEntity<>(messageSender, HttpStatus.OK);
  }

  @GetMapping("/all/number/{fkUser}")
  public ResponseEntity<Integer> getUserMessagesNumber(@PathVariable("fkUser") Long fkUser) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    List<Message> messages = messageService.findAllMessages();
    messages.removeIf(message ->
      (!connectedUser.getId().equals(message.getFkReceiver().getId())
        && !connectedUser.getId().equals(message.getFkSender().getId()))
        || (!fkUser.equals(message.getFkReceiver().getId())
        && !fkUser.equals(message.getFkSender().getId())));
    return new ResponseEntity<>(messages.size(), HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<Message> addMessage(@RequestBody Message message) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(message.getFkSender().getId())
      && !connectedUser.getId().equals(message.getFkReceiver().getId())) {
      Message newMessage = messageService.addMessage(message);
      return new ResponseEntity<>(newMessage, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @PutMapping()
  public ResponseEntity<Message> updateMessage(@RequestBody Message message) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(message.getFkSender().getId())) {
      Message updateMessage = messageService.updateMessage(message);
      return new ResponseEntity<>(updateMessage, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteMessage(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Message message = messageService.findMessageById(id);
    if (connectedUser.getId().equals(message.getFkSender().getId())) {
      messageService.deleteMessageById(id);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}

