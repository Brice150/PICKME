package com.packages.backend.service;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  public List<UserDTO> getAllUsers(AdminSearch adminSearch) {
    User connectedUser = userService.getConnectedUser();
    List<User> users = adminRepository.getAllUsers(connectedUser.getId(), adminSearch.getNickname(), adminSearch.getGenders(), adminSearch.getMinAge().intValue(), adminSearch.getMaxAge().intValue());
    List<Tuple> userStatsTuples = adminRepository.getAllUsersStats(connectedUser.getId());
    Map<Long, Tuple> userStatsMap = userStatsTuples.stream()
      .collect(Collectors.toMap(tuple -> tuple.get("id", Long.class), Function.identity()));
    users.forEach(user -> {
      user.setDistance(distanceService.calculateDistance(connectedUser, user).longValue());
      Tuple userStatsTuple = userStatsMap.get(user.getId());
      if (userStatsTuple != null) {
        user.setTotalDislikes(userStatsTuple.get("totalDislikes", Long.class));
        user.setTotalLikes(userStatsTuple.get("totalLikes", Long.class));
        user.setTotalMatches(userStatsTuple.get("totalMatches", Long.class));
      }
    });
    return users.stream()
      .filter(user -> user.getDistance() <= adminSearch.getDistance())
      .sorted(Comparator.comparing(User::getTotalLikes).reversed())
      .map(userDTOMapper).toList();
  }

  public void deleteUserById(Long userId) {
    userService.deleteUserById(userId);
  }
}
