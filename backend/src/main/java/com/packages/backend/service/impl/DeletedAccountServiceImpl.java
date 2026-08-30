package com.packages.backend.service.impl;

import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.DeletedAccountRepository;
import com.packages.backend.service.DeletedAccountService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeletedAccountServiceImpl implements DeletedAccountService {

  private static final String DELETED_BY_USER = "User";
  private static final String DELETED_BY_ADMIN = "Admin";

  private final DeletedAccountRepository deletedAccountRepository;

  public DeletedAccountServiceImpl(DeletedAccountRepository deletedAccountRepository) {
    this.deletedAccountRepository = deletedAccountRepository;
  }

  @Override
  public void addDeletedAccount(User userToDelete, User connectedUser) {
    DeletedAccount deletedAccount = new DeletedAccount(
      userToDelete.getNickname(),
      userToDelete.getEmail(),
      userToDelete.getRegisteredDate(),
      new Date(),
      userToDelete.getStats().getTotalDislikes(),
      userToDelete.getStats().getTotalLikes(),
      userToDelete.getStats().getTotalMatches(),
      connectedUser.getUserRole() == UserRole.ROLE_USER ? DELETED_BY_USER : DELETED_BY_ADMIN
    );
    deletedAccountRepository.save(deletedAccount);
  }
}
