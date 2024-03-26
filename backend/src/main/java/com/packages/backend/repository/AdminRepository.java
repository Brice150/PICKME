package com.packages.backend.repository;

import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {
  @Query(
    "SELECT u FROM User u " +
      " WHERE u.id != :connectedId" +
      " AND LOWER(u.nickname) LIKE CONCAT('%', LOWER(:nickname), '%')" +
      " AND u.genderAge.gender IN :genders" +
      " AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.birthDate)) BETWEEN :minAge AND :maxAge" +
      " ORDER BY u.nickname"
  )
  List<User> getAllUsers(@Param("connectedId") Long connectedId, @Param("nickname") String nickname, @Param("genders") Set<Gender> genders, @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge, Pageable pageable);
}
