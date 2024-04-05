package com.packages.backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class MyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
  /**
   * Used to make customizable error messages and codes when login fails
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
    throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    PrintWriter writer = response.getWriter();
    writer.println("HTTP Status 401 - " + authEx.getMessage());
  }

  @Override
  public void afterPropertiesSet() {
    setRealmName("YOUR REALM");
    super.afterPropertiesSet();
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("http://localhost:4200");
    config.addAllowedOrigin("https://pick-me.up.railway.app");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}
