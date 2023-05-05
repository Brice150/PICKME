package com.packages.backend.service;

import com.packages.backend.admin.AdminRepository;
import com.packages.backend.admin.AdminService;
import com.packages.backend.user.UserDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
  @Mock
  private AdminRepository adminRepository;
  @Mock
  private UserDTOMapper userDTOMapper;
  private AdminService adminService;

  @BeforeEach
  void setUp() {
    adminService = new AdminService(adminRepository, userDTOMapper);
  }
}
