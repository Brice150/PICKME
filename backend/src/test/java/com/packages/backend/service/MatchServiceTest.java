package com.packages.backend.service;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchRepository;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

  @Test
  void testAddMatch() {
    // Arrange
    User user1 = new User();
    user1.setId(1L);
    User user2 = new User();
    user2.setId(1L);
    Date matchDate = new Date();
    Match match = new Match(matchDate, user1, user2);

    // Act
    matchService.addMatch(match);

    // Assert
    verify(matchRepository, times(1)).save(match);
    assertEquals(matchDate, match.getDate());
  }

  @Test
  void testFindAllMatchesByFk() {
    // Arrange
    User connectedUser = new User();
    connectedUser.setId(1L);
    User user1 = new User();
    user1.setId(2L);

    when(userService.findConnectedUser()).thenReturn(connectedUser);

    List<Match> matches = new ArrayList<>();
    matches.add(new Match(new Date(), connectedUser, user1));

    when(matchRepository.findAllMatchesByFk(connectedUser.getId())).thenReturn(matches);

    // Act
    List<Match> result = matchService.findAllMatchesByFk();

    // Assert
    verify(userService, times(1)).findConnectedUser();
    verify(matchRepository, times(1)).findAllMatchesByFk(connectedUser.getId());
    assertEquals(matches.size(), result.size());
  }

  @Test
  void testFindMatchById() {
    // Arrange
    Long matchId = 1L;
    Match match = new Match();

    when(matchRepository.findMatchById(matchId)).thenReturn(Optional.of(match));

    // Act
    Match result = matchService.findMatchById(matchId);

    // Assert
    verify(matchRepository, times(1)).findMatchById(matchId);
    assertEquals(match, result);
  }

  @Test
  void testFindMatchByFk() {
    // Arrange
    Long fkSender = 1L;
    Long fkReceiver = 2L;
    User connectedUser = new User();
    connectedUser.setId(1L);

    when(userService.findConnectedUser()).thenReturn(connectedUser);
    when(matchRepository.findMatchByFk(fkSender, fkReceiver)).thenReturn(Optional.empty());

    // Act
    Optional<Match> result = matchService.findMatchByFk(fkSender, fkReceiver);

    // Assert
    verify(userService, times(1)).findConnectedUser();
    verify(matchRepository, times(1)).findMatchByFk(fkSender, fkReceiver);
    assertEquals(Optional.empty(), result);
  }

  void testDeleteMatchById() {
    // Arrange
    Long matchId = 1L;

    // Act
    matchService.deleteMatchById(matchId);

    // Assert
    verify(matchRepository, times(1)).deleteMatchById(matchId);
  }
}
