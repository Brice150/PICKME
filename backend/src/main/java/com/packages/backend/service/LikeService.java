package com.packages.backend.service;

import com.packages.backend.model.entity.Like;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.StatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class LikeService {
  private final LikeRepository likeRepository;
  private final DislikeRepository dislikeRepository;
  private final MessageRepository messageRepository;
  private final UserService userService;
  private final StatsRepository statsRepository;
  private final NotificationService notificationService;
  private static final String FORBIDDEN = "FORBIDDEN";

  public LikeService(LikeRepository likeRepository, DislikeRepository dislikeRepository, MessageRepository messageRepository, UserService userService, StatsRepository statsRepository, NotificationService notificationService) {
    this.likeRepository = likeRepository;
    this.dislikeRepository = dislikeRepository;
    this.messageRepository = messageRepository;
    this.userService = userService;
    this.statsRepository = statsRepository;
    this.notificationService = notificationService;
  }

  public String addLike(Long userId) {
    String matchNotification = null;
    User connectedUser = userService.getConnectedUser();
    User likedUser = userService.getUserById(userId);

    if (isForbidden(connectedUser, likedUser)) {
      return FORBIDDEN;
    }

    Stats userStats = statsRepository.getById(likedUser.getId());
    userStats.setTotalLikes(userStats.getTotalLikes() + 1);
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(likedUser.getId(), connectedUser.getId());
    if (previousReceiverLike.isPresent()) {
      matchNotification = handleMatch(likedUser, userStats, connectedUser);
    }
    Like like = new Like(new Date(), connectedUser.getId(), likedUser.getId());
    like.setDate(new Date());
    likeRepository.save(like);
    statsRepository.save(userStats);
    return matchNotification;
  }

  private boolean isForbidden(User connectedUser, User likedUser) {
    return isSenderAlreadyLiked(connectedUser, likedUser) || isSenderAlreadyDisliked(connectedUser, likedUser);
  }

  private boolean isSenderAlreadyLiked(User connectedUser, User likedUser) {
    return likeRepository.getLikeByFk(connectedUser.getId(), likedUser.getId()).isPresent();
  }

  private boolean isSenderAlreadyDisliked(User connectedUser, User likedUser) {
    return dislikeRepository.getDislikeByFk(connectedUser.getId(), likedUser.getId()).isPresent();
  }

  @Transactional
  public void deleteLikeByFk(User connectedUser, User dislikedUser) {
    Optional<Like> previousSenderLike = likeRepository.getLikeByFk(connectedUser.getId(), dislikedUser.getId());
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(dislikedUser.getId(), connectedUser.getId());
    Stats userStats = statsRepository.getById(dislikedUser.getId());
    userStats.setTotalDislikes(userStats.getTotalDislikes() + 1);
    previousSenderLike.ifPresent(likeSender -> {
      userStats.setTotalLikes(userStats.getTotalLikes() - 1);
      previousReceiverLike.ifPresent(likeReceiver -> handleDismatch(userStats, connectedUser, dislikedUser));
      likeRepository.deleteLikeById(likeSender.getId());
    });
    statsRepository.save(userStats);
  }

  private String handleMatch(User likedUser, Stats userStats, User connectedUser) {
    userStats.setTotalMatches(userStats.getTotalMatches() + 1);
    Stats connectedUserStats = statsRepository.getById(connectedUser.getId());
    connectedUserStats.setTotalMatches(connectedUserStats.getTotalMatches() + 1);
    notificationService.sendNotification("New match with " + connectedUser.getNickname(), "match", likedUser);
    statsRepository.save(connectedUserStats);
    return likedUser.getNickname();
  }

  private void handleDismatch(Stats userStats, User connectedUser, User dislikedUser) {
    messageRepository.deleteMessagesByFk(connectedUser.getId(), dislikedUser.getId());
    notificationService.sendNotification(connectedUser.getNickname() + " decided to unmatch", "unmatch", dislikedUser);
    userStats.setTotalMatches(userStats.getTotalMatches() - 1);
    Stats connectedUserStats = statsRepository.getById(connectedUser.getId());
    connectedUserStats.setTotalMatches(connectedUserStats.getTotalMatches() - 1);
    statsRepository.save(connectedUserStats);
  }
}

