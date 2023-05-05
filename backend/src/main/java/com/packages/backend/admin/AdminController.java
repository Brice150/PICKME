package com.packages.backend.admin;

import com.packages.backend.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping("/user/all")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return new ResponseEntity<>(adminService.findAllUsers(), HttpStatus.OK);
  }

  @DeleteMapping("/user/{email}")
  public ResponseEntity<?> deleteUser(@PathVariable("email") String email) throws IOException {
    adminService.deleteUserByEmail(email);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/message/{id}")
  public ResponseEntity<?> deleteMessage(@PathVariable("id") Long id) {
    adminService.deleteMessageById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
