package com.packages.backend.model;

import com.packages.backend.model.user.enums.Gender;

import java.util.Date;

public class Registration {
  private final String nickname;
  private final String job;
  private final String city;
  private final Date birthDate;
  private final Gender gender;
  private final Gender genderSearch;
  private final Long minAge;
  private final Long maxAge;
  private final String email;
  private final String password;

  public Registration(String nickname, String job, String city, Date birthDate, Gender gender, Gender genderSearch, Long minAge, Long maxAge, String email, String password) {
    this.nickname = nickname;
    this.job = job;
    this.city = city;
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
