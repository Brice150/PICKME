package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.Like;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DislikeRepository;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.StatsRepository;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.LikeResult;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeServiceImpl")
class LikeServiceImplTest {

  @Mock
  private LikeRepository likeRepository;
  @Mock
  private DislikeRepository dislikeRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private StatsRepository statsRepository;
  @Mock
  private UserService userService;
  @Mock
  private NotificationService notificationService;

  @Captor
  private ArgumentCaptor<Like> likeCaptor;

  @InjectMocks
  private LikeServiceImpl likeService;

  @Test
  @DisplayName("registers a like that does not create a match")
  void addLikeRegistersALikeWithoutMatch() {
    User connectedUser = TestFixtures.user(1L);
    User likedUser = TestFixtures.user(2L);
    Stats likedUserStats = likedUser.getStats();
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getUserById(2L)).thenReturn(likedUser);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.empty());
    when(dislikeRepository.getDislikeByFk(1L, 2L)).thenReturn(Optional.empty());
    when(statsRepository.getReferenceById(2L)).thenReturn(likedUserStats);
    when(likeRepository.getLikeByFk(2L, 1L)).thenReturn(Optional.empty());

    LikeResult result = likeService.addLike(2L);

    assertThat(result).isEqualTo(new LikeResult.Liked());
    assertThat(likedUserStats.getTotalLikes()).isEqualTo(1L);
    assertThat(likedUserStats.getTotalMatches()).isZero();
    verify(likeRepository).save(likeCaptor.capture());
    assertThat(likeCaptor.getValue().getFkSender()).isEqualTo(1L);
    assertThat(likeCaptor.getValue().getFkReceiver()).isEqualTo(2L);
    verify(statsRepository).save(likedUserStats);
    verifyNoInteractions(notificationService);
  }

  @Test
  @DisplayName("creates a match when the liked user had already liked back")
  void addLikeCreatesAMatch() {
    User connectedUser = TestFixtures.user(1L);
    User likedUser = TestFixtures.user(2L);
    Stats likedUserStats = likedUser.getStats();
    Stats connectedUserStats = connectedUser.getStats();
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getUserById(2L)).thenReturn(likedUser);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.empty());
    when(dislikeRepository.getDislikeByFk(1L, 2L)).thenReturn(Optional.empty());
    when(statsRepository.getReferenceById(2L)).thenReturn(likedUserStats);
    when(statsRepository.getReferenceById(1L)).thenReturn(connectedUserStats);
    when(likeRepository.getLikeByFk(2L, 1L)).thenReturn(Optional.of(new Like(new Date(), 2L, 1L)));

    LikeResult result = likeService.addLike(2L);

    assertThat(result).isEqualTo(new LikeResult.Matched(likedUser.getNickname()));
    assertThat(likedUserStats.getTotalLikes()).isEqualTo(1L);
    assertThat(likedUserStats.getTotalMatches()).isEqualTo(1L);
    assertThat(connectedUserStats.getTotalMatches()).isEqualTo(1L);
    verify(notificationService).sendNotification("New match with " + connectedUser.getNickname(), "match", likedUser);
    verify(statsRepository).save(connectedUserStats);
    verify(statsRepository).save(likedUserStats);
  }

  @Test
  @DisplayName("rejects a profile that has already been liked")
  void addLikeRejectsAnAlreadyLikedProfile() {
    User connectedUser = TestFixtures.user(1L);
    User likedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getUserById(2L)).thenReturn(likedUser);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.of(new Like(new Date(), 1L, 2L)));

    assertThat(likeService.addLike(2L)).isEqualTo(new LikeResult.Forbidden());
    verify(likeRepository, never()).save(any());
    verifyNoInteractions(statsRepository);
  }

  @Test
  @DisplayName("rejects a profile that has already been disliked")
  void addLikeRejectsAnAlreadyDislikedProfile() {
    User connectedUser = TestFixtures.user(1L);
    User likedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userService.getUserById(2L)).thenReturn(likedUser);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.empty());
    when(dislikeRepository.getDislikeByFk(1L, 2L)).thenReturn(Optional.of(new Dislike(new Date(), 1L, 2L)));

    assertThat(likeService.addLike(2L)).isEqualTo(new LikeResult.Forbidden());
    verify(likeRepository, never()).save(any());
    verifyNoInteractions(statsRepository);
  }

  @Test
  @DisplayName("only counts the dislike when no like had been sent")
  void deleteLikeByFkOnlyCountsTheDislike() {
    User connectedUser = TestFixtures.user(1L);
    User dislikedUser = TestFixtures.user(2L);
    Stats dislikedUserStats = dislikedUser.getStats();
    dislikedUserStats.setTotalLikes(4L);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.empty());
    when(likeRepository.getLikeByFk(2L, 1L)).thenReturn(Optional.empty());
    when(statsRepository.getReferenceById(2L)).thenReturn(dislikedUserStats);

    likeService.deleteLikeByFk(connectedUser, dislikedUser);

    assertThat(dislikedUserStats.getTotalDislikes()).isEqualTo(1L);
    assertThat(dislikedUserStats.getTotalLikes()).isEqualTo(4L);
    verify(likeRepository, never()).deleteLikeById(anyLong());
    verify(statsRepository).save(dislikedUserStats);
  }

  @Test
  @DisplayName("removes the like previously sent without undoing any match")
  void deleteLikeByFkRemovesThePreviousLike() {
    User connectedUser = TestFixtures.user(1L);
    User dislikedUser = TestFixtures.user(2L);
    Stats dislikedUserStats = dislikedUser.getStats();
    dislikedUserStats.setTotalLikes(4L);
    Like previousLike = like(7L, 1L, 2L);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.of(previousLike));
    when(likeRepository.getLikeByFk(2L, 1L)).thenReturn(Optional.empty());
    when(statsRepository.getReferenceById(2L)).thenReturn(dislikedUserStats);

    likeService.deleteLikeByFk(connectedUser, dislikedUser);

    assertThat(dislikedUserStats.getTotalDislikes()).isEqualTo(1L);
    assertThat(dislikedUserStats.getTotalLikes()).isEqualTo(3L);
    verify(likeRepository).deleteLikeById(7L);
    verify(notificationService, never()).sendNotification(anyString(), anyString(), any(User.class));
    verifyNoInteractions(messageRepository);
  }

  @Test
  @DisplayName("undoes the match, drops the conversation and warns the disliked user")
  void deleteLikeByFkUndoesTheMatch() {
    User connectedUser = TestFixtures.user(1L);
    User dislikedUser = TestFixtures.user(2L);
    Stats dislikedUserStats = dislikedUser.getStats();
    dislikedUserStats.setTotalLikes(4L);
    dislikedUserStats.setTotalMatches(2L);
    Stats connectedUserStats = connectedUser.getStats();
    connectedUserStats.setTotalMatches(3L);
    when(likeRepository.getLikeByFk(1L, 2L)).thenReturn(Optional.of(like(7L, 1L, 2L)));
    when(likeRepository.getLikeByFk(2L, 1L)).thenReturn(Optional.of(like(8L, 2L, 1L)));
    when(statsRepository.getReferenceById(2L)).thenReturn(dislikedUserStats);
    when(statsRepository.getReferenceById(1L)).thenReturn(connectedUserStats);

    likeService.deleteLikeByFk(connectedUser, dislikedUser);

    assertThat(dislikedUserStats.getTotalMatches()).isEqualTo(1L);
    assertThat(connectedUserStats.getTotalMatches()).isEqualTo(2L);
    verify(messageRepository).deleteMessagesByFk(1L, 2L);
    verify(notificationService).sendNotification(connectedUser.getNickname() + " decided to unmatch", "unmatch", dislikedUser);
    verify(likeRepository).deleteLikeById(7L);
    verify(statsRepository).save(connectedUserStats);
    verify(statsRepository).save(dislikedUserStats);
  }

  private Like like(Long id, Long fkSender, Long fkReceiver) {
    Like like = new Like(new Date(), fkSender, fkReceiver);
    like.setId(id);
    return like;
  }
}
