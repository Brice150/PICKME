package com.packages.backend.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AlcoholDrinking {
  @JsonProperty("Never drinks alcohol")
  NO("Never drinks alcohol"),
  @JsonProperty("Drinks sometimes alcohol")
  MAYBE("Drinks sometimes alcohol"),
  @JsonProperty("Drinks a lot alcohol")
  YES("Drinks a lot alcohol");

  private final String description;

  AlcoholDrinking(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(AlcoholDrinking alcoholDrinking) {
    return alcoholDrinking != null ? alcoholDrinking.getDescription() : null;
  }
}
