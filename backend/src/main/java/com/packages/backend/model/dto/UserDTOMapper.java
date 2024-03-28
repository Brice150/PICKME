package com.packages.backend.model.dto;

import com.packages.backend.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {
  @Override
  public UserDTO apply(User user) {
    return new UserDTO(
      user.getId(),
      user.getUserRole(),
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
      user.getStats(),
      user.getNotifications()
    );
  }
}
