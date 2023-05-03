package com.packages.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping()
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String login() {
    return "logged in successfully";
  }

  @GetMapping("/user/all")
  public ResponseEntity<List<User>> getAllUsers() {
    return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
  }

  @GetMapping("/user/all/like")
  public ResponseEntity<List<User>> getAllUsersThatLiked() {
    return new ResponseEntity<>(userService.findAllUsersThatLiked(), HttpStatus.OK);
  }

  @GetMapping("/user/all/match")
  public ResponseEntity<List<User>> getAllUsersThatMatched() {
    return new ResponseEntity<>(userService.findAllUsersThatMatched(), HttpStatus.OK);
  }

  @GetMapping("/user")
  public ResponseEntity<User> getConnectedUser() {
    return new ResponseEntity<>(userService.findConnectedUser(), HttpStatus.OK);
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
    return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
  }

  @PutMapping("/user")
  public ResponseEntity<User> updateUser(@RequestBody User user) {
    Optional<User> updatedUser = userService.updateUser(user);
    return updatedUser.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping("/user")
  public ResponseEntity<?> deleteConnectedUser() throws IOException {
    userService.deleteConnectedUser();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
