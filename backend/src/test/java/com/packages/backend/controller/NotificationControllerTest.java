package com.packages.backend.controller;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.NotificationStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("NotificationController")
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NotificationService notificationService;
  @MockitoBean
  private NotificationStream notificationStream;

  @Test
  @DisplayName("opens the stream the menu listens on")
  void streamUserNotificationsOpensAStream() throws Exception {
    when(notificationStream.openForConnectedUser()).thenReturn(new SseEmitter());

    mockMvc.perform(get("/notification/stream"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM));
  }

  @Test
  @DisplayName("reads the notifications of the connected user")
  void getAllUserNotificationsReadsThem() throws Exception {
    Notification notification = new Notification("New match with nickname2", "match", new Date(), false, TestFixtures.user(1L));
    when(notificationService.getAllUserNotifications()).thenReturn(List.of(notification));

    mockMvc.perform(get("/notification/all"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].content").value("New match with nickname2"))
      .andExpect(jsonPath("$[0].link").value("match"))
      .andExpect(jsonPath("$[0].seen").value(false));
  }

  @Test
  @DisplayName("marks the notifications as seen")
  void markUserNotificationsAsSeenMarksThem() throws Exception {
    mockMvc.perform(put("/notification"))
      .andExpect(status().isOk());

    verify(notificationService).markUserNotificationsAsSeen();
  }
}
