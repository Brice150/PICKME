package com.packages.backend.likes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/like")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class LikeController {
  private final LikeService likeService;

  public LikeController(LikeService likeService) {
    this.likeService = likeService;
  }

  @GetMapping("/{fkSender}/{fkReceiver}")
  public ResponseEntity<Like> getLikeByFk(@PathVariable("fkSender") Long fkSender, @PathVariable("fkReceiver") Long fkReceiver) {
    Optional<Like> foundLike = likeService.findLikeByFk(fkSender, fkReceiver);
    return foundLike.map(like -> new ResponseEntity<>(like, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping()
  public ResponseEntity<String> addLike(@RequestBody Like like) {
    String notification = likeService.addLike(like);
    return !"FORBIDDEN".equals(notification) ?
      new ResponseEntity<>(notification, HttpStatus.CREATED) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteLike(@PathVariable("id") Long id) {
    return "OK".equals(likeService.deleteLikeById(id)) ?
      new ResponseEntity<>(HttpStatus.OK) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }
}

