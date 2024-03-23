package com.packages.backend.model;

import com.packages.backend.model.user.enums.Gender;

import java.util.Date;

public class Registration {
  private final String nickname;
  private final String job;
  private final String city;
  private final String latitude;
  private final String longitude;
  private final Long distanceSearch;
  private final Date birthDate;
  private final Gender gender;
  private final Gender genderSearch;
  private final Long minAge;
  private final Long maxAge;
  private final String email;
  private final String password;

  public Registration(String nickname, String job, String city, String latitude, String longitude, Long distanceSearch, Date birthDate, Gender gender, Gender genderSearch, Long minAge, Long maxAge, String email, String password) {
    this.nickname = nickname;
    this.job = job;
    this.city = city;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distanceSearch = distanceSearch;
    this.birthDate = birthDate;
    this.gender = gender;
    this.genderSearch = genderSearch;
    this.minAge = minAge;
    this.maxAge = maxAge;
    this.email = email;
    this.password = password;
  }

  public String getNickname() {
    return nickname;
  }

  public String getJob() {
    return job;
  }

  public String getCity() {
    return city;
  }

  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public Long getDistanceSearch() {
    return distanceSearch;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public Gender getGender() {
    return gender;
  }

  public Gender getGenderSearch() {
    return genderSearch;
  }

  public Long getMinAge() {
    return minAge;
  }

  public Long getMaxAge() {
    return maxAge;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
