package com.packages.backend.service.impl;

import com.packages.backend.model.Registration;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.service.RegistrationService;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {

  private final UserService userService;

  public RegistrationServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
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
