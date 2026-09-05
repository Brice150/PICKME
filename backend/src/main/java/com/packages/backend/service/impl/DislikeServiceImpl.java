package com.packages.backend.service.impl;

import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import com.packages.backend.service.DislikeService;
import com.packages.backend.service.LikeService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class DislikeServiceImpl implements DislikeService {

  private final DislikeRepository dislikeRepository;
  private final LikeService likeService;
  private final UserService userService;

  public DislikeServiceImpl(DislikeRepository dislikeRepository, UserService userService, LikeService likeService) {
    this.dislikeRepository = dislikeRepository;
    this.userService = userService;
    this.likeService = likeService;
  }

  @Override
  @Transactional
  public ServiceStatus addDislike(Long userId) {
    User connectedUser = userService.getConnectedUser();
    User dislikedUser = userService.getUserById(userId);
    Optional<Dislike> previousSenderDislike = dislikeRepository.getDislikeByFk(connectedUser.getId(), dislikedUser.getId());
    if (previousSenderDislike.isPresent()) {
      return ServiceStatus.FORBIDDEN;
    }
    likeService.deleteLikeByFk(connectedUser, dislikedUser);
    Dislike dislike = new Dislike(new Date(), connectedUser.getId(), dislikedUser.getId());
    dislikeRepository.save(dislike);
    return ServiceStatus.OK;
  }
}
