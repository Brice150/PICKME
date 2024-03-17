package com.packages.backend.repository;

import com.packages.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
  @Query("SELECT m FROM Match m WHERE m.fkReceiver.id = :connectedId OR m.fkSender.id = :connectedId")
  List<Match> getAllUserMatches(@Param("connectedId") Long connectedId);
}
