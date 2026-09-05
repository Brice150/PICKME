package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.AdminRepository;
import com.packages.backend.service.AccountDeletionService;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl")
class AdminServiceImplTest {

  private static final PageRequest FIRST_PAGE = PageRequest.of(0, 5);

  @Mock
  private AdminRepository adminRepository;
  @Mock
  private UserService userService;
  @Mock
  private DistanceService distanceService;
  @Mock
  private AccountDeletionService accountDeletionService;

  @Captor
  private ArgumentCaptor<Date> dateCaptor;

  private AdminServiceImpl adminService;

  @BeforeEach
  void setUp() {
    adminService = new AdminServiceImpl(adminRepository, userService, new UserDTOMapper(), distanceService, accountDeletionService);
  }

  @Test
  @DisplayName("counts the recent activity over the last week")
  void getAdminStatsCountsTheLastWeek() {
    AdminStats stats = new AdminStats(10L, 2L, 3L, 1L);
    when(adminRepository.getAdminStats(any(Date.class))).thenReturn(stats);

    assertThat(adminService.getAdminStats()).isSameAs(stats);

    verify(adminRepository).getAdminStats(dateCaptor.capture());
    long now = System.currentTimeMillis();
    assertThat(dateCaptor.getValue()).isBetween(
      new Date(now - TimeUnit.DAYS.toMillis(8)),
      new Date(now - TimeUnit.DAYS.toMillis(6)));
  }

  @Test
  @DisplayName("lists the first page of every account when no search is submitted")
  void getAllUsersListsTheFirstPageWithoutSearch() {
    User connectedUser = TestFixtures.user(1L);
    User listedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(distanceService.calculateDistance(connectedUser, listedUser)).thenReturn(42.7);
    when(adminRepository.getAllUsers(1L, "", FIRST_PAGE)).thenReturn(List.of(listedUser));

    List<UserDTO> users = adminService.getAllUsers(null, null);

    assertThat(users).singleElement().extracting(UserDTO::id).isEqualTo(2L);
    assertThat(listedUser.getGeolocation().getDistance()).isEqualTo(42L);
  }

  @Test
  @DisplayName("falls back on the first page and on an empty filter when the search is empty")
  void getAllUsersFallsBackOnTheFirstPage() {
    User connectedUser = TestFixtures.user(1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(adminRepository.getAllUsers(1L, "", FIRST_PAGE)).thenReturn(List.of());

    assertThat(adminService.getAllUsers(new AdminSearch(null, null), -1)).isEmpty();
  }

  @Test
  @DisplayName("keeps the default order when the search carries an empty sort column")
  void getAllUsersKeepsTheDefaultOrder() {
    User connectedUser = TestFixtures.user(1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(adminRepository.getAllUsers(1L, "bob", PageRequest.of(2, 5))).thenReturn(List.of());

    assertThat(adminService.getAllUsers(new AdminSearch("bob", ""), 2)).isEmpty();
  }

  @Test
  @DisplayName("sorts the accounts on the requested column")
  void getAllUsersSortsOnTheRequestedColumn() {
    User connectedUser = TestFixtures.user(1L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(adminRepository.getAllUsers(1L, "bob", "totalLikes", PageRequest.of(1, 5))).thenReturn(List.of());

    assertThat(adminService.getAllUsers(new AdminSearch("bob", "totalLikes"), 1)).isEmpty();
  }

  @Test
  @DisplayName("lists the archived accounts in their default order")
  void getAllDeletedAccountsListsThemInTheDefaultOrder() {
    List<DeletedAccount> accounts = List.of(new DeletedAccount("nickname", "user@pickme.com", new Date(), new Date(), 0L, 0L, 0L, "User"));
    when(adminRepository.getAllDeletedAccounts("", FIRST_PAGE)).thenReturn(accounts);

    assertThat(adminService.getAllDeletedAccounts(null, null)).isEqualTo(accounts);
  }

  @Test
  @DisplayName("sorts the archived accounts on the requested column")
  void getAllDeletedAccountsSortsOnTheRequestedColumn() {
    when(adminRepository.getAllDeletedAccounts("bob", "totalMatches", FIRST_PAGE)).thenReturn(List.of());

    assertThat(adminService.getAllDeletedAccounts(new AdminSearch("bob", "totalMatches"), 0)).isEmpty();
  }

  @Test
  @DisplayName("delegates the deletion of an account to the deletion service")
  void deleteUserByIdDelegatesToTheDeletionService() {
    adminService.deleteUserById(2L);

    verify(accountDeletionService).deleteUserById(2L);
  }
}
