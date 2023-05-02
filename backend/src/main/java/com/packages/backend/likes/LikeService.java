package com.packages.backend.likes;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LikeService {
  private final LikeRepository likeRepository;
  private final UserService userService;
  private final MatchService matchService;

  @Autowired
  public LikeService(LikeRepository likeRepository, UserService userService, MatchService matchService) {
    this.likeRepository = likeRepository;
    this.userService = userService;
    this.matchService = matchService;
  }

  public String addLike(Like like) {
    String notification = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (Objects.equals(connectedUser.getId(), like.getFkSender().getId())
      && !Objects.equals(connectedUser.getId(), like.getFkReceiver().getId())) {
      User likedUser = userService.findUserById(like.getFkReceiver().getId());
      Optional<Match> previousMatch = matchService.findMatchByFk(like.getFkSender().getId(), like.getFkReceiver().getId());
      if (previousMatch.isPresent()) {
        return "FORBIDDEN";
      }
      Optional<Like> previousSenderLike = findLikeByFk(like.getFkSender().getId(), like.getFkReceiver().getId());
      if (previousSenderLike.isPresent()) {
        return "FORBIDDEN";
      }
      Optional<Like> previousReceiverLike = findLikeByFk(like.getFkReceiver().getId(), like.getFkSender().getId());
      if (previousReceiverLike.isPresent()) {
        Match newMatch = new Match(null, connectedUser, likedUser);
        matchService.addMatch(newMatch);
        notification = likedUser.getNickname();
      }
      like.setDate(new Date());
      likeRepository.save(like);
      return notification;
    } else {
      return "FORBIDDEN";
    }
  }

  public List<Like> findAllLikes() {
    return likeRepository.findAll();
  }

  public Like findLikeById(Long id) {
    return likeRepository.findLikeById(id)
      .orElseThrow(() -> new LikeNotFoundException("Like by id " + id + " was not found"));
  }

  public Optional<Like> findLikeByFk(Long fkSender, Long fkReceiver) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (!Objects.equals(connectedUser.getId(), fkSender)
      && !Objects.equals(connectedUser.getId(), fkReceiver)) {
      return Optional.empty();
    }
    return likeRepository.findLikeByFk(fkSender, fkReceiver);
  }

  @Transactional
  public String deleteLikeById(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Like like = findLikeById(id);
    if (Objects.equals(connectedUser.getId(), like.getFkSender().getId())) {
      likeRepository.deleteLikeById(id);
      Optional<Match> match = matchService.findMatchByFk(like.getFkSender().getId(), like.getFkReceiver().getId());
      match.ifPresent(value -> matchService.deleteMatchById(value.getId()));
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

