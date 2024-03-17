package com.packages.backend.repository;

import com.packages.backend.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {

  void deletePictureById(Long id);

  Optional<Picture> findPictureById(Long id);

  @Query("SELECT p FROM Picture p WHERE p.fkUser.id = :fkUser")
  Optional<List<Picture>> getUserPicturesByFk(@Param("fkUser") Long fkUser);
}
