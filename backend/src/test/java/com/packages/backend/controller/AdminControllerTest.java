package com.packages.backend.controller;

import com.packages.backend.model.AdminSearch;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.entity.DeletedAccount;
import com.packages.backend.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
@DisplayName("AdminController")
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AdminService adminService;

  @Test
  @DisplayName("exposes the counters of the dashboard")
  void getAdminStatsExposesTheCounters() throws Exception {
    when(adminService.getAdminStats()).thenReturn(new AdminStats(10L, 2L, 3L, 1L));

    mockMvc.perform(get("/admin/stats"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalUsers").value(10))
      .andExpect(jsonPath("$.totalDeletedAccounts").value(2))
      .andExpect(jsonPath("$.totalRecentUsers").value(3))
      .andExpect(jsonPath("$.totalRecentDeletedAccounts").value(1));
  }

  @Test
  @DisplayName("binds the search criteria and the page number of an account search")
  void getAllUsersBindsTheSearchCriteria() throws Exception {
    when(adminService.getAllUsers(any(AdminSearch.class), eq(2))).thenReturn(List.of());

    mockMvc.perform(post("/admin/user/all/2")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"bob\",\"orderBy\":\"totalLikes\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("lists the archived accounts")
  void getAllDeletedAccountsListsThem() throws Exception {
    DeletedAccount account = new DeletedAccount("nickname", "user@pickme.com", new Date(), new Date(), 0L, 0L, 0L, "User");
    when(adminService.getAllDeletedAccounts(any(AdminSearch.class), eq(0))).thenReturn(List.of(account));

    mockMvc.perform(post("/admin/deleted-account/all/0")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"\",\"orderBy\":\"\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].email").value("user@pickme.com"))
      .andExpect(jsonPath("$[0].deletedBy").value("User"));
  }

  @Test
  @DisplayName("deletes the account of a user")
  void deleteUserDeletesTheAccount() throws Exception {
    mockMvc.perform(delete("/admin/7"))
      .andExpect(status().isOk());

    verify(adminService).deleteUserById(7L);
  }
}
