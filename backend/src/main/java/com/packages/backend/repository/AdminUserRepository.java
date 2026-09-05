package com.packages.backend.repository;

import com.packages.backend.model.AdminStats;
import com.packages.backend.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Reads the accounts for the back office. The ordering is carried by the {@link Pageable} rather
 * than by a {@code CASE} in the query, so that adding a sort column costs nothing here.
 */
@Repository
public interface AdminUserRepository extends JpaRepository<User, Long> {

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
      " AND LOWER(u.email) LIKE CONCAT('%', LOWER(:email), '%')"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId, @Param("email") String email, Pageable pageable);
}
