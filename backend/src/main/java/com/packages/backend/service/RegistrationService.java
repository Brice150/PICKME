package com.packages.backend.service;

import com.packages.backend.model.Registration;
import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserRole;
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
        request.getCity(),
        request.getGender(),
        request.getGenderSearch(),
        request.getMinAge(),
        request.getMaxAge(),
        request.getEmail(),
        request.getPassword()
      )
    );
  }
}
