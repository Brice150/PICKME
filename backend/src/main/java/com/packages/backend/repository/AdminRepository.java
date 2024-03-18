package com.packages.backend.repository;

import com.packages.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;

public interface AdminRepository extends JpaRepository<User, Long> {
  @Query(
    "SELECT u FROM User u " +
      " WHERE u.id != :connectedId" +
      " ORDER BY u.nickname"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId);

  @Query(
    "SELECT DISTINCT u.id as id, count(d) as totalDislikes, count(l) as totalLikes, count(likeReceiver) as totalMatches" +
      " FROM User u " +
      " LEFT JOIN Dislike d ON u.id = d.fkReceiver" +
      " LEFT JOIN Like l ON u.id = l.fkReceiver" +
      " LEFT JOIN Like likeSender ON u.id = likeSender.fkSender" +
      " LEFT JOIN Like likeReceiver ON u.id = likeReceiver.fkReceiver" +
      " AND likeReceiver.id IS NOT NULL" +
      " AND likeSender.id IS NOT NULL" +
      " WHERE u.id != :connectedId" +
      " GROUP BY u.id"
  )
  List<Tuple> getAllUsersStats(@Param("connectedId") Long connectedId);
}
