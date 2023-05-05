package com.packages.backend.service;

import com.packages.backend.likes.LikeRepository;
import com.packages.backend.likes.LikeService;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private UserService userService;
  @Mock
  private MatchService matchService;
  private LikeService likeService;

  @BeforeEach
  void setUp() {
    likeService = new LikeService(likeRepository, userService, matchService);
  }
}
