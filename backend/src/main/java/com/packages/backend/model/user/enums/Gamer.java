package com.packages.backend.model.user.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Gamer {
  @JsonProperty("Never plays video games")
  NO("Never plays video games"),
  @JsonProperty("Plays video games sometimes")
  MAYBE("Plays video games sometimes"),
  @JsonProperty("Plays video games a lot")
  YES("Plays video games a lot");

  private final String description;

  Gamer(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Gamer gamer) {
    return gamer != null ? gamer.getDescription() : null;
  }
}
