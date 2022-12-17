package com.packages.backend.repository;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeRepository;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class LikeRepoTest {

  @Autowired
  private LikeRepository underTest;
  @Autowired
  private UserRepository userRepository;

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
    underTest.deleteAll();
  }

  /*
  @Test
  void shouldFindLikeById() {
    User user1 = new User();
    user1.setId(1L);
    Like like = new Like(
      new Date(), new User(),new User()
    );
    userRepository.save(user1);
    underTest.save(like);
    Optional<like> likeFound = underTest.findLikeById(like.getId());
    assertThat(likeFound.isPresent()).isTrue();
  }
  */

  @Test
  void shouldNotFindLikeById() {
    Optional<Like> likeFound = underTest.findLikeById(10L);
    assertThat(likeFound.isPresent()).isFalse();
  }

  /*
  @Test
  void shouldDeleteLikeById() {
    User user1 = new User();
    user1.setId(1L);
    Like like = new Like(
      new Date(), new User(),new User()
    );
    userRepository.save(user1);
    underTest.save(like);
    underTest.deleteLikeById(like.getId());
    assertThat(underTest.findLikeById(like.getId()).isPresent()).isFalse();
  }
  */
}
