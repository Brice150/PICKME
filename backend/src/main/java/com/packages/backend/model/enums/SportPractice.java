package com.packages.backend.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SportPractice {
  @JsonProperty("Never practices sport")
  NO("Never practices sport"),
  @JsonProperty("Practices sport sometimes")
  MAYBE("Practices sport sometimes"),
  @JsonProperty("Athlete")
  YES("Athlete");

  private final String description;

  SportPractice(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(SportPractice sportPractice) {
    return sportPractice != null ? sportPractice.getDescription() : null;
  }
}
