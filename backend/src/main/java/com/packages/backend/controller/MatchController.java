package com.packages.backend.controller;

import com.packages.backend.model.Match;
import com.packages.backend.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/match")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class MatchController {
  private final MatchService matchService;

  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<Match>> getAllUserMatches() {
    return new ResponseEntity<>(matchService.getAllUserMatches(), HttpStatus.OK);
  }
}

