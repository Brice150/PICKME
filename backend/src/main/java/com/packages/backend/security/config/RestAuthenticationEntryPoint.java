package com.packages.backend.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Answers an unauthenticated call with a plain 401 instead of the {@code WWW-Authenticate} header
 * of the default entry point, which would make the browser open its own credentials popup on top
 * of the login screen of the application.
 */
@Component
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

  private static final String REALM = "pickme";

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
    throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().println("HTTP Status 401 - " + authException.getMessage());
  }

  @Override
  public void afterPropertiesSet() {
    setRealmName(REALM);
    super.afterPropertiesSet();
  }
}
