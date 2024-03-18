package com.packages.backend.model.user.enums;

public enum Personality {
  INTROVERT("Introvert"),
  AMBIVERT("Ambivert"),
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
