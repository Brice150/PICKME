package com.packages.backend.controller;

import com.packages.backend.service.DislikeService;
import com.packages.backend.service.ServiceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DislikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("DislikeController")
class DislikeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DislikeService dislikeService;

  @Test
  @DisplayName("registers a dislike")
  void addDislikeRegistersIt() throws Exception {
    when(dislikeService.addDislike(2L)).thenReturn(null);

    mockMvc.perform(post("/dislike/2"))
      .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("rejects a profile that has already been disliked")
  void addDislikeRejectsAnAlreadyDislikedProfile() throws Exception {
    when(dislikeService.addDislike(2L)).thenReturn(ServiceStatus.FORBIDDEN);

    mockMvc.perform(post("/dislike/2"))
      .andExpect(status().isForbidden());
  }
}
