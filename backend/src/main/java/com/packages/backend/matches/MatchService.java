package com.packages.backend.matches;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MatchService {
  private final MatchRepository matchRepository;

  @Autowired
  public MatchService(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  public Match addMatch(Match match) {
    match.setDate(new Date());
    return matchRepository.save(match);
  }

  public List<Match> findAllMatches() {
    return matchRepository.findAll();
  }

  public Match findMatchById(Long id) {
    return matchRepository.findMatchById(id)
      .orElseThrow(() -> new MatchNotFoundException("Match by id " + id + " was not found"));
  }

  @Transactional
  public void deleteMatchById(Long id) {
    matchRepository.deleteMatchById(id);
  }
}

