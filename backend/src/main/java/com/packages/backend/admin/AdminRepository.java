package com.packages.backend.admin;

import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
import com.packages.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Match, Long> {
  @Transactional
  @Modifying
  @Query("DELETE FROM Message m WHERE m.id = :id")
  void deleteMessageById(@Param("id") Long id);

  @Transactional
  @Modifying
  @Query("DELETE FROM Match m WHERE m.id = :id")
  void deleteMatchById(@Param("id") Long id);

  @Transactional
  @Modifying
  @Query("DELETE FROM Like l WHERE l.id = :id")
  void deleteLikeById(@Param("id") Long id);

  @Transactional
  @Modifying
  @Query("DELETE FROM Picture p WHERE p.id = :id")
  void deletePictureById(@Param("id") Long id);

  @Transactional
  @Modifying
  @Query("DELETE FROM User u WHERE u.email = :email")
  void deleteUserByEmail(@Param("email") String email);

  @Query("SELECT m FROM Message m WHERE m.fkSender.id = :fkUser OR m.fkReceiver.id = :fkUser")
  List<Message> findAllMessagesByFk(@Param("fkUser") Long fkUser);

  @Query("SELECT m FROM Match m WHERE m.fkSender.id = :fkUser OR m.fkReceiver.id = :fkUser")
  List<Match> findAllMatchesByFk(@Param("fkUser") Long fkUser);

  @Query("SELECT l FROM Like l WHERE l.fkSender.id = :fkUser OR l.fkReceiver.id = :fkUser")
  List<Like> findAllLikesByFk(@Param("fkUser") Long fkUser);

  @Query("SELECT p FROM Picture p WHERE p.fkUser.id = :fkUser")
  List<Picture> findAllPicturesByFk(@Param("fkUser") Long fkUser);

  @Query("SELECT u FROM User u")
  List<User> findAllUsers();

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> findUserByEmail(@Param("email") String email);
}
