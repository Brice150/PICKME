package com.packages.backend.service;

import com.packages.backend.model.Like;
import com.packages.backend.model.user.User;
import com.packages.backend.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class LikeService {
  private final LikeRepository likeRepository;
  private final UserService userService;
  private static final String FORBIDDEN = "FORBIDDEN";

  @Autowired
  public LikeService(LikeRepository likeRepository, UserService userService) {
    this.likeRepository = likeRepository;
    this.userService = userService;
  }

  public String addLike(Long userId) {
    String matchNotification = null;
    User connectedUser = userService.getConnectedUser();
    User likedUser = userService.getUserById(userId);
    Optional<Like> previousSenderLike = likeRepository.getLikeByFk(connectedUser.getId(), likedUser.getId());
    if (previousSenderLike.isPresent()) {
      return FORBIDDEN;
    }
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(likedUser.getId(), connectedUser.getId());
    if (previousReceiverLike.isPresent()) {
      matchNotification = likedUser.getNickname();
    }
    Like like = new Like(new Date(), connectedUser.getId(), likedUser.getId());
    like.setDate(new Date());
    likeRepository.save(like);
    //TODO: save user stats
    return matchNotification;
  }

  @Transactional
  public void deleteLikeByFk(Long connectedUserId, Long userId) {
    Optional<Like> previousSenderLike = likeRepository.getLikeByFk(connectedUserId, userId);
    previousSenderLike.ifPresent(like -> likeRepository.deleteLikeById(like.getId()));
  }
}

