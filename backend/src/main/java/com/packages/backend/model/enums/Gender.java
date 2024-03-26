package com.packages.backend.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gender {
  @JsonProperty("Man")
  MAN("Man"),
  @JsonProperty("Woman")
  WOMAN("Woman"),
  @JsonProperty("Other")
  OTHER("Other");

  private final String description;

  Gender(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Gender gender) {
    return gender != null ? gender.getDescription() : null;
  }
}
