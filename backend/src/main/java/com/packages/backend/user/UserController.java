package com.packages.backend.user;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeService;
import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import com.packages.backend.messages.Message;
import com.packages.backend.messages.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping()
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class UserController {

  private final UserService userService;
  private final MessageService messageService;
  private final LikeService likeService;
  private final MatchService matchService;

  public UserController(UserService userService, MessageService messageService, LikeService likeService, MatchService matchService) {
    this.userService = userService;
    this.messageService = messageService;
    this.likeService = likeService;
    this.matchService = matchService;
  }

  @GetMapping("/login")
  public String login() {
    return "logged in successfully";
  }

  @GetMapping("/user/all")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.findAllUsers();
    List<Like> likes = likeService.findAllLikes();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    users.removeIf(user -> currentUserEmail.equals(user.getEmail()));
    users.removeIf(user ->
      !connectedUser.getGender().equals(user.getGenderSearch())
      || !connectedUser.getGenderSearch().equals(user.getGender())
        || !connectedUser.getRelationshipType().equals(user.getRelationshipType())
    );
    likes.removeIf(like -> like.getFkSender().getId() != connectedUser.getId());
    for (Like like: likes) {
      users.removeIf(user ->
        like.getFkReceiver().getId() == user.getId()
      );
    }
    for (User user: users) {
      user.setMessagesReceived(null);
      user.setMessagesSent(null);
      user.setLikes(null);
      user.setMatches(null);
      user.setPassword(null);
      user.setTokens(null);
      user.setUserRole(UserRole.HIDDEN);
    }
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/user/all/like")
  public ResponseEntity<List<User>> getAllUsersThatLiked() {
    List<User> users = userService.findAllUsers();
    List<Like> likes = likeService.findAllLikes();
    List<Match> matches = matchService.findAllMatches();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    users.removeIf(user -> currentUserEmail.equals(user.getEmail()));
    likes.removeIf(like -> like.getFkReceiver().getId() != connectedUser.getId());
    if (likes.size() == 0) {
      return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }
    matches.removeIf(match ->
      match.getFkReceiver().getId() != connectedUser.getId()
      && match.getFkSender().getId() != connectedUser.getId()
    );
    for (Like like: likes) {
      users.removeIf(user -> like.getFkSender().getId() != user.getId());
    }
    for (Match match: matches) {
      users.removeIf(user ->
        match.getFkSender().getId() == user.getId()
        || match.getFkReceiver().getId() == user.getId()
      );
    }
    for (User user: users) {
      user.setMessagesReceived(null);
      user.setMessagesSent(null);
      user.setLikes(null);
      user.setMatches(null);
      user.setPassword(null);
      user.setTokens(null);
      user.setUserRole(UserRole.HIDDEN);
    }
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/user/all/match")
  public ResponseEntity<List<User>> getAllUsersThatMatched() {
    List<User> users = userService.findAllUsers();
    List<Match> matches = matchService.findAllMatches();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    users.removeIf(user -> currentUserEmail.equals(user.getEmail()));
    matches.removeIf(match ->
      match.getFkReceiver().getId() != connectedUser.getId()
        && match.getFkSender().getId() != connectedUser.getId()
    );
    if (matches.size() == 0) {
      return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }
    for (Match match: matches) {
      users.removeIf(user ->
        match.getFkSender().getId() != user.getId()
        && match.getFkReceiver().getId() != user.getId()
      );
    }
    for (User user: users) {
      user.setMessagesReceived(null);
      user.setMessagesSent(null);
      user.setLikes(null);
      user.setMatches(null);
      user.setPassword(null);
      user.setTokens(null);
      user.setUserRole(UserRole.HIDDEN);
    }
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/user")
  public ResponseEntity<User> getConnectedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User user = userService.findUserByEmail(currentUserEmail);
    user.setPassword(null);
    user.setTokens(null);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
    User user = userService.findUserById(id);
    user.setMessagesReceived(null);
    user.setMessagesSent(null);
    user.setLikes(null);
    user.setMatches(null);
    user.setPassword(null);
    user.setTokens(null);
    user.setUserRole(UserRole.HIDDEN);
    user.setEmail(null);
    user.setGender(null);
    user.setGenderSearch(null);
    user.setRelationshipType(null);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PutMapping("/user")
  public ResponseEntity<User> updateUser(@RequestBody User user) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(user.getId())) {
      user.setEmail(currentUserEmail);
      user.setUserRole(connectedUser.getUserRole());
      User updateUser = userService.updateUser(user);
      return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/user/{email}")
  public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    if (currentUserEmail.equals(email)) {
      userService.deleteUserByEmail(email);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}
