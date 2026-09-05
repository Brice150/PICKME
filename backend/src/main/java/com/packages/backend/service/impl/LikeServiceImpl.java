package com.packages.backend.service.impl;

import com.packages.backend.model.entity.Like;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.StatsRepository;
import com.packages.backend.service.LikeService;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

  private final LikeRepository likeRepository;
  private final DislikeRepository dislikeRepository;
  private final MessageRepository messageRepository;
  private final StatsRepository statsRepository;
  private final UserService userService;
  private final NotificationService notificationService;

  public LikeServiceImpl(LikeRepository likeRepository, DislikeRepository dislikeRepository, MessageRepository messageRepository, UserService userService, StatsRepository statsRepository, NotificationService notificationService) {
    this.likeRepository = likeRepository;
    this.dislikeRepository = dislikeRepository;
    this.messageRepository = messageRepository;
    this.userService = userService;
    this.statsRepository = statsRepository;
    this.notificationService = notificationService;
  }

  @Override
  @Transactional
  public String addLike(Long userId) {
    String matchNotification = null;
    User connectedUser = userService.getConnectedUser();
    User likedUser = userService.getUserById(userId);

    if (isForbidden(connectedUser, likedUser)) {
      return ServiceStatus.FORBIDDEN;
    }

    Stats userStats = statsRepository.getReferenceById(likedUser.getId());
    userStats.setTotalLikes(userStats.getTotalLikes() + 1);
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(likedUser.getId(), connectedUser.getId());
    if (previousReceiverLike.isPresent()) {
      matchNotification = handleMatch(likedUser, userStats, connectedUser);
    }
    Like like = new Like(new Date(), connectedUser.getId(), likedUser.getId());
    likeRepository.save(like);
    statsRepository.save(userStats);
    return matchNotification;
  }

  /**
   * Tells whether the connected user already answered that profile.
   *
   * @param connectedUser connected user
   * @param likedUser     liked user
   * @return true when the profile has already been liked or disliked
   */
  private boolean isForbidden(User connectedUser, User likedUser) {
    return isSenderAlreadyLiked(connectedUser, likedUser) || isSenderAlreadyDisliked(connectedUser, likedUser);
  }

  /**
   * Tells whether the connected user already liked that profile.
   *
   * @param connectedUser connected user
   * @param likedUser     liked user
   * @return true when a like already exists
   */
  private boolean isSenderAlreadyLiked(User connectedUser, User likedUser) {
    return likeRepository.getLikeByFk(connectedUser.getId(), likedUser.getId()).isPresent();
  }

  /**
   * Tells whether the connected user already disliked that profile.
   *
   * @param connectedUser connected user
   * @param likedUser     liked user
   * @return true when a dislike already exists
   */
  private boolean isSenderAlreadyDisliked(User connectedUser, User likedUser) {
    return dislikeRepository.getDislikeByFk(connectedUser.getId(), likedUser.getId()).isPresent();
  }

  @Override
  @Transactional
  public void deleteLikeByFk(User connectedUser, User dislikedUser) {
    Optional<Like> previousSenderLike = likeRepository.getLikeByFk(connectedUser.getId(), dislikedUser.getId());
    Optional<Like> previousReceiverLike = likeRepository.getLikeByFk(dislikedUser.getId(), connectedUser.getId());
    Stats userStats = statsRepository.getReferenceById(dislikedUser.getId());
    userStats.setTotalDislikes(userStats.getTotalDislikes() + 1);
    previousSenderLike.ifPresent(likeSender -> {
      userStats.setTotalLikes(userStats.getTotalLikes() - 1);
      previousReceiverLike.ifPresent(likeReceiver -> handleDismatch(userStats, connectedUser, dislikedUser));
      likeRepository.deleteLikeById(likeSender.getId());
    });
    statsRepository.save(userStats);
  }

  /**
   * Increments the match counters of both users and warns the liked one.
   *
   * @param likedUser     liked user
   * @param userStats     statistics of the liked user, updated in place
   * @param connectedUser connected user
   * @return the nickname of the matched user
   */
  private String handleMatch(User likedUser, Stats userStats, User connectedUser) {
    userStats.setTotalMatches(userStats.getTotalMatches() + 1);
    Stats connectedUserStats = statsRepository.getReferenceById(connectedUser.getId());
    connectedUserStats.setTotalMatches(connectedUserStats.getTotalMatches() + 1);
    notificationService.sendNotification("New match with " + connectedUser.getNickname(), "match", likedUser);
    statsRepository.save(connectedUserStats);
    return likedUser.getNickname();
  }

  /**
   * Drops the conversation, decrements the match counters of both users and warns the disliked one.
   *
   * @param userStats     statistics of the disliked user, updated in place
   * @param connectedUser connected user
   * @param dislikedUser  disliked user
   */
  private void handleDismatch(Stats userStats, User connectedUser, User dislikedUser) {
    messageRepository.deleteMessagesByFk(connectedUser.getId(), dislikedUser.getId());
    notificationService.sendNotification(connectedUser.getNickname() + " decided to unmatch", "unmatch", dislikedUser);
    userStats.setTotalMatches(userStats.getTotalMatches() - 1);
    Stats connectedUserStats = statsRepository.getReferenceById(connectedUser.getId());
    connectedUserStats.setTotalMatches(connectedUserStats.getTotalMatches() - 1);
    statsRepository.save(connectedUserStats);
  }
}
