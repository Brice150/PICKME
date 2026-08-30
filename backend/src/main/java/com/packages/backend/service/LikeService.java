package com.packages.backend.service;

import com.packages.backend.model.entity.User;

public interface LikeService {

  /**
   * Registers a like from the connected user, creating a match when the other user had already
   * liked them.
   *
   * @param userId identifier of the liked user
   * @return the nickname of the matched user, {@code null} when there is no match, or
   * {@link ServiceStatus#FORBIDDEN} when the profile has already been liked or disliked
   */
  String addLike(Long userId);

  /**
   * Removes a previous like and undoes the match it may have created.
   *
   * @param connectedUser connected user
   * @param dislikedUser  user being disliked
   */
  void deleteLikeByFk(User connectedUser, User dislikedUser);
}
