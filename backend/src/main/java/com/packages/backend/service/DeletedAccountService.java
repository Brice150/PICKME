package com.packages.backend.service;

import com.packages.backend.model.entity.User;

public interface DeletedAccountService {

  /**
   * Archives the statistics of a deleted account and records who deleted it.
   *
   * @param userToDelete  account being deleted
   * @param connectedUser author of the deletion, the owner of the account or an administrator
   */
  void addDeletedAccount(User userToDelete, User connectedUser);
}
