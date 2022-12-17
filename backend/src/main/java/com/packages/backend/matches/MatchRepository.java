package com.packages.backend.matches;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

  void deleteMatchById(Long id);

  Optional<Match> findMatchById(Long id);
}
