package com.packages.backend.controller;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.entity.DeletedAccount;
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

  @GetMapping("/stats")
  public ResponseEntity<AdminStats> getAdminStats() {
    return new ResponseEntity<>(adminService.getAdminStats(), HttpStatus.OK);
  }

  @PostMapping("/user/all/{page}")
  public ResponseEntity<List<UserDTO>> getAllUsers(@PathVariable("page") Integer page, @RequestBody AdminSearch adminSearch) {
    return new ResponseEntity<>(adminService.getAllUsers(adminSearch, page), HttpStatus.OK);
  }

  @PostMapping("/deleted-account/all/{page}")
  public ResponseEntity<List<DeletedAccount>> getAllDeletedAccounts(@PathVariable("page") Integer page, @RequestBody AdminSearch adminSearch) {
    return new ResponseEntity<>(adminService.getAllDeletedAccounts(adminSearch, page), HttpStatus.OK);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
    adminService.deleteUserById(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
