package com.packages.backend.user;

import com.packages.backend.pictures.Picture;

import java.util.Date;
import java.util.List;

public record RestrictedUserDTO(
  Long id,
  String nickname,
  String email,
  UserRole userRole,
  String mainPicture,
  String gender,
  String genderSearch,
  String relationshipType,
  Date birthDate,
  String city,
  Long height,
  String languages,
  String job,
  String description,
  String smokes,
  String alcoholDrinking,
  String organised,
  String personality,
  String sportPractice,
  String animals,
  String parenthood,
  String gamer,
  String activities,
  List<Picture> pictures
) {
}
