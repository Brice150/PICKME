package com.packages.backend.controller;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.entity.Message;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("MatchController")
class MatchControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("exposes the matches with their conversation")
  void getAllUserMatchesExposesTheConversations() throws Exception {
    Match match = new Match(
      new UserDTOMapperRestricted().apply(TestFixtures.user(2L)),
      List.of(new Message("hello", new Date(), "nickname1", 1L, 2L))
    );
    when(userService.getAllUserMatches()).thenReturn(List.of(match));

    mockMvc.perform(get("/match/all"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].user.id").value(2))
      .andExpect(jsonPath("$[0].user.userRole").value("HIDDEN"))
      .andExpect(jsonPath("$[0].messages[0].content").value("hello"));
  }
}
