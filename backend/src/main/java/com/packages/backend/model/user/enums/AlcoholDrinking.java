package com.packages.backend.model.user.enums;

public enum AlcoholDrinking {
  NO("Never drinks alcohol"),
  MAYBE("Drinks sometimes alcohol"),
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
