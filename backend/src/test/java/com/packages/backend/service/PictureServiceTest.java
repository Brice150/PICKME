package com.packages.backend.service;

import com.packages.backend.pictures.Picture;
import com.packages.backend.pictures.PictureRepository;
import com.packages.backend.pictures.PictureService;
import com.packages.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PictureServiceTest {

  @Mock
  private PictureRepository pictureRepository;
  private PictureService underTest;

  @BeforeEach
  void setUp() {
    underTest = new PictureService(pictureRepository);
  }

  @Test
  void shouldAddPicture() {
    Picture picture = new Picture(
      "test.png",new User()
    );
    underTest.addPicture(picture);
    ArgumentCaptor<Picture> pictureArgumentCaptor = ArgumentCaptor.forClass(Picture.class);
    verify(pictureRepository).save(pictureArgumentCaptor.capture());
    Picture capturedPicture = pictureArgumentCaptor.getValue();
    assertThat(capturedPicture).isEqualTo(picture);
  }

  @Test
  void shouldFindAllPicture() {
    underTest.findAllPictures();
    verify(pictureRepository).findAll();
  }

  /*
  @Test
  void shouldFindPictureById() {
    picture picture = new picture(
      1L,"",new User()
    );
    underTest.addPicture(picture);
    underTest.findPictureById(picture.getId());
    verify(pictureRepository).findPictureById(picture.getId());
  }
   */

  @Test
  void shouldDeletePicture() {
    underTest.deletePictureByContent("test.png");
    verify(pictureRepository).deletePictureByContent("test.png");
  }
}
