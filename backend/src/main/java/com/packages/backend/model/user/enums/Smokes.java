package com.packages.backend.model.user.enums;

public enum Smokes {
  NO("Never smokes"),
  MAYBE("Smokes sometimes"),
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
