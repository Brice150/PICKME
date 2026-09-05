package com.packages.backend.controller;

import com.packages.backend.service.LikeResult;
import com.packages.backend.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class LikeController {
  private final LikeService likeService;

  public LikeController(LikeService likeService) {
    this.likeService = likeService;
  }

  @PostMapping("/{userId}")
  public ResponseEntity<String> addLike(@PathVariable("userId") Long userId) {
    return switch (likeService.addLike(userId)) {
      case LikeResult.Matched(String nickname) -> new ResponseEntity<>(nickname, HttpStatus.CREATED);
      case LikeResult.Liked ignored -> new ResponseEntity<>(HttpStatus.CREATED);
      case LikeResult.Forbidden ignored -> new ResponseEntity<>(HttpStatus.FORBIDDEN);
    };
  }
}
