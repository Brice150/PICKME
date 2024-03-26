package com.packages.backend.service;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
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

  public List<UserDTO> getAllUsers(AdminSearch adminSearch, Integer page) {
    if (null == adminSearch || null == adminSearch.getNickname() || null == adminSearch.getGenders() || null == adminSearch.getMinAge() || null == adminSearch.getMaxAge() || null == adminSearch.getDistance()) {
      return Collections.emptyList();
    }
    if (null == page) {
      page = 0;
    }
    User connectedUser = userService.getConnectedUser();
    List<User> users = adminRepository.getAllUsers(connectedUser.getId(), adminSearch.getNickname(), adminSearch.getGenders(), adminSearch.getMinAge().intValue(), adminSearch.getMaxAge().intValue(), PageRequest.of(page, 25));
    users.forEach(user -> user.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, user).longValue()));
    return users.stream()
      .filter(user -> user.getGeolocation().getDistance() <= adminSearch.getDistance())
      .sorted(Comparator.comparing(user -> user.getStats() != null ? user.getStats().getTotalLikes() : 0L, Comparator.reverseOrder()))
      .map(userDTOMapper)
      .toList();
  }

  public void deleteUserById(Long userId) {
    userService.deleteUserById(userId);
  }
}
