package com.packages.backend.model.dto;

import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class UserDTOMapperRestricted implements Function<User, UserDTO> {

  @Override
  public UserDTO apply(User user) {
    return apply(user, user.getPictures());
  }

  /**
   * Maps a user with an explicit list of pictures. Used by the selection screen, which only sends
   * the main picture of each candidate instead of triggering the lazy loading of every album during
   * the JSON serialization.
   *
   * @param user     user to map
   * @param pictures pictures to expose
   * @return the restricted view of the user
   */
  public UserDTO apply(User user, List<Picture> pictures) {
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
      null,
      user.getGenderAge(),
      user.getPreferences(),
      user.getGeolocation(),
      pictures,
      null,
      null
    );
  }
}
