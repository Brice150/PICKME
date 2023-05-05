package com.packages.backend.repository;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeRepository;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class LikeRepoTest {

  @Autowired
  private LikeRepository likeRepository;

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
  void testFindLikeById() {
    //given
    Like like1 = new Like(
      new Date(), user1, user2
    );

    likeRepository.save(like1);

    // when
    Optional<Like> found = likeRepository.findLikeById(like1.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found).contains(like1);
  }

  @Test
  void testFindLikeByFk() {
    //given
    Like like1 = new Like(
      new Date(), user1, user2
    );

    likeRepository.save(like1);

    // when
    Optional<Like> found = likeRepository.findLikeByFk(like1.getFkSender().getId(), like1.getFkReceiver().getId());

    // then
    assertThat(found).isPresent();
    assertThat(found).contains(like1);
  }

  @Test
  void testDeleteLikeById() {
    //given
    Like like1 = new Like(
      new Date(), user1, user2
    );

    likeRepository.save(like1);

    // when
    likeRepository.deleteLikeById(like1.getId());

    // then
    Optional<Like> found = likeRepository.findLikeById(like1.getId());
    assertThat(found).isEmpty();
  }
}
