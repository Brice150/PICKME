package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import com.packages.backend.service.LikeService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DislikeServiceImpl")
class DislikeServiceImplTest {

  @Mock
  private DislikeRepository dislikeRepository;
  @Mock
  private UserService userService;
  @Mock
  private LikeService likeService;

  @Captor
  private ArgumentCaptor<Dislike> dislikeCaptor;

  @InjectMocks
  private DislikeServiceImpl dislikeService;

  @Test
  @DisplayName("registers the dislike and drops the like previously sent")
  void addDislikeRegistersTheDislike() {
    User connectedUser = TestFixtures.user(1L);
    User dislikedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getUserById(2L)).thenReturn(dislikedUser);
    when(dislikeRepository.getDislikeByFk(1L, 2L)).thenReturn(Optional.empty());

    String status = dislikeService.addDislike(2L);

    assertThat(status).isNull();
    verify(likeService).deleteLikeByFk(connectedUser, dislikedUser);
    verify(dislikeRepository).save(dislikeCaptor.capture());
    Dislike saved = dislikeCaptor.getValue();
    assertThat(saved.getFkSender()).isEqualTo(1L);
    assertThat(saved.getFkReceiver()).isEqualTo(2L);
    assertThat(saved.getDate()).isNotNull();
  }

  @Test
  @DisplayName("rejects a profile that has already been disliked")
  void addDislikeRejectsAnAlreadyDislikedProfile() {
    User connectedUser = TestFixtures.user(1L);
    User dislikedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getUserById(2L)).thenReturn(dislikedUser);
    when(dislikeRepository.getDislikeByFk(1L, 2L))
      .thenReturn(Optional.of(new Dislike(new Date(), 1L, 2L)));

    String status = dislikeService.addDislike(2L);

    assertThat(status).isEqualTo(ServiceStatus.FORBIDDEN);
    verify(likeService, never()).deleteLikeByFk(connectedUser, dislikedUser);
    verify(dislikeRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }
}
