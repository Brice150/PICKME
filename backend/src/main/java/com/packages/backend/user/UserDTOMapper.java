package com.packages.backend.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {
  @Override
  public UserDTO apply(User user) {
    return new UserDTO(
      user.getId(),
      user.getNickname(),
      user.getEmail(),
      user.getUserRole(),
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
      user.getPictures(),
      user.getLikes(),
      user.getMatches(),
      user.getMessagesSent(),
      user.getMessagesReceived()
    );
  }
}
