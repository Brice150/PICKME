package com.packages.backend.likes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

  void deleteLikeById(Long id);

  Optional<Like> findLikeById(Long id);
}
