package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.AdminUserRepository;
import com.packages.backend.repository.DeletedAccountRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl")
class AdminServiceImplTest {

  private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "registeredDate");

  @Mock
  private AdminUserRepository adminUserRepository;
  @Mock
  private DeletedAccountRepository deletedAccountRepository;
  @Mock
  private UserService userService;
  @Mock
  private DistanceService distanceService;
  @Mock
  private AccountDeletionService accountDeletionService;

  @Captor
  private ArgumentCaptor<Date> dateCaptor;
  @Captor
  private ArgumentCaptor<Pageable> pageableCaptor;

  private AdminServiceImpl adminService;

  @BeforeEach
  void setUp() {
    adminService = new AdminServiceImpl(adminUserRepository, deletedAccountRepository, userService,
      new UserDTOMapper(), distanceService, accountDeletionService);
  }

  /** Runs an account search and returns the page the repository has been asked for. */
  private Pageable searchUsers(AdminSearch adminSearch, Integer page) {
    when(userService.getConnectedUser()).thenReturn(TestFixtures.user(1L));
    when(adminUserRepository.getAllUsers(eq(1L), anyString(), any(Pageable.class)))
      .thenReturn(List.of());

    adminService.getAllUsers(adminSearch, page);

    verify(adminUserRepository).getAllUsers(eq(1L), anyString(), pageableCaptor.capture());
    return pageableCaptor.getValue();
  }

  @Test
  @DisplayName("counts the recent activity over the last week")
  void getAdminStatsCountsTheLastWeek() {
    AdminStats stats = new AdminStats(10L, 2L, 3L, 1L);
    when(adminUserRepository.getAdminStats(any(Date.class))).thenReturn(stats);

    assertThat(adminService.getAdminStats()).isSameAs(stats);

    verify(adminUserRepository).getAdminStats(dateCaptor.capture());
    long now = System.currentTimeMillis();
    assertThat(dateCaptor.getValue()).isBetween(
      new Date(now - TimeUnit.DAYS.toMillis(8)),
      new Date(now - TimeUnit.DAYS.toMillis(6)));
  }

  @Test
  @DisplayName("lists the newest accounts first when no statistic is requested")
  void getAllUsersOrdersOnTheRegistrationDateByDefault() {
    Pageable pageable = searchUsers(null, null);

    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(5);
    assertThat(pageable.getSort()).isEqualTo(NEWEST_FIRST);
  }

  @Test
  @DisplayName("sorts the accounts on the requested statistic")
  void getAllUsersSortsOnTheRequestedStatistic() {
    Pageable pageable = searchUsers(new AdminSearch("bob", "totalLikes"), 2);

    assertThat(pageable.getPageNumber()).isEqualTo(2);
    // The statistics of an account live on their own table, hence the path.
    assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "stats.totalLikes"));
  }

  @Test
  @DisplayName("ignores a sort column the back office does not expose")
  void getAllUsersIgnoresAnUnknownSortColumn() {
    // Handing an arbitrary property to Spring Data would either fail or let the client order the
    // accounts on a column the back office never offers.
    Pageable pageable = searchUsers(new AdminSearch("", "password"), 0);

    assertThat(pageable.getSort()).isEqualTo(NEWEST_FIRST);
  }

  @Test
  @DisplayName("falls back on the first page when the page number makes no sense")
  void getAllUsersFallsBackOnTheFirstPage() {
    assertThat(searchUsers(new AdminSearch(null, null), -1).getPageNumber()).isZero();
  }

  @Test
  @DisplayName("passes the email filter of the search and measures the distance of each account")
  void getAllUsersPassesTheEmailFilter() {
    User connectedUser = TestFixtures.user(1L);
    User listedUser = TestFixtures.user(2L);
    when(userService.getConnectedUser()).thenReturn(connectedUser);
    when(distanceService.calculateDistance(connectedUser, listedUser)).thenReturn(42.7);
    when(adminUserRepository.getAllUsers(eq(1L), eq("bob"), any(Pageable.class)))
      .thenReturn(List.of(listedUser));

    List<UserDTO> users = adminService.getAllUsers(new AdminSearch("bob", null), 0);

    assertThat(users).singleElement().extracting(UserDTO::id).isEqualTo(2L);
    assertThat(listedUser.getGeolocation().getDistance()).isEqualTo(42L);
  }

  @Test
  @DisplayName("lists the last closed accounts first when no statistic is requested")
  void getAllDeletedAccountsOrdersOnTheDeletionDateByDefault() {
    List<DeletedAccount> accounts = List.of(new DeletedAccount("nickname", "user@pickme.com", new Date(), new Date(), 0L, 0L, 0L, "User"));
    when(deletedAccountRepository.getAllDeletedAccounts(eq(""), any(Pageable.class)))
      .thenReturn(accounts);

    assertThat(adminService.getAllDeletedAccounts(null, null)).isEqualTo(accounts);

    verify(deletedAccountRepository).getAllDeletedAccounts(eq(""), pageableCaptor.capture());
    assertThat(pageableCaptor.getValue().getSort())
      .isEqualTo(Sort.by(Sort.Direction.DESC, "deletionDate"));
  }

  @Test
  @DisplayName("sorts the archived accounts on the requested statistic, without any path")
  void getAllDeletedAccountsSortsOnTheRequestedStatistic() {
    when(deletedAccountRepository.getAllDeletedAccounts(eq("bob"), any(Pageable.class)))
      .thenReturn(List.of());

    adminService.getAllDeletedAccounts(new AdminSearch("bob", "totalMatches"), 1);

    verify(deletedAccountRepository).getAllDeletedAccounts(eq("bob"), pageableCaptor.capture());
    // An archive carries its own counters, unlike an account whose statistics are a separate row.
    assertThat(pageableCaptor.getValue().getSort())
      .isEqualTo(Sort.by(Sort.Direction.DESC, "totalMatches"));
  }

  @Test
  @DisplayName("delegates the deletion of an account to the deletion service")
  void deleteUserByIdDelegatesToTheDeletionService() {
    adminService.deleteUserById(2L);

    verify(accountDeletionService).deleteUserById(2L);
  }
}
