package com.packages.backend.service;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeRepository;
import com.packages.backend.likes.LikeService;
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
public class LikeServiceTest {

  @Mock
  private LikeRepository likeRepository;
  private LikeService underTest;

  @BeforeEach
  void setUp() {
    underTest = new LikeService(likeRepository);
  }

  @Test
  void shouldAddLike() {
    Like like = new Like(
      new Date(), new User(),new User()
    );
    underTest.addLike(like);
    ArgumentCaptor<Like> likeArgumentCaptor = ArgumentCaptor.forClass(Like.class);
    verify(likeRepository).save(likeArgumentCaptor.capture());
    Like capturedLike = likeArgumentCaptor.getValue();
    assertThat(capturedLike).isEqualTo(like);
  }

  @Test
  void shouldFindAllLikes() {
    underTest.findAllLikes();
    verify(likeRepository).findAll();
  }

  /*
  @Test
  void shouldFindLikeById() {
    Like like = new Like(
      new Date(), new User(),new User()
    );
    underTest.addLike(like);
    underTest.findLikeById(like.getId());
    verify(likeRepository).findLikeById(like.getId());
  }
   */

  @Test
  void shouldDeleteLike() {
    underTest.deleteLikeById(1L);
    verify(likeRepository).deleteLikeById(1L);
  }
}
