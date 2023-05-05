package com.packages.backend.matches;

import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MatchService {
  private final MatchRepository matchRepository;
  private final UserService userService;

  @Autowired
  public MatchService(MatchRepository matchRepository, UserService userService) {
    this.matchRepository = matchRepository;
    this.userService = userService;
  }

  public void addMatch(Match match) {
    match.setDate(new Date());
    matchRepository.save(match);
  }

  public List<Match> findAllMatchesByFk() {
    User connectedUser = userService.findConnectedUser();
    List<Match> matches = matchRepository.findAllMatchesByFk(connectedUser.getId());
    Comparator<Match> matchesSort = Comparator
      .comparing(Match::getDate, Date::compareTo);
    matches.sort(matchesSort);
    return matches;
  }

  public Match findMatchById(Long id) {
    return matchRepository.findMatchById(id)
      .orElseThrow(() -> new MatchNotFoundException("Match by id " + id + " was not found"));
  }

  public Optional<Match> findMatchByFk(Long fkSender, Long fkReceiver) {
    User connectedUser = userService.findConnectedUser();
    if (!Objects.equals(connectedUser.getId(), fkSender)
      && !Objects.equals(connectedUser.getId(), fkReceiver)) {
      return Optional.empty();
    }
    return matchRepository.findMatchByFk(fkSender, fkReceiver);
  }

  @Transactional
  public void deleteMatchById(Long id) {
    matchRepository.deleteMatchById(id);
  }
}

