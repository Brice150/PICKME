package com.packages.backend.model.user.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Parenthood {
  @JsonProperty("Does not want children")
  NO("Does not want children"),
  @JsonProperty("Will want children someday")
  MAYBE("Will want children someday"),
  @JsonProperty("Has children")
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
