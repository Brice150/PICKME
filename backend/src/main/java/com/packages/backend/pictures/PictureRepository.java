package com.packages.backend.pictures;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {

  void deletePictureByContent(String content);

  Optional<Picture> findPictureByContent(String content);
}
