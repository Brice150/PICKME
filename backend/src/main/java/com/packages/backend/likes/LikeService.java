package com.packages.backend.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LikeService {
  private final LikeRepository likeRepository;

  @Autowired
  public LikeService(LikeRepository likeRepository) {
    this.likeRepository = likeRepository;
  }

  public Like addLike(Like like) {
    like.setDate(new Date());
    return likeRepository.save(like);
  }

  public List<Like> findAllLikes() {
    return likeRepository.findAll();
  }

  public Like findLikeById(Long id) {
    return likeRepository.findLikeById(id)
      .orElseThrow(() -> new LikeNotFoundException("Like by id " + id + " was not found"));
  }

  @Transactional
  public void deleteLikeById(Long id) {
    likeRepository.deleteLikeById(id);
  }
}

