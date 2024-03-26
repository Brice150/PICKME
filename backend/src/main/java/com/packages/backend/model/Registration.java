package com.packages.backend.model;

import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;

import java.util.Date;

public class Registration {
  private final String nickname;
  private final String job;
  private final Date birthDate;
  private final String email;
  private final String password;
  private final GenderAge genderAge;
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
