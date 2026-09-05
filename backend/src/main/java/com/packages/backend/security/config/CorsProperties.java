package com.packages.backend.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Origins the browser is allowed to call the API from, declared per environment instead of being
 * hard coded in the security configuration.
 *
 * @param allowedOrigins origins of the front end applications
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(List<String> allowedOrigins) {

  public CorsProperties {
    allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
  }
}
