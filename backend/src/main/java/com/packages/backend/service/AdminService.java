package com.packages.backend.service;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.entity.DeletedAccount;

import java.util.List;

public interface AdminService {

  /**
   * Returns the counters displayed on the administration dashboard.
   *
   * @return the statistics of the application
   */
  AdminStats getAdminStats();

  /**
   * Returns one page of accounts matching the search criteria.
   *
   * @param adminSearch email filter and sort order, both optional
   * @param page        zero based page number, {@code null} being handled as the first page
   * @return at most one page of accounts
   */
  List<UserDTO> getAllUsers(AdminSearch adminSearch, Integer page);

  /**
   * Returns one page of archived accounts matching the search criteria.
   *
   * @param adminSearch email filter and sort order, both optional
   * @param page        zero based page number, {@code null} being handled as the first page
   * @return at most one page of archived accounts
   */
  List<DeletedAccount> getAllDeletedAccounts(AdminSearch adminSearch, Integer page);

  /**
   * Deletes the account of a user.
   *
   * @param userId identifier of the user to delete
   */
  void deleteUserById(Long userId);
}
