package com.packages.backend.repository;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("AdminRepository")
class AdminRepositoryTest {

  private static final PageRequest FIRST_PAGE = PageRequest.of(0, 5);

  @Autowired
  private AdminRepository adminRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DeletedAccountRepository deletedAccountRepository;
  @Autowired
  private EntityManager entityManager;

  private static Date daysAgo(int days) {
    return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Persists an account with the statistics the back office sorts on.
   *
   * @param email          email of the account
   * @param registeredDate date the account was opened
   * @param totalLikes     number of likes received
   * @return the persisted account
   */
  private User persistUser(String email, Date registeredDate, long totalLikes) {
    User user = TestFixtures.user(null);
    user.setEmail(email);
    user.setRegisteredDate(registeredDate);
    user.getStats().setTotalLikes(totalLikes);
    User saved = userRepository.saveAndFlush(user);
    entityManager.clear();
    return saved;
  }

  private void persistDeletedAccount(String email, Date deletionDate, long totalLikes) {
    deletedAccountRepository.saveAndFlush(new DeletedAccount(
      "nickname", email, daysAgo(100), deletionDate, 0L, totalLikes, 0L, "User"));
  }

  @Test
  @DisplayName("counts the accounts, the archives and the recent activity of both")
  void getAdminStatsCountsEverything() {
    persistUser("recent@pickme.com", daysAgo(2), 0);
    persistUser("old@pickme.com", daysAgo(30), 0);
    persistDeletedAccount("recently.deleted@pickme.com", daysAgo(3), 0);
    persistDeletedAccount("long.gone@pickme.com", daysAgo(60), 0);

    AdminStats stats = adminRepository.getAdminStats(daysAgo(7));

    assertThat(stats.getTotalUsers()).isEqualTo(2L);
    assertThat(stats.getTotalDeletedAccounts()).isEqualTo(2L);
    assertThat(stats.getTotalRecentUsers()).isEqualTo(1L);
    assertThat(stats.getTotalRecentDeletedAccounts()).isEqualTo(1L);
  }

  @Test
  @DisplayName("searches the accounts on a fragment of email, whatever the case")
  void getAllUsersSearchesOnAFragmentOfEmail() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    User bob = persistUser("BOB@pickme.com", daysAgo(2), 0);
    persistUser("alice@pickme.com", daysAgo(3), 0);

    List<User> found = adminRepository.getAllUsers(connectedUser.getId(), "bob", FIRST_PAGE);

    assertThat(found).extracting(User::getId).containsExactly(bob.getId());
  }

  @Test
  @DisplayName("lists the most recent accounts first and never the administrator themselves")
  void getAllUsersOrdersOnTheRegistrationDate() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    User newest = persistUser("newest@pickme.com", daysAgo(2), 0);
    User oldest = persistUser("oldest@pickme.com", daysAgo(30), 0);

    List<User> found = adminRepository.getAllUsers(connectedUser.getId(), "", FIRST_PAGE);

    assertThat(found).extracting(User::getId).containsExactly(newest.getId(), oldest.getId());
  }

  @Test
  @DisplayName("sorts the accounts on the requested statistic")
  void getAllUsersSortsOnTheRequestedStatistic() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    User popular = persistUser("popular@pickme.com", daysAgo(2), 42);
    User shy = persistUser("shy@pickme.com", daysAgo(3), 1);

    List<User> found = adminRepository.getAllUsers(
      connectedUser.getId(), "", "totalLikes", FIRST_PAGE);

    assertThat(found).extracting(User::getId).containsExactly(popular.getId(), shy.getId());
  }

  @Test
  @DisplayName("pages through the accounts")
  void getAllUsersPagesThroughTheAccounts() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    for (int index = 0; index < 7; index++) {
      persistUser("user" + index + "@pickme.com", daysAgo(index + 2), 0);
    }

    assertThat(adminRepository.getAllUsers(connectedUser.getId(), "", FIRST_PAGE)).hasSize(5);
    assertThat(adminRepository.getAllUsers(connectedUser.getId(), "", PageRequest.of(1, 5)))
      .hasSize(2);
  }

  @Test
  @DisplayName("lists the archived accounts, most recently closed first")
  void getAllDeletedAccountsOrdersOnTheDeletionDate() {
    persistDeletedAccount("oldest@pickme.com", daysAgo(30), 0);
    persistDeletedAccount("newest@pickme.com", daysAgo(1), 0);

    List<DeletedAccount> found = adminRepository.getAllDeletedAccounts("", FIRST_PAGE);

    assertThat(found).extracting(DeletedAccount::getEmail)
      .containsExactly("newest@pickme.com", "oldest@pickme.com");
  }

  @Test
  @DisplayName("sorts the archived accounts on the requested statistic")
  void getAllDeletedAccountsSortsOnTheRequestedStatistic() {
    persistDeletedAccount("shy@pickme.com", daysAgo(1), 1);
    persistDeletedAccount("popular@pickme.com", daysAgo(30), 42);

    List<DeletedAccount> found = adminRepository.getAllDeletedAccounts("", "totalLikes", FIRST_PAGE);

    assertThat(found).extracting(DeletedAccount::getEmail)
      .containsExactly("popular@pickme.com", "shy@pickme.com");
  }
}
