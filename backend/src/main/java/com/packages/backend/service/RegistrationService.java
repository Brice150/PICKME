package com.packages.backend.service;

import com.packages.backend.model.Registration;
import com.packages.backend.model.user.User;
import com.packages.backend.model.user.enums.UserRole;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
  private final UserService userService;

  public RegistrationService(UserService userService) {
    this.userService = userService;
  }

  public String register(Registration request) {
    return userService.signUpUser(
      new User(
        UserRole.ROLE_USER,
        request.getBirthDate(),
        request.getNickname(),
        request.getJob(),
        request.getEmail(),
        request.getPassword(),
        request.getGenderAge(),
        request.getGeolocation()
      )
    );
  }
}
