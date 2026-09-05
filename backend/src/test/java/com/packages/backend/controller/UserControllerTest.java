package com.packages.backend.controller;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.dto.UserUpdateRequest;
import com.packages.backend.service.AccountDeletionService;
import com.packages.backend.service.CandidateSelectionService;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("UserController")
class UserControllerTest {

  private static final UserDTOMapper MAPPER = new UserDTOMapper();

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;
  @MockitoBean
  private CandidateSelectionService candidateSelectionService;
  @MockitoBean
  private AccountDeletionService accountDeletionService;

  @Test
  @DisplayName("answers the login call with the connected account")
  void loginAnswersWithTheConnectedAccount() throws Exception {
    when(userService.getConnectedUserDTO()).thenReturn(MAPPER.apply(TestFixtures.user(1L)));

    mockMvc.perform(get("/login"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.email").value("user1@pickme.com"));
  }

  @Test
  @DisplayName("reads a page of candidates")
  void getAllSelectedUsersReadsAPage() throws Exception {
    when(candidateSelectionService.getAllSelectedUsers(3)).thenReturn(List.of(MAPPER.apply(TestFixtures.user(2L))));

    mockMvc.perform(get("/user/all/3"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(2));
  }

  @Test
  @DisplayName("reads the connected account")
  void getConnectedUserReadsTheAccount() throws Exception {
    when(userService.getConnectedUserDTO()).thenReturn(MAPPER.apply(TestFixtures.user(1L)));

    mockMvc.perform(get("/user"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nickname").value("nickname1"));
  }

  @Test
  @DisplayName("answers an update with the account as it has been saved")
  void updateUserAnswersWithTheSavedAccount() throws Exception {
    when(userService.updateUser(any(UserUpdateRequest.class)))
      .thenReturn(MAPPER.apply(TestFixtures.user(1L)));

    mockMvc.perform(put("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"nickname\":\"newNickname\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("closes the account of the connected user")
  void deleteConnectedUserClosesTheAccount() throws Exception {
    mockMvc.perform(delete("/user"))
      .andExpect(status().isOk());

    verify(accountDeletionService).deleteConnectedUser();
  }
}
