package com.packages.backend.service.impl;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.AdminUserRepository;
import com.packages.backend.repository.DeletedAccountRepository;
import com.packages.backend.service.AccountDeletionService;
import com.packages.backend.service.AdminService;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {

  // Number of accounts listed on a single back office page.
  private static final int ADMIN_PAGE_SIZE = 5;
  // Number of days an account or a connection is considered recent.
  private static final int RECENT_PERIOD_IN_DAYS = 7;
  // The statistics the back office is allowed to sort on. Anything else is ignored rather than
  // handed to Spring Data, which would either fail or expose a property the client should not
  // be able to order on.
  private static final Set<String> SORTABLE_STATISTICS =
    Set.of("totalDislikes", "totalLikes", "totalMatches");
  // Order of the lists when no statistic is requested.
  private static final Sort NEWEST_ACCOUNTS_FIRST = Sort.by(Sort.Direction.DESC, "registeredDate");
  private static final Sort LAST_DELETED_FIRST = Sort.by(Sort.Direction.DESC, "deletionDate");

  private final AdminUserRepository adminUserRepository;
  private final DeletedAccountRepository deletedAccountRepository;
  private final UserService userService;
  private final UserDTOMapper userDTOMapper;
  private final DistanceService distanceService;
  private final AccountDeletionService accountDeletionService;

  public AdminServiceImpl(AdminUserRepository adminUserRepository, DeletedAccountRepository deletedAccountRepository, UserService userService, UserDTOMapper userDTOMapper, DistanceService distanceService, AccountDeletionService accountDeletionService) {
    this.adminUserRepository = adminUserRepository;
    this.deletedAccountRepository = deletedAccountRepository;
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
    return adminUserRepository.getAdminStats(calendar.getTime());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDTO> getAllUsers(AdminSearch adminSearch, Integer page) {
    User connectedUser = userService.getConnectedUser();
    // The statistics of an account live on its own table, hence the path.
    PageRequest pageRequest = toPageRequest(adminSearch, page, NEWEST_ACCOUNTS_FIRST, "stats.");
    List<User> users = adminUserRepository.getAllUsers(
      connectedUser.getId(), toEmailFilter(adminSearch), pageRequest);
    users.forEach(user -> user.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, user).longValue()));
    return users.stream().map(userDTOMapper).toList();
  }

  @Override
  public List<DeletedAccount> getAllDeletedAccounts(AdminSearch adminSearch, Integer page) {
    PageRequest pageRequest = toPageRequest(adminSearch, page, LAST_DELETED_FIRST, "");
    return deletedAccountRepository.getAllDeletedAccounts(toEmailFilter(adminSearch), pageRequest);
  }

  @Override
  public void deleteUserById(Long userId) {
    accountDeletionService.deleteUserById(userId);
  }

  /**
   * Builds the page to read, ordered by the requested statistic when the search asks for a known
   * one, and by the default order otherwise.
   *
   * @param adminSearch  search criteria, possibly null
   * @param page         requested page number
   * @param defaultSort  order to apply when no statistic is requested
   * @param statisticPath prefix leading to the statistics of the sorted entity
   * @return the page request to hand to the repository
   */
  private PageRequest toPageRequest(AdminSearch adminSearch, Integer page, Sort defaultSort, String statisticPath) {
    String statistic = toSortedStatistic(adminSearch);
    Sort sort = statistic == null
      ? defaultSort
      : Sort.by(Sort.Direction.DESC, statisticPath + statistic);
    return PageRequest.of(toPageNumber(page), ADMIN_PAGE_SIZE, sort);
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
   * Reads the statistic a search asks to sort on, ignoring anything the back office does not
   * expose as a sortable column.
   *
   * @param adminSearch search criteria, possibly null
   * @return the statistic to sort on, or null to keep the default order
   */
  private String toSortedStatistic(AdminSearch adminSearch) {
    if (adminSearch == null || adminSearch.getOrderBy() == null) {
      return null;
    }
    return SORTABLE_STATISTICS.contains(adminSearch.getOrderBy()) ? adminSearch.getOrderBy() : null;
  }
}
