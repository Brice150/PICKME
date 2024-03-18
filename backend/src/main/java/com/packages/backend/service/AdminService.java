package com.packages.backend.service;

import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.model.user.UserStats;
import com.packages.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminService {
  private final AdminRepository adminRepository;
  private final UserService userService;
  private final UserDTOMapper userDTOMapper;

  @Autowired
  public AdminService(AdminRepository adminRepository, UserService userService, UserDTOMapper userDTOMapper) {
    this.adminRepository = adminRepository;
    this.userService = userService;
    this.userDTOMapper = userDTOMapper;
  }

  public List<UserDTO> getAllUsers() {
    User connectedUser = userService.getConnectedUser();
    List<User> users = adminRepository.getAllUsers(connectedUser.getId());
    List<UserStats> usersStats = adminRepository.getAllUsersStats(connectedUser.getId());
    Map<Long, UserStats> userStatsMap = usersStats.stream()
      .collect(Collectors.toMap(UserStats::getId, Function.identity()));
    users.forEach(user -> {
      UserStats userStats = userStatsMap.get(user.getId());
      if (userStats != null) {
        user.setTotalDislikes(userStats.getTotalDislikes());
        user.setTotalLikes(userStats.getTotalLikes());
        user.setTotalMatches(userStats.getTotalMatches());
      }
    });
    return users.stream().map(userDTOMapper).toList();
  }

  public void deleteUserById(Long userId) {
    userService.deleteUserById(userId);
  }
}
