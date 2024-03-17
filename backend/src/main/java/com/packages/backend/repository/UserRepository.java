package com.packages.backend.repository;

import com.packages.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> getUserByEmail(@Param("email") String email);

  @Transactional
  @Modifying
  @Query("DELETE FROM Message m WHERE m.fkSender = :fkUser OR m.fkReceiver = :fkUser")
  void deleteUserMessagesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Picture p WHERE p.fkUser = :fkUser")
  void deleteUserPicturesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Like l WHERE l.fkSender = :fkUser OR l.fkReceiver = :fkUser")
  void deleteUserLikesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Dislike d WHERE d.fkSender = :fkUser OR d.fkReceiver = :fkUser")
  void deleteUserDislikesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Match m WHERE m.fkSender = :fkUser OR m.fkReceiver = :fkUser")
  void deleteUserMatchesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM User u WHERE u.email = :email")
  void deleteUserByEmail(@Param("email") String email);

  @Query(
    "SELECT DISTINCT u.* FROM User u" +
      " LEFT JOIN Like l ON u.id = l.fkReceiver" +
      " LEFT JOIN Dislike d ON u.id = d.fkReceiver" +
      " WHERE u.genderSearch = :genderSearch" +
      " AND u.gender = :gender" +
      " AND u.minAge > :minAge" +
      " AND u.maxAge < :maxAge" +
      " AND u.id != :id" +
      " AND (l.fkSender IS NULL OR l.fkSender != :id)" +
      " AND (d.fkSender IS NULL OR d.fkSender != :id)"
  )
  List<User> getAllUsers(@Param("genderSearch") String genderSearch, @Param("gender") String gender, @Param("minAge") Long minAge, @Param("maxAge") Long maxAge, @Param("id") Long id);
}
