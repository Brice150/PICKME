package com.packages.backend.controller;

import com.packages.backend.model.Registration;
import com.packages.backend.service.RegistrationService;
import com.packages.backend.service.ServiceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RegistrationController")
class RegistrationControllerTest {

  private static final String VALID_PAYLOAD = """
    {
      "nickname": "nickname",
      "job": "job",
      "birthDate": "1995-06-15T00:00:00.000+00:00",
      "email": "user@pickme.com",
      "password": "password",
      "genderAge": {"gender": "Man", "genderSearch": "Woman", "minAge": 18, "maxAge": 99},
      "geolocation": {"latitude": "48.8566", "longitude": "2.3522", "distanceSearch": 100}
    }
    """;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RegistrationService registrationService;

  @Test
  @DisplayName("creates the account of a valid registration")
  void registerCreatesTheAccount() throws Exception {
    when(registrationService.register(any(Registration.class))).thenReturn(ServiceStatus.OK);

    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .content(VALID_PAYLOAD))
      .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("answers a rejected registration with its reason")
  void registerAnswersWithTheReasonOfTheRejection() throws Exception {
    when(registrationService.register(any(Registration.class))).thenReturn("Email already taken");

    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .content(VALID_PAYLOAD))
      .andExpect(status().isForbidden())
      .andExpect(content().string("Email already taken"));
  }

  @Test
  @DisplayName("rejects an incomplete form before it reaches the service")
  void registerRejectsAnIncompleteForm() throws Exception {
    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "nickname": "",
            "job": "job",
            "birthDate": "1995-06-15T00:00:00.000+00:00",
            "email": "user@pickme.com",
            "password": "password",
            "genderAge": {"gender": "Man", "genderSearch": "Woman", "minAge": 18, "maxAge": 99},
            "geolocation": {"latitude": "48.8566", "longitude": "2.3522", "distanceSearch": 100}
          }
          """))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
      .andExpect(content().string("Nickname is empty"));

    verify(registrationService, never()).register(any(Registration.class));
  }

  @Test
  @DisplayName("rejects a form whose nested criteria are incomplete")
  void registerRejectsIncompleteNestedCriteria() throws Exception {
    mockMvc.perform(post("/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "nickname": "nickname",
            "job": "job",
            "birthDate": "1995-06-15T00:00:00.000+00:00",
            "email": "user@pickme.com",
            "password": "password",
            "genderAge": {"gender": "Man", "genderSearch": "Woman", "minAge": 18, "maxAge": 99},
            "geolocation": {"latitude": "", "longitude": "2.3522", "distanceSearch": 100}
          }
          """))
      .andExpect(status().isBadRequest())
      .andExpect(content().string("Latitude is empty"));
  }
}
