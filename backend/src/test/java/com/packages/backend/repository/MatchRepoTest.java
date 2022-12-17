package com.packages.backend.repository;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchRepository;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class MatchRepoTest {

  @Autowired
  private MatchRepository underTest;
  @Autowired
  private UserRepository userRepository;

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
    underTest.deleteAll();
  }

  /*
  @Test
  void shouldFindMatchById() {
    User user1 = new User();
    user1.setId(1L);
    Match match = new Match(
      new Date(), new User(),new User()
    );
    userRepository.save(user1);
    underTest.save(match);
    Optional<Match> matchFound = underTest.findMatchById(match.getId());
    assertThat(matchFound.isPresent()).isTrue();
  }
  */

  @Test
  void shouldNotFindMatchById() {
    Optional<Match> matchFound = underTest.findMatchById(10L);
    assertThat(matchFound.isPresent()).isFalse();
  }

  /*
  @Test
  void shouldDeleteMatchById() {
    User user1 = new User();
    user1.setId(1L);
    Match match = new Match(
      new Date(), new User(),new User()
    );
    userRepository.save(user1);
    underTest.save(match);
    underTest.deleteMatchById(match.getId());
    assertThat(underTest.findMatchById(match.getId()).isPresent()).isFalse();
  }
  */
}
