package com.packages.backend.service;

public interface AccountDeletionService {

  /**
   * Deletes the account of the connected user and everything it owns.
   */
  void deleteConnectedUser();

  /**
   * Deletes the account of another user, which is reserved to the administrators.
   *
   * @param userId identifier of the user to delete
   */
  void deleteUserById(Long userId);
}
