package com.packages.backend.admin;

import com.packages.backend.messages.Message;
import com.packages.backend.messages.MessageService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AdminController {

  private final UserService userService;
  private final MessageService messageService;

  public AdminController(UserService userService, MessageService messageService) {
    this.userService = userService;
    this.messageService = messageService;
  }

  @GetMapping("/user/all")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.findAllUsers();
    for (User user: users) {
      user.setMessagesReceived(null);
      user.setMessagesSent(null);
      user.setPassword(null);
      user.setTokens(null);
    }
    Comparator<User> usersSort = Comparator
      .comparing(User::getUserRole, (role1, role2) -> role2.compareTo(role1))
      .thenComparing(User::getUsername);
    Collections.sort(users, usersSort);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @DeleteMapping("/user/{email}")
  public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
    userService.deleteUserByEmail(email);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/message/all")
  public ResponseEntity<List<Message>> getAllMessages() {
    List<Message> messages = messageService.findAllMessages();
    Comparator<Message> messagesSort = Comparator
      .comparing(Message::getDate, (date1, date2) -> date2.compareTo(date1));
    Collections.sort(messages, messagesSort);
    return new ResponseEntity<>(messages, HttpStatus.OK);
  }

  @DeleteMapping("/message/{id}")
  public ResponseEntity<?> deleteMessage(@PathVariable("id") Long id) {
    messageService.deleteMessageById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
