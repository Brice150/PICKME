package com.packages.backend.controller;

import com.packages.backend.model.entity.Message;
import com.packages.backend.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/message")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class MessageController {
  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping()
  public ResponseEntity<Message> addMessage(@RequestBody Message message) {
    Optional<Message> newMessage = messageService.addMessage(message);
    return newMessage.map(messageAdded -> new ResponseEntity<>(messageAdded, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @PutMapping()
  public ResponseEntity<Message> updateMessage(@RequestBody Message message) {
    Optional<Message> updatedMessage = messageService.updateMessage(message);
    return updatedMessage.map(messageUpdated -> new ResponseEntity<>(messageUpdated, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") Long messageId) {
    return "OK".equals(messageService.deleteMessageById(messageId)) ?
      new ResponseEntity<>(HttpStatus.OK) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }
}

