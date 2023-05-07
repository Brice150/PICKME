package com.packages.backend.service;

import com.packages.backend.likes.Like;
import com.packages.backend.likes.LikeNotFoundException;
import com.packages.backend.likes.LikeRepository;
import com.packages.backend.likes.LikeService;
import com.packages.backend.matches.MatchService;
import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private UserService userService;
  @Mock
  private MatchService matchService;
  private LikeService likeService;

  @BeforeEach
  void setUp() {
    likeService = new LikeService(likeRepository, userService, matchService);
  }

  @Test
  void findLikeByIdExistingLikeReturnsLike() {
    // Arrange
    Long likeId = 1L;
    Like expectedLike = new Like();
    when(likeRepository.findLikeById(likeId)).thenReturn(Optional.of(expectedLike));

    // Act
    Like actualLike = likeService.findLikeById(likeId);

    // Assert
    assertSame(expectedLike, actualLike);
    verify(likeRepository, times(1)).findLikeById(likeId);
  }

  @Test
  void findLikeByIdNonExistingLikeThrowsLikeNotFoundException() {
    // Arrange
    Long likeId = 1L;
    when(likeRepository.findLikeById(likeId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(LikeNotFoundException.class, () -> likeService.findLikeById(likeId));
    verify(likeRepository, times(1)).findLikeById(likeId);
  }

  @Test
  void findLikeByFkCorrectFkReturnsOptionalLike() {
    // Arrange
    Long senderId = 1L;
    Long receiverId = 2L;
    Like expectedLike = new Like();
    User user = new User();
    user.setId(1L);
    when(userService.findConnectedUser()).thenReturn(user);
    when(likeRepository.findLikeByFk(senderId, receiverId)).thenReturn(Optional.of(expectedLike));

    // Act
    Optional<Like> actualLike = likeService.findLikeByFk(senderId, receiverId);

    // Assert
    assertTrue(actualLike.isPresent());
    assertSame(expectedLike, actualLike.get());
    verify(userService, times(1)).findConnectedUser();
    verify(likeRepository, times(1)).findLikeByFk(senderId, receiverId);
  }

  @Test
  void findLikeByFkIncorrectFkReturnsEmptyOptional() {
    // Arrange
    Long senderId = 1L;
    Long receiverId = 2L;
    User user = new User();
    user.setId(3L);
    when(userService.findConnectedUser()).thenReturn(user);

    // Act
    Optional<Like> actualLike = likeService.findLikeByFk(senderId, receiverId);

    // Assert
    assertFalse(actualLike.isPresent());
    verify(userService, times(1)).findConnectedUser();
    verify(likeRepository, never()).findLikeByFk(anyLong(), anyLong());
  }
}
