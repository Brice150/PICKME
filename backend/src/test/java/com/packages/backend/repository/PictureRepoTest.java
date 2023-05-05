package com.packages.backend.repository;

import com.packages.backend.pictures.Picture;
import com.packages.backend.pictures.PictureRepository;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PictureRepoTest {

  @Autowired
  private PictureRepository pictureRepository;
  @Autowired
  private UserRepository userRepository;
  private User user1;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setEmail("user1@gmail.com");
    user1.setNickname("user1");
    user1.setPassword("password");

    userRepository.save(user1);
  }

  @Test
  void testFindAllPicturesByFk() {
    // given
    Picture picture1 = new Picture(
      "picture1", user1
    );
    Picture picture2 = new Picture(
      "picture2", user1
    );
    pictureRepository.save(picture1);
    pictureRepository.save(picture2);

    // when
    Long connectedId = user1.getId();
    List<Picture> found = pictureRepository.findAllPicturesByFk(connectedId);

    // then
    assertThat(found).isNotNull().hasSize(2);
  }

  @Test
  void testFindPictureById() {
    // given
    Picture picture1 = new Picture(
      "picture1", user1
    );
    pictureRepository.save(picture1);

    // when
    Optional<Picture> found = pictureRepository.findPictureById(picture1.getId());

    // then
    assertThat(found).isPresent().contains(picture1);
  }

  @Test
  void testDeletePictureById() {
    // given
    Picture picture1 = new Picture(
      "picture1", user1
    );
    pictureRepository.save(picture1);

    // when
    pictureRepository.deletePictureById(picture1.getId());

    // then
    Optional<Picture> found = pictureRepository.findPictureById(picture1.getId());
    assertThat(found).isEmpty();
  }
}
