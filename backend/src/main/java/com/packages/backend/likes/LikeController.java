package com.packages.backend.likes;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRole;
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

  @GetMapping("/{fkSender}/{fkReceiver}")
  public ResponseEntity<Like> getLikeByFk(@PathVariable("fkSender") Long fkSender, @PathVariable("fkReceiver") Long fkReceiver) {
    List<Like> likes = likeService.findAllLikes();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    likes.removeIf(like ->
        fkSender != like.getFkSender().getId()
        || fkReceiver != like.getFkReceiver().getId()
      );
    if (likes.size() == 0) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Like like = likes.get(0);
    return new ResponseEntity<>(like, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> addLike(@RequestBody Like like) {
    String matchNotification = null;
    Boolean alreadyMatched = false;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId() == like.getFkSender().getId()
      && connectedUser.getId() != like.getFkReceiver().getId()) {
      User likedUser = userService.findUserById(like.getFkReceiver().getId());
      List<Match> matches = matchService.findAllMatches();
      for (Match previousMatch : matches) {
        if ((connectedUser.getId() == previousMatch.getFkReceiver().getId()
          && likedUser.getId() == previousMatch.getFkSender().getId())
          || (connectedUser.getId() == previousMatch.getFkSender().getId()
          && likedUser.getId() == previousMatch.getFkReceiver().getId())) {
          alreadyMatched = true;
        }
      }
      List<Like> likes = likeService.findAllLikes();
      for (Like previousLike : likes) {
        if (previousLike.getFkReceiver().getId() == like.getFkReceiver().getId()
            && previousLike.getFkSender().getId() == like.getFkSender().getId()) {
          return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (connectedUser.getId() == previousLike.getFkReceiver().getId()
            && likedUser.getId() == previousLike.getFkSender().getId()
            && !alreadyMatched) {
          matchNotification = likedUser.getNickname();
          Match match = new Match(null, connectedUser, likedUser);
          matchService.addMatch(match);
        }
      }
      Like newLike = likeService.addLike(like);
      return new ResponseEntity<>(matchNotification, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteLike(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Like like = likeService.findLikeById(id);
    if (connectedUser.getId() == like.getFkSender().getId()) {
      likeService.deleteLikeById(id);
      List<Match> matches = matchService.findAllMatches();
      for (Match previousMatch : matches) {
        if ((connectedUser.getId() == previousMatch.getFkReceiver().getId()
            && like.getFkReceiver().getId() == previousMatch.getFkSender().getId())
          || (connectedUser.getId() == previousMatch.getFkSender().getId())
            && like.getFkReceiver().getId() == previousMatch.getFkReceiver().getId()) {
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

