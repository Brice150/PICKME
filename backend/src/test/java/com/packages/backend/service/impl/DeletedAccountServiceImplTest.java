package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.DeletedAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeletedAccountServiceImpl")
class DeletedAccountServiceImplTest {

  @Mock
  private DeletedAccountRepository deletedAccountRepository;

  @Captor
  private ArgumentCaptor<DeletedAccount> deletedAccountCaptor;

  @InjectMocks
  private DeletedAccountServiceImpl deletedAccountService;

  @Test
  @DisplayName("archives the statistics of an account closed by its owner")
  void addDeletedAccountArchivesAnAccountClosedByItsOwner() {
    User userToDelete = TestFixtures.user(1L);
    userToDelete.getStats().setTotalDislikes(3L);
    userToDelete.getStats().setTotalLikes(5L);
    userToDelete.getStats().setTotalMatches(2L);

    deletedAccountService.addDeletedAccount(userToDelete, userToDelete);

    verify(deletedAccountRepository).save(deletedAccountCaptor.capture());
    DeletedAccount archived = deletedAccountCaptor.getValue();
    assertThat(archived.getNickname()).isEqualTo(userToDelete.getNickname());
    assertThat(archived.getEmail()).isEqualTo(userToDelete.getEmail());
    assertThat(archived.getRegisteredDate()).isEqualTo(userToDelete.getRegisteredDate());
    assertThat(archived.getDeletionDate()).isNotNull();
    assertThat(archived.getTotalDislikes()).isEqualTo(3L);
    assertThat(archived.getTotalLikes()).isEqualTo(5L);
    assertThat(archived.getTotalMatches()).isEqualTo(2L);
    assertThat(archived.getDeletedBy()).isEqualTo("User");
  }

  @Test
  @DisplayName("records the administrator that closed an account")
  void addDeletedAccountRecordsTheAdministratorThatClosedIt() {
    User userToDelete = TestFixtures.user(1L);
    User admin = TestFixtures.user(2L, UserRole.ROLE_ADMIN);

    deletedAccountService.addDeletedAccount(userToDelete, admin);

    verify(deletedAccountRepository).save(deletedAccountCaptor.capture());
    assertThat(deletedAccountCaptor.getValue().getDeletedBy()).isEqualTo("Admin");
  }
}
