package com.packages.backend.service;

import com.packages.backend.pictures.PictureRepository;
import com.packages.backend.pictures.PictureService;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PictureServiceTest {

  @Mock
  private PictureRepository pictureRepository;
  @Mock
  private UserService userService;
  private PictureService pictureService;

  @BeforeEach
  void setUp() {
    pictureService = new PictureService(pictureRepository, userService);
  }
}
