package com.packages.backend.model.user.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Smokes {
  @JsonProperty("Never smokes")
  NO("Never smokes"),
  @JsonProperty("Smokes sometimes")
  MAYBE("Smokes sometimes"),
  @JsonProperty("Smokes a lot")
  YES("Smokes a lot");

  private final String description;

  Smokes(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Smokes smokes) {
    return smokes != null ? smokes.getDescription() : null;
  }
}
