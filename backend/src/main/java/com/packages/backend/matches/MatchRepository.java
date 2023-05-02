package com.packages.backend.matches;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

  void deleteMatchById(Long id);

  @Query("SELECT m FROM Match m WHERE m.fkReceiver.id = :connectedId OR m.fkSender.id = :connectedId")
  List<Match> findAllMatchesByFk(@Param("connectedId") Long connectedId);

  Optional<Match> findMatchById(Long id);

  @Query("SELECT m FROM Match m WHERE (m.fkReceiver.id = :fkReceiver AND m.fkSender.id = :fkSender) OR (m.fkReceiver.id = :fkSender AND m.fkSender.id = :fkReceiver)")
  Optional<Match> findMatchByFk(@Param("fkSender") Long fkSender, @Param("fkReceiver") Long fkReceiver);
}
