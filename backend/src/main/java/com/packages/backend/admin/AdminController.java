package com.packages.backend.admin;

import com.packages.backend.messages.Message;
import com.packages.backend.messages.MessageService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @DeleteMapping("/user/delete/{email}")
  public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
    userService.deleteUserByEmail(email);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/message/all")
  public ResponseEntity<List<Message>> getAllMessages() {
    List<Message> messages = messageService.findAllMessages();
    return new ResponseEntity<>(messages, HttpStatus.OK);
  }

  @DeleteMapping("/message/delete/{id}")
  public ResponseEntity<?> deleteMessage(@PathVariable("id") Long id) {
    messageService.deleteMessageById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
