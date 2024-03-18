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
  List<Picture> pictures,
  String mainPicture,
  String nickname,
  String job,
  String city,
  Long height,
  String gender,
  String genderSearch,
  Long minAge,
  Long maxAge,
  String email,
  String description,
  String alcoholDrinking,
  String smokes,
  String organised,
  String personality,
  String sportPractice,
  String animals,
  String parenthood,
  String gamer,
  Long totalDislikes,
  Long totalLikes,
  Long totalMatches
) {
}
