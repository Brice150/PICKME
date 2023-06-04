package com.packages.backend.user;

import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
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
  Optional<User> findUserByEmail(@Param("email") String email);

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

  @Query("SELECT u FROM User u WHERE u.genderSearch = :gender AND u.gender = :genderSearch")
  List<User> findAllUsers(@Param("genderSearch") String genderSearch, @Param("gender") String gender);

  @Query("SELECT p FROM Picture p WHERE p.id = :id")
  Optional<Picture> findPictureById(Long id);
}
