package com.packages.backend.model;

import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * Registration form. Every rule that does not need the database is checked here, the controller
 * rejecting an incomplete form before it reaches the service.
 */
public record Registration(

  @NotBlank(message = "Nickname is empty")
  String nickname,

  @NotBlank(message = "Job is empty")
  String job,

  @NotNull(message = "Birth date is empty")
  Date birthDate,

  @NotBlank(message = "Email is empty")
  String email,

  @NotBlank(message = "Password is empty")
  String password,

  @NotNull(message = "Gender or Age is empty")
  @Valid
  GenderAge genderAge,

  @NotNull(message = "Geolocation is empty")
  @Valid
  Geolocation geolocation
) {
}
