package com.packages.backend.likes;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class LikeController {
  private final LikeService likeService;
  private final UserService userService;
  private final MatchService matchService;

  public LikeController(LikeService likeService, UserService userService, MatchService matchService) {
    this.likeService = likeService;
    this.userService = userService;
    this.matchService = matchService;
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

  @GetMapping("/find/all")
  public ResponseEntity<List<Like>> getAllLikes() {
    List<Like> likes = likeService.findAllLikes();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    likes.removeIf(like -> !connectedUser.getId().equals(like.getFkReceiver().getId()));
    return new ResponseEntity<>(likes, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity<Like> addLike(@RequestBody Like like) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(like.getFkSender().getId())
      && !connectedUser.getId().equals(like.getFkReceiver().getId())) {
      User likedUser = userService.findUserById(like.getFkReceiver().getId());
      Like newLike = likeService.addLike(like);
      List<Like> likes = likeService.findAllLikes();
      for (Like previousLike : likes) {
        if (connectedUser.getId().equals(previousLike.getFkReceiver().getId())) {
          Match match = new Match(null, connectedUser, likedUser);
          matchService.addMatch(match);
        }
      }
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
      List<Match> matches = matchService.findAllMatches();
      for (Match previousMatch : matches) {
        if (connectedUser.getId().equals(previousMatch.getFkReceiver().getId())
          || connectedUser.getId().equals(previousMatch.getFkSender().getId())) {
          matchService.deleteMatchById(previousMatch.getId());
        }
      }
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}

