package com.packages.backend.model.user.enums;

public enum Organised {
  NO("Messy"),
  MAYBE("Reasonably organised"),
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
