package com.packages.backend.service;

import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DislikeService {
  private final DislikeRepository dislikeRepository;
  private final LikeService likeService;
  private final UserService userService;
  private static final String FORBIDDEN = "FORBIDDEN";

  public DislikeService(DislikeRepository dislikeRepository, UserService userService, LikeService likeService) {
    this.dislikeRepository = dislikeRepository;
    this.userService = userService;
    this.likeService = likeService;
  }

  public String addDislike(Long userId) {
    User connectedUser = userService.getConnectedUser();
    User dislikedUser = userService.getUserById(userId);
    Optional<Dislike> previousSenderDislike = dislikeRepository.getDislikeByFk(connectedUser.getId(), dislikedUser.getId());
    if (previousSenderDislike.isPresent()) {
      return FORBIDDEN;
    }
    likeService.deleteLikeByFk(connectedUser.getId(), dislikedUser.getId());
    Dislike dislike = new Dislike(new Date(), connectedUser.getId(), dislikedUser.getId());
    dislike.setDate(new Date());
    dislikeRepository.save(dislike);
    return null;
  }
}

