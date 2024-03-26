package com.packages.backend.model.user;

import com.packages.backend.model.Picture;
import com.packages.backend.model.user.enums.UserRole;

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
  GenderAge genderAge,
  Preferences preferences,
  Geolocation geolocation,
  List<Picture> pictures,
  Stats stats
) {
}
