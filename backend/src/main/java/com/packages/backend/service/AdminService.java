package com.packages.backend.service;

import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    return adminRepository.getAllUsers().stream().map(userDTOMapper).toList();
  }

  public void deleteUserById(Long userId) {
    userService.deleteUserById(userId);
  }
}
