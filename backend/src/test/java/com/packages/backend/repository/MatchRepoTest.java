package com.packages.backend.repository;

import com.packages.backend.matches.Match;
import com.packages.backend.matches.MatchRepository;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MatchRepoTest {

  @Autowired
  private MatchRepository matchRepository;
  @Autowired
  private UserRepository userRepository;

  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setEmail("user1@gmail.com");
    user1.setNickname("user1");
    user1.setPassword("password");

    user2 = new User();
    user2.setEmail("user2@gmail.com");
    user2.setNickname("user2");
    user2.setPassword("password");

    userRepository.save(user1);
    userRepository.save(user2);
  }

  @Test
  void testFindAllMatchesByFk() {
    // given
    Match match1 = new Match(
      new Date(), user1, user2
    );
    Match match2 = new Match(
      new Date(), user2, user1
    );
    matchRepository.save(match1);
    matchRepository.save(match2);

    // when
    Long connectedId = user1.getId();
    List<Match> found = matchRepository.findAllMatchesByFk(connectedId);

    // then
    assertThat(found).isNotNull().hasSize(2);
  }

  @Test
  void testFindMatchById() {
    // given
    Match match1 = new Match(
      new Date(), user1, user2
    );
    matchRepository.save(match1);

    // when
    Optional<Match> found = matchRepository.findMatchById(match1.getId());

    // then
    assertThat(found).isPresent().contains(match1);
  }

  @Test
  void testFindMatchByFk() {
    // given
    Match match1 = new Match(
      new Date(), user1, user2
    );
    matchRepository.save(match1);

    // when
    Optional<Match> found = matchRepository.findMatchByFk(user1.getId(), user2.getId());

    // then
    assertThat(found).isPresent().contains(match1);
  }

  @Test
  void testDeleteMatchById() {
    // given
    Match match1 = new Match(
      new Date(), user1, user2
    );
    matchRepository.save(match1);

    // when
    matchRepository.deleteMatchById(match1.getId());

    // then
    Optional<Match> found = matchRepository.findMatchById(match1.getId());
    assertThat(found).isEmpty();
  }
}
