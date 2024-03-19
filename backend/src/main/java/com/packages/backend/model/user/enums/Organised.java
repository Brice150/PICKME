package com.packages.backend.model.user.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Organised {
  @JsonProperty("Messy")
  NO("Messy"),
  @JsonProperty("Reasonably organised")
  MAYBE("Reasonably organised"),
  @JsonProperty("Very organised")
  YES("Very organised");

  private final String description;

  Organised(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Organised organised) {
    return organised != null ? organised.getDescription() : null;
  }
}
