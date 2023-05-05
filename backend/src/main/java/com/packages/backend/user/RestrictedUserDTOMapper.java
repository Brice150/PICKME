package com.packages.backend.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RestrictedUserDTOMapper implements Function<User, RestrictedUserDTO> {

  @Override
  public RestrictedUserDTO apply(User user) {
    return new RestrictedUserDTO(
      user.getId(),
      user.getNickname(),
      user.getEmail(),
      UserRole.HIDDEN,
      user.getMainPicture(),
      user.getGender(),
      user.getGenderSearch(),
      user.getRelationshipType(),
      user.getBirthDate(),
      user.getCity(),
      user.getHeight(),
      user.getLanguages(),
      user.getJob(),
      user.getDescription(),
      user.getSmokes(),
      user.getAlcoholDrinking(),
      user.getOrganised(),
      user.getPersonality(),
      user.getSportPractice(),
      user.getAnimals(),
      user.getParenthood(),
      user.getGamer(),
      user.getActivities(),
      user.getPictures()
    );
  }
}
