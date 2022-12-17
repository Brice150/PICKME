package com.packages.backend.pictures;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {

  void deletePictureById(Long id);

  Optional<Picture> findPictureById(Long id);
}
