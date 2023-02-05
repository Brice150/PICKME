package com.packages.backend.user;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeService;
import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping()
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class UserController {

  private final UserService userService;
  private final LikeService likeService;
  private final MatchService matchService;

  public UserController(UserService userService, LikeService likeService, MatchService matchService) {
    this.userService = userService;
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
      || !connectedUser.getCity().equals(user.getCity())
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
    Comparator<User> usersSort = Comparator
      .comparing((User user) -> compareAttributes(connectedUser.getRelationshipType(), user.getRelationshipType()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getPersonality(), user.getPersonality()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getParenthood(), user.getParenthood()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSmokes(), user.getSmokes()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getOrganised(), user.getOrganised()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSportPractice(), user.getSportPractice()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAnimals(), user.getAnimals()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAlcoholDrinking(), user.getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getGamer(), user.getGamer()));
    Collections.sort(users, usersSort);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/user/all/like")
  public ResponseEntity<List<User>> getAllUsersThatLiked() {
    List<User> users = new ArrayList<User>();
    List<Like> likes = likeService.findAllLikes();
    List<Match> matches = matchService.findAllMatches();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    for (Like like : likes) {
      if (like.getFkReceiver().getId() == connectedUser.getId()) {
        users.add(like.getFkSender());
      }
    }
    for (Match match: matches) {
      users.removeIf(user ->
        (match.getFkSender().getId() == user.getId()
          && match.getFkReceiver().getId() == connectedUser.getId())
        || (match.getFkReceiver().getId() == user.getId()
          && match.getFkSender().getId() == connectedUser.getId())
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
    Comparator<User> usersSort = Comparator
      .comparing((User user) -> compareAttributes(connectedUser.getRelationshipType(), user.getRelationshipType()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getPersonality(), user.getPersonality()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getParenthood(), user.getParenthood()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSmokes(), user.getSmokes()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getOrganised(), user.getOrganised()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSportPractice(), user.getSportPractice()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAnimals(), user.getAnimals()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAlcoholDrinking(), user.getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getGamer(), user.getGamer()));
    Collections.sort(users, usersSort);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/user/all/match")
  public ResponseEntity<List<User>> getAllUsersThatMatched() {
    List<User> users = new ArrayList<User>();
    List<Match> matches = matchService.findAllMatches();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Collections.sort(matches, (Match match1, Match match2) ->
      match2.getDate().compareTo(match1.getDate()));
    for (Match match: matches) {
      if (match.getFkReceiver().getId() == connectedUser.getId()) {
        users.add(match.getFkSender());
      }
       else if (match.getFkSender().getId() == connectedUser.getId()) {
        users.add(match.getFkReceiver());
      }
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

  public static int compareAttributes(String attribute1, String attribute2) {
    int result=0;
    if ((attribute1 == null && attribute2 == null)) {
      result = 0;
    }
    else if (attribute1 != null && attribute2 == null) {
      result = 0;
    }
    else if (attribute1 == null && attribute2 != null) {
      result = 0;
    }
    else if (attribute1 != null && attribute2 != null) {
      if (attribute1.equals(attribute2)) {
        result = -1;
      }
      else {
        result = 1;
      }
    }
    return result;
  }
}
