package com.packages.backend.service;

import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.DeletedAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeletedAccountService {
  private final DeletedAccountRepository deletedAccountRepository;

  public DeletedAccountService(DeletedAccountRepository deletedAccountRepository) {
    this.deletedAccountRepository = deletedAccountRepository;
  }

  public void addDeletedAccount(User userToDelete, User connectedUser) {
    DeletedAccount deletedAccount = new DeletedAccount(
      userToDelete.getNickname(),
      userToDelete.getEmail(),
      userToDelete.getRegisteredDate(),
      new Date(),
      userToDelete.getStats().getTotalDislikes(),
      userToDelete.getStats().getTotalLikes(),
      userToDelete.getStats().getTotalMatches(),
      connectedUser.getNickname()
    );
    deletedAccountRepository.save(deletedAccount);
  }
}
