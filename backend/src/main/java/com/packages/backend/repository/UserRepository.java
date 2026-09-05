package com.packages.backend.repository;

import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> getUserByEmail(@Param("email") String email);

  @Transactional
  @Modifying
  @Query("DELETE FROM Notification n WHERE n.fkUser.id = :fkUser")
  void deleteUserNotificationsByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Preferences p WHERE p.id = :fkUser")
  void deleteUserPreferencesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM GenderAge g WHERE g.id = :fkUser")
  void deleteUserGenderAgeByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Geolocation g WHERE g.id = :fkUser")
  void deleteUserGeolocationByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Stats s WHERE s.id = :fkUser")
  void deleteUserStatsByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Message m WHERE m.fkSender = :fkUser OR m.fkReceiver = :fkUser")
  void deleteUserMessagesByFk(@Param("fkUser") Long fkUser);

  @Transactional
  @Modifying
  @Query("DELETE FROM Picture p WHERE p.fkUser.id = :fkUser")
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
  @Query("DELETE FROM User u WHERE u.email = :email")
  void deleteUserByEmail(@Param("email") String email);

  // The single valued associations needed to score and to expose a candidate are loaded with the
  // root entity: without those JOIN FETCH clauses Hibernate runs four extra SELECT per candidate,
  // which was by far the main cost of the selection screen. Pictures stay lazy on purpose, they
  // hold base64 payloads and are loaded separately for the current page only.
  //
  // The age criteria is expressed as a range of birth dates rather than as an age computed on
  // every row: the comparison stays portable and can use an index on the birth date.
  @Query(
    "SELECT DISTINCT u FROM User u" +
      " JOIN FETCH u.genderAge genderAge" +
      " JOIN FETCH u.geolocation" +
      " LEFT JOIN FETCH u.preferences" +
      " LEFT JOIN FETCH u.stats" +
      " LEFT JOIN Like l ON u.id = l.fkReceiver AND l.fkSender = :connectedId" +
      " LEFT JOIN Dislike d ON u.id = d.fkReceiver AND d.fkSender = :connectedId" +
      " WHERE genderAge.genderSearch = :gender" +
      " AND genderAge.gender = :genderSearch" +
      " AND u.birthDate >= :earliestBirthDate" +
      " AND u.birthDate < :latestBirthDate" +
      " AND u.id != :connectedId" +
      " AND l.fkSender IS NULL" +
      " AND d.fkSender IS NULL"
  )
  List<User> getAllUsers(@Param("genderSearch") Gender genderSearch, @Param("gender") Gender gender, @Param("earliestBirthDate") Date earliestBirthDate, @Param("latestBirthDate") Date latestBirthDate, @Param("connectedId") Long connectedId);

  @Query(
    value =
      "SELECT u.*" +
        " FROM users u" +
        " INNER JOIN (" +
        "     SELECT fk_receiver, MAX(date) AS max_date" +
        "     FROM likes" +
        "     WHERE fk_sender = :connectedId" +
        "     GROUP BY fk_receiver" +
        " ) likeSender ON u.id = likeSender.fk_receiver" +
        " INNER JOIN (" +
        "     SELECT fk_sender, MAX(date) AS max_date" +
        "     FROM likes" +
        "     WHERE fk_receiver = :connectedId" +
        "     GROUP BY fk_sender" +
        " ) likeReceiver ON u.id = likeReceiver.fk_sender" +
        " LEFT JOIN (" +
        "     SELECT fk_receiver, MAX(date) AS max_date" +
        "     FROM messages" +
        "     WHERE fk_sender = :connectedId" +
        "     GROUP BY fk_receiver" +
        " ) messageSender ON u.id = messageSender.fk_receiver" +
        " LEFT JOIN (" +
        "     SELECT fk_sender, MAX(date) AS max_date" +
        "     FROM messages" +
        "     WHERE fk_receiver = :connectedId" +
        "     GROUP BY fk_sender" +
        " ) messageReceiver ON u.id = messageReceiver.fk_sender" +
        " WHERE u.id != :connectedId" +
        " ORDER BY" +
        " COALESCE(" +
        "     GREATEST(" +
        "         likeSender.max_date," +
        "         likeReceiver.max_date," +
        "         COALESCE(messageReceiver.max_date, '0001-01-01')," +
        "         COALESCE(messageSender.max_date, '0001-01-01')" +
        "     )," +
        "     '0001-01-01'" +
        " ) DESC",
    nativeQuery = true
  )
  List<User> getAllUserMatches(@Param("connectedId") Long connectedId);
}
