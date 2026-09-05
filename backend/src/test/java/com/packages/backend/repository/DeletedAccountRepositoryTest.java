package com.packages.backend.repository;

import com.packages.backend.model.entity.DeletedAccount;
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
@DisplayName("DeletedAccountRepository")
class DeletedAccountRepositoryTest {

  private static final Sort LAST_DELETED_FIRST = Sort.by(Sort.Direction.DESC, "deletionDate");
  private static final Sort MOST_MATCHED_FIRST = Sort.by(Sort.Direction.DESC, "totalMatches");

  @Autowired
  private DeletedAccountRepository deletedAccountRepository;

  private static Date daysAgo(int days) {
    return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private void persist(String email, Date deletionDate, long totalMatches) {
    deletedAccountRepository.saveAndFlush(new DeletedAccount(
      "nickname", email, daysAgo(100), deletionDate, 0L, 0L, totalMatches, "User"));
  }

  @Test
  @DisplayName("lists the archived accounts in the order carried by the page")
  void listsThemInTheRequestedOrder() {
    persist("oldest@pickme.com", daysAgo(30), 0);
    persist("newest@pickme.com", daysAgo(1), 0);

    List<DeletedAccount> found =
      deletedAccountRepository.getAllDeletedAccounts("", PageRequest.of(0, 5, LAST_DELETED_FIRST));

    assertThat(found).extracting(DeletedAccount::getEmail)
      .containsExactly("newest@pickme.com", "oldest@pickme.com");
  }

  @Test
  @DisplayName("sorts the archived accounts on their own counters")
  void sortsOnTheirOwnCounters() {
    persist("shy@pickme.com", daysAgo(1), 1);
    persist("popular@pickme.com", daysAgo(30), 42);

    List<DeletedAccount> found =
      deletedAccountRepository.getAllDeletedAccounts("", PageRequest.of(0, 5, MOST_MATCHED_FIRST));

    assertThat(found).extracting(DeletedAccount::getEmail)
      .containsExactly("popular@pickme.com", "shy@pickme.com");
  }

  @Test
  @DisplayName("searches the archives on a fragment of email, whatever the case")
  void searchesOnAFragmentOfEmail() {
    persist("BOB@pickme.com", daysAgo(1), 0);
    persist("alice@pickme.com", daysAgo(2), 0);

    List<DeletedAccount> found = deletedAccountRepository.getAllDeletedAccounts(
      "bob", PageRequest.of(0, 5, LAST_DELETED_FIRST));

    assertThat(found).extracting(DeletedAccount::getEmail).containsExactly("BOB@pickme.com");
  }
}
