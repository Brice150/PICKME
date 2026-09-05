package com.packages.backend.repository;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("PictureRepository")
class PictureRepositoryTest {

  @Autowired
  private PictureRepository pictureRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EntityManager entityManager;

  private User persistUser() {
    User user = TestFixtures.user(null);
    user.setEmail("user" + System.nanoTime() + "@pickme.com");
    User saved = userRepository.saveAndFlush(user);
    entityManager.clear();
    return saved;
  }

  private Picture persistPicture(User owner, String content, boolean isMainPicture) {
    return pictureRepository.saveAndFlush(new Picture(content, isMainPicture, owner));
  }

  @Test
  @DisplayName("returns the main picture of each account of the page")
  void findDisplayedPicturesReturnsTheMainPictureOfEachAccount() {
    User first = persistUser();
    User second = persistUser();
    persistPicture(first, "first-secondary", false);
    Picture firstMain = persistPicture(first, "first-main", true);
    Picture secondMain = persistPicture(second, "second-main", true);
    persistPicture(second, "second-secondary", false);

    List<Picture> displayed = pictureRepository.findDisplayedPicturesByUserIds(
      List.of(first.getId(), second.getId()));

    assertThat(displayed).extracting(Picture::getId)
      .containsExactlyInAnyOrder(firstMain.getId(), secondMain.getId());
  }

  @Test
  @DisplayName("falls back on the most recent picture of an album that lost its main one")
  void findDisplayedPicturesFallsBackOnTheMostRecentPicture() {
    User user = persistUser();
    persistPicture(user, "older", false);
    Picture mostRecent = persistPicture(user, "newer", false);

    List<Picture> displayed = pictureRepository.findDisplayedPicturesByUserIds(List.of(user.getId()));

    assertThat(displayed).extracting(Picture::getId).containsExactly(mostRecent.getId());
  }

  @Test
  @DisplayName("sends back exactly one picture per account, never more")
  void findDisplayedPicturesNeverReturnsTwoPicturesOfTheSameAccount() {
    User user = persistUser();
    persistPicture(user, "main", true);
    persistPicture(user, "secondary", false);
    persistPicture(user, "another", false);

    assertThat(pictureRepository.findDisplayedPicturesByUserIds(List.of(user.getId()))).hasSize(1);
  }

  @Test
  @DisplayName("ignores the accounts that have no picture at all")
  void findDisplayedPicturesIgnoresTheEmptyAlbums() {
    User withoutPicture = persistUser();

    assertThat(pictureRepository.findDisplayedPicturesByUserIds(List.of(withoutPicture.getId())))
      .isEmpty();
  }

  @Test
  @DisplayName("returns a whole album with its main picture first")
  void findAllByUserIdPutsTheMainPictureFirst() {
    User user = persistUser();
    Picture oldest = persistPicture(user, "oldest", false);
    Picture main = persistPicture(user, "main", true);
    Picture newest = persistPicture(user, "newest", false);

    List<Picture> album = pictureRepository.findAllByUserId(user.getId());

    assertThat(album).extracting(Picture::getId)
      .containsExactly(main.getId(), newest.getId(), oldest.getId());
  }

  @Test
  @DisplayName("finds and deletes a picture from its identifier")
  void findAndDeletePictureById() {
    User user = persistUser();
    Picture picture = persistPicture(user, "content", true);

    assertThat(pictureRepository.findPictureById(picture.getId())).isPresent();

    pictureRepository.deletePictureById(picture.getId());
    entityManager.flush();

    assertThat(pictureRepository.findPictureById(picture.getId())).isEmpty();
  }
}
