package com.packages.backend.service;

import com.packages.backend.model.Match;
import com.packages.backend.model.user.User;
import com.packages.backend.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {
  private final MatchRepository matchRepository;
  private final UserService userService;

  @Autowired
  public MatchService(MatchRepository matchRepository, UserService userService) {
    this.matchRepository = matchRepository;
    this.userService = userService;
  }

  public List<Match> getAllUserMatches() {
    User connectedUser = userService.getConnectedUser();
    return matchRepository.getAllUserMatches(connectedUser.getId());
  }
}

