package com.packages.backend.likes;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class LikeService {
  private final LikeRepository likeRepository;
  private final UserService userService;
  private final MatchService matchService;
  private static final String FORBIDDEN = "FORBIDDEN";

  @Autowired
  public LikeService(LikeRepository likeRepository, UserService userService, MatchService matchService) {
    this.likeRepository = likeRepository;
    this.userService = userService;
    this.matchService = matchService;
  }

  public String addLike(Like like) {
    String notification = null;
    User connectedUser = userService.findConnectedUser();
    if (Objects.equals(connectedUser.getId(), like.getFkSender().getId())
      && !Objects.equals(connectedUser.getId(), like.getFkReceiver().getId())) {
      Optional<User> likedUser = userService.findUserById(like.getFkReceiver().getId());
      Optional<Match> previousMatch = matchService.findMatchByFk(like.getFkSender().getId(), like.getFkReceiver().getId());
      if (previousMatch.isPresent()) {
        return FORBIDDEN;
      }
      Optional<Like> previousSenderLike = findLikeByFk(like.getFkSender().getId(), like.getFkReceiver().getId());
      if (previousSenderLike.isPresent()) {
        return FORBIDDEN;
      }
      Optional<Like> previousReceiverLike = findLikeByFk(like.getFkReceiver().getId(), like.getFkSender().getId());
      if (previousReceiverLike.isPresent() && likedUser.isPresent()) {
        Match newMatch = new Match(null, connectedUser, likedUser.get());
        matchService.addMatch(newMatch);
        notification = likedUser.get().getNickname();
      }
      like.setDate(new Date());
      likeRepository.save(like);
      return notification;
    } else {
      return FORBIDDEN;
    }
  }

  public Like findLikeById(Long id) {
    return likeRepository.findLikeById(id)
      .orElseThrow(() -> new LikeNotFoundException("Like by id " + id + " was not found"));
  }

  public Optional<Like> findLikeByFk(Long fkSender, Long fkReceiver) {
    User connectedUser = userService.findConnectedUser();
    if (!Objects.equals(connectedUser.getId(), fkSender)
      && !Objects.equals(connectedUser.getId(), fkReceiver)) {
      return Optional.empty();
    }
    return likeRepository.findLikeByFk(fkSender, fkReceiver);
  }

  @Transactional
  public String deleteLikeById(Long id) {
    User connectedUser = userService.findConnectedUser();
    Like like = findLikeById(id);
    if (Objects.equals(connectedUser.getId(), like.getFkSender().getId())) {
      likeRepository.deleteLikeById(id);
      Optional<Match> match = matchService.findMatchByFk(like.getFkSender().getId(), like.getFkReceiver().getId());
      match.ifPresent(value -> matchService.deleteMatchById(value.getId()));
      return "OK";
    } else {
      return FORBIDDEN;
    }
  }
}

