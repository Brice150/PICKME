package com.packages.backend.model.dto;

import com.packages.backend.model.entity.*;
import com.packages.backend.model.enums.UserRole;

import java.util.Date;
import java.util.List;

public record UserDTO(
  Long id,
  UserRole userRole,
  Date birthDate,
  Boolean gold,
  String nickname,
  String job,
  Long height,
  String email,
  String description,
  Date registeredDate,
  GenderAge genderAge,
  Preferences preferences,
  Geolocation geolocation,
  List<Picture> pictures,
  Stats stats,
  List<Notification> notifications
) {
}
