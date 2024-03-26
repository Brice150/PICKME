package com.packages.backend.model.user;

import com.packages.backend.model.user.enums.UserRole;
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
      user.getNickname(),
      user.getJob(),
      user.getHeight(),
      user.getEmail(),
      user.getDescription(),
      user.getGenderAge(),
      user.getPreferences(),
      user.getGeolocation(),
      user.getPictures(),
      null
    );
  }
}
