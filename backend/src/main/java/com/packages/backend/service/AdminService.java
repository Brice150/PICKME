package com.packages.backend.service;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AdminService {
  private final AdminRepository adminRepository;
  private final UserService userService;
  private final UserDTOMapper userDTOMapper;
  private final DistanceService distanceService;

  @Autowired
  public AdminService(AdminRepository adminRepository, UserService userService, UserDTOMapper userDTOMapper, DistanceService distanceService) {
    this.adminRepository = adminRepository;
    this.userService = userService;
    this.userDTOMapper = userDTOMapper;
    this.distanceService = distanceService;
  }

  public AdminStats getAdminStats() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DAY_OF_MONTH, -7);
    Date recentDate = calendar.getTime();
    return adminRepository.getAdminStats(recentDate);
  }

  public List<UserDTO> getAllUsers(AdminSearch adminSearch, Integer page) {
    page = page == null ? 0 : page;
    User connectedUser = userService.getConnectedUser();
    String email = adminSearch != null && adminSearch.getEmail() != null ? adminSearch.getEmail() : "";
    String orderBy = adminSearch != null && !"".equals(adminSearch.getOrderBy()) ? adminSearch.getOrderBy() : null;
    List<User> users;
    if (null == orderBy) {
      users = adminRepository.getAllUsers(connectedUser.getId(), email, PageRequest.of(page, 5));
    } else {
      users = adminRepository.getAllUsers(connectedUser.getId(), email, orderBy, PageRequest.of(page, 5));
    }
    users.forEach(user -> user.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, user).longValue()));
    return users.stream().map(userDTOMapper).toList();
  }

  public List<DeletedAccount> getAllDeletedAccounts(AdminSearch adminSearch, Integer page) {
    page = page == null ? 0 : page;
    String email = adminSearch != null && adminSearch.getEmail() != null ? adminSearch.getEmail() : "";
    String orderBy = adminSearch != null && !"".equals(adminSearch.getOrderBy()) ? adminSearch.getOrderBy() : null;
    if (null == orderBy) {
      return adminRepository.getAllDeletedAccounts(email, PageRequest.of(page, 5));
    }
    return adminRepository.getAllDeletedAccounts(email, orderBy, PageRequest.of(page, 5));
  }

  public void deleteUserById(Long userId) {
    userService.deleteUserById(userId);
  }
}
