package com.packages.backend.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

  void deleteLikeById(Long id);

  Optional<Like> findLikeById(Long id);

  @Query("SELECT l FROM Like l WHERE l.fkReceiver.id = :fkReceiver AND l.fkSender.id = :fkSender")
  Optional<Like> findLikeByFk(@Param("fkSender") Long fkSender, @Param("fkReceiver") Long fkReceiver);
}
