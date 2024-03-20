package com.packages.backend.controller;

import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<UserDTO> login() {
    return new ResponseEntity<>(userService.getConnectedUserDTO(), HttpStatus.OK);
  }

  @GetMapping("/user/all")
  public ResponseEntity<List<UserDTO>> getAllSelectedUsers() {
    return new ResponseEntity<>(userService.getAllSelectedUsers(), HttpStatus.OK);
  }

  @GetMapping("/user")
  public ResponseEntity<UserDTO> getConnectedUser() {
    return new ResponseEntity<>(userService.getConnectedUserDTO(), HttpStatus.OK);
  }

  @PutMapping("/user")
  public ResponseEntity<UserDTO> updateUser(@RequestBody User user) {
    Optional<UserDTO> updatedUser = userService.updateUser(user);
    return updatedUser.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping("/user")
  public ResponseEntity<Void> deleteConnectedUser() {
    userService.deleteConnectedUser();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
