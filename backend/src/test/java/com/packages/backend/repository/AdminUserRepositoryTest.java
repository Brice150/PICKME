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
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("AdminUserRepository")
class AdminUserRepositoryTest {

  private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "registeredDate");
  private static final Sort MOST_LIKED_FIRST = Sort.by(Sort.Direction.DESC, "stats.totalLikes");

  @Autowired
  private AdminUserRepository adminUserRepository;
  @Autowired
  private DeletedAccountRepository deletedAccountRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EntityManager entityManager;

  private static Date daysAgo(int days) {
    return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private PageRequest firstPage(Sort sort) {
    return PageRequest.of(0, 5, sort);
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

  private void persistDeletedAccount(String email, Date deletionDate) {
    deletedAccountRepository.saveAndFlush(new DeletedAccount(
      "nickname", email, daysAgo(100), deletionDate, 0L, 0L, 0L, "User"));
  }

  @Test
  @DisplayName("counts the accounts, the archives and the recent activity of both")
  void getAdminStatsCountsEverything() {
    persistUser("recent@pickme.com", daysAgo(2), 0);
    persistUser("old@pickme.com", daysAgo(30), 0);
    persistDeletedAccount("recently.deleted@pickme.com", daysAgo(3));
    persistDeletedAccount("long.gone@pickme.com", daysAgo(60));

    AdminStats stats = adminUserRepository.getAdminStats(daysAgo(7));

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

    List<User> found = adminUserRepository.getAllUsers(
      connectedUser.getId(), "bob", firstPage(NEWEST_FIRST));

    assertThat(found).extracting(User::getId).containsExactly(bob.getId());
  }

  @Test
  @DisplayName("applies the order carried by the page and never lists the administrator")
  void getAllUsersAppliesTheRequestedOrder() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    User newest = persistUser("newest@pickme.com", daysAgo(2), 0);
    User oldest = persistUser("oldest@pickme.com", daysAgo(30), 0);

    List<User> found = adminUserRepository.getAllUsers(
      connectedUser.getId(), "", firstPage(NEWEST_FIRST));

    assertThat(found).extracting(User::getId).containsExactly(newest.getId(), oldest.getId());
  }

  @Test
  @DisplayName("sorts on a statistic that lives on another table")
  void getAllUsersSortsOnANestedStatistic() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    User popular = persistUser("popular@pickme.com", daysAgo(30), 42);
    User shy = persistUser("shy@pickme.com", daysAgo(2), 1);

    List<User> found = adminUserRepository.getAllUsers(
      connectedUser.getId(), "", firstPage(MOST_LIKED_FIRST));

    assertThat(found).extracting(User::getId).containsExactly(popular.getId(), shy.getId());
  }

  @Test
  @DisplayName("pages through the accounts")
  void getAllUsersPagesThroughTheAccounts() {
    User connectedUser = persistUser("admin@pickme.com", daysAgo(1), 0);
    for (int index = 0; index < 7; index++) {
      persistUser("user" + index + "@pickme.com", daysAgo(index + 2), 0);
    }

    assertThat(adminUserRepository.getAllUsers(connectedUser.getId(), "", firstPage(NEWEST_FIRST)))
      .hasSize(5);
    assertThat(adminUserRepository.getAllUsers(
      connectedUser.getId(), "", PageRequest.of(1, 5, NEWEST_FIRST))).hasSize(2);
  }
}
