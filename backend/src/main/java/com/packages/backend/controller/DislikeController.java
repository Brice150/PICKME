package com.packages.backend.controller;

import com.packages.backend.service.DislikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dislike")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class DislikeController {
  private final DislikeService dislikeService;

  public DislikeController(DislikeService dislikeService) {
    this.dislikeService = dislikeService;
  }

  @PostMapping("/{userId}")
  public ResponseEntity<String> addDislike(@PathVariable("userId") Long userId) {
    String status = dislikeService.addDislike(userId);
    return !"FORBIDDEN".equals(status) ?
      new ResponseEntity<>(HttpStatus.CREATED) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }
}

