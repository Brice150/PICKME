package com.packages.backend.service;

public interface DislikeService {

  /**
   * Registers a dislike from the connected user and removes the like they may have sent before.
   *
   * @param userId identifier of the disliked user
   * @return {@code null} when the dislike has been registered, or {@link ServiceStatus#FORBIDDEN}
   * when the profile had already been disliked
   */
  String addDislike(Long userId);
}
