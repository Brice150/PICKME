package com.packages.backend.security;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.AdminStats;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.entity.User;
import com.packages.backend.service.AdminService;
import com.packages.backend.service.NotificationService;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Checks the rules of the whole filter chain rather than a single endpoint: who reaches the API,
 * who reaches the back office, and which origins the browser is allowed to call it from.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("security rules")
class SecurityRulesTest {

  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;
  @MockitoBean
  private AdminService adminService;
  @MockitoBean
  private NotificationService notificationService;

  @BeforeEach
  void setUp(WebApplicationContext context) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(SecurityMockMvcConfigurers.springSecurity())
      .build();
  }

  /**
   * The front end only sends the credentials on the login call, then relies on the session cookie
   * for every other one. This reproduces that exchange end to end.
   */
  @Test
  @DisplayName("keeps the user logged in through the session opened by the login call")
  void theSessionOpenedByTheLoginCallAuthenticatesTheNextOnes() throws Exception {
    User connectedUser = TestFixtures.user(1L);
    connectedUser.setPassword(new BCryptPasswordEncoder().encode("password"));
    when(userService.loadUserByUsername(connectedUser.getEmail())).thenReturn(connectedUser);
    when(userService.getConnectedUserDTO()).thenReturn(new UserDTOMapper().apply(connectedUser));
    when(notificationService.getAllUserNotifications()).thenReturn(List.of());

    MvcResult login = mockMvc.perform(get("/login")
        .with(httpBasic(connectedUser.getEmail(), "password")))
      .andExpect(status().isOk())
      .andReturn();

    MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
    assertThat(session).as("the login call must open a session").isNotNull();

    // No credentials this time, only the cookie the browser would send back.
    mockMvc.perform(get("/notification/all").session(session))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("answers an unauthenticated call with a 401")
  void anonymousCallsAreRejected() throws Exception {
    mockMvc.perform(get("/user"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("leaves the registration open to anybody")
  void registrationStaysPublic() throws Exception {
    // The payload is empty on purpose: a 400 proves the request went through the filter chain and
    // was rejected by the validation of the controller, not by the authentication.
    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/registration")
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("answers the health probe without asking for credentials")
  void healthProbeStaysPublic() throws Exception {
    mockMvc.perform(get("/actuator/health"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("UP"))
      // The probe must not disclose the state of the database or of the disk to anonymous callers.
      .andExpect(jsonPath("$.components").doesNotExist());
  }

  @Test
  @DisplayName("lets an authenticated user reach their own account")
  @WithMockUser(roles = "USER")
  void authenticatedUsersReachTheirAccount() throws Exception {
    when(userService.getConnectedUserDTO()).thenReturn(new UserDTOMapper().apply(TestFixtures.user(1L)));

    mockMvc.perform(get("/user"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("keeps a standard user out of the back office")
  @WithMockUser(roles = "USER")
  void standardUsersCannotReachTheBackOffice() throws Exception {
    mockMvc.perform(get("/admin/stats"))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("lets an administrator reach the back office")
  @WithMockUser(roles = "ADMIN")
  void administratorsReachTheBackOffice() throws Exception {
    when(adminService.getAdminStats()).thenReturn(new AdminStats(0L, 0L, 0L, 0L));

    mockMvc.perform(get("/admin/stats"))
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName("accepts the preflight of an allowed origin")
  void allowedOriginsPassThePreflight() throws Exception {
    mockMvc.perform(options("/user")
        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
      .andExpect(status().isOk())
      .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"))
      .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
  }

  @Test
  @DisplayName("rejects the preflight of an unknown origin")
  void unknownOriginsAreRejected() throws Exception {
    mockMvc.perform(options("/user")
        .header(HttpHeaders.ORIGIN, "https://evil.example.com")
        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name()))
      .andExpect(status().isForbidden());
  }
}
