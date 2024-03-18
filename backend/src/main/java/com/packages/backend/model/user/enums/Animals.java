package com.packages.backend.model.user.enums;

public enum Animals {
  NO("Does not like animals"),
  MAYBE("Likes animals"),
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
