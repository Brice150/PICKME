package com.packages.backend.controller;

import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserUpdateRequest;
import com.packages.backend.service.AccountDeletionService;
import com.packages.backend.service.CandidateSelectionService;
import com.packages.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping()
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class UserController {
  private final UserService userService;
  private final CandidateSelectionService candidateSelectionService;
  private final AccountDeletionService accountDeletionService;

  public UserController(UserService userService, CandidateSelectionService candidateSelectionService, AccountDeletionService accountDeletionService) {
    this.userService = userService;
    this.candidateSelectionService = candidateSelectionService;
    this.accountDeletionService = accountDeletionService;
  }

  @GetMapping("/login")
  public ResponseEntity<UserDTO> login() {
    return new ResponseEntity<>(userService.getConnectedUserDTO(), HttpStatus.OK);
  }

  @GetMapping("/user/all/{page}")
  public ResponseEntity<List<UserDTO>> getAllSelectedUsers(@PathVariable("page") Integer page) {
    return new ResponseEntity<>(candidateSelectionService.getAllSelectedUsers(page), HttpStatus.OK);
  }

  @GetMapping("/user")
  public ResponseEntity<UserDTO> getConnectedUser() {
    return new ResponseEntity<>(userService.getConnectedUserDTO(), HttpStatus.OK);
  }

  @PutMapping("/user")
  public ResponseEntity<UserDTO> updateUser(@RequestBody UserUpdateRequest request) {
    return new ResponseEntity<>(userService.updateUser(request), HttpStatus.OK);
  }

  @DeleteMapping("/user")
  public ResponseEntity<Void> deleteConnectedUser() {
    accountDeletionService.deleteConnectedUser();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
