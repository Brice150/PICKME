package com.packages.backend.repository;

import com.packages.backend.pictures.Picture;
import com.packages.backend.pictures.PictureRepository;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PictureRepoTest {

  @Autowired
  private PictureRepository underTest;
  @Autowired
  private UserRepository userRepository;

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
    underTest.deleteAll();
  }

  /*
  @Test
  void shouldFindPictureById() {
    User user1 = new User();
    user1.setId(1L);
    Picture picture = new Picture(
      "",new User()
    );
    userRepository.save(user1);
    underTest.save(picture);
    Optional<Picture> pictureFound = underTest.findPictureById(picture.getId());
    assertThat(pictureFound.isPresent()).isTrue();
  }
  */

  @Test
  void shouldNotFindPictureById() {
    Optional<Picture> pictureFound = underTest.findPictureById(10L);
    assertThat(pictureFound.isPresent()).isFalse();
  }

  /*
  @Test
  void shouldDeletePictureById() {
    User user1 = new User();
    user1.setId(1L);
    Picture picture = new Picture(
      "",new User()
    );
    userRepository.save(user1);
    underTest.save(picture);
    underTest.deletePictureById(picture.getId());
    assertThat(underTest.findPictureById(picture.getId()).isPresent()).isFalse();
  }
  */
}
