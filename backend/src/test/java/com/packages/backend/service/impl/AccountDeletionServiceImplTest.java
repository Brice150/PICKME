package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.NotificationRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.DeletedAccountService;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountDeletionServiceImpl")
class AccountDeletionServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private UserService userService;
  @Mock
  private DeletedAccountService deletedAccountService;

  @Captor
  private ArgumentCaptor<List<Notification>> notificationsCaptor;

  @InjectMocks
  private AccountDeletionServiceImpl accountDeletionService;

  @Test
  @DisplayName("removes everything the account owns and warns its matches")
  void deleteConnectedUserRemovesEverythingItOwns() {
    User connectedUser = TestFixtures.user(1L);
    User matchedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(userRepository.getAllUserMatches(1L)).thenReturn(List.of(matchedUser));

    accountDeletionService.deleteConnectedUser();

    verify(notificationRepository).saveAll(notificationsCaptor.capture());
    assertThat(notificationsCaptor.getValue()).singleElement().satisfies(notification -> {
      assertThat(notification.getContent()).isEqualTo("nickname1 has deleted his account");
      assertThat(notification.getLink()).isEqualTo("delete");
      assertThat(notification.getSeen()).isFalse();
      assertThat(notification.getFkUser()).isSameAs(matchedUser);
    });
    verify(userRepository).deleteUserNotificationsByFk(1L);
    verify(userRepository).deleteUserGeolocationByFk(1L);
    verify(userRepository).deleteUserPreferencesByFk(1L);
    verify(userRepository).deleteUserGenderAgeByFk(1L);
    verify(userRepository).deleteUserStatsByFk(1L);
    verify(userRepository).deleteUserPicturesByFk(1L);
    verify(userRepository).deleteUserLikesByFk(1L);
    verify(userRepository).deleteUserDislikesByFk(1L);
    verify(userRepository).deleteUserMessagesByFk(1L);
    verify(userRepository).deleteUserByEmail("user1@pickme.com");
    verify(deletedAccountService).addDeletedAccount(connectedUser, connectedUser);
  }

  @Test
  @DisplayName("tells the matches when an administrator closed the account")
  void deleteUserByIdTellsTheMatchesTheAdministratorClosedIt() {
    User admin = TestFixtures.user(1L, UserRole.ROLE_ADMIN);
    User deletedUser = TestFixtures.user(2L);
    User matchedUser = TestFixtures.user(3L);
    when(userService.getConnectedUser()).thenReturn(admin);
    when(userService.getUserById(2L)).thenReturn(deletedUser);
    when(userRepository.getAllUserMatches(2L)).thenReturn(List.of(matchedUser));

    accountDeletionService.deleteUserById(2L);

    verify(notificationRepository).saveAll(notificationsCaptor.capture());
    assertThat(notificationsCaptor.getValue()).singleElement()
      .extracting(Notification::getContent)
      .isEqualTo("Admin has deleted nickname2's account");
    verify(userRepository).deleteUserByEmail("user2@pickme.com");
    verify(deletedAccountService).addDeletedAccount(deletedUser, admin);
  }
}
