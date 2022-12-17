package com.packages.backend.likes;

import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class LikeController {
  private final LikeService likeService;
  private final UserService userService;

  public LikeController(LikeService likeService, UserService userService) {
    this.likeService = likeService;
    this.userService = userService;
  }

  @GetMapping("/find/{id}")
  public ResponseEntity<Like> getLikeById(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Like like = likeService.findLikeById(id);
    if (connectedUser.getId().equals(like.getFkSender().getId())
      || connectedUser.getId().equals(like.getFkReceiver().getId())) {
      return new ResponseEntity<>(like, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/add")
  public ResponseEntity<Like> addLike(@RequestBody Like like) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(like.getFkSender().getId())
      && !connectedUser.getId().equals(like.getFkReceiver().getId())) {
      Like newLike = likeService.addLike(like);
      return new ResponseEntity<>(newLike, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteLike(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Like like = likeService.findLikeById(id);
    if (connectedUser.getId().equals(like.getFkSender().getId())) {
      likeService.deleteLikeById(id);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}

