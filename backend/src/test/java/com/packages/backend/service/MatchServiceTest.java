package com.packages.backend.service;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchRepository;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

  @Mock
  private MatchRepository matchRepository;
  private MatchService underTest;

  @BeforeEach
  void setUp() {
    underTest = new MatchService(matchRepository);
  }

  @Test
  void shouldAddMatch() {
    Match match = new Match(
      new Date(), new User(),new User()
    );
    underTest.addMatch(match);
    ArgumentCaptor<Match> matchArgumentCaptor = ArgumentCaptor.forClass(Match.class);
    verify(matchRepository).save(matchArgumentCaptor.capture());
    Match capturedMatch = matchArgumentCaptor.getValue();
    assertThat(capturedMatch).isEqualTo(match);
  }

  @Test
  void shouldFindAllMatches() {
    underTest.findAllMatches();
    verify(matchRepository).findAll();
  }

  /*
  @Test
  void shouldFindMatchById() {
    Match match = new Match(
      new Date(), new User(),new User()
    );
    underTest.addMatch(match);
    underTest.findMatchById(match.getId());
    verify(matchRepository).findMatchById(match.getId());
  }
   */

  @Test
  void shouldDeleteMatch() {
    underTest.deleteMatchById(1L);
    verify(matchRepository).deleteMatchById(1L);
  }
}
