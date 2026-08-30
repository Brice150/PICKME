package com.packages.backend.model;

import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class Registration {

  @NotBlank(message = "Nickname is empty")
  private final String nickname;

  @NotBlank(message = "Job is empty")
  private final String job;

  @NotNull(message = "Birth date is empty")
  private final Date birthDate;

  @NotBlank(message = "Email is empty")
  private final String email;

  @NotBlank(message = "Password is empty")
  private final String password;

  @NotNull(message = "Gender or Age is empty")
  @Valid
  private final GenderAge genderAge;

  @NotNull(message = "Geolocation is empty")
  @Valid
  private final Geolocation geolocation;

  public Registration(String nickname, String job, Date birthDate, String email, String password, GenderAge genderAge, Geolocation geolocation) {
    this.nickname = nickname;
    this.job = job;
    this.birthDate = birthDate;
    this.email = email;
    this.password = password;
    this.genderAge = genderAge;
    this.geolocation = geolocation;
  }

  public String getNickname() {
    return nickname;
  }

  public String getJob() {
    return job;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public GenderAge getGenderAge() {
    return genderAge;
  }

  public Geolocation getGeolocation() {
    return geolocation;
  }
}
