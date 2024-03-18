package com.packages.backend.model.user;

import com.packages.backend.model.user.enums.*;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapperRestricted implements Function<User, UserDTO> {
  @Override
  public UserDTO apply(User user) {
    return new UserDTO(
      user.getId(),
      UserRole.HIDDEN,
      user.getBirthDate(),
      user.getGold(),
      user.getPictures(),
      user.getMainPicture(),
      user.getNickname(),
      user.getJob(),
      user.getCity(),
      user.getHeight(),
      Gender.getDescriptionNullSafe(user.getGender()),
      Gender.getDescriptionNullSafe(user.getGenderSearch()),
      user.getMinAge(),
      user.getMaxAge(),
      user.getEmail(),
      user.getDescription(),
      AlcoholDrinking.getDescriptionNullSafe(user.getAlcoholDrinking()),
      Smokes.getDescriptionNullSafe(user.getSmokes()),
      Organised.getDescriptionNullSafe(user.getOrganised()),
      Personality.getDescriptionNullSafe(user.getPersonality()),
      SportPractice.getDescriptionNullSafe(user.getSportPractice()),
      Animals.getDescriptionNullSafe(user.getAnimals()),
      Parenthood.getDescriptionNullSafe(user.getParenthood()),
      Gamer.getDescriptionNullSafe(user.getGamer()),
      null,
      null,
      null
    );
  }
}