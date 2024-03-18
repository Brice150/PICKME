package com.packages.backend.repository;

import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<User, Long> {
  @Query(
    "SELECT u FROM User u " +
      " WHERE u.id != :connectedId" +
      " ORDER BY u.nickname"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId);

  @Query(
    "SELECT u.id, count(d) as totalDislikes, count(l) as totalLikes, 0 as totalMatches" +
      " FROM User u " +
      " LEFT JOIN Dislike d ON u.id = d.fkReceiver" +
      " LEFT JOIN Like l ON u.id = l.fkReceiver" +
      " WHERE u.id != :connectedId" +
      " GROUP BY u.id"
  )
  List<UserStats> getAllUsersStats(@Param("connectedId") Long connectedId);
}
