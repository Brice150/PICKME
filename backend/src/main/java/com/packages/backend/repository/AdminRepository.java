package com.packages.backend.repository;

import com.packages.backend.model.AdminStats;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {
  @Query(
    "SELECT new com.packages.backend.model.AdminStats( " +
      "COUNT(u), " +
      "(SELECT COUNT(d) FROM DeletedAccount d), " +
      "(SELECT COUNT(u) FROM User u WHERE u.registeredDate >= :recentDate), " +
      "(SELECT COUNT(d) FROM DeletedAccount d WHERE d.deletionDate >= :recentDate) " +
      " )" +
      "FROM User u"
  )
  AdminStats getAdminStats(@Param("recentDate") Date recentDate);

  @Query(
    "SELECT u FROM User u " +
      " WHERE u.id != :connectedId" +
      " AND LOWER(u.email) LIKE CONCAT('%', LOWER(:email), '%')" +
      " ORDER BY u.registeredDate DESC"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId, @Param("email") String email, Pageable pageable);

  @Query(
    "SELECT u FROM User u " +
      " WHERE u.id != :connectedId" +
      " AND LOWER(u.email) LIKE CONCAT('%', LOWER(:email), '%')" +
      " ORDER BY CASE" +
      " WHEN :orderBy = 'totalDislikes' THEN u.stats.totalDislikes" +
      " WHEN :orderBy = 'totalLikes' THEN u.stats.totalLikes" +
      " WHEN :orderBy = 'totalMatches' THEN u.stats.totalMatches" +
      " END DESC"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId, @Param("email") String email, @Param("orderBy") String orderBy, Pageable pageable);

  @Query(
    "SELECT d FROM DeletedAccount d " +
      " WHERE LOWER(d.email) LIKE CONCAT('%', LOWER(:email), '%')" +
      " ORDER BY d.deletionDate DESC"
  )
  List<DeletedAccount> getAllDeletedAccounts(@Param("email") String email, Pageable pageable);

  @Query(
    "SELECT d FROM DeletedAccount d " +
      " WHERE LOWER(d.email) LIKE CONCAT('%', LOWER(:email), '%')" +
      " ORDER BY CASE" +
      " WHEN :orderBy = 'totalDislikes' THEN d.totalDislikes" +
      " WHEN :orderBy = 'totalLikes' THEN d.totalLikes" +
      " WHEN :orderBy = 'totalMatches' THEN d.totalMatches" +
      " END DESC"
  )
  List<DeletedAccount> getAllDeletedAccounts(@Param("email") String email, @Param("orderBy") String orderBy, Pageable pageable);
}
