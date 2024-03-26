package com.packages.backend.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Personality {
  @JsonProperty("Introvert")
  INTROVERT("Introvert"),
  @JsonProperty("Ambivert")
  AMBIVERT("Ambivert"),
  @JsonProperty("Extravert")
  EXTRAVERT("Extravert");

  private final String description;

  Personality(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Personality personality) {
    return personality != null ? personality.getDescription() : null;
  }
}
