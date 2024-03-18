package com.packages.backend.model.user.enums;

public enum SportPractice {
  NO("Never practice sport"),
  MAYBE("Practices sport sometimes"),
  YES("Athlete");

  private final String description;

  SportPractice(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(SportPractice sportPractice) {
    return sportPractice != null ? sportPractice.getDescription() : null;
  }
}
