package com.packages.backend.model.user.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Animals {
  @JsonProperty("Does not like animals")
  NO("Does not like animals"),
  @JsonProperty("Likes animals")
  MAYBE("Likes animals"),
  @JsonProperty("Has animals")
  YES("Has animals");

  private final String description;

  Animals(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Animals animals) {
    return animals != null ? animals.getDescription() : null;
  }
}
