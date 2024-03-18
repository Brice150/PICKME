package com.packages.backend.model.user.enums;

public enum Gender {
  MAN("Man"),
  WOMAN("Woman"),
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
