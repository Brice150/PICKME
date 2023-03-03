package com.packages.backend.admin;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeService;
import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import com.packages.backend.messages.Message;
import com.packages.backend.messages.MessageService;
import com.packages.backend.pictures.Picture;
import com.packages.backend.pictures.PictureNotFoundException;
import com.packages.backend.pictures.PictureService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.Paths.get;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AdminController {
  private final UserService userService;
  private final LikeService likeService;
  private final MatchService matchService;
  private final PictureService pictureService;
  private final MessageService messageService;
  public static final String IMAGEDIRECTORY = "src/main/resources/pictures";

  public AdminController(LikeService likeService, UserService userService, MessageService messageService, PictureService pictureService, MatchService matchService) {
    this.userService = userService;
    this.messageService = messageService;
    this.likeService = likeService;
    this.matchService = matchService;
    this.pictureService = pictureService;
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
  public ResponseEntity<?> deleteUser(@PathVariable("email") String email) throws IOException {
    User selectedUser = userService.findUserByEmail(email);
    List<Like> likes = likeService.findAllLikes();
    for (Like like : likes) {
      if (selectedUser.getId() == like.getFkSender().getId()
        || selectedUser.getId() == like.getFkReceiver().getId()) {
        likeService.deleteLikeById(like.getId());
      }
    }
    List<Match> matches = matchService.findAllMatches();
    for (Match match : matches) {
      if (selectedUser.getId() == match.getFkReceiver().getId()
        || selectedUser.getId() == match.getFkSender().getId()) {
        matchService.deleteMatchById(match.getId());
      }
    }
    List<Message> messages = messageService.findAllMessages();
    for (Message message : messages) {
      if (selectedUser.getId() == message.getFkReceiver().getId()
        || selectedUser.getId() == message.getFkSender().getId()) {
        messageService.deleteMessageById(message.getId());
      }
    }
    List<Picture> pictures = pictureService.findAllPictures();
    for (Picture picture : pictures) {
      if (selectedUser.getId() == picture.getFkUser().getId()) {
        if (picture.getContent() != null) {
          Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(picture.getContent());
          if (Files.exists(imagePath)) {
            Files.delete(imagePath);
          }
          else {
            throw new PictureNotFoundException(picture.getContent() + " was not found on the server");
          }
        }
        pictureService.deletePictureById(picture.getId());
      }
    }
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
