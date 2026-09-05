package com.packages.backend.security.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CorsProperties")
class CorsPropertiesTest {

  @Test
  @DisplayName("allows no origin at all when none is configured")
  void missingOriginsBecomeAnEmptyList() {
    assertThat(new CorsProperties(null).allowedOrigins()).isEmpty();
  }

  @Test
  @DisplayName("keeps the configured origins out of reach of the rest of the application")
  void configuredOriginsAreCopied() {
    List<String> origins = new ArrayList<>(List.of("http://localhost:4200"));

    CorsProperties properties = new CorsProperties(origins);
    origins.add("https://evil.example.com");

    assertThat(properties.allowedOrigins()).containsExactly("http://localhost:4200");
    assertThatThrownBy(() -> properties.allowedOrigins().add("https://evil.example.com"))
      .isInstanceOf(UnsupportedOperationException.class);
  }
}
