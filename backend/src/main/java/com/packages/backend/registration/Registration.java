package com.packages.backend.registration;

import java.util.Date;

public class Registration {
  private final String nickname;
  private final String email;
  private final String password;
  private final String gender;
  private final String genderSearch;
  private final String relationshipType;
  private final Date birthDate;
  private final String city;

  public Registration(String nickname, String email, String password, String gender, String genderSearch, String relationshipType, Date birthDate, String city) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
    this.gender = gender;
    this.genderSearch = genderSearch;
    this.relationshipType = relationshipType;
    this.birthDate = birthDate;
    this.city = city;
  }

  public String getNickname() {
    return nickname;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getGender() {
    return gender;
  }

  public String getGenderSearch() {
    return genderSearch;
  }

  public String getRelationshipType() {
    return relationshipType;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public String getCity() {
    return city;
  }
}
