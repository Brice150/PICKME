package com.packages.backend.controller;

import com.packages.backend.TestFixtures;
import com.packages.backend.exception.PictureNotFoundException;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.service.PictureService;
import com.packages.backend.service.ServiceStatus;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PictureController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("PictureController")
class PictureControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PictureService pictureService;

  @Test
  @DisplayName("answers an added picture with the picture as it has been saved")
  void addPictureAnswersWithTheSavedPicture() throws Exception {
    when(pictureService.addPicture("base64"))
      .thenReturn(Optional.of(new Picture("base64", true, TestFixtures.user(1L))));

    mockMvc.perform(post("/picture")
        .contentType(MediaType.TEXT_PLAIN)
        .content("base64"))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.content").value("base64"))
      .andExpect(jsonPath("$.isMainPicture").value(true));
  }

  @Test
  @DisplayName("refuses a picture when the album is full or already holds it")
  void addPictureRefusesAFullOrDuplicateAlbum() throws Exception {
    when(pictureService.addPicture("base64")).thenReturn(Optional.empty());

    mockMvc.perform(post("/picture")
        .contentType(MediaType.TEXT_PLAIN)
        .content("base64"))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("reads the album of a user")
  void getUserPicturesReadsTheAlbum() throws Exception {
    when(pictureService.getUserPictures(2L))
      .thenReturn(List.of(new Picture("base64", true, TestFixtures.user(2L))));

    mockMvc.perform(get("/picture/user/2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].content").value("base64"));
  }

  @Test
  @DisplayName("promotes a picture as the main one")
  void selectMainPicturePromotesIt() throws Exception {
    when(pictureService.selectMainPictureById(3L)).thenReturn(ServiceStatus.OK);

    mockMvc.perform(put("/picture/3"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("refuses to promote a picture owned by somebody else")
  void selectMainPictureRefusesAPictureOfAnotherUser() throws Exception {
    when(pictureService.selectMainPictureById(3L)).thenReturn(ServiceStatus.FORBIDDEN);

    mockMvc.perform(put("/picture/3"))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("deletes a picture of the connected user")
  void deletePictureDeletesIt() throws Exception {
    when(pictureService.deletePictureById(3L)).thenReturn(ServiceStatus.OK);

    mockMvc.perform(delete("/picture/3"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("refuses to delete a picture owned by somebody else")
  void deletePictureRefusesAPictureOfAnotherUser() throws Exception {
    when(pictureService.deletePictureById(3L)).thenReturn(ServiceStatus.FORBIDDEN);

    mockMvc.perform(delete("/picture/3"))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("answers a missing picture with a 404 carrying the reason")
  void deletePictureAnswersNotFoundForAMissingPicture() throws Exception {
    when(pictureService.deletePictureById(3L))
      .thenThrow(new PictureNotFoundException("Picture by id 3 was not found"));

    mockMvc.perform(delete("/picture/3"))
      .andExpect(status().isNotFound())
      .andExpect(content().string("Picture by id 3 was not found"));
  }
}
