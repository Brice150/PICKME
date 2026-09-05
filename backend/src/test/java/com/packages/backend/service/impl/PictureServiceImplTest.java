package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.exception.PictureNotFoundException;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PictureServiceImpl")
class PictureServiceImplTest {

  @Mock
  private PictureRepository pictureRepository;
  @Mock
  private UserService userService;

  @InjectMocks
  private PictureServiceImpl pictureService;

  @Test
  @DisplayName("promotes the first picture of an album as its main picture")
  void addPicturePromotesTheFirstPictureAsMainPicture() {
    User connectedUser = TestFixtures.user(1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);

    Optional<Picture> added = pictureService.addPicture("content");

    assertThat(added).isPresent();
    assertThat(added.get().getContent()).isEqualTo("content");
    assertThat(added.get().getIsMainPicture()).isTrue();
    assertThat(added.get().getFkUser()).isSameAs(connectedUser);
    verify(pictureRepository).save(added.get());
  }

  @Test
  @DisplayName("adds a secondary picture without touching the main one")
  void addPictureAddsASecondaryPicture() {
    User connectedUser = TestFixtures.user(1L);
    connectedUser.getPictures().add(new Picture("first", true, connectedUser));
    when(userService.getConnectedUser()).thenReturn(connectedUser);

    Optional<Picture> added = pictureService.addPicture("second");

    assertThat(added).isPresent();
    assertThat(added.get().getIsMainPicture()).isFalse();
  }

  @Test
  @DisplayName("rejects a picture when the album is full")
  void addPictureRejectsAFullAlbum() {
    User connectedUser = TestFixtures.user(1L);
    List<Picture> pictures = new ArrayList<>(IntStream.range(0, 6)
      .mapToObj(index -> new Picture("content" + index, index == 0, connectedUser))
      .toList());
    connectedUser.setPictures(pictures);
    when(userService.getConnectedUser()).thenReturn(connectedUser);

    assertThat(pictureService.addPicture("seventh")).isEmpty();
    verify(pictureRepository, never()).save(any());
  }

  @Test
  @DisplayName("rejects a picture already present in the album")
  void addPictureRejectsADuplicate() {
    User connectedUser = TestFixtures.user(1L);
    connectedUser.getPictures().add(new Picture("content", true, connectedUser));
    when(userService.getConnectedUser()).thenReturn(connectedUser);

    assertThat(pictureService.addPicture("content")).isEmpty();
    verify(pictureRepository, never()).save(any());
  }

  @Test
  @DisplayName("finds a picture from its identifier")
  void getPictureByIdReturnsThePicture() {
    Picture picture = new Picture("content", true, TestFixtures.user(1L));
    when(pictureRepository.findPictureById(10L)).thenReturn(Optional.of(picture));

    assertThat(pictureService.getPictureById(10L)).isSameAs(picture);
  }

  @Test
  @DisplayName("fails when no picture matches the identifier")
  void getPictureByIdFailsWhenThePictureIsUnknown() {
    when(pictureRepository.findPictureById(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> pictureService.getPictureById(10L))
      .isInstanceOf(PictureNotFoundException.class)
      .hasMessage("Picture by id 10 was not found");
  }

  @Test
  @DisplayName("returns the whole album of a user")
  void getUserPicturesReturnsTheAlbum() {
    List<Picture> pictures = List.of(new Picture("content", true, TestFixtures.user(2L)));
    when(pictureRepository.findAllByUserId(2L)).thenReturn(pictures);

    assertThat(pictureService.getUserPictures(2L)).isEqualTo(pictures);
  }

  @Test
  @DisplayName("moves the main flag to the selected picture")
  void selectMainPictureByIdMovesTheMainFlag() {
    User connectedUser = TestFixtures.user(1L);
    Picture main = picture(1L, true, connectedUser);
    Picture selected = picture(2L, false, connectedUser);
    connectedUser.getPictures().addAll(List.of(main, selected));
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(pictureRepository.findPictureById(2L)).thenReturn(Optional.of(selected));

    assertThat(pictureService.selectMainPictureById(2L)).isEqualTo(ServiceStatus.OK);
    assertThat(main.getIsMainPicture()).isFalse();
    assertThat(selected.getIsMainPicture()).isTrue();
    verify(pictureRepository).saveAll(connectedUser.getPictures());
  }

  @Test
  @DisplayName("rejects the selection of a picture that is already the main one")
  void selectMainPictureByIdRejectsTheCurrentMainPicture() {
    User connectedUser = TestFixtures.user(1L);
    Picture main = picture(1L, true, connectedUser);
    connectedUser.getPictures().add(main);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(pictureRepository.findPictureById(1L)).thenReturn(Optional.of(main));

    assertThat(pictureService.selectMainPictureById(1L)).isEqualTo(ServiceStatus.FORBIDDEN);
    verify(pictureRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("rejects the selection of a picture owned by somebody else")
  void selectMainPictureByIdRejectsAPictureOfAnotherUser() {
    User connectedUser = TestFixtures.user(1L);
    Picture foreignPicture = picture(2L, false, TestFixtures.user(2L));
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(pictureRepository.findPictureById(2L)).thenReturn(Optional.of(foreignPicture));

    assertThat(pictureService.selectMainPictureById(2L)).isEqualTo(ServiceStatus.FORBIDDEN);
    verify(pictureRepository, never()).saveAll(any());
  }

  @Test
  @DisplayName("deletes a picture of the connected user")
  void deletePictureByIdDeletesTheOwnedPicture() {
    User connectedUser = TestFixtures.user(1L);
    Picture picture = picture(5L, false, connectedUser);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(pictureRepository.findPictureById(5L)).thenReturn(Optional.of(picture));

    assertThat(pictureService.deletePictureById(5L)).isEqualTo(ServiceStatus.OK);
    verify(pictureRepository).deletePictureById(5L);
  }

  @Test
  @DisplayName("refuses to delete a picture owned by somebody else")
  void deletePictureByIdRefusesAPictureOfAnotherUser() {
    User connectedUser = TestFixtures.user(1L);
    Picture foreignPicture = picture(5L, false, TestFixtures.user(2L));
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(pictureRepository.findPictureById(5L)).thenReturn(Optional.of(foreignPicture));

    assertThat(pictureService.deletePictureById(5L)).isEqualTo(ServiceStatus.FORBIDDEN);
    verify(pictureRepository, never()).deletePictureById(anyLong());
  }

  private Picture picture(Long id, boolean isMainPicture, User owner) {
    Picture picture = new Picture("content" + id, isMainPicture, owner);
    picture.setId(id);
    return picture;
  }
}
