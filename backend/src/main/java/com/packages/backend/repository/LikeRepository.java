package com.packages.backend.repository;

import com.packages.backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
  void deleteLikeById(Long id);

  @Query("SELECT l FROM Like l WHERE l.fkReceiver.id = :fkReceiver AND l.fkSender.id = :fkSender")
  Optional<Like> getLikeByFk(@Param("fkSender") Long fkSender, @Param("fkReceiver") Long fkReceiver);
}
