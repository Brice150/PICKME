package com.packages.backend.repository;

import com.packages.backend.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {

  void deletePictureById(Long id);

  Optional<Picture> findPictureById(Long id);
}
