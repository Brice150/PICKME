package com.packages.backend.controller;

import com.packages.backend.service.LikeService;
import com.packages.backend.service.LikeResult;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("LikeController")
class LikeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LikeService likeService;

  @Test
  @DisplayName("answers a like that created a match with the nickname to display")
  void addLikeAnswersWithTheMatchedNickname() throws Exception {
    when(likeService.addLike(2L)).thenReturn(new LikeResult.Matched("nickname2"));

    mockMvc.perform(post("/like/2"))
      .andExpect(status().isCreated())
      .andExpect(content().string("nickname2"));
  }

  @Test
  @DisplayName("answers a like without a match with an empty body")
  void addLikeAnswersWithoutABodyWhenThereIsNoMatch() throws Exception {
    when(likeService.addLike(2L)).thenReturn(new LikeResult.Liked());

    mockMvc.perform(post("/like/2"))
      .andExpect(status().isCreated())
      .andExpect(content().string(""));
  }

  @Test
  @DisplayName("rejects a profile the connected user already answered")
  void addLikeRejectsAnAlreadyAnsweredProfile() throws Exception {
    when(likeService.addLike(2L)).thenReturn(new LikeResult.Forbidden());

    mockMvc.perform(post("/like/2"))
      .andExpect(status().isForbidden());
  }
}
