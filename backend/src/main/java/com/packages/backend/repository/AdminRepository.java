package com.packages.backend.repository;

import com.packages.backend.model.user.User;
import com.packages.backend.model.user.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Set;

public interface AdminRepository extends JpaRepository<User, Long> {
  @Query(
    "SELECT u FROM User u " +
      " WHERE u.id != :connectedId" +
      " AND LOWER(u.nickname) LIKE CONCAT('%', LOWER(:nickname), '%')" +
      " AND u.gender IN :genders" +
      " AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.birthDate)) BETWEEN :minAge AND :maxAge" +
      " ORDER BY u.nickname"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId, @Param("nickname") String nickname, @Param("genders") Set<Gender> genders, @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

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
