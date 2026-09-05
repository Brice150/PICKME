package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateSelectionServiceImpl")
class CandidateSelectionServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private PictureRepository pictureRepository;
  @Mock
  private UserService userService;
  @Mock
  private DistanceService distanceService;

  private User connectedUser;
  private CandidateSelectionServiceImpl selectionService;

  @BeforeEach
  void setUp() {
    connectedUser = TestFixtures.user(1L);
    selectionService = new CandidateSelectionServiceImpl(userRepository, likeRepository,
      pictureRepository, new UserDTOMapperRestricted(), userService, distanceService,
      new AffinityScorer());
    when(userService.getConnectedUser()).thenReturn(connectedUser);
  }

  /** Makes the repository answer the selection query with the given candidates. */
  private void candidates(User... candidates) {
    when(userRepository.getAllUsers(any(Gender.class), any(Gender.class), any(Date.class),
      any(Date.class), eq(1L))).thenReturn(List.of(candidates));
  }

  private static LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  @Test
  @DisplayName("returns the candidates within the search radius, with their main picture")
  void keepsTheCandidatesWithinTheRadius() {
    User close = TestFixtures.user(2L);
    User other = TestFixtures.user(3L);
    User far = TestFixtures.user(4L);
    candidates(close, other, far);
    when(likeRepository.getGoldByConnectedUserId(1L)).thenReturn(List.of(3L));
    when(distanceService.calculateDistance(connectedUser, close)).thenReturn(5.0);
    when(distanceService.calculateDistance(connectedUser, other)).thenReturn(8.0);
    when(distanceService.calculateDistance(connectedUser, far)).thenReturn(500.0);
    Picture mainPicture = new Picture("content", true, close);
    when(pictureRepository.findDisplayedPicturesByUserIds(List.of(2L, 3L)))
      .thenReturn(List.of(mainPicture));

    List<UserDTO> selected = selectionService.getAllSelectedUsers(null);

    assertThat(selected).extracting(UserDTO::id).containsExactly(2L, 3L);
    assertThat(selected.get(0).pictures()).containsExactly(mainPicture);
    assertThat(selected.get(1).pictures()).isEmpty();
    assertThat(selected).allSatisfy(dto -> assertThat(dto.userRole()).isEqualTo(UserRole.HIDDEN));
    assertThat(close.getGold()).isFalse();
    assertThat(other.getGold()).isTrue();
  }

  @Test
  @DisplayName("turns the age criteria into a range of birth dates")
  void selectsOnBirthDates() {
    connectedUser.getGenderAge().setMinAge(25L);
    connectedUser.getGenderAge().setMaxAge(35L);
    ArgumentCaptor<Date> earliest = ArgumentCaptor.forClass(Date.class);
    ArgumentCaptor<Date> latest = ArgumentCaptor.forClass(Date.class);
    when(userRepository.getAllUsers(any(Gender.class), any(Gender.class), earliest.capture(),
      latest.capture(), eq(1L))).thenReturn(List.of());
    when(likeRepository.getGoldByConnectedUserId(1L)).thenReturn(List.of());

    assertThat(selectionService.getAllSelectedUsers(-1)).isEmpty();

    LocalDate today = LocalDate.now();
    assertThat(toLocalDate(earliest.getValue())).isEqualTo(today.minusYears(36).plusDays(1));
    assertThat(toLocalDate(latest.getValue())).isEqualTo(today.minusYears(25).plusDays(1));
  }

  @Test
  @DisplayName("returns nothing beyond the last page")
  void returnsNothingBeyondTheLastPage() {
    User candidate = TestFixtures.user(2L);
    candidates(candidate);
    when(likeRepository.getGoldByConnectedUserId(1L)).thenReturn(List.of());
    when(distanceService.calculateDistance(connectedUser, candidate)).thenReturn(5.0);

    assertThat(selectionService.getAllSelectedUsers(1)).isEmpty();
    verify(pictureRepository, never()).findDisplayedPicturesByUserIds(anyList());
  }
}
