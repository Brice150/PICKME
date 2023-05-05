package com.packages.backend.service;

import com.packages.backend.matches.MatchRepository;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

  @Mock
  private MatchRepository matchRepository;
  @Mock
  private UserService userService;
  private MatchService matchService;

  @BeforeEach
  void setUp() {
    matchService = new MatchService(matchRepository, userService);
  }
}
