package com.packages.backend.matches;

import com.packages.backend.likes.Like;
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
@RequestMapping("/match")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class MatchController {
  private final MatchService matchService;
  private final UserService userService;

  public MatchController(MatchService matchService, UserService userService) {
    this.matchService = matchService;
    this.userService = userService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<Match>> getAllUserMatches() {
    List<Match> matches = matchService.findAllMatches();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    matches.removeIf(match -> !connectedUser.getId().equals(match.getFkReceiver().getId())
                              && !connectedUser.getId().equals(match.getFkSender().getId()));
    return new ResponseEntity<>(matches, HttpStatus.OK);
  }
}

