package com.packages.backend.controller;

import com.packages.backend.exception.MessageNotFoundException;
import com.packages.backend.model.entity.Message;
import com.packages.backend.service.MessageService;
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

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "USER")
@DisplayName("MessageController")
class MessageControllerTest {

  private static final String PAYLOAD = "{\"content\":\"hello\",\"fkReceiver\":2}";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("answers a sent message with the message as it has been saved")
  void addMessageAnswersWithTheSavedMessage() throws Exception {
    when(messageService.addMessage(any(Message.class)))
      .thenReturn(Optional.of(new Message("hello", new Date(), "nickname1", 1L, 2L)));

    mockMvc.perform(post("/message")
        .contentType(MediaType.APPLICATION_JSON)
        .content(PAYLOAD))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.content").value("hello"))
      .andExpect(jsonPath("$.sender").value("nickname1"));
  }

  @Test
  @DisplayName("refuses a message sent to a profile that is not a match")
  void addMessageRefusesAProfileThatIsNotAMatch() throws Exception {
    when(messageService.addMessage(any(Message.class))).thenReturn(Optional.empty());

    mockMvc.perform(post("/message")
        .contentType(MediaType.APPLICATION_JSON)
        .content(PAYLOAD))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("answers an edited message with its new content")
  void updateMessageAnswersWithTheNewContent() throws Exception {
    when(messageService.updateMessage(any(Message.class)))
      .thenReturn(Optional.of(new Message("edited", new Date(), "nickname1", 1L, 2L)));

    mockMvc.perform(put("/message")
        .contentType(MediaType.APPLICATION_JSON)
        .content(PAYLOAD))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").value("edited"));
  }

  @Test
  @DisplayName("refuses to edit a message written by somebody else")
  void updateMessageRefusesAMessageOfAnotherUser() throws Exception {
    when(messageService.updateMessage(any(Message.class))).thenReturn(Optional.empty());

    mockMvc.perform(put("/message")
        .contentType(MediaType.APPLICATION_JSON)
        .content(PAYLOAD))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("deletes a message of the connected user")
  void deleteMessageDeletesIt() throws Exception {
    when(messageService.deleteMessageById(10L)).thenReturn(ServiceStatus.OK);

    mockMvc.perform(delete("/message/10"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("refuses to delete a message written by somebody else")
  void deleteMessageRefusesAMessageOfAnotherUser() throws Exception {
    when(messageService.deleteMessageById(10L)).thenReturn(ServiceStatus.FORBIDDEN);

    mockMvc.perform(delete("/message/10"))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("answers a missing message with a 404 carrying the reason")
  void deleteMessageAnswersNotFoundForAMissingMessage() throws Exception {
    when(messageService.deleteMessageById(10L))
      .thenThrow(new MessageNotFoundException("Message by id 10 was not found"));

    mockMvc.perform(delete("/message/10"))
      .andExpect(status().isNotFound())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
      .andExpect(content().string("Message by id 10 was not found"));
  }
}
