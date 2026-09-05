package com.packages.backend.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caps how often a single client may try to log in, so that guessing a password becomes too slow
 * to be worth it. Credentials are checked on every call to {@code /login}, which without this
 * would let an attacker run through a dictionary at the speed of the network.
 * <p>
 * The counters are held in memory: they protect one instance, and a deployment spread over
 * several of them would need a shared store.
 */
public class LoginRateLimitFilter extends OncePerRequestFilter {

  private static final String LOGIN_PATH = "/login";
  private static final int MAX_ATTEMPTS_PER_WINDOW = 10;
  private static final Duration WINDOW = Duration.ofMinutes(1);
  // Above this many clients the expired counters are swept, so that a flood of distinct addresses
  // cannot grow the map without bound.
  private static final int MAX_TRACKED_CLIENTS = 10_000;

  private final Map<String, Attempts> attemptsByClient = new ConcurrentHashMap<>();
  private final Clock clock;

  public LoginRateLimitFilter() {
    this(Clock.systemUTC());
  }

  LoginRateLimitFilter(Clock clock) {
    this.clock = clock;
  }

  /**
   * Attempts made by one client since the beginning of its current window.
   *
   * @param windowStart start of the window the attempts are counted in
   * @param count       number of attempts made so far
   */
  private record Attempts(Instant windowStart, int count) {
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !LOGIN_PATH.equals(request.getRequestURI());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
    if (isOverTheLimit(request.getRemoteAddr())) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().println("Too many login attempts, please try again in a minute");
      return;
    }
    filterChain.doFilter(request, response);
  }

  /**
   * Records an attempt and tells whether the client has gone past what a window allows.
   *
   * @param client address the attempt comes from
   * @return true when the attempt has to be refused
   */
  private boolean isOverTheLimit(String client) {
    Instant now = clock.instant();
    sweepExpiredCounters(now);
    Attempts attempts = attemptsByClient.compute(client, (address, current) ->
      current == null || isExpired(current, now)
        ? new Attempts(now, 1)
        : new Attempts(current.windowStart(), current.count() + 1));
    return attempts.count() > MAX_ATTEMPTS_PER_WINDOW;
  }

  private void sweepExpiredCounters(Instant now) {
    if (attemptsByClient.size() > MAX_TRACKED_CLIENTS) {
      attemptsByClient.values().removeIf(attempts -> isExpired(attempts, now));
    }
  }

  private boolean isExpired(Attempts attempts, Instant now) {
    return Duration.between(attempts.windowStart(), now).compareTo(WINDOW) >= 0;
  }
}
