package com.packages.backend.service.impl;

import com.packages.backend.model.entity.User;
import com.packages.backend.service.DistanceService;
import org.springframework.stereotype.Service;

@Service
public class DistanceServiceImpl implements DistanceService {

  private static final double EARTH_RADIUS_KM = 6371.0;

  @Override
  public Double calculateDistance(User connectedUser, User user) {
    double latitudeUserRadian = Math.toRadians(Double.parseDouble(user.getGeolocation().getLatitude()));
    double longitudeUserRadian = Math.toRadians(Double.parseDouble(user.getGeolocation().getLongitude()));
    double latitudeConnectedUserRadian = Math.toRadians(Double.parseDouble(connectedUser.getGeolocation().getLatitude()));
    double longitudeConnectedUserRadian = Math.toRadians(Double.parseDouble(connectedUser.getGeolocation().getLongitude()));

    // Haversine formula
    double distanceLongitude = longitudeUserRadian - longitudeConnectedUserRadian;
    double distanceLatitude = latitudeUserRadian - latitudeConnectedUserRadian;
    double a = Math.pow(Math.sin(distanceLatitude / 2), 2) + Math.cos(latitudeConnectedUserRadian) * Math.cos(latitudeUserRadian) * Math.pow(Math.sin(distanceLongitude / 2), 2);

    return EARTH_RADIUS_KM * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
  }
}
