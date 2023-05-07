package com.packages.backend.service;

import com.packages.backend.user.RestrictedUserDTOMapper;
import com.packages.backend.user.UserDTOMapper;
import com.packages.backend.user.UserRepository;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock
  private RestrictedUserDTOMapper restrictedUserDTOMapper;
  @Mock
  private UserDTOMapper userDTOMapper;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(
      userRepository,
      bCryptPasswordEncoder,
      restrictedUserDTOMapper,
      userDTOMapper);
  }
}
