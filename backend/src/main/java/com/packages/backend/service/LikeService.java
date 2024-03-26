package com.packages.backend.service;

import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.Like;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class LikeService {
  private final LikeRepository likeRepository;
  private final DislikeRepository dislikeRepository;
  private final UserService userService;
  private final StatsRepository statsRepository;
  private static final String FORBIDDEN = "FORBIDDEN";

  @Autowired
  public LikeService(LikeRepository likeRepository, DislikeRepository dislikeRepository, UserService userService, StatsRepository statsRepository) {
    this.likeRepository = likeRepository;
    this.dislikeRepository = dislikeRepository;
    this.userService = userService;
    this.statsRepository = statsRepository;
  }

  public String addLike(Long userId) {
    String matchNotification = null;
    User connectedUser = userService.getConnectedUser();
    User likedUser = userService.getUserById(userId);
    Optional<Like> previousSenderLike = likeRepository.getLikeByFk(connectedUser.getId(), likedUser.getId());
    if (previousSenderLike.isPresent()) {
      return FORBIDDEN;
    }
    Optional<Dislike> previousSenderDislike = dislikeRepository.getDislikeByFk(connectedUser.getId(), likedUser.getId());
    if (previousSenderDislike.isPresent()) {
      return FORBIDDEN;
    }
    Stats userStats = statsRepository.getById(likedUser.getId());
    userStats.setTotalLikes(userStats.getTotalLikes() + 1);
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(likedUser.getId(), connectedUser.getId());
    if (previousReceiverLike.isPresent()) {
      matchNotification = likedUser.getNickname();
      userStats.setTotalMatches(userStats.getTotalMatches() + 1);
      Stats connectedUserStats = statsRepository.getById(connectedUser.getId());
      connectedUserStats.setTotalMatches(connectedUserStats.getTotalMatches() + 1);
      statsRepository.save(connectedUserStats);
    }
    Like like = new Like(new Date(), connectedUser.getId(), likedUser.getId());
    like.setDate(new Date());
    likeRepository.save(like);
    statsRepository.save(userStats);
    return matchNotification;
  }

  @Transactional
  public void deleteLikeByFk(Long connectedUserId, Long userId) {
    Optional<Like> previousSenderLike = likeRepository.getLikeByFk(connectedUserId, userId);
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(userId, connectedUserId);
    Stats userStats = statsRepository.getById(userId);
    userStats.setTotalDislikes(userStats.getTotalDislikes() + 1);
    previousSenderLike.ifPresent(likeSender -> {
      userStats.setTotalLikes(userStats.getTotalLikes() - 1);
      previousReceiverLike.ifPresent(likeReceiver -> {
        userStats.setTotalMatches(userStats.getTotalMatches() - 1);
        Stats connectedUserStats = statsRepository.getById(connectedUserId);
        connectedUserStats.setTotalMatches(connectedUserStats.getTotalMatches() - 1);
        statsRepository.save(connectedUserStats);
      });
      likeRepository.deleteLikeById(likeSender.getId());
    });
    statsRepository.save(userStats);
  }
}

