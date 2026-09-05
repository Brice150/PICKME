package com.packages.backend.service.impl;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.AdminRepository;
import com.packages.backend.service.AccountDeletionService;
import com.packages.backend.service.AdminService;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

  // Number of accounts listed on a single back office page.
  private static final int ADMIN_PAGE_SIZE = 5;
  // Number of days an account or a connection is considered recent.
  private static final int RECENT_PERIOD_IN_DAYS = 7;

  private final AdminRepository adminRepository;
  private final UserService userService;
  private final UserDTOMapper userDTOMapper;
  private final DistanceService distanceService;
  private final AccountDeletionService accountDeletionService;

  public AdminServiceImpl(AdminRepository adminRepository, UserService userService, UserDTOMapper userDTOMapper, DistanceService distanceService, AccountDeletionService accountDeletionService) {
    this.adminRepository = adminRepository;
    this.userService = userService;
    this.userDTOMapper = userDTOMapper;
    this.distanceService = distanceService;
    this.accountDeletionService = accountDeletionService;
  }

  @Override
  public AdminStats getAdminStats() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DAY_OF_MONTH, -RECENT_PERIOD_IN_DAYS);
    return adminRepository.getAdminStats(calendar.getTime());
  }

  @Override
  public List<UserDTO> getAllUsers(AdminSearch adminSearch, Integer page) {
    User connectedUser = userService.getConnectedUser();
    PageRequest pageRequest = PageRequest.of(toPageNumber(page), ADMIN_PAGE_SIZE);
    String email = toEmailFilter(adminSearch);
    String orderBy = toOrderBy(adminSearch);
    List<User> users = orderBy == null
      ? adminRepository.getAllUsers(connectedUser.getId(), email, pageRequest)
      : adminRepository.getAllUsers(connectedUser.getId(), email, orderBy, pageRequest);
    users.forEach(user -> user.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, user).longValue()));
    return users.stream().map(userDTOMapper).toList();
  }

  @Override
  public List<DeletedAccount> getAllDeletedAccounts(AdminSearch adminSearch, Integer page) {
    PageRequest pageRequest = PageRequest.of(toPageNumber(page), ADMIN_PAGE_SIZE);
    String email = toEmailFilter(adminSearch);
    String orderBy = toOrderBy(adminSearch);
    return orderBy == null
      ? adminRepository.getAllDeletedAccounts(email, pageRequest)
      : adminRepository.getAllDeletedAccounts(email, orderBy, pageRequest);
  }

  @Override
  public void deleteUserById(Long userId) {
    accountDeletionService.deleteUserById(userId);
  }

  /**
   * Falls back on the first page when no valid page number has been requested.
   *
   * @param page requested page number
   * @return the zero based page number to query
   */
  private int toPageNumber(Integer page) {
    return page == null || page < 0 ? 0 : page;
  }

  /**
   * Reads the email filter of a search, an empty filter matching every account.
   *
   * @param adminSearch search criteria, possibly null
   * @return the email filter
   */
  private String toEmailFilter(AdminSearch adminSearch) {
    return adminSearch != null && adminSearch.getEmail() != null ? adminSearch.getEmail() : "";
  }

  /**
   * Reads the sort order of a search.
   *
   * @param adminSearch search criteria, possibly null
   * @return the column to sort on, or null to keep the default order
   */
  private String toOrderBy(AdminSearch adminSearch) {
    return adminSearch != null && adminSearch.getOrderBy() != null && !adminSearch.getOrderBy().isEmpty()
      ? adminSearch.getOrderBy()
      : null;
  }
}
