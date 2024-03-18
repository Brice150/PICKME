package com.packages.backend.service;

import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
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
    List<Tuple> userStatsTuples = adminRepository.getAllUsersStats(connectedUser.getId());
    Map<Long, Tuple> userStatsMap = userStatsTuples.stream()
      .collect(Collectors.toMap(tuple -> tuple.get("id", Long.class), Function.identity()));
    users.forEach(user -> {
      Tuple userStatsTuple = userStatsMap.get(user.getId());
      if (userStatsTuple != null) {
        user.setTotalDislikes(userStatsTuple.get("totalDislikes", Long.class));
        user.setTotalLikes(userStatsTuple.get("totalLikes", Long.class));
        user.setTotalMatches(userStatsTuple.get("totalMatches", Long.class));
      }
    });
    return users.stream().map(userDTOMapper).toList();
  }

  public void deleteUserById(Long userId) {
    userService.deleteUserById(userId);
  }
}