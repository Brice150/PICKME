package com.packages.backend.matches;

import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/find/{id}")
  public ResponseEntity<Match> getMatchById(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Match match = matchService.findMatchById(id);
    if (connectedUser.getId().equals(match.getFkSender().getId())
      || connectedUser.getId().equals(match.getFkReceiver().getId())) {
      return new ResponseEntity<>(match, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/add")
  public ResponseEntity<Match> addMatch(@RequestBody Match match) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(match.getFkSender().getId())
      && !connectedUser.getId().equals(match.getFkReceiver().getId())) {
      Match newMatch = matchService.addMatch(match);
      return new ResponseEntity<>(newMatch, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteMatch(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Match match = matchService.findMatchById(id);
    if (connectedUser.getId().equals(match.getFkSender().getId())) {
      matchService.deleteMatchById(id);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}

