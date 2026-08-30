package com.packages.backend.service;

import com.packages.backend.model.entity.User;

public interface DistanceService {

  /**
   * Returns the great circle distance between two users, using the Haversine formula.
   *
   * @param connectedUser connected user
   * @param user          other user
   * @return the distance in kilometres
   */
  Double calculateDistance(User connectedUser, User user);
}
