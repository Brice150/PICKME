package com.packages.backend.security.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRateLimitFilter")
class LoginRateLimitFilterTest {

  private final AtomicReference<Instant> now =
    new AtomicReference<>(Instant.parse("2026-01-01T10:00:00Z"));

  /** A clock the test moves forward by hand, to reach the end of a window without waiting. */
  private final Clock clock = new Clock() {
    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }

    @Override
    public Instant instant() {
      return now.get();
    }
  };

  private final LoginRateLimitFilter filter = new LoginRateLimitFilter(clock);

  /**
   * Sends one login attempt from an address.
   *
   * @param client address the attempt comes from
   * @return the response the filter produced
   */
  private MockHttpServletResponse attemptLogin(String client) throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
    request.setRemoteAddr(client);
    MockHttpServletResponse response = new MockHttpServletResponse();
    filter.doFilter(request, response, new MockFilterChain());
    return response;
  }

  @Test
  @DisplayName("lets a reasonable number of attempts through")
  void aReasonableNumberOfAttemptsGoesThrough() throws Exception {
    for (int attempt = 1; attempt <= 10; attempt++) {
      assertThat(attemptLogin("10.0.0.1").getStatus()).isEqualTo(200);
    }
  }

  @Test
  @DisplayName("refuses the attempts once the client has gone past the limit")
  void tooManyAttemptsAreRefused() throws Exception {
    for (int attempt = 1; attempt <= 10; attempt++) {
      attemptLogin("10.0.0.2");
    }

    MockHttpServletResponse refused = attemptLogin("10.0.0.2");

    assertThat(refused.getStatus()).isEqualTo(429);
    assertThat(refused.getContentAsString()).contains("Too many login attempts");
  }

  @Test
  @DisplayName("counts each client on its own")
  void eachClientHasItsOwnCounter() throws Exception {
    for (int attempt = 1; attempt <= 11; attempt++) {
      attemptLogin("10.0.0.3");
    }

    assertThat(attemptLogin("10.0.0.4").getStatus()).isEqualTo(200);
  }

  @Test
  @DisplayName("lets the client try again once the window has passed")
  void theWindowEventuallyResets() throws Exception {
    for (int attempt = 1; attempt <= 11; attempt++) {
      attemptLogin("10.0.0.5");
    }
    assertThat(attemptLogin("10.0.0.5").getStatus()).isEqualTo(429);

    now.updateAndGet(instant -> instant.plus(Duration.ofMinutes(1)));

    assertThat(attemptLogin("10.0.0.5").getStatus()).isEqualTo(200);
  }

  @Test
  @DisplayName("never gets in the way of the other endpoints")
  void theOtherEndpointsAreLeftAlone() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user");
    request.setRemoteAddr("10.0.0.6");

    for (int attempt = 1; attempt <= 50; attempt++) {
      MockHttpServletResponse response = new MockHttpServletResponse();
      filter.doFilter(request, response, new MockFilterChain());
      assertThat(response.getStatus()).isEqualTo(200);
    }
  }
}
