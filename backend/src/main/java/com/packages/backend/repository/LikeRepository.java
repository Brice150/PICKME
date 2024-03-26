package com.packages.backend.repository;

import com.packages.backend.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
  void deleteLikeById(Long id);

  @Query("SELECT l FROM Like l WHERE l.fkReceiver = :fkReceiver AND l.fkSender = :fkSender")
  Optional<Like> getLikeByFk(@Param("fkSender") Long fkSender, @Param("fkReceiver") Long fkReceiver);

  @Query("SELECT l.fkSender FROM Like l WHERE l.fkReceiver = :connectedId")
  List<Long> getGoldByConnectedUserId(@Param("connectedId") Long connectedId);
}
