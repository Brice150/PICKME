package com.packages.backend.model.user.enums;

public enum Parenthood {
  NO("Doesn't want children"),
  MAYBE("Will want children someday"),
  YES("Has children");

  private final String description;

  Parenthood(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public static String getDescriptionNullSafe(Parenthood parenthood) {
    return parenthood != null ? parenthood.getDescription() : null;
  }
}
