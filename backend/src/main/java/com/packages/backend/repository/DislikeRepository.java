package com.packages.backend.repository;

import com.packages.backend.model.Dislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DislikeRepository extends JpaRepository<Dislike, Long> {
  @Query("SELECT d FROM Dislike d WHERE d.fkReceiver = :fkReceiver AND d.fkSender = :fkSender")
  Optional<Dislike> getDislikeByFk(@Param("fkSender") Long fkSender, @Param("fkReceiver") Long fkReceiver);
}
