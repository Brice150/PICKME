package com.packages.backend.controller;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @PostMapping("/all")
  public ResponseEntity<List<UserDTO>> getAllUsers(@RequestBody AdminSearch adminSearch) {
    return new ResponseEntity<>(adminService.getAllUsers(adminSearch), HttpStatus.OK);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
    adminService.deleteUserById(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
