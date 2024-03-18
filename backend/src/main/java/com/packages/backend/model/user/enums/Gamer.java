package com.packages.backend.model.user.enums;

public enum Gamer {
  NO("Never play video games"),
  MAYBE("Play video games sometimes"),
  YES("Play video games a lot");

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
